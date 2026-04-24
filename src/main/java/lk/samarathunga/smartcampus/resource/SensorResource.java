package lk.samarathunga.smartcampus.resource;

import lk.samarathunga.smartcampus.exception.InvalidReferenceException;
import lk.samarathunga.smartcampus.exception.ResourceNotFoundException;
import lk.samarathunga.smartcampus.model.Room;
import lk.samarathunga.smartcampus.model.Sensor;
import lk.samarathunga.smartcampus.model.SensorReading;
import lk.samarathunga.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sensor Resource - manages campus sensors.
 * Base path: /api/v1/sensors
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    /**
     * GET /api/v1/sensors
     * GET /api/v1/sensors?type=CO2
     * Returns all sensors, optionally filtered by type.
     */
    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> all = new ArrayList<>(DataStore.sensors.values());

        if (type != null && !type.trim().isEmpty()) {
            return all.stream()
                    .filter(s -> s.getType() != null && s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        return all;
    }

    /**
     * POST /api/v1/sensors
     * Registers a new sensor. Validates that the given roomId exists.
     */
    @POST
    public Response registerSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sensor body is required.")
                    .build();
        }

        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sensor id is required.")
                    .build();
        }

        if (DataStore.sensors.containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A sensor with id '" + sensor.getId() + "' already exists.")
                    .build();
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("roomId is required.")
                    .build();
        }

        Room room = DataStore.rooms.get(sensor.getRoomId());
        if (room == null) {
            throw new InvalidReferenceException(
                "The roomId '" + sensor.getRoomId() + "' does not refer to any existing room."
            );
        }

        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        DataStore.sensors.put(sensor.getId(), sensor);
        room.getSensorIds().add(sensor.getId());
        DataStore.readings.putIfAbsent(sensor.getId(), new ArrayList<SensorReading>());

        URI location = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(location).entity(sensor).build();
    }

    /**
     * Sub-resource locator for readings.
     * Delegates /api/v1/sensors/{sensorId}/readings to SensorReadingResource.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        if (!DataStore.sensors.containsKey(sensorId)) {
            throw new ResourceNotFoundException("Sensor not found with id: " + sensorId);
        }
        return new SensorReadingResource(sensorId);
    }
}
