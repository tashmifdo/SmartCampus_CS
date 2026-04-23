//Tashmi Fernando - 20241074 - w2121293
package com.smartcampus.api.resources;

import java.net.URI;
import java.util.List;

import com.smartcampus.api.model.ApiError;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.exceptions.RoomNotEmptyException;
import com.smartcampus.api.store.InMemoryStore;

import jakarta.ws.rs.Consumes; // For @POST payload
import jakarta.ws.rs.DELETE; // For @DELETE method
import jakarta.ws.rs.GET; 
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam; // For @PathParam in getRoom and deleteRoom
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context; // For UriInfo injection
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo; // For building Location header in createRoom and getting request path for error responses

@Path("rooms") // Base path for all room-related endpoints
@Produces(MediaType.APPLICATION_JSON)
public class SensorRoomResource {
    // Using singleton store instance for simplicity
    private final InMemoryStore store = InMemoryStore.getInstance();

    @Context
    private UriInfo uriInfo; // Injected by JAX-RS to access request URI info for building responses

    @GET
    public List<Room> listRooms() { // List all rooms without pagination for simplicity.
        return store.listRooms();
    }

    @POST // Create a new room with validation and error handling.
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        if (room == null || isBlank(room.getId()) || isBlank(room.getName()) || room.getCapacity() <= 0) {
            return badRequest("Invalid room payload. Required: id, name, capacity>0.");
        }
        // Check for duplicate room ID before creation
        Room created = store.createRoom(room);
        if (created == null) {
            return conflict("Room with id '%s' already exists.".formatted(room.getId()));
        }
        // Build Location header for the newly created room resource
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location).entity(created).build();
    }

    @GET  // Get details of a specific room by ID with error handling for not found case.
    @Path("{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) {
            return notFound("Room '%s' not found.".formatted(roomId));
        } // Return the found room details.
        return Response.ok(room).build();
    }

    @DELETE // Delete a room by ID with checks for existence and active sensors before deletion.
    @Path("{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) { // Check if the room exists before attempting deletion.
        Room existing = store.getRoom(roomId);
        if (existing == null) {
            return notFound("Room '%s' not found.".formatted(roomId));
        }

        if (store.roomHasActiveSensors(roomId)) { // Prevent deletion if there are active sensors in the room.
            throw new RoomNotEmptyException(roomId);
        }

        store.deleteRoom(roomId);
        return Response.noContent().build();
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
    // Helper method for conflict responses when trying to create a resource that already exists.
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
    private Response notFound(String message) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ApiError(
                        Response.Status.NOT_FOUND.getStatusCode(),
                        Response.Status.NOT_FOUND.getReasonPhrase(),
                        message,
                        requestPath()))
                .build();
    }
    // Helper method to extract the request path for error response payloads.
    private String requestPath() {
        return uriInfo == null ? null : uriInfo.getRequestUri().getPath();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
