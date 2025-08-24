# Testing the Unified XML to JSON Transformer

This guide shows how to test the `UnifiedXmlToJsonTransformer` with the provided test data.

## Test Data Files

- **XML Input**: `company-employees-test.xml` - Contains comprehensive company data
- **Configuration**: `unified-company-employees-config.json` - Unified mapping configuration

## Test Scenarios

### 1. Basic Company Information
The configuration will extract:
- Company name: "TechCorp Solutions"
- Industry: "Technology" (with default value)
- Founded year: 2010 (with default value)

### 2. Employee Arrays
- **allEmployees**: Raw employee elements (5 employees)
- **mappedEmployees**: Structured employee objects with specific fields
- **allSkills**: All skill elements across all employees

### 3. Department Information
- **departments**: Structured department objects with manager details

### 4. Address Information
- **companyAddresses**: Structured address objects

### 5. Contact Information
- **contactInfo**: Structured contact objects

## Testing with the API

### Using the Unified Transformer Controller

The controller now uses a RESTful approach with the new `UnifiedTransformationConfig` model:
- **URL**: `/api/unified-transformer/transform/{configName}`
- **Method**: POST
- **Body**: XML string (raw text)
- **Path Parameter**: `configName` (configuration file name without .json extension)

```bash
# Test the transformation endpoint
curl -X POST http://localhost:8080/api/unified-transformer/transform/unified-company-employees-config \
  -H "Content-Type: text/plain" \
  -d '<?xml version="1.0" encoding="UTF-8"?><company><name>TechCorp Solutions</name><industry>Technology</industry><founded>2010</founded><employees><employee><id>EMP001</id><name>John Smith</name><position>Senior Software Engineer</position><salary>95000</salary><department><deptId>DEV</deptId><deptName>Development</deptName></department><skills><skill>Java</skill><skill>Spring Boot</skill></skills></employee></employees></company>'
```

### Expected Output Structure

```json
{
  "success": true,
  "result": "{\"companyName\":\"TechCorp Solutions\",\"employees\":[...]}",
  "message": "XML transformed successfully using configuration: unified-company-employees-config",
  "configUsed": "unified-company-employees-config",
  "configInfo": {
    "name": "Unified Company Employees Configuration",
    "version": "2.0",
    "description": "Unified configuration for company employees XML to JSON transformation",
    "mappingCount": 3
  }
}
```

## Testing Individual Mapping Types

### Single Value Mapping Test

Use the existing configuration file `unified-company-employees-config.json` which contains:

```json
{
  "xmlPath": "company/name",
  "jsonPath": "companyName",
  "type": "single",
  "dataType": "string"
}
```

**Expected Output**: `{"companyName": "TechCorp Solutions"}`

### Simple Array Mapping Test

```json
{
  "xmlPath": "company/employees/employee",
  "jsonPath": "allEmployees",
  "type": "array",
  "isArray": true
}
```

**Expected Output**: Array of 5 employee elements with all their XML content

### Hierarchical Array Mapping Test

The new structure uses children instead of fields:

```json
{
  "xmlPath": "company/employees/employee",
  "jsonPath": "mappedEmployees",
  "type": "array",
  "isArray": true,
  "children": [
    {
      "xmlPath": "id",
      "jsonPath": "employeeId",
      "type": "single",
      "dataType": "string",
      "required": true
    },
    {
      "xmlPath": "name",
      "jsonPath": "fullName",
      "type": "single",
      "dataType": "string",
      "required": true,
      "transform": "trim"
    },
    {
      "xmlPath": "department",
      "jsonPath": "departmentInfo",
      "type": "object",
      "children": [
        {
          "xmlPath": "deptId",
          "jsonPath": "deptCode",
          "type": "single",
          "dataType": "string"
        },
        {
          "xmlPath": "deptName",
          "jsonPath": "departmentName",
          "type": "single",
          "dataType": "string"
        }
      ]
    }
  ]
}
```

**Expected Output**: Array of 5 structured employee objects with nested department information

## Testing Nested Field Extraction

### Department with Manager Information

```json
{
  "xmlPath": "company/departments/department",
  "jsonPath": "departments",
  "type": "array",
  "isArray": true,
  "children": [
    {
      "xmlPath": "deptId",
      "jsonPath": "departmentCode",
      "type": "single"
    },
    {
      "xmlPath": "deptName",
      "jsonPath": "departmentName",
      "type": "single"
    },
    {
      "xmlPath": "manager",
      "jsonPath": "managerInfo",
      "type": "object",
      "children": [
        {
          "xmlPath": "name",
          "jsonPath": "managerName",
          "type": "single"
        },
        {
          "xmlPath": "email",
          "jsonPath": "managerEmail",
          "type": "single"
        }
      ]
    }
  ]
}
```

