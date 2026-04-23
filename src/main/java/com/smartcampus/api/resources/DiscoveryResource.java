//Tashmi Fernando - 20241074 - w2121293
package com.smartcampus.api.resources;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Context;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @Context // Injected by JAX-RS to access request URI info for building responses
    private UriInfo uriInfo;

    @GET // Discovery endpoint to provide API metadata and available resources
    public Map<String, Object> getDiscovery() {
        String base = uriInfo.getBaseUri().toString();
        // base ends with "/api/v1/"; ensure consistent link building
        if (!base.endsWith("/")) {
            base = base + "/";
        }
        // Constructing a structured response with API metadata and resource links
        Map<String, String> contact = new LinkedHashMap<>();
        contact.put("role", "Lead Backend Architect");
        contact.put("email", "admin@smartcampus.example");
        // Using LinkedHashMap to preserve insertion order in the JSON response for better readability
        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms", base + "rooms");
        resources.put("sensors", base + "sensors");
        // Additional resources can be added here in the future as needed.
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name", "Smart Campus Sensor & Room Management API");
        response.put("version", "v1");
        response.put("timestamp", OffsetDateTime.now().toString());
        response.put("contact", contact);
        response.put("resources", resources);
        return response;
    }
}
