# XML to JSON Transformer Service

A Spring Boot service that transforms XML documents to JSON based on external configuration files. This service provides flexible property mapping, data type conversion, transformation rules, and **enhanced array handling with property-to-property conversion**.

## Features

- **External Configuration**: Define XML to JSON mappings in JSON configuration files
- **Flexible Property Mapping**: Map XML paths to JSON paths with different naming conventions
- **Data Type Support**: Automatic conversion between XML and JSON data types
- **Transformation Rules**: Apply custom transformations (uppercase, lowercase, trim, replace)
- **Enhanced Array Handling**: Support for XML arrays with property-to-property mapping
- **Default Values**: Configure default values for missing XML elements
- **REST API**: Simple HTTP endpoints for transformation and configuration management
- **Caching**: In-memory caching of transformation configurations

## Architecture

### Core Components

1. **XmlToJsonTransformer**: Main transformation service
2. **ConfigurationService**: Manages transformation configurations
3. **TransformerController**: REST API endpoints
4. **PropertyMapping**: Individual property mapping configuration
5. **TransformationConfig**: Complete transformation configuration

### Data Flow

```
XML Input → Parse XML → Apply Configuration → Generate JSON → Output
                ↓
        Configuration Files (JSON)
```

## Configuration Format

### Property Mapping

```json
{
  "xmlPath": "person/name",
  "jsonPath": "fullName",
  "dataType": "string",
  "required": true,
  "defaultValue": "Unknown",
  "transform": "uppercase"
}
```

### Configuration Structure

```json
{
  "name": "Configuration Name",
  "version": "1.0",
  "description": "Description",
  "propertyMappings": [...],
  "arrayMappings": {...},
  "typeConversions": {...},
  "defaultValues": {...},
  "transformations": {...}
}
```

## Enhanced Array Mappings

The service now supports two types of array mappings:

### 1. Simple Array Mapping (Original)
```json
"arrayMappings": {
  "person/skills/skill": "skills"
}
```

### 2. Enhanced Array Mapping with Property Conversion (Inline)
```json
"arrayMappings": {
  "person/addresses/address|street:streetAddress,city:cityName,zipCode:postalCode": "mappedAddresses"
}
```

**Format**: `"xmlPath|xmlProp1:jsonProp1,xmlProp2:jsonProp2"`

- **Before pipe (`|`)**: XML path to array elements
- **After pipe (`|`)**: Property mappings in format `xmlProperty:jsonProperty`

### 3. Readable Nested Property Mapping (New Structured Approach)
```json
"nestedPropertyMappings": [
  {
    "xmlPath": "company/employees/employee",
    "jsonPath": "mappedEmployees",
    "properties": [
      {
        "xmlField": "id",
        "jsonField": "employeeId",
        "dataType": "string",
        "required": true
      },
      {
        "xmlField": "name",
        "jsonField": "fullName",
        "dataType": "string",
        "required": true,
        "transform": "trim"
      },
      {
        "xmlField": "department/deptId",
        "jsonField": "deptCode",
        "dataType": "string"
      }
    ]
  }
]
```

**Benefits of the Readable Approach:**
- ✅ **Clear Structure**: Each property mapping is clearly defined
- ✅ **Type Safety**: Individual data types for each field
- ✅ **Validation**: Required field support
- ✅ **Transformation**: Field-level transformations
- ✅ **Default Values**: Field-level default values
- ✅ **Nested Paths**: Support for complex nested XML structures
- ✅ **Maintainability**: Easy to read, modify, and extend
- ✅ **Documentation**: Self-documenting configuration

### Comparison: Inline vs. Readable Approach

#### Inline Approach (Compact)
```json
"arrayMappings": {
  "company/employees/employee|id:employeeId,name:fullName,position:jobTitle,salary:annualSalary": "mappedEmployees"
}
```

#### Readable Approach (Structured)
```json
"nestedPropertyMappings": [
  {
    "xmlPath": "company/employees/employee",
    "jsonPath": "mappedEmployees",
    "properties": [
      {
        "xmlField": "id",
        "jsonField": "employeeId",
        "dataType": "string",
        "required": true
      },
      {
        "xmlField": "name",
        "jsonField": "fullName",
        "dataType": "string",
        "required": true,
        "transform": "trim"
      },
      {
        "xmlField": "position",
        "jsonField": "jobTitle",
        "dataType": "string"
      },
      {
        "xmlField": "salary",
        "jsonField": "annualSalary",
        "dataType": "integer"
      }
    ]
  }
]
```

**Choose the approach that fits your needs:**
- **Inline**: For simple mappings and quick prototypes
- **Readable**: For complex mappings, production systems, and team collaboration

### Array Mapping Examples

#### Basic Property Mapping
```json
"company/employees/employee|id:employeeId,name:fullName,position:jobTitle": "mappedEmployees"
```

**XML Input:**
```xml
<company>
  <employees>
    <employee>
      <id>EMP001</id>
      <name>John Doe</name>
      <position>Developer</position>
    </employee>
  </employees>
</company>
```

