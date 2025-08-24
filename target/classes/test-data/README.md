# Test XML Files for Unified XML to JSON Transformer

This directory contains XML test files for testing the new unified transformation system with hierarchical mapping support.

## Test XML Files

### `unified-company-employees-config.xml`
**Purpose**: Comprehensive testing of the unified hierarchical mapping system
**Features**: 
- Company information with employees, departments, addresses, and contacts
- Complex nested structures for testing hierarchical children
- Multiple array types with different nesting levels
- Rich metadata for comprehensive testing

**Structure**:
```
company
├── name, industry, founded (basic info)
├── employees (array)
│   └── employee
│       ├── id, name, position, salary
│       ├── department (nested object)
│       │   ├── deptId, deptName
│       └── skills (array)
│           └── skill
├── departments (array)
│   └── department
│       ├── deptId, deptName
│       └── manager (nested object)
│           ├── name, email
├── addresses (array)
│   └── address
│       ├── type, street, city, state, zip
├── contacts (array)
│   └── contact
│       ├── type, value, primary
└── metadata
    ├── lastUpdated, version, active
    ├── employeeCount, departmentCount
    ├── totalRevenue, foundedLocation
```

**Use Cases**:
- **Hierarchical Mapping**: Test nested children structure
- **Array Processing**: Test array mappings with children
- **Object Creation**: Test object type mappings
- **Mixed Types**: Test arrays of different types
- **Deep Nesting**: Test unlimited nesting levels
- **Data Types**: Test various data type conversions
- **Transformations**: Test transformation rules
- **Default Values**: Test default value handling

## Testing Scenarios

### 1. Basic Company Information
- Company name extraction
- Industry with default values
- Founded year with data type conversion

### 2. Employee Array Processing
- Raw employee array extraction
- Structured employee mapping with children
- Nested department information
- Skills array with nested structure

### 3. Department Management
- Department array with manager information
- Nested manager object creation
- Complex nested path navigation

### 4. Address and Contact Handling
- Multiple address types
- Contact information with boolean conversion
- Array processing with mixed content

### 5. Metadata Processing
- Various data types (string, boolean, integer)
- Complex nested structures
- Default value handling

## Configuration Testing

### Unified Configuration File
- **File**: `../configs/unified-company-employees-config.json`
- **Structure**: Hierarchical children instead of flat fields
- **Features**: Single, array, and object mapping types

### Mapping Types Tested

#### Single Value Mappings
```json
{
  "xmlPath": "company/name",
  "jsonPath": "companyName",
  "type": "single",
  "dataType": "string"
}
```

#### Array Mappings
```json
{
  "xmlPath": "company/employees/employee",
  "jsonPath": "allEmployees",
  "type": "array",
  "isArray": true
}
```

#### Object Mappings with Children
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
      "type": "single"
    },
    {
      "xmlPath": "department",
      "jsonPath": "departmentInfo",
      "type": "object",
      "children": [...]
    }
  ]
}
```

## Test Execution

### Using the Unified Transformer Controller

```bash
# Test the transformation endpoint
curl -X POST http://localhost:8080/api/unified-transformer/transform/unified-company-employees-config \
  -H "Content-Type: text/plain" \
  -d @unified-company-employees-config.xml
```

### Expected Output Structure

```json
{
  "companyName": "TechCorp Solutions",
  "businessType": "Technology",
  "foundedYear": 2010,
  "allEmployees": [...],
  "mappedEmployees": [
    {
      "employeeId": "EMP001",
      "fullName": "John Smith",
      "departmentInfo": {
        "deptCode": "DEV",
        "departmentName": "Development"
      },
      "employeeSkills": ["Java", "Spring Boot"]
    }
  ],
  "departments": [
    {
      "departmentCode": "DEV",
      "managerInfo": {
        "managerName": "Alex Rodriguez",
        "managerEmail": "alex.rodriguez@techcorp.com"
      }
    }
  ]
}
```

## Validation Points

### 1. Hierarchical Structure
- Verify nested objects are created correctly
- Check array processing with children
- Validate unlimited nesting support

### 2. Data Type Conversion
- String to integer conversion (founded year)
- String to boolean conversion (active status)
- Default value application

### 3. Array Processing
- Multiple employee extraction
- Skills array handling
- Department array with nested objects

### 4. Transformation Rules
- Trim transformation on names
- Uppercase/lowercase transformations
- Custom transformation patterns

### 5. Error Handling
- Missing field handling
- Default value fallbacks
- Invalid path logging

## Performance Testing

### Large XML Documents
- Test with expanded employee counts (1000+)
- Monitor memory usage during deep nesting
- Verify array processing efficiency

### Complex Nested Structures
- Test with 5+ nesting levels
- Verify XPath evaluation performance
- Check memory usage for complex structures

## Troubleshooting

### Common Test Issues

1. **Missing Array Output**
   - Verify `isArray: true` is set
   - Check XML path contains correct element names
   - Ensure proper array type configuration

2. **Nested Object Issues**
   - Verify object type is set correctly
   - Check children array is properly defined
   - Ensure nested paths are correct

3. **Data Type Problems**
   - Verify XML contains valid data for specified type
   - Check default value configuration
   - Validate transformation syntax

### Debug Information

The transformer provides detailed debug output:
- Node count for each path
- Array processing results
- Field mapping success/failure
- Transformation application status

## Future Test Data

### Planned Test Files
- **Deep Nesting**: Test extremely deep nested structures
- **Mixed Arrays**: Test arrays with different child types
- **Complex Transformations**: Test advanced transformation rules
- **Edge Cases**: Test boundary conditions and error scenarios

### Test Data Generation
- **Scripts**: Automated test data generation
- **Variations**: Different data types and structures
- **Scalability**: Large document testing
- **Performance**: Load testing with massive XML files

This test data directory provides comprehensive testing capabilities for the new unified transformation system, ensuring robust validation of hierarchical mapping, array processing, and complex nested structure handling.
