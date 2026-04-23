//Tashmi Fernando - 20241074 - w2121293
package com.smartcampus.api.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String id;
    private String name;
    private int capacity;
    private String regulations;
    private List<String> sensorIds = new ArrayList<>(); // List of sensor IDs associated with this room, initialized to an empty list to avoid null issues

    public Room() { // Default constructor for JSON deserialization
    }

    public Room(String id, String name, int capacity, String regulations) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.regulations = regulations;
    }

    public String getId() { // Getter for the unique identifier of the room.
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() { // Getter for the human-readable name of the room.
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() { // Getter for the maximum occupancy of the room.
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getRegulations() { // Getter for the regulations governing the room.
        return regulations;
    }

    public void setRegulations(String regulations) {
        this.regulations = regulations;
    }

    public List<String> getSensorIds() { // Getter for the list of sensor IDs associated with the room.
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds == null ? new ArrayList<>() : sensorIds;
    }
}