**JSON Output:**
```json
{
  "mappedEmployees": [
    {
      "employeeId": "EMP001",
      "fullName": "John Doe",
      "jobTitle": "Developer"
    }
  ]
}
```

#### Nested Property Mapping
```json
"company/offices/office|officeId:id,address/location:location,city:cityName": "mappedOffices"
```

**XML Input:**
```xml
<company>
  <offices>
    <office>
      <officeId>OFF001</officeId>
      <address>
        <location>123 Main St</location>
      </address>
      <city>New York</city>
    </office>
  </offices>
</company>
```

**JSON Output:**
```json
{
  "mappedOffices": [
    {
      "id": "OFF001",
      "location": "123 Main St",
      "cityName": "New York"
    }
  ]
}
```

#### Attribute Support
```json
"company/projects/project|projectId:id,status:projectStatus": "mappedProjects"
```

**XML Input:**
```xml
<company>
  <projects>
    <project projectId="PRJ001" status="ACTIVE">
      <name>Website Redesign</name>
    </project>
  </projects>
</company>
```

**JSON Output:**
```json
{
  "mappedProjects": [
    {
      "id": "PRJ001",
      "projectStatus": "ACTIVE"
    }
  ]
}
```

## Supported Data Types

- **string**: Text values
- **integer/int**: Whole numbers
- **long**: Large whole numbers
- **double/float**: Decimal numbers
- **bigdecimal**: Precise decimal numbers
- **biginteger**: Large whole numbers
- **boolean/bool**: True/false values
- **date**: Date values (ISO format)
- **datetime**: Date-time values (ISO format)

## Transformation Rules

- **uppercase**: Convert to uppercase
- **lowercase**: Convert to lowercase
- **trim**: Remove leading/trailing whitespace
- **replace:old->new**: Replace text patterns

## API Endpoints

### Health Check
```
GET /api/health
```

### Transform XML to JSON
```
POST /api/transform/{configName}
Body: XML content
```

### Transform with Inline Configuration
```
POST /api/transform
Body: {"xml": "XML content", "config": "JSON configuration"}
```

### Configuration Management
```
GET /api/configs                    # List available configurations
GET /api/configs/{configName}       # Get specific configuration
POST /api/configs/{configName}      # Cache new configuration
DELETE /api/configs/{configName}    # Remove configuration
DELETE /api/configs                 # Clear all configurations
```

## Usage Examples

### 1. Basic Transformation

**XML Input:**
```xml
<person>
    <name>John Doe</name>
    <age>30</age>
    <email>JOHN.DOE@EXAMPLE.COM</email>
</person>
```

**Configuration:**
```json
{
  "propertyMappings": [
    {
      "xmlPath": "person/name",
      "jsonPath": "fullName",
      "dataType": "string"
    },
    {
      "xmlPath": "person/age",
      "jsonPath": "age",
      "dataType": "integer"
    },
    {
      "xmlPath": "person/email",
      "jsonPath": "email",
      "dataType": "string",
      "transform": "lowercase"
    }
  ]
}
```

**JSON Output:**
```json
{
  "fullName": "John Doe",
  "age": 30,
  "email": "john.doe@example.com"
}
```

### 2. Complex Nested Structure

**XML Input:**
```xml
<order>
    <orderId>ORD-001</orderId>
    <customer>
        <customerId>CUST-001</customerId>
        <name>Jane Smith</name>
    </customer>
    <totalAmount>99.99</totalAmount>
    <currency>USD</currency>
</order>
```

**Configuration:**
```json
{
  "propertyMappings": [
    {
      "xmlPath": "order/orderId",
      "jsonPath": "orderId",
      "dataType": "string"
    },
    {
      "xmlPath": "order/customer/customerId",
      "jsonPath": "customer.id",
      "dataType": "string"
    },
    {
      "xmlPath": "order/customer/name",
      "jsonPath": "customer.name",
      "dataType": "string"
    },
    {
      "xmlPath": "order/totalAmount",
      "jsonPath": "financial.totalAmount",
      "dataType": "bigdecimal"
    },
    {
      "xmlPath": "order/currency",
      "jsonPath": "financial.currency",
      "dataType": "string",
      "defaultValue": "USD"
    }
  ]
}
```

**JSON Output:**
```json
{
  "orderId": "ORD-001",
  "customer": {
    "id": "CUST-001",
    "name": "Jane Smith"
  },
  "financial": {
    "totalAmount": 99.99,
    "currency": "USD"
  }
}
```

### 3. Enhanced Array Handling

**XML Input:**
```xml
<person>
    <name>John Doe</name>
    <addresses>
        <address>
            <street>123 Main St</street>
            <city>New York</city>
            <zipCode>10001</zipCode>
        </address>
        <address>
            <street>456 Oak Ave</street>
            <city>Los Angeles</city>
            <zipCode>90210</zipCode>
        </address>
    </addresses>
</person>
```

**Configuration:**
```json
{
  "propertyMappings": [
    {
      "xmlPath": "person/name",
      "jsonPath": "name",
      "dataType": "string"
    }
  ],
  "arrayMappings": {
    "person/addresses/address|street:streetAddress,city:cityName,zipCode:postalCode": "mappedAddresses"
  }
}
```

