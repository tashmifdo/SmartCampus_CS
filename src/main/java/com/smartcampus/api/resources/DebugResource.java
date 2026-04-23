//Tashmi Fernando - 20241074 - w2121293
package com.smartcampus.api.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("debug") // Base path for debug-related endpoints; in production, this should be protected or removed.
@Produces(MediaType.APPLICATION_JSON)
public class DebugResource {

    @GET
    @Path("boom")
    public String boom() {
        throw new IllegalStateException("Simulated server error (debug endpoint)");
    }
}
