package lk.samarathunga.smartcampus.store;

import lk.samarathunga.smartcampus.model.Room;
import lk.samarathunga.smartcampus.model.Sensor;
import lk.samarathunga.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory data store for the Smart Campus API.
 * Uses ConcurrentHashMap to prevent race conditions on concurrent requests.
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
public class DataStore {

    public static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    static {
        // Seed rooms
        Room r1 = new Room("HALL-101", "Main Lecture Hall", 120);
        Room r2 = new Room("LAB-202", "Network & Security Lab", 40);
        Room r3 = new Room("LIB-001", "Central Library", 200);

        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
        rooms.put(r3.getId(), r3);

        // Seed sensors
        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 23.4, "HALL-101");
        Sensor s2 = new Sensor("HUM-001",  "Humidity",    "ACTIVE", 55.0, "HALL-101");
        Sensor s3 = new Sensor("CO2-001",  "CO2",         "ACTIVE", 420.0, "LAB-202");
        Sensor s4 = new Sensor("OCC-001",  "Occupancy",   "MAINTENANCE", 0.0, "LIB-001");

        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);
        sensors.put(s4.getId(), s4);

        // Link sensors to rooms
        r1.getSensorIds().add(s1.getId());
        r1.getSensorIds().add(s2.getId());
        r2.getSensorIds().add(s3.getId());
        r3.getSensorIds().add(s4.getId());

        // Initialise reading lists
        readings.put(s1.getId(), new ArrayList<>());
        readings.put(s2.getId(), new ArrayList<>());
        readings.put(s3.getId(), new ArrayList<>());
        readings.put(s4.getId(), new ArrayList<>());
    }

    private DataStore() {}
}
