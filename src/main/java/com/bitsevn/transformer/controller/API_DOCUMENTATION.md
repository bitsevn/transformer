# XML to JSON Transformer API Documentation

## Base URL
```
http://localhost:8080/api
```

## Controllers Overview

The API is organized into two main controllers:

1. **TransformerController** (`/api`) - Handles XML to JSON transformation operations
2. **ConfigurationController** (`/api/configs`) - Manages transformation configurations

## Transformer Controller (`/api`)

### Transform XML to JSON using File-based Configuration
```
POST /transform/{configName}
```
Transform XML using a named configuration from files.

**Request Body:** XML string

**Response:**
```json
{
  "result": "transformed JSON string"
}
```

### Transform with Inline Configuration
```
POST /transform
```
Transform XML using inline configuration.

**Request Body:**
```json
{
  "xml": "XML string",
  "config": "JSON configuration string"
}
```

### Transform using MongoDB Configuration
```
POST /mongo/transform/{configName}
```
Transform XML using a configuration stored in MongoDB.

**Request Body:** XML string

**Response:**
```json
{
  "configName": "config-name",
  "result": "transformed JSON string"
}
```

## Configuration Controller (`/api/configs`)

### File-based Configuration Management

#### Get Available Configurations
```
GET /configs
```
Returns list of cached configuration names.

#### Get Configuration
```
GET /configs/{configName}
```
Returns a specific configuration by name.

#### Cache Configuration
```
POST /configs/{configName}
```
Cache a configuration in memory.

**Request Body:** TransformationConfig object

#### Remove Configuration from Cache
```
DELETE /configs/{configName}
```
Remove a configuration from memory cache.

#### Clear All Cached Configurations
```
DELETE /configs
```
Clear all configurations from memory cache.

### MongoDB Configuration Management

#### 1. Configuration CRUD Operations

##### Save New Configuration
```
POST /mongo/configs
```
Save a new configuration to MongoDB.

**Request Body:** TransformationConfig object
```json
{
  "name": "config-name",
  "version": "1.0",
  "description": "Configuration description",
  "propertyMappings": [...],
  "arrayMappings": {...},
  "nestedPropertyMappings": [...]
}
```

**Response:**
```json
{
  "message": "Configuration saved successfully",
  "name": "config-name",
  "id": "config-name"
}
```

##### Get All Configurations
```
GET /mongo/configs
```
Retrieve all configurations from MongoDB.

**Response:**
```json
{
  "count": 5,
  "configurations": [...]
}
```

##### Get Configuration by Name
```
GET /mongo/configs/{configName}
```
Retrieve a specific configuration by name.

**Response:** TransformationConfig object

##### Get Configuration by ID
```
GET /mongo/configs/id/{configId}
```
Retrieve a configuration by MongoDB ObjectId.

**Response:** TransformationConfig object

##### Update Configuration
```
PUT /mongo/configs/{configName}
```
Update an existing configuration.

**Request Body:** Updated TransformationConfig object

**Response:**
```json
{
  "message": "Configuration updated successfully",
  "name": "config-name"
}
```

##### Delete Configuration
```
DELETE /mongo/configs/{configName}
```
Delete a configuration from MongoDB.

**Response:**
```json
{
  "message": "Configuration deleted successfully",
  "name": "config-name"
}
```

#### 2. Configuration Queries

##### Check Configuration Exists
```
GET /mongo/configs/{configName}/exists
```
Check if a configuration exists in MongoDB.

**Response:**
```json
{
  "name": "config-name",
  "exists": true
}
```

##### Get Configuration Count
```
GET /mongo/configs/count
```
Get total number of configurations in MongoDB.

**Response:**
```json
{
  "count": 5
}
```

##### Find by Version
```
GET /mongo/configs/version/{version}
```
Find all configurations with a specific version.

**Response:**
```json
{
  "version": "1.0",
  "count": 3,
  "configurations": [...]
}
```

##### Search by Description
```
GET /mongo/configs/search/description?description=search_term
```
Find configurations containing text in description.

**Response:**
```json
{
  "searchTerm": "search_term",
  "count": 2,
  "configurations": [...]
}
```

##### Find Configurations with Array Mappings
```
GET /mongo/configs/with-array-mappings
```
Find all configurations that have array mappings defined.

**Response:**
```json
{
  "count": 3,
  "configurations": [...]
}
```

##### Find Configurations with Nested Mappings
```
GET /mongo/configs/with-nested-mappings
```
Find all configurations that have nested property mappings.

**Response:**
```json
{
  "count": 2,
  "configurations": [...]
}
```

##### Search by XML Path Pattern
```
GET /mongo/configs/search/xml-path?xmlPathPattern=person/.*
```
Find configurations with XML paths matching a regex pattern.

