# Global Exception Handling in REST API Layer

## Overview

This document describes the implementation of Spring's Global Exception Advice pattern for centralized exception handling across all REST controllers in the transformer application.

## Architecture

### 1. GlobalExceptionHandler Class

**Location**: `src/main/java/com/bitsevn/transformer/exception/GlobalExceptionHandler.java`

**Purpose**: Centralized exception handling for all REST controllers using Spring's `@ControllerAdvice` annotation.

**Key Features**:
- **`@ControllerAdvice`**: Automatically applies to all controllers in the application
- **Centralized Logic**: All exception handling logic in one place
- **Consistent Responses**: Uniform error response format across all endpoints
- **Easy Maintenance**: Single point of modification for error handling

### 2. Exception Types Handled

#### **Custom Business Exceptions**
```java
// ConfigurationNotFoundException (404)
@ExceptionHandler(TransformationConfigController.ConfigurationNotFoundException.class)

// ConfigurationValidationException (400)
@ExceptionHandler(TransformationConfigController.ConfigurationValidationException.class)
```

#### **Framework Exceptions**
```java
// IllegalArgumentException (400)
@ExceptionHandler(IllegalArgumentException.class)

// DataAccessException (500) - MongoDB/Database errors
@ExceptionHandler(DataAccessException.class)

// JsonProcessingException (400) - JSON parsing errors
@ExceptionHandler(JsonProcessingException.class)
```

#### **Generic Exceptions**
```java
// RuntimeException (500)
@ExceptionHandler(RuntimeException.class)

// Exception (500) - Catch-all for unexpected errors
@ExceptionHandler(Exception.class)
```

## Implementation Details

### 1. Controller Exception Handling

#### **Before (Individual Try-Catch)**
```java
@PostMapping
public ResponseEntity<?> createConfiguration(@RequestBody TransformationConfig config) {
    try {
        // Business logic
        TransformationConfig savedConfig = repository.save(config);
        return ResponseEntity.ok(savedConfig);
    } catch (Exception e) {
        return ResponseEntity.internalServerError()
            .body(Map.of("error", "Failed to create configuration: " + e.getMessage()));
    }
}
```

#### **After (Global Exception Handling)**
```java
@PostMapping
public ResponseEntity<?> createConfiguration(@RequestBody TransformationConfig config) {
    // Business logic only - no exception handling
    TransformationConfig savedConfig = repository.save(config);
    return ResponseEntity.ok(savedConfig);
}
```

### 2. Exception Throwing Strategy

#### **For Business Logic Errors**
```java
// Throw custom exceptions for business logic failures
if (configOpt.isEmpty()) {
    throw new ConfigurationNotFoundException("Configuration with name '" + name + "' not found");
}
```

#### **For Validation Errors**
```java
// Throw IllegalArgumentException for validation failures
if (xmlInput == null || xmlInput.trim().isEmpty()) {
    throw new IllegalArgumentException("XML input is required");
}
```

#### **For Checked Exceptions**
```java
try {
    // Code that throws checked exceptions
    String result = transformer.transform(xmlInput);
    return ResponseEntity.ok(result);
} catch (Exception e) {
    // Wrap in RuntimeException for global handling
    throw new RuntimeException("Operation failed: " + e.getMessage(), e);
}
```

## Error Response Format

### Standard Error Response Structure
```json
{
  "success": false,
  "error": "Descriptive error message",
  "timestamp": 1703123456789,
  "path": "/api/transformation-configs/name/test-config",
  "status": 404,
  "type": "ConfigurationNotFoundException"
}
```

### HTTP Status Code Mapping

| Exception Type | HTTP Status | Description |
|----------------|-------------|-------------|
| `ConfigurationNotFoundException` | 404 | Resource not found |
| `ConfigurationValidationException` | 400 | Validation error |
| `IllegalArgumentException` | 400 | Invalid argument |
| `DataAccessException` | 500 | Database error |
| `JsonProcessingException` | 400 | JSON format error |
| `RuntimeException` | 500 | Runtime error |
| `Exception` | 500 | Unexpected error |

## Benefits of Global Exception Handling

### 1. **Code Quality**
- **Cleaner Controllers**: Business logic is separated from error handling
- **Single Responsibility**: Controllers focus on their core functionality
- **Readability**: Code is easier to read and understand

### 2. **Maintainability**
- **Centralized Logic**: All error handling in one place
- **Easy Updates**: Modify error handling without touching controllers
- **Consistent Changes**: Apply changes across all endpoints simultaneously

### 3. **Consistency**
- **Uniform Responses**: All errors follow the same format
- **Standardized Status Codes**: Proper HTTP status codes for each error type
- **Consistent Error Messages**: Standardized error message structure

### 4. **Debugging & Monitoring**
- **Rich Error Information**: Timestamp, path, and exception type included
- **Request Tracing**: Easy to track which endpoint caused the error
- **Centralized Logging**: All errors can be logged from one location

## Usage Examples

