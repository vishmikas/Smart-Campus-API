package lk.samarathunga.smartcampus.resource;

import lk.samarathunga.smartcampus.exception.ResourceNotFoundException;
import lk.samarathunga.smartcampus.exception.SensorUnavailableException;
import lk.samarathunga.smartcampus.model.Sensor;
import lk.samarathunga.smartcampus.model.SensorReading;
import lk.samarathunga.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sub-resource for sensor readings.
 * Resolved via locator in SensorResource.
 * Paths: GET  /api/v1/sensors/{sensorId}/readings
 *        POST /api/v1/sensors/{sensorId}/readings
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * GET /api/v1/sensors/{sensorId}/readings
     * Returns the full historical log for the sensor.
     */
    @GET
    public List<SensorReading> getReadings() {
        return DataStore.readings.getOrDefault(sensorId, new ArrayList<>());
    }

    /**
     * POST /api/v1/sensors/{sensorId}/readings
     * Appends a new reading. Also updates currentValue on the parent Sensor.
     * Blocked if sensor status is MAINTENANCE.
     */
    @POST
    public Response addReading(SensorReading reading) {
        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Reading body is required.")
                    .build();
        }

        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor not found: " + sensorId);
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is under maintenance and cannot accept new readings."
            );
        }

        // Auto-generate ID and timestamp if not provided
        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Store the reading
        List<SensorReading> list = DataStore.readings.computeIfAbsent(sensorId, k -> new ArrayList<>());
        list.add(reading);

        // Side-effect: sync currentValue on the parent sensor
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