**JSON Output:**
```json
{
  "name": "John Doe",
  "mappedAddresses": [
    {
      "streetAddress": "123 Main St",
      "cityName": "New York",
      "postalCode": "10001"
    },
    {
      "streetAddress": "456 Oak Ave",
      "cityName": "Los Angeles",
      "postalCode": "90210"
    }
  ]
}
```

### 4. Complex Business Object Arrays

**XML Input:**
```xml
<company>
    <name>TechCorp</name>
    <employees>
        <employee>
            <id>EMP001</id>
            <name>John Doe</name>
            <position>Senior Developer</position>
            <salary>85000</salary>
            <department>
                <deptId>DEV</deptId>
                <deptName>Development</deptName>
            </department>
        </employee>
        <employee>
            <id>EMP002</id>
            <name>Jane Smith</name>
            <position>Product Manager</position>
            <salary>95000</salary>
            <department>
                <deptId>PM</deptId>
                <deptName>Product Management</deptName>
            </department>
        </employee>
    </employees>
</company>
```

**Configuration:**
```json
{
  "propertyMappings": [
    {
      "xmlPath": "company/name",
      "jsonPath": "companyName",
      "dataType": "string"
    }
  ],
  "arrayMappings": {
    "company/employees/employee|id:employeeId,name:fullName,position:jobTitle,salary:annualSalary,department/deptId:deptCode,department/deptName:departmentName": "mappedEmployees"
  }
}
```

**JSON Output:**
```json
{
  "companyName": "TechCorp",
  "mappedEmployees": [
    {
      "employeeId": "EMP001",
      "fullName": "John Doe",
      "jobTitle": "Senior Developer",
      "annualSalary": 85000,
      "deptCode": "DEV",
      "departmentName": "Development"
    },
    {
      "employeeId": "EMP002",
      "fullName": "Jane Smith",
      "jobTitle": "Product Manager",
      "annualSalary": 95000,
      "deptCode": "PM",
      "departmentName": "Product Management"
    }
  ]
}
```

## Running the Service

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build and Run
```bash
# Build the project
mvn clean install

# Run the service
mvn spring-boot:run

# Or run the JAR
java -jar target/transformer-1.0-SNAPSHOT.jar
```

### Test the Service
```bash
# Health check
curl http://localhost:8080/api/health

# Transform XML using sample configuration
curl -X POST http://localhost:8080/api/transform/sample-config \
  -H "Content-Type: text/plain" \
  -d '<person><name>John Doe</name><age>30</age></person>'

# Transform XML using array mapping demo
curl -X POST http://localhost:8080/api/transform/array-mapping-demo \
  -H "Content-Type: text/plain" \
  -d '<company><name>TechCorp</name><employees><employee><id>EMP001</id><name>John Doe</name><position>Developer</position></employee></employees></company>'

# Transform XML using readable configuration
curl -X POST http://localhost:8080/api/transform/readable-array-config \
  -H "Content-Type: text/plain" \
  -d '<company><name>TechCorp</name><employees><employee><id>EMP001</id><name>John Doe</name><position>Developer</position><salary>85000</salary><department><deptId>DEV</deptId><deptName>Development</deptName></department></employee></employees></company>'

# List available configurations
curl http://localhost:8080/api/configs
```

## Configuration File Location

Place your configuration files in:
```
src/main/resources/configs/
```

The service will automatically load configurations from this directory.

## Error Handling

The service provides comprehensive error handling:
- Invalid XML: Returns 400 Bad Request with error details
- Missing configuration: Returns 404 Not Found
- Transformation errors: Returns 400 Bad Request with error message
- Configuration validation: Ensures required fields are present

## Performance Considerations

- **Caching**: Configurations are cached in memory for faster access
- **Lazy Loading**: Configurations are loaded only when needed
- **Efficient Parsing**: Uses DOM parsing for XML and Jackson for JSON
- **Memory Management**: Proper resource cleanup for large XML documents

## Extending the Service

### Custom Transformations
Add new transformation rules in the `XmlToJsonTransformer.applyTransformation()` method.

### Custom Data Types
Extend the `convertToDataType()` method to support additional data types.

### Custom XPath Functions
Enhance the XPath evaluation in `evaluateXPath()` method for complex XML navigation.

### Enhanced Array Mappings
The new array mapping system supports:
- Property-to-property conversion
- Nested path navigation
- Attribute extraction
- Complex object mapping within arrays

## Troubleshooting

### Common Issues

1. **Configuration not found**: Ensure configuration file is in `src/main/resources/configs/`
2. **XML parsing errors**: Check XML syntax and encoding
3. **Property mapping failures**: Verify XML paths and JSON paths in configuration
4. **Data type conversion errors**: Ensure data types match the actual XML content
5. **Array mapping syntax**: Use correct format: `xmlPath|prop1:jsonProp1,prop2:jsonProp2`

### Debug Mode
Enable debug logging in `application.properties`:
```properties
logging.level.com.bitsevn.transformer=DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