### 1. **Throwing Custom Exceptions**
```java
// In TransformationConfigController
@GetMapping("/name/{name}")
public ResponseEntity<?> getConfigurationByName(@PathVariable String name) {
    Optional<TransformationConfig> configOpt = repository.findByName(name);
    
    if (configOpt.isPresent()) {
        return ResponseEntity.ok(configOpt.get());
    } else {
        // This will be caught by GlobalExceptionHandler
        throw new ConfigurationNotFoundException("Configuration with name '" + name + "' not found");
    }
}
```

### 2. **Throwing Validation Exceptions**
```java
// In TransformerController
@PostMapping("/transform/{configName}")
public ResponseEntity<?> transformXmlToJson(@PathVariable String configName, @RequestBody String xmlInput) {
    if (xmlInput == null || xmlInput.trim().isEmpty()) {
        // This will be caught by GlobalExceptionHandler
        throw new IllegalArgumentException("XML input is required");
    }
    
    // Business logic continues...
}
```

### 3. **Wrapping Checked Exceptions**
```java
try {
    // Code that throws checked exceptions
    String result = service.process(input);
    return ResponseEntity.ok(result);
} catch (IOException e) {
    // Wrap in RuntimeException for global handling
    throw new RuntimeException("Operation failed: " + e.getMessage(), e);
}
```

## Configuration

### 1. **Spring Boot Auto-Configuration**
The `@ControllerAdvice` annotation automatically registers the exception handler with Spring Boot.

### 2. **Exception Handler Priority**
Spring processes exception handlers in the following order:
1. Most specific exception types first
2. Generic exception types last
3. `Exception.class` as the final catch-all

### 3. **Response Entity Creation**
All exception handlers return `ResponseEntity<?>` objects with:
- Appropriate HTTP status codes
- Consistent error response format
- Request context information

## Best Practices

### 1. **Exception Naming**
- Use descriptive exception names
- Follow the pattern: `[Entity][Action]Exception`
- Examples: `ConfigurationNotFoundException`, `UserValidationException`

### 2. **Error Messages**
- Provide clear, actionable error messages
- Include relevant context (e.g., field names, values)
- Avoid exposing sensitive information

### 3. **HTTP Status Codes**
- Use appropriate HTTP status codes for each error type
- Follow REST API conventions
- Consider client needs when choosing status codes

### 4. **Exception Hierarchy**
- Create custom exceptions that extend `RuntimeException`
- Use inheritance for related exception types
- Keep exception hierarchy simple and logical

## Migration Guide

### From Individual Try-Catch to Global Handling

#### **Step 1: Remove Try-Catch Blocks**
```java
// Remove this
try {
    // business logic
} catch (Exception e) {
    return ResponseEntity.internalServerError()
        .body(Map.of("error", e.getMessage()));
}

// Keep only business logic
// business logic
```

#### **Step 2: Throw Appropriate Exceptions**
```java
// Instead of returning error responses, throw exceptions
if (validationFails) {
    throw new ValidationException("Validation failed");
}
```

#### **Step 3: Wrap Checked Exceptions**
```java
try {
    // code that throws checked exceptions
} catch (IOException e) {
    throw new RuntimeException("Operation failed", e);
}
```

## Testing

### 1. **Unit Testing Controllers**
- Test business logic without exception handling concerns
- Verify that appropriate exceptions are thrown
- Mock dependencies to test error scenarios

### 2. **Integration Testing**
- Test end-to-end error handling
- Verify HTTP status codes and response formats
- Test different exception types

### 3. **Exception Handler Testing**
- Test individual exception handlers
- Verify response format consistency
- Test edge cases and error conditions

## Monitoring and Logging

### 1. **Error Tracking**
- All errors are centralized in one location
- Easy to implement logging and monitoring
- Consistent error reporting across the application

### 2. **Performance Monitoring**
- Track error rates by exception type
- Monitor response times for error scenarios
- Identify patterns in error occurrences

### 3. **Alerting**
- Set up alerts for critical error types
- Monitor error frequency and severity
- Track application health and stability

## Future Enhancements

### 1. **Additional Exception Types**
- Add more specific business exceptions
- Implement domain-specific error handling
- Support for internationalized error messages

### 2. **Enhanced Error Responses**
- Include error codes for client handling
- Add suggestions for error resolution
- Support for error categorization

### 3. **Advanced Logging**
- Structured logging for better analysis
- Correlation IDs for request tracing
- Integration with monitoring systems

## Conclusion

The Global Exception Handling approach using Spring's `@ControllerAdvice` provides a robust, maintainable, and consistent way to handle errors across the REST API layer. It separates concerns, improves code quality, and provides a better developer and user experience.

By centralizing exception handling, we achieve:
- **Better Code Organization**: Controllers focus on business logic
- **Consistent Error Responses**: Uniform format across all endpoints
- **Easier Maintenance**: Single point of modification for error handling
- **Improved Debugging**: Rich error information for troubleshooting
- **Professional API**: Standard HTTP status codes and error formats

This approach follows Spring Boot best practices and provides a solid foundation for building robust, production-ready REST APIs.
