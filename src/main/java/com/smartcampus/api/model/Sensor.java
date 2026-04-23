//Tashmi Fernando - 20241074 - w2121293
package com.smartcampus.api.model;

public class Sensor {
    private String id;
    private String type;
    private String status;
    private double currentValue;
    private String roomId;

    public Sensor() { // Default constructor for JSON deserialization
    }

    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }

    public String getId() { // Getter for the unique identifier of the sensor.
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() { // Getter for the type of the sensor.
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() { // Getter for the operational status of the sensor.
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCurrentValue() { // Getter for the current value measured by the sensor.
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public String getRoomId() { // Getter for the ID of the room to which the sensor is associated.
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