**Expected Output**: Array of 6 department objects with nested manager details

## Testing Data Type Conversions

### Integer Conversion Test

```json
{
  "xmlPath": "company/founded",
  "jsonPath": "foundedYear",
  "type": "single",
  "dataType": "integer"
}
```

**Expected Output**: `{"foundedYear": 2010}` (as integer, not string)

### Boolean Conversion Test

```json
{
  "xmlPath": "company/metadata/active",
  "jsonPath": "isActive",
  "type": "single",
  "dataType": "boolean"
}
```

**Expected Output**: `{"isActive": true}` (as boolean, not string)

## Testing Transformations

### Trim Transformation Test

```json
{
  "xmlPath": "name",
  "jsonPath": "fullName",
  "transform": "trim"
}
```

**Expected Output**: Employee names with leading/trailing whitespace removed

### Custom Transformation Test

```json
{
  "transformations": {
    "capitalize": "replace:^[a-z]|\\b[a-z]:${0:0:1}${0:1}"
  }
}
```

**Expected Output**: First letter of each word capitalized

## Testing Default Values

### Field-level Default Test

```json
{
  "xmlPath": "salary",
  "jsonPath": "annualSalary",
  "dataType": "integer",
  "defaultValue": 50000
}
```

**Expected Output**: Uses default value if salary is missing

### Global Default Test

```json
{
  "defaultValues": {
    "businessType": "Technology",
    "foundedYear": 2000
  }
}
```

**Expected Output**: Applies to all mappings that reference these defaults

## Performance Testing

### Large XML Test
- Test with XML containing 1000+ employees
- Monitor memory usage and processing time
- Verify array processing efficiency

### Complex Nested Structure Test
- Test with deeply nested XML (5+ levels)
- Verify XPath evaluation performance
- Check memory usage for complex structures

## Error Handling Tests

### Invalid XML Path Test
```json
{
  "xmlPath": "company/nonexistent/path",
  "jsonPath": "testField",
  "type": "single"
}
```

**Expected Behavior**: Logs error, skips mapping, continues processing

### Missing Required Field Test
```json
{
  "xmlPath": "requiredField",
  "jsonPath": "requiredJsonField",
  "required": true
}
```

**Expected Behavior**: Uses default value if specified, logs warning

### Data Type Conversion Failure Test
```json
{
  "xmlPath": "invalidNumber",
  "jsonPath": "numberField",
  "dataType": "integer"
}
```

**Expected Behavior**: Falls back to string, logs error

## Debug Information

The transformer provides debug output for:
- Number of nodes found for each path
- Array processing results
- Field mapping success/failure
- Transformation application status

Check console output for detailed processing information.

## Validation

After transformation, verify:
1. All expected fields are present
2. Data types are correct
3. Arrays contain expected number of elements
4. Nested structures are properly mapped
5. Transformations are applied correctly
6. Default values are used when appropriate

## Troubleshooting

### Common Issues

1. **No output for array mappings**
   - Check if `isArray` is set to `true`
   - Verify XML path contains correct element names

2. **Missing nested fields**
   - Ensure XML path uses forward slashes (`/`)
   - Check for typos in field names

3. **Data type conversion errors**
   - Verify XML contains valid data for specified type
   - Use `defaultValue` for fallback values

4. **Transformations not working**
   - Check transformation name spelling
   - Verify custom transformation syntax

### Debug Commands

```bash
# Check transformer health
curl -X POST http://localhost:8080/api/unified-transformer/health

# List available configuration files
curl -X POST http://localhost:8080/api/unified-transformer/configs

# Check if a specific configuration exists
curl -X POST http://localhost:8080/api/unified-transformer/configs/unified-company-employees-config/exists

# Get configuration details
curl -X POST http://localhost:8080/api/unified-transformer/configs/unified-company-employees-config/details

# Test with minimal configuration
curl -X POST http://localhost:8080/api/unified-transformer/transform/unified-company-employees-config \
  -H "Content-Type: text/plain" \
  -d '<test>value</test>'
```

## Configuration File Management

