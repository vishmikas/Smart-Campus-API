package lk.samarathunga.smartcampus.model;

/**
 * Represents a single timestamped measurement recorded by a sensor.
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
public class SensorReading {

    private String id;
    private long timestamp;
    private double value;

    public SensorReading() {}

    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
