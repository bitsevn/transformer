# Configuration Services

This directory contains two configuration services for the XML to JSON Transformer:

## 1. ConfigurationService (File-based)

The original file-based configuration service that reads configurations from JSON files in the `configs/` directory.

**Features:**
- Loads configurations from classpath JSON files
- In-memory caching for performance
- Simple file-based storage

**Usage:**
```java
@Autowired
private ConfigurationService configurationService;

TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
```

## 2. MongoConfigurationService (MongoDB-based)

A new MongoDB-based configuration service that stores and retrieves configurations from a MongoDB database.

**Features:**
- MongoDB persistence for configurations
- Advanced querying capabilities
- In-memory caching with MongoDB sync
- CRUD operations for configurations
- Search by various criteria

**Prerequisites:**
- MongoDB instance running (default: localhost:27017)
- Database: `transformer`
- Collection: `transformation_configs`

**Configuration:**
```properties
# application.properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=transformer
spring.data.mongodb.auto-index-creation=true
```

**Usage:**
```java
@Autowired
private MongoConfigurationService mongoConfigService;

// Load configuration by name
TransformationConfig config = mongoConfigService.loadConfiguration("simple-person-config");

// Save new configuration
TransformationConfig newConfig = new TransformationConfig();
newConfig.setName("new-config");
newConfig.setVersion("1.0");
mongoConfigService.saveConfiguration(newConfig);

// Find configurations by criteria
List<TransformationConfig> configs = mongoConfigService.findConfigurationsByVersion("1.0");
List<TransformationConfig> arrayConfigs = mongoConfigService.findConfigurationsWithArrayMappings();

// Update configuration
mongoConfigService.updateConfiguration("config-name", updatedConfig);

// Delete configuration
mongoConfigService.deleteConfiguration("config-name");
```

**Advanced Querying:**
```java
// Find configurations with specific XML path patterns
List<TransformationConfig> personConfigs = mongoConfigService
    .findConfigurationsByXmlPathPattern("person/.*");

// Find configurations with nested property mappings
List<TransformationConfig> nestedConfigs = mongoConfigService
    .findConfigurationsWithNestedMappings();

// Find configurations by description
List<TransformationConfig> descConfigs = mongoConfigService
    .findConfigurationsByDescription("employee");
```

**Caching:**
```java
// Cache management
mongoConfigService.clearCache();
mongoConfigService.refreshConfiguration("config-name");
mongoConfigService.refreshAllConfigurations();

// Get cached configurations
List<String> cachedNames = mongoConfigService.getCachedConfigurationNames();
TransformationConfig cached = mongoConfigService.getCachedConfiguration("config-name");
```

## Migration from File-based to MongoDB

To migrate existing configurations:

1. **Start MongoDB service**
2. **Load existing configurations:**
   ```java
   // Load from file service
   TransformationConfig config = fileConfigService.loadConfiguration("config-name");
   
   // Save to MongoDB
   mongoConfigService.saveConfiguration(config);
   ```

3. **Switch to MongoDB service:**
   ```java
   // Change from
   @Autowired
   private ConfigurationService configurationService;
   
   // To
   @Autowired
   private MongoConfigurationService configurationService;
   ```

## MongoDB Schema

The `transformation_configs` collection stores documents with this structure:

```json
{
  "_id": "ObjectId",
  "name": "simple-person-config",
  "version": "1.0",
  "description": "Configuration for simple person XML to JSON transformation",
  "propertyMappings": [...],
  "arrayMappings": {...},
  "nestedPropertyMappings": [...],
  "typeConversions": {...},
  "defaultValues": {...},
  "transformations": {...}
}
```

## Performance Considerations

- **Caching**: Both services use in-memory caching for frequently accessed configurations
- **MongoDB Queries**: Use repository methods for better performance than raw queries
- **Indexing**: MongoDB automatically creates indexes on `_id` and `name` fields
- **Connection Pooling**: Spring Boot manages MongoDB connection pooling

## Error Handling

Both services handle errors gracefully:
- Return `null` for missing configurations
- Return empty lists for failed queries
- Log errors to console (can be enhanced with proper logging framework)
- Cache failures don't affect subsequent operations

## Future Enhancements

- **Audit Trail**: Track configuration changes and versions
- **Validation**: Schema validation for configuration documents
- **Encryption**: Sensitive configuration data encryption
- **Backup/Restore**: Configuration backup and restore functionality
- **Multi-tenancy**: Support for multiple organizations/tenants