**Response:**
```json
{
  "xmlPathPattern": "person/.*",
  "count": 4,
  "configurations": [...]
}
```

#### 3. JSON Import Operations

##### Import Configuration from JSON
```
POST /mongo/configs/from-json
```
Load a configuration from JSON string and save to MongoDB.

**Request Body:**
```json
{
  "jsonConfig": "{\"name\":\"config\",\"version\":\"1.0\",...}"
}
```

**Response:**
```json
{
  "message": "Configuration loaded from JSON and saved successfully",
  "name": "config"
}
```

##### Import Multiple Configurations from JSON Array
```
POST /mongo/configs/from-json-array
```
Load multiple configurations from JSON array and save to MongoDB.

**Request Body:**
```json
{
  "jsonConfigs": "[{\"name\":\"config1\",...},{\"name\":\"config2\",...}]"
}
```

**Response:**
```json
{
  "message": "Configurations processed successfully",
  "totalProcessed": 5,
  "saved": 4,
  "skipped": 1
}
```

#### 4. Cache Management

##### Refresh Configuration
```
POST /mongo/configs/{configName}/refresh
```
Clear cache and reload configuration from MongoDB.

**Response:**
```json
{
  "message": "Configuration refreshed successfully",
  "name": "config-name",
  "configuration": {...}
}
```

##### Refresh All Configurations
```
POST /mongo/configs/refresh-all
```
Clear cache and reload all configurations from MongoDB.

**Response:**
```json
{
  "message": "All configurations refreshed successfully",
  "count": 5
}
```

##### Get Cached Configuration Names
```
GET /mongo/configs/cached
```
Get list of all cached configuration names.

**Response:**
```json
{
  "count": 3,
  "cachedConfigurations": ["config1", "config2", "config3"]
}
```

##### Clear MongoDB Cache
```
DELETE /mongo/configs/cached
```
Clear all cached configurations from memory.

**Response:**
```json
{
  "message": "MongoDB configuration cache cleared successfully"
}
```



## Error Responses

All endpoints return consistent error responses:

```json
{
  "error": "Error description message"
}
```

## HTTP Status Codes

- **200 OK**: Success
- **201 Created**: Resource created successfully
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

## Example Usage

### 1. Transform XML using File-based Configuration
```bash
curl -X POST http://localhost:8080/api/transform/person-config \
  -H "Content-Type: text/plain" \
  -d '<person><name>John Doe</name></person>'
```

### 2. Transform XML using Inline Configuration
```bash
curl -X POST http://localhost:8080/api/transform \
  -H "Content-Type: application/json" \
  -d '{
    "xml": "<person><name>John Doe</name></person>",
    "config": "{\"name\":\"inline-config\",\"propertyMappings\":[{\"xmlPath\":\"person/name\",\"jsonPath\":\"fullName\",\"dataType\":\"string\"}]}"
  }'
```

### 3. Transform XML using MongoDB Configuration
```bash
curl -X POST http://localhost:8080/api/mongo/transform/person-config \
  -H "Content-Type: text/plain" \
  -d '<person><name>John Doe</name></person>'
```

### 4. Save a Configuration to MongoDB
```bash
curl -X POST http://localhost:8080/api/configs/mongo \
  -H "Content-Type: application/json" \
  -d '{
    "name": "person-config",
    "version": "1.0",
    "description": "Person XML to JSON transformation",
    "propertyMappings": [
      {
        "xmlPath": "person/name",
        "jsonPath": "fullName",
        "dataType": "string"
      }
    ]
  }'
```

### 5. Find Configurations by Version
```bash
curl http://localhost:8080/api/configs/mongo/version/1.0
```

### 6. Search Configurations by Description
```bash
curl "http://localhost:8080/api/configs/mongo/search/description?description=person"
```

## Migration from File-based to MongoDB

1. **Start MongoDB service**
2. **Import existing configurations:**
   ```bash
   # Load from file and save to MongoDB
   curl -X POST http://localhost:8080/api/configs/mongo/from-json \
     -H "Content-Type: application/json" \
     -d '{"jsonConfig": "..."}'
   ```
3. **Switch to MongoDB endpoints:**
   - Use `/api/mongo/transform/{configName}` instead of `/api/transform/{configName}`
   - Use `/api/configs/mongo/*` for configuration management

## Notes

- All MongoDB operations include automatic caching
- Configuration names must be unique
- Updates preserve the configuration name from the URL path
- Search operations support regex patterns for XML paths
- Cache operations are independent of MongoDB persistence
- Error handling includes detailed error messages
- All responses include relevant metadata (counts, names, etc.)
