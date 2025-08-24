# TransformationConfig Management API

## Overview

This document describes the comprehensive REST API for managing `TransformationConfig` entities in MongoDB. The API provides full CRUD operations, search capabilities, and statistics.

**Base URL**: `/api/transformation-configs`

## Authentication

Currently, no authentication is required. All endpoints are publicly accessible.

## Response Format

All API responses follow a consistent format:

```json
{
  "success": true|false,
  "message": "Description of the operation result",
  "data": {...} // Optional data payload
}
```

## API Endpoints

### 1. Create Configuration

**POST** `/api/transformation-configs`

Creates a new TransformationConfig in MongoDB.

**Request Body**:
```json
{
  "name": "company-employees-config",
  "version": "2.0",
  "description": "Configuration for transforming company employee data",
  "unifiedMappings": [
    {
      "xmlPath": "company/name",
      "jsonPath": "companyName",
      "type": "single",
      "dataType": "string"
    }
  ],
  "transformations": {
    "uppercase": "uppercase",
    "trim": "trim"
  },
  "defaultValues": {
    "businessType": "Technology"
  }
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Configuration created successfully",
  "config": {
    "id": "507f1f77bcf86cd799439011",
    "name": "company-employees-config",
    "version": "2.0",
    "description": "Configuration for transforming company employee data"
  }
}
```

**Error Response** (400 Bad Request):
```json
{
  "success": false,
  "error": "Configuration with name 'company-employees-config' already exists",
  "suggestion": "Use PUT /api/transformation-configs/{name} to update existing configuration"
}
```

### 2. Get All Configurations

**GET** `/api/transformation-configs`

Retrieves all TransformationConfig entities from MongoDB.

**Response** (200 OK):
```json
{
  "success": true,
  "totalCount": 3,
  "configs": [
    {
      "id": "507f1f77bcf86cd799439011",
      "name": "company-employees-config",
      "version": "2.0",
      "description": "Configuration for transforming company employee data",
      "mappingsCount": 5
    },
    {
      "id": "507f1f77bcf86cd799439012",
      "name": "library-books-config",
      "version": "1.0",
      "description": "Configuration for library book transformations",
      "mappingsCount": 8
    }
  ]
}
```

### 3. Get Configuration by Name

**GET** `/api/transformation-configs/name/{name}`

Retrieves a specific TransformationConfig by its name.

**Path Parameters**:
- `name`: The name of the configuration

**Response** (200 OK):
```json
{
  "success": true,
  "config": {
    "id": "507f1f77bcf86cd799439011",
    "name": "company-employees-config",
    "version": "2.0",
    "description": "Configuration for transforming company employee data",
    "unifiedMappings": [...],
    "transformations": {...},
    "defaultValues": {...}
  }
}
```

**Response** (404 Not Found):
```json
{
  "success": false,
  "error": "Configuration not found"
}
```

### 4. Get Configuration by ID

**GET** `/api/transformation-configs/id/{id}`

Retrieves a specific TransformationConfig by its MongoDB ID.

**Path Parameters**:
- `id`: The MongoDB ObjectId of the configuration

**Response**: Same format as get by name.

### 5. Get Configurations by Version

**GET** `/api/transformation-configs/version/{version}`

Retrieves all TransformationConfig entities with a specific version.

**Path Parameters**:
- `version`: The version string to search for

**Response** (200 OK):
```json
{
  "success": true,
  "version": "2.0",
  "totalCount": 2,
  "configs": [
    {
      "id": "507f1f77bcf86cd799439011",
      "name": "company-employees-config",
      "version": "2.0",
      "description": "Configuration for transforming company employee data",
      "mappingsCount": 5
    }
  ]
}
```

### 6. Search Configurations by Name

**GET** `/api/transformation-configs/search/name?q={query}`

Searches for configurations whose names contain the specified text (case-insensitive).

**Query Parameters**:
- `q`: The search query string

**Response**: Same format as get by version, with additional `searchQuery` field.

### 7. Search Configurations by Description

**GET** `/api/transformation-configs/search/description?q={query}`

Searches for configurations whose descriptions contain the specified text (case-insensitive).

**Query Parameters**:
- `q`: The search query string

**Response**: Same format as search by name.

### 8. Get Configurations by Mapping Type

**GET** `/api/transformation-configs/type/{mappingType}`

Retrieves configurations that contain mappings of a specific type (single, array, or object).

**Path Parameters**:
- `mappingType`: The mapping type to filter by

**Response**: Same format as get by version, with additional `mappingType` field.

### 9. Update Configuration by Name

**PUT** `/api/transformation-configs/{name}`

Updates an existing TransformationConfig by name.

**Path Parameters**:
- `name`: The name of the configuration to update

