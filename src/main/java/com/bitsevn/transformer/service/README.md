# Configuration Services

This directory contains the unified configuration service for the XML to JSON Transformer.

## ConfigurationService (Unified)

The unified configuration service that reads configurations from JSON files in the `configs/` directory and supports the new hierarchical mapping structure.

**Features:**
- Loads unified configurations from classpath JSON files
- In-memory caching for performance
- Support for hierarchical nested mappings
- Automatic .json extension handling
- Type-safe configuration models

**Configuration Structure:**
The service now uses `UnifiedTransformationConfig` which supports:
- **Single Mappings**: Simple value extraction
- **Array Mappings**: Collection of elements
- **Object Mappings**: Structured objects with children
- **Nested Children**: Unlimited levels of nesting

**Usage:**
```java
@Autowired
private ConfigurationService configurationService;

// Load unified configuration
UnifiedTransformationConfig config = configurationService.loadUnifiedConfiguration("unified-company-employees-config");

// Load with automatic .json extension handling
UnifiedTransformationConfig config = configurationService.loadUnifiedConfigurationAuto("unified-company-employees-config");

// Load from JSON string
UnifiedTransformationConfig config = configurationService.loadUnifiedConfigurationFromJson(jsonString);
```

**Configuration File Format:**
```json
{
  "name": "Configuration Name",
  "version": "2.0",
  "description": "Description",
  "unifiedMappings": [
    {
      "xmlPath": "company/name",
      "jsonPath": "companyName",
      "type": "single",
      "dataType": "string"
    },
    {
      "xmlPath": "company/employees/employee",
      "jsonPath": "employees",
      "type": "array",
      "isArray": true,
      "children": [
        {
          "xmlPath": "id",
          "jsonPath": "employeeId",
          "type": "single"
        },
        {
          "xmlPath": "department",
          "jsonPath": "deptInfo",
          "type": "object",
          "children": [...]
        }
      ]
    }
  ],
  "transformations": {...},
  "defaultValues": {...}
}
```

**Caching:**
```java
// Cache management
configurationService.clearCache();
configurationService.removeCachedUnifiedConfiguration("config-name");

// Get cached configurations
List<String> cachedNames = configurationService.getCachedUnifiedConfigurationNames();
UnifiedTransformationConfig cached = configurationService.getCachedUnifiedConfiguration("config-name");
```

**Validation:**
```java
// Check if configuration exists
boolean exists = configurationService.unifiedConfigurationExists("config-name");

// Convert between formats (for backward compatibility)
Map<String, Object> configMap = configurationService.convertToMap(unifiedConfig);
UnifiedTransformationConfig unifiedConfig = configurationService.convertFromMap(configMap);
```

## Benefits of the New Unified Approach

1. **Hierarchical Structure**: Support for unlimited levels of nesting
2. **Consistent Interface**: Same mapping properties at all levels
3. **Type Safety**: Strongly-typed configuration models
4. **Better Organization**: Logical grouping of related mappings
5. **Easier Maintenance**: Clear hierarchy makes configurations easier to understand
6. **Flexible Mapping**: Support for single values, arrays, and structured objects

## Migration from Old System

The old three-mapping system (propertyMappings, arrayMappings, nestedPropertyMappings) has been replaced with a single unified structure. All configurations now use the `UnifiedTransformationConfig` model with hierarchical children instead of flat fields.

## File Structure

- **Configuration Files**: Located in `src/main/resources/configs/`
- **Model Classes**: `UnifiedTransformationConfig` with nested `UnifiedMapping` classes
- **Service**: `ConfigurationService` for loading and caching configurations
- **Repository**: `TransformationConfigRepository` for MongoDB persistence (if needed)
