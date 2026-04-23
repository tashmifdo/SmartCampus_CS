# Smart Campus Sensor & Room Management API

## Overview

This project implements a lightweight RESTful API designed to manage rooms, sensors, and sensor readings within a smart campus environment.

- **Tech stack used**: Java 17, JAX-RS (Jersey 3.x), embedded Grizzly HTTP server, JSON.
- **The API base URL when running locally is**: `http://localhost:8080/api/v1`
- **Discovery point**: `GET /api/v1` returns a JSON document.
- **In-memory persistence**: all data lives in a shared `InMemoryStore` singleton for the lifetime of the process.
- **Error handling**: domain exceptions are mapped to structured JSON (`ApiError`) with appropriate HTTP status codes (e.g., `409`, `422`, `500`).

## Core design characteristics

1. Versioned API path for controlled evolution.
2. JSON request/response format.
3. Standard HTTP methods (GET, POST, PUT, DELETE) for CRUD-style operations.
4. Discovery endpoint at /api/v1 to expose available high-level resources.
5. Validation and exception mapping to return meaningful HTTP status codes.

### Resource model

- **Rooms** (`/rooms`)
	- `GET /rooms` — list rooms
	- `POST /rooms` — create room
	- `GET /rooms/{roomId}` — fetch a room
	- `DELETE /rooms/{roomId}` — delete a room (blocked with `409` if it has ACTIVE sensors)
- **Sensors** (`/sensors`)
	- `GET /sensors` — list sensors
	- `GET /sensors?type=CO2` — filter sensors by type
	- `POST /sensors` — create sensor (requires an existing `roomId`, otherwise `422`)
- **Sensor readings** (sub-resource locator)
	- `GET /sensors/{sensorId}/readings` — list readings for a sensor
	- `POST /sensors/{sensorId}/readings` — append a reading (auto-generates `id` and `timestamp` if omitted)

## Build and Run Instructions (Vscode/NetBeans)

### NetBeans

1. Open NetBeans.
2. Select File -> Open Project.
3. Choose the project folder and open it.
4. Wait until NetBeans finishes Maven project scanning and dependency loading.
5. In the Projects view, right-click the project and select Clean and Build.
6. Confirm that the build finishes with BUILD SUCCESS in the Output window.
7. Right-click the project and select Run.
8. NetBeans runs mvn jetty:run and starts the server on port 8080.
9. Confirm server startup in the Output window.
10. Keep the run process active while testing endpoints.

### Vscode

1. Open VS Code.
2. Select File -> Open Folder.
3. Open the project folder.
4. Wait until Maven project loading is completed in VS Code.
5. Open the Command Palette with Ctrl+Shift+P.
6. Run Tasks: Run Task and select maven: build to execute clean package.
7. Confirm BUILD SUCCESS in the terminal output.
8. Run Tasks: Run Task again and select maven: jetty:run.
9. Keep the running task active while testing API endpoints.


```bash
java -version
mvn -v
```

### Build

1. From the repo root, run:

```bash
mvn clean test
```

2. Create a runnable shaded JAR:

```bash
mvn clean package
```

This produces `target/smart-campus-api-1.0.0-SNAPSHOT-all.jar`.

### Run the server

Option A (recommended during development):

```bash
mvn exec:java
```

Option B (run the packaged JAR):

```bash
java -jar target/smart-campus-api-1.0.0-SNAPSHOT-all.jar
```

The server listens on port **8080** and prints:

```
Smart Campus API started: http://0.0.0.0:8080/api/v1
```

## Sample curl commands.


1) Discovery entry point:

```bash
curl http://localhost:8080/api/v1
```

2) Create a room:

```bash
curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d '{"id":"R1","name":"Lab 1","capacity":30,"regulations":"No food"}'
```

3) List rooms:

```bash
curl http://localhost:8080/api/v1/rooms
```

4) Create a sensor (linked to `R1`):

```bash
curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d '{"id":"S1","type":"CO2","roomId":"R1"}'
```

5) List sensors (and filter by type):

```bash
curl http://localhost:8080/api/v1/sensors
curl "http://localhost:8080/api/v1/sensors?type=CO2"
```

6) Append a sensor reading (id + timestamp are optional):

```bash
curl -X POST http://localhost:8080/api/v1/sensors/S1/readings -H "Content-Type: application/json" -d '{"value":412.5}'
```

7) List readings for a sensor:

```bash
curl http://localhost:8080/api/v1/sensors/S1/readings
```

8) Create and then delete an empty room (demonstrates `DELETE`):

```bash
curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d '{"id":"R2","name":"Meeting Room","capacity":8}'

curl -i -X DELETE http://localhost:8080/api/v1/rooms/R2
```

