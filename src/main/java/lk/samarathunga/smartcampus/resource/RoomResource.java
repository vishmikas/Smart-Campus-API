package lk.samarathunga.smartcampus.resource;

import lk.samarathunga.smartcampus.exception.ResourceNotFoundException;
import lk.samarathunga.smartcampus.exception.RoomHasSensorsException;
import lk.samarathunga.smartcampus.model.Room;
import lk.samarathunga.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Room Resource - manages campus rooms.
 * Base path: /api/v1/rooms
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    /**
     * GET /api/v1/rooms
     * Returns all rooms in the system.
     */
    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(DataStore.rooms.values());
    }

    /**
     * POST /api/v1/rooms
     * Creates a new room. Returns 201 Created with Location header.
     */
    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room id is required.")
                    .build();
        }

        if (room.getName() == null || room.getName().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room name is required.")
                    .build();
        }

        if (room.getCapacity() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room capacity must be greater than zero.")
                    .build();
        }

        if (DataStore.rooms.containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A room with id '" + room.getId() + "' already exists.")
                    .build();
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        DataStore.rooms.put(room.getId(), room);

        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(location).entity(room).build();
    }

    /**
     * GET /api/v1/rooms/{roomId}
     * Returns a single room by ID.
     */
    @GET
    @Path("/{roomId}")
    public Room getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new ResourceNotFoundException("Room not found with id: " + roomId);
        }
        return room;
    }

    /**
     * DELETE /api/v1/rooms/{roomId}
     * Deletes a room. Blocked if the room still has sensors assigned.
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new ResourceNotFoundException("Room not found with id: " + roomId);
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomHasSensorsException(
                "Cannot delete room '" + roomId + "': " + room.getSensorIds().size()
                + " sensor(s) are still assigned to it."
            );
        }

        DataStore.rooms.remove(roomId);
        return Response.ok("Room '" + roomId + "' deleted successfully.").build();
    }
}
