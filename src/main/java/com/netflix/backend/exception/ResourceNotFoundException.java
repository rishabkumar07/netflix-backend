package com.netflix.backend.exception;

/**
 * Thrown when a requested resource doesn't exist in the DB.
 * Maps to HTTP 404 in GlobalExceptionHandler.
 *
 * .NET equivalent: a custom exception used before returning NotFound() in a controller,
 * except here the mapping to HTTP status lives in @ControllerAdvice instead of the controller.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
