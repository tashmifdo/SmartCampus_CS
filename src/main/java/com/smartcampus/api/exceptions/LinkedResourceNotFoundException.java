//Tashmi Fernando - 20241074 - w2121293
package com.smartcampus.api.exceptions;

public class LinkedResourceNotFoundException extends RuntimeException {
    private final String resourceType;
    private final String resourceId;

    public LinkedResourceNotFoundException(String resourceType, String resourceId, String message) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    } // Constructor to initialize the exception with the type and ID of the linked resource that was not found, along with a custom error message.

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}
