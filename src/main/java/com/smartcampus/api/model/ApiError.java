//Tashmi Fernando - 20241074 - w2121293
package com.smartcampus.api.model;

import java.time.OffsetDateTime;

public class ApiError {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiError() { // Default constructor for JSON deserialization
    }

    public ApiError(int status, String error, String message, String path) {
        this.timestamp = OffsetDateTime.now().toString();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public String getTimestamp() { // Getter for the timestamp when the error occurred, formatted as an ISO-8601 string.
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() { // Getter for the HTTP status code associated with the error.
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() { // Getter for a short error description, typically the HTTP status reason phrase.
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() { // Getter for a detailed error message providing more context about the error.
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() { // Getter for the request path that resulted in the error, useful for debugging and client feedback.
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
