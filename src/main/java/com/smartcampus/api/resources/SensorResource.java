//Tashmi Fernando - 20241074 - w2121293
package com.smartcampus.api.resources;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.smartcampus.api.model.ApiError; // For consistent error response payloads
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.api.store.InMemoryStore;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("sensors") // Base path for all sensor-related endpoints
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource { // Using singleton store instance for simplicity

    private final InMemoryStore store = InMemoryStore.getInstance();

    @Context
    private UriInfo uriInfo; // Injected by JAX-RS to access request URI info for building responses

    @GET // List sensors with optional filtering by type (case-insensitive)
    public List<Sensor> listSensors(@QueryParam("type") String type) {
        List<Sensor> all = store.listSensors();
        if (type == null || type.trim().isEmpty()) {
            return all;
        }
        // Normalize the type parameter for case-insensitive comparison
        String normalized = type.trim().toLowerCase(Locale.ROOT);
        return all.stream()
                .filter(s -> s.getType() != null && s.getType().trim().toLowerCase(Locale.ROOT).equals(normalized))
                .collect(Collectors.toList());
    }

    // Sub-Resource Locator: /api/v1/sensors/{sensorId}/readings
    @Path("{sensorId}/readings")
    public SensorReadingResource readings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId, store);
    }

    @POST // Create a new sensor with validation and error handling.
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        if (sensor == null || isBlank(sensor.getId()) || isBlank(sensor.getType()) || isBlank(sensor.getRoomId())) {
            return badRequest("Invalid sensor payload. Required: id, type, roomId.");
        }

        if (isBlank(sensor.getStatus())) {
            sensor.setStatus("ACTIVE");
        }

        if (!store.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                    "room",
                    sensor.getRoomId(),
                    "roomId '%s' does not exist.".formatted(sensor.getRoomId()));
        }

        if (store.sensorExists(sensor.getId())) {
            return conflict("Sensor with id '%s' already exists.".formatted(sensor.getId()));
        }
        // Create the sensor and return the created resource with Location header
        Sensor created = store.createSensor(sensor);
        if (created == null) {
            // defensive fallback; should be covered by checks above
            return conflict("Unable to create sensor.");
        }
        // Build Location header for the newly created sensor resource.
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location).entity(created).build();
    }
    // Helper methods for consistent error responses with ApiError payloads.
    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ApiError(
                        Response.Status.BAD_REQUEST.getStatusCode(),
                        Response.Status.BAD_REQUEST.getReasonPhrase(),
                        message,
                        requestPath()))
                .build();
    }
    // Helper method for not found responses when a requested resource does not exist.
    private Response conflict(String message) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ApiError(
                        Response.Status.CONFLICT.getStatusCode(),
                        Response.Status.CONFLICT.getReasonPhrase(),
                        message,
                        requestPath()))
                .build();
    }
    // Helper method for not found responses when a requested resource does not exist.
    private String requestPath() {
        return uriInfo == null ? null : uriInfo.getRequestUri().getPath();
    }
    // Utility method to check if a string is null or blank (empty or whitespace only).
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