### Available Configuration Files
The controller automatically loads configuration files from `classpath:configs/` using the `ConfigurationService`:

- `unified-company-employees-config.json` - Unified mapping configuration
- `company-employees-config.json` - Legacy configuration (if migrated)

### Adding New Configurations
1. Place your JSON configuration file in `src/main/resources/configs/`
2. Use the filename (without .json extension) in the URL path
3. The `ConfigurationService` will automatically load and cache the configuration

### Configuration File Format
Your configuration file should follow this structure:

```json
{
  "name": "Configuration Name",
  "version": "2.0",
  "description": "Description of the configuration",
  "unifiedMappings": [
    {
      "xmlPath": "path/to/xml/element",
      "jsonPath": "path.to.json.field",
      "type": "single|array|object",
      "isArray": true|false,
      "children": [
        {
          "xmlPath": "child/path",
          "jsonPath": "child.field",
          "type": "single|array|object",
          "children": [...]
        }
      ]
    }
  ],
  "transformations": {...},
  "defaultValues": {...}
}
```

## ConfigurationService Integration

The controller now uses the `ConfigurationService` for:
- **Caching**: Configurations are automatically cached for performance
- **Error Handling**: Proper error messages for missing or invalid configurations
- **File Management**: Automatic .json extension handling
- **Monitoring**: Track which configurations are loaded and cached

### Benefits of ConfigurationService
- **Performance**: Configurations are cached after first load
- **Reliability**: Centralized configuration management
- **Flexibility**: Support for both legacy and unified formats
- **Monitoring**: Track configuration usage and cache status

## New Hierarchical Model Architecture

### UnifiedTransformationConfig Model

The new system uses a strongly-typed hierarchical model:

```java
public class UnifiedTransformationConfig {
    private String name;
    private String version;
    private String description;
    private List<UnifiedMapping> unifiedMappings;
    private Map<String, String> transformations;
    private Map<String, Object> defaultValues;
    private Map<String, Object> metadata;
}
```

### Hierarchical UnifiedMapping

Each mapping can now have children, creating a tree structure:

```java
public static class UnifiedMapping {
    private String xmlPath;
    private String jsonPath;
    private String type; // "single", "array", "object"
    private Boolean isArray;
    private List<UnifiedMapping> children; // Nested mappings
    // ... other properties
}
```

### Benefits of the New Hierarchical Structure

1. **True Nesting**: Support for unlimited levels of nesting
2. **Consistent Structure**: Same mapping properties at all levels
3. **Flexible Organization**: Group related mappings logically
4. **Easier Maintenance**: Clear hierarchy makes configurations easier to understand
5. **Reusable Patterns**: Common mapping patterns can be defined once and reused

### Mapping Types

- **single**: Simple value extraction
- **array**: Collection of elements
- **object**: Structured object with children

### Example Hierarchical Structure

```
company/employees/employee (array)
├── id (single)
├── name (single)
├── department (object)
│   ├── deptId (single)
│   └── deptName (single)
└── skills (array)
    └── skill (single)
```

### Utility Methods

The new model includes helpful methods:
- **`hasChildren()`**: Check if mapping has child mappings
- **`isLeaf()`**: Check if mapping is a leaf node
- **`getDepth()`**: Get the depth in the hierarchy
- **`getTotalMappingCount()`**: Count all mappings in the hierarchy

### Backward Compatibility

The system maintains backward compatibility by:
- Converting `UnifiedTransformationConfig` to `Map<String, Object>` for the transformer
- Supporting both legacy and unified configuration formats
- Providing conversion utilities between formats

### Enhanced API Endpoints

#### **Configuration Details** (`/configs/{configName}/details`)
Returns detailed information about a configuration:
```json
{
  "configName": "unified-company-employees-config",
  "config": {
    "name": "Unified Company Employees Configuration",
    "version": "2.0",
    "description": "Unified configuration for company employees XML to JSON transformation",
    "unifiedMappingsCount": 3,
    "transformationsCount": 2,
    "defaultValuesCount": 1,
    "metadataCount": 0
  },
  "message": "Configuration details retrieved successfully"
}
```

#### **Enhanced Health Check**
Now includes model type information:
```json
{
  "status": "healthy",
  "service": "Unified XML to JSON Transformer",
  "message": "Service is running and ready to process transformations",
  "configLocation": "classpath:configs/",
  "supportedFormats": "JSON configuration files",
  "modelTypes": "UnifiedTransformationConfig, TransformationConfig"
}
```