---

## Coursework report Answers with questions

<!-- ===================== -->
<!--       
PART 1: Service Architecture & Setup. 
Question 1:
In your report, explain the default life cycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.
Answer:
In this project, the resource classes should be treated as short-lived request handlers, not as the place where shared application state lives. The shared data is kept in the singleton InMemoryStore, which all resource classes access through InMemoryStore.getInstance(). That store uses ConcurrentHashMap for rooms and sensors, and CopyOnWriteArrayList for readings, so the data remains available across requests and is safe for concurrent access at the collection level.
This matters because the resource classes themselves are not responsible for persistence. They should stay stateless and delegate all shared state operations to the store. The code also shows that shared mutable objects still need careful coordination: for example, when a sensor is created, the room’s sensorIds list is updated inside a synchronized block on the room object. That prevents inconsistent updates while multiple requests are running at the same time.
Question 2:
Why is the provision of Hypermedia (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?
Answer:
This project includes a discovery endpoint at GET /api/v1 that returns a JSON document with API metadata and fully qualified links to the main collections, including rooms and sensors. Those links are built from the runtime base URI, so they stay correct across different deployment environments.
That is useful because the API tells clients where to go next instead of forcing them to hardcode paths or host details. The discovery response makes the API more self-describing, easier to navigate, and less tightly coupled to a specific deployment address. It also gives client developers a single starting point for locating the main resources.

PART 2: Room Management.
Question 3:
When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.
Answer:
In this project, GET /api/v1/rooms returns full Room objects rather than just IDs. That means the client receives the room id, name, capacity, regulations, and sensorIds in one response.
This is convenient for clients because they can display or process the data immediately without making extra calls for each room. The tradeoff is a larger payload, especially as the number of rooms grows. Returning only IDs would reduce response size, but it would also force the client to make more requests to retrieve details. For this implementation, returning full room objects is the more practical choice because it reduces client-side complexity and avoids extra round trips.

Question 4:
Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.
Answer:
The DELETE room operation is idempotent in terms of final server state. If a room is deleted successfully once, repeating the same DELETE request does not change the state again because the room is already gone.
The implementation behaves as follows: if the room does not exist, the resource returns 404 Not Found. If the room exists but has ACTIVE sensors assigned, the delete is blocked and RoomNotEmptyException is mapped to 409 Conflict. If the room exists and has no active sensors, the store removes it and the endpoint returns 204 No Content.
So, if a client sends the same DELETE request multiple times, the first request may delete the room, and later requests will simply report that the room is no longer there. The state does not change after the first successful deletion.
Therefore, the implementation is idempotent by state outcome, because repeated identical DELETE calls lead to the same stable state. However, in a strict response-oriented interpretation, some evaluators may describe it as not strictly idempotent because the first call returns 204 while later calls return 404. In both interpretations, no additional side effects occur after the target state has been reached.
PART 3: Sensor Operations & Linking.
Question 5:
We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?
Answer:
Both the sensor creation endpoint and the reading append endpoint explicitly consume JSON. That means the runtime expects a request body with Content-Type: application/json.
If a client sends text/plain, application/xml, or any other unsupported media type, JAX-RS will reject the request before the resource method runs and return 415 Unsupported Media Type. In other words, the request body will not be treated as valid input for those methods. This keeps the contract strict and ensures that JSON deserialization is only attempted when the client sends the correct format.
Technical implications are:
	Parsing does not proceed, so JSON deserialization and validation logic are skipped.
	Resource creation logic is not executed, so no partial writes occur.
	The client receives a clear protocol-level error and must resend using application/json.
	If exceptional mappers are configured, the service can return a structured custom error body while keeping status 415.
In summary, JAX-RS enforces the declared media contract strictly. A format mismatch is handled as an unsupported media type error, which protects endpoint integrity and keeps API behavior predictable.

Question 6:
You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?
Answer:
The sensor list endpoint uses @QueryParam("type") to filter sensors by type. In this project, that means GET /api/v1/sensors returns all sensors, while GET /api/v1/sensors?type=CO2 returns only sensors whose type matches CO2, ignoring case.
This is the better design for filtering because the path still identifies the collection, and the query string expresses an optional filter over that collection. It also scales better if more filters are added later, such as status or roomId, without needing new route patterns. A path-based design is more appropriate for identity, while query parameters are better for searching and filtering.
Query parameters are superior for filtering and searching because:
	They are naturally optional: the same endpoint supports both unfiltered and filtered retrieval.
	They scale better: additional filters can be added without redesigning path patterns.
	They improve maintainability: one collection endpoint handles many search combinations.
	They align with common web and API conventions for list filtering, sorting, and pagination.
	They simplify client implementation: clients can compose criteria dynamically without hardcoding many route variants.

PART 4: Deep Nesting with Sub-Resources.
Question 7:
Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?
Answer:
This project uses a sub-resource locator from SensorResource to SensorReadingResource at /sensors/{sensorId}/readings. That means the sensor resource handles the sensor collection, while the reading resource handles the nested reading operations for a specific sensor.
This keeps the code organized and prevents one controller from growing into a large, mixed-purpose class. The reading resource has the sensorId context it needs, and it can apply sensor-level checks in one place, including existence checks and the MAINTENANCE status rule. The result is cleaner routing, easier maintenance, and simpler extension when new reading-related endpoints are added.
This delegation improves architecture in several important ways:
	Better modularity: Each class handles one bounded responsibility, so code is easier to organize and reason about.
	Lower complexity per class: Smaller classes reduce cognitive load and avoid large monolithic controllers.
	Easier maintenance.
	Improved team scalability: Multiple developers can work on different resource areas with fewer merge conflicts.
	Cleaner dependency management: Sub-resource classes can inject only the services they actually need.
In contrast, putting every nested path into one massive controller usually leads to long files, mixed responsibilities, duplicated validation logic, and fragile refactoring. The Sub-Resource Locator pattern keeps large APIs maintainable by mapping route hierarchy to class hierarchy in a clean and scalable way.
PART 5: Advanced Error Handling, Exception Mapping & Logging.
Question 8:
Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?
Answer:
When the sensor creation payload contains a roomId that does not exist, the request body is syntactically valid JSON, but the business data is semantically invalid. This project handles that case with LinkedResourceNotFoundException, which is mapped to 422 Unprocessable Entity.
That is more accurate than 404 because the endpoint itself does exist. The problem is not that the route is missing; the problem is that the payload refers to a room that cannot be found. Using 422 makes the error clearer for client developers because it signals a payload validation problem rather than a missing endpoint.
Using 422 communicates the failure layer more precisely:
	Transport and syntax are valid.
	The addressed endpoint is valid.
	The semantic content violates application rules.
This distinction helps clients implement correct error handling. Clients can treat 422 as a data-correction problem (fix payload values and retry) rather than a routing problem (wrong URL). It also improves API clarity, debugging speed, and contract precision in complex integrations.
Therefore, for dependency validation failures inside an otherwise valid payload, 422 is generally the most semantically accurate status code.

Question 9:
From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?
Answer:
Exposing internal Java stack traces to external clients creates a serious information disclosure risk. A stack trace reveals implementation details that should remain private inside the server boundary. This information can be used by attackers to profile the system and design targeted attacks.
An attacker can gather several high-value details from stack traces:
	Technology fingerprinting: Java version, framework classes, servlet container behavior, and third-party libraries.
	Package and class structure: internal namespaces, module boundaries, and likely business components.
	Method names and call flow: exact execution paths, validation points, and weak error-prone areas.
	File names and line numbers: precise source locations for faults, making vulnerability reproduction easier.
	Deployment clues: filesystem paths, environment-specific directories, and possible operating system hints.
	Data-handling context: whether failures happen during parsing, authorization, database access, or object mapping.
This intelligence supports reconnaissance, which is often the first stage of exploitation. Attackers can combine leaked details with known CVEs, craft more accurate payloads, and reduce trial-and-error effort.
For this reason, production APIs should never return raw stack traces. Instead, they should return sanitized error responses with generic messages and a stable error code, while full traces are recorded only in secured server logs.

Question 10:
Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?
Answer:
Using JAX-RS filters for logging is advantageous because logging is a cross-cutting concern that applies to all endpoints. Filters provide centralized interception for incoming requests and outgoing responses, so the same policy is enforced consistently without duplicating code in each resource method.
Key advantages are:
•	Consistency: Every endpoint is logged uniformly for method, URI, and status code.
•	Reduced duplication: Logging logic is written once instead of being repeated in every handler.
•	Better maintainability: Log format, masking rules, and verbosity can be updated in one place.
•	Lower risk of omissions: New endpoints are automatically covered by filters, preventing missing logs.
•	Separation of concerns: Resource classes stay focused on business logic, improving readability and testability.
•	Easier compliance and observability: Centralized logging supports monitoring, auditing, and incident investigation.
The filter records the incoming method and URL, then records the response status for every request. This keeps the resource classes focused on business logic, avoids duplicated logging code, and ensures that new endpoints are covered automatically. It also makes the logging behavior consistent across the whole API.
       -->
<!-- ===================== -->