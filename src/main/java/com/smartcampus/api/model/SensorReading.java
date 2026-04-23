//Tashmi Fernando - 20241074 - w2121293

package com.smartcampus.api.model;

public class SensorReading {
    private String id;
    private long timestamp;
    private double value;

    public SensorReading() {
    }

    public SensorReading(String id, long timestamp, double value) { // Constructor to initialize a sensor reading with an ID, timestamp, and value.
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getId() { // Getter for the unique identifier of the sensor reading.
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() { // Getter for the timestamp of the sensor reading, represented as a long.
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() { // Getter for the value of the sensor reading, represented as a double.
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
