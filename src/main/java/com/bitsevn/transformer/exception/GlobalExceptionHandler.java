package com.bitsevn.transformer.exception;

import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.bitsevn.transformer.service.TransformationConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Global Exception Handler for all REST controllers
 * Provides centralized exception handling across the application
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ConfigurationNotFoundException
     */
    @ExceptionHandler(TransformationConfigService.ConfigurationNotFoundException.class)
    public ResponseEntity<?> handleConfigurationNotFoundException(
            TransformationConfigService.ConfigurationNotFoundException ex, 
            WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of(
                "success", false,
                "error", ex.getMessage(),
                "timestamp", System.currentTimeMillis(),
                "path", request.getDescription(false).replace("uri=", ""),
                "status", HttpStatus.NOT_FOUND.value(),
                "type", "ConfigurationNotFoundException"
            ));
    }



    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "success", false,
                "error", "Invalid argument: " + ex.getMessage(),
                "timestamp", System.currentTimeMillis(),
                "path", request.getDescription(false).replace("uri=", ""),
                "status", HttpStatus.BAD_REQUEST.value(),
                "type", "IllegalArgumentException"
            ));
    }

    /**
     * Handle MongoDB-specific exceptions
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccessException(DataAccessException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "success", false,
                "error", "Database operation failed: " + ex.getMessage(),
                "timestamp", System.currentTimeMillis(),
                "path", request.getDescription(false).replace("uri=", ""),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "type", "DataAccessException"
            ));
    }

    /**
     * Handle JSON parsing exceptions
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<?> handleJsonProcessingException(JsonProcessingException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "success", false,
                "error", "Invalid JSON format: " + ex.getMessage(),
                "timestamp", System.currentTimeMillis(),
                "path", request.getDescription(false).replace("uri=", ""),
                "status", HttpStatus.BAD_REQUEST.value(),
                "type", "JsonProcessingException"
            ));
    }

    /**
     * Handle generic RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "success", false,
                "error", "Runtime error: " + ex.getMessage(),
                "timestamp", System.currentTimeMillis(),
                "path", request.getDescription(false).replace("uri=", ""),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "type", "RuntimeException"
            ));
    }

    /**
     * Handle generic Exception (catch-all)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "success", false,
                "error", "An unexpected error occurred: " + ex.getMessage(),
                "timestamp", System.currentTimeMillis(),
                "path", request.getDescription(false).replace("uri=", ""),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "type", ex.getClass().getSimpleName()
            ));
    }
}
