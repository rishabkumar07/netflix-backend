package com.netflix.backend.exception;

/**
 * Thrown when a requested resource doesn't exist in the DB.
 * Maps to HTTP 404 in GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