**Request Body**: Same format as create, but the name field will be overridden with the path parameter.

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Configuration updated successfully",
  "config": {
    "id": "507f1f77bcf86cd799439011",
    "name": "company-employees-config",
    "version": "2.1",
    "description": "Updated configuration description"
  }
}
```

### 10. Update Configuration by ID

**PUT** `/api/transformation-configs/id/{id}`

Updates an existing TransformationConfig by MongoDB ID.

**Path Parameters**:
- `id`: The MongoDB ObjectId of the configuration to update

**Request Body**: Same format as create.

**Response**: Same format as update by name.

### 11. Delete Configuration by Name

**DELETE** `/api/transformation-configs/{name}`

Deletes a TransformationConfig by name.

**Path Parameters**:
- `name`: The name of the configuration to delete

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Configuration 'company-employees-config' deleted successfully"
}
```

### 12. Delete Configuration by ID

**DELETE** `/api/transformation-configs/id/{id}`

Deletes a TransformationConfig by MongoDB ID.

**Path Parameters**:
- `id`: The MongoDB ObjectId of the configuration to delete

**Response**: Same format as delete by name.

### 13. Get Configuration Statistics

**GET** `/api/transformation-configs/stats`

Retrieves statistical information about all configurations.

**Response** (200 OK):
```json
{
  "success": true,
  "statistics": {
    "totalConfigurations": 5,
    "arrayMappings": 3,
    "objectMappings": 4,
    "singleMappings": 5
  }
}
```

### 14. Check Configuration Existence

**GET** `/api/transformation-configs/{name}/exists`

Checks if a configuration with the specified name exists.

**Path Parameters**:
- `name`: The name of the configuration to check

**Response** (200 OK):
```json
{
  "success": true,
  "name": "company-employees-config",
  "exists": true,
  "message": "Configuration found"
}
```

## Error Handling

### HTTP Status Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Error Response Format

```json
{
  "success": false,
  "error": "Description of the error"
}
```

## Usage Examples

### cURL Examples

#### Create Configuration
```bash
curl -X POST http://localhost:8080/api/transformation-configs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test-config",
    "version": "1.0",
    "description": "Test configuration",
    "unifiedMappings": []
  }'
```

#### Get All Configurations
```bash
curl -X GET http://localhost:8080/api/transformation-configs
```

#### Get Configuration by Name
```bash
curl -X GET http://localhost:8080/api/transformation-configs/name/test-config
```

#### Update Configuration
```bash
curl -X PUT http://localhost:8080/api/transformation-configs/test-config \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test-config",
    "version": "1.1",
    "description": "Updated test configuration",
    "unifiedMappings": []
  }'
```

#### Delete Configuration
```bash
curl -X DELETE http://localhost:8080/api/transformation-configs/test-config
```

#### Search Configurations
```bash
curl -X GET "http://localhost:8080/api/transformation-configs/search/name?q=company"
```

#### Get Statistics
```bash
curl -X GET http://localhost:8080/api/transformation-configs/stats
```

## Data Model

### TransformationConfig

```json
{
  "id": "MongoDB ObjectId",
  "name": "string (unique)",
  "version": "string",
  "description": "string",
  "unifiedMappings": [
    {
      "xmlPath": "string",
      "jsonPath": "string",
      "type": "single|array|object",
      "isArray": "boolean",
      "children": [...],
      "dataType": "string",
      "required": "boolean",
      "defaultValue": "any",
      "transform": "string",
      "description": "string"
    }
  ],
  "transformations": {
    "transformationName": "transformationRule"
  },
  "defaultValues": {
    "fieldName": "defaultValue"
  },
  "metadata": {
    "key": "value"
  }
}
```

## Best Practices

1. **Naming Convention**: Use descriptive, lowercase names with hyphens (e.g., `company-employees-config`)
2. **Versioning**: Use semantic versioning (e.g., `1.0`, `1.1`, `2.0`)
3. **Descriptions**: Provide clear, concise descriptions for all configurations
4. **Validation**: Ensure all required fields are provided when creating/updating configurations
5. **Error Handling**: Always check the `success` field in responses
6. **Caching**: Consider caching frequently accessed configurations for better performance

## Rate Limiting

Currently, no rate limiting is implemented. All endpoints are available without restrictions.

## Monitoring and Logging

- All API calls are logged for debugging and monitoring purposes
- Error responses include detailed error messages
- Success responses include operation confirmation messages

## Future Enhancements

- **Authentication & Authorization**: Role-based access control
- **Rate Limiting**: API usage throttling
- **Audit Trail**: Track configuration changes and versions
- **Bulk Operations**: Create/update/delete multiple configurations at once
- **Configuration Validation**: Schema validation for configuration data
- **Import/Export**: Configuration backup and restore functionality
