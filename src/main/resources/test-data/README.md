# Test XML Files for XML to JSON Transformer

This directory contains various XML test files to help test different scenarios of the XML to JSON transformation service.

## Simple XML Files

### 1. `simple-person.xml`
**Purpose**: Basic property mapping testing
**Features**: 
- Simple flat structure
- Different data types (string, integer, boolean, date)
- No nested elements or arrays

**Use Cases**:
- Basic property mapping validation
- Data type conversion testing
- Simple transformation rules

### 2. `simple-order.xml`
**Purpose**: Basic order structure testing
**Features**:
- Simple order information
- Different data types
- Flat structure

**Use Cases**:
- Basic order processing
- Simple property mapping
- Data validation

## Medium Complexity XML Files

### 3. `company-employees.xml`
**Purpose**: Array mapping testing
**Features**:
- Company information
- Employee array with nested structures
- Skills arrays
- Department information

**Use Cases**:
- Array mapping with property conversion
- Nested property extraction
- Multiple array handling
- Complex object mapping within arrays

### 4. `company-offices.xml`
**Purpose**: Nested property mapping testing
**Features**:
- Office locations with addresses
- Contact information
- Capacity and timezone data
- International addresses

**Use Cases**:
- Nested path navigation
- Address structure mapping
- International data handling
- Complex nested property extraction

### 5. `company-projects.xml`
**Purpose**: Attribute extraction and complex mapping testing
**Features**:
- Project information with attributes
- Team member arrays
- Technology arrays
- Project status and priority

**Use Cases**:
- XML attribute extraction
- Complex nested structures
- Multiple array types
- Project management scenarios

## Complex XML Files

### 6. `complex-order.xml`
**Purpose**: Advanced transformation testing
**Features**:
- Complex customer structure
- Multiple address types
- Item arrays with specifications
- Financial calculations
- Tracking information
- Rich metadata

**Use Cases**:
- Complex business object transformation
- Multiple nested levels
- Financial data processing
- E-commerce scenarios
- Advanced property mapping

### 7. `library-books.xml`
**Purpose**: Library management testing
**Features**:
- Book catalog with metadata
- Author information
- Classification systems
- Availability tracking
- Review systems
- Statistics and circulation data

**Use Cases**:
- Library management systems
- Complex metadata handling
- Statistical data processing
- Multi-level classification
- Review and rating systems

## Testing Scenarios

### Basic Testing
- Use `simple-person.xml` and `simple-order.xml` for:
  - Property mapping validation
  - Data type conversion
  - Basic transformation rules

### Array Mapping Testing
- Use `company-employees.xml` for:
  - Employee array processing
  - Skills array handling
  - Department mapping

### Nested Property Testing
- Use `company-offices.xml` for:
  - Address structure mapping
  - Nested path navigation
  - International data handling

### Attribute Testing
- Use `company-projects.xml` for:
  - XML attribute extraction
  - Project status mapping
  - Team member processing

### Complex Business Logic
- Use `complex-order.xml` for:
  - E-commerce scenarios
  - Financial calculations
  - Customer relationship management
  - Order tracking systems

### Library Systems
- Use `library-books.xml` for:
  - Catalog management
  - Metadata processing
  - Classification systems
  - Statistical analysis

## Configuration Examples

### Simple Person Mapping
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
    }
  ]
}
```

### Employee Array Mapping
```json
{
  "arrayMappings": {
    "company/employees/employee|id:employeeId,name:fullName,position:jobTitle,salary:annualSalary": "mappedEmployees"
  }
}
```

### Readable Employee Mapping
```json
{
  "nestedPropertyMappings": [
    {
      "xmlPath": "company/employees/employee",
      "jsonPath": "mappedEmployees",
      "properties": [
        {
          "xmlField": "id",
          "jsonField": "employeeId",
          "dataType": "string"
        },
        {
          "xmlField": "name",
          "jsonField": "fullName",
          "dataType": "string"
        }
      ]
    }
  ]
}
```

## Usage

1. **Copy XML files** to your test environment
2. **Create appropriate configurations** for each test scenario
3. **Run transformations** using the REST API
4. **Validate results** against expected JSON output
5. **Test edge cases** with modified XML data

## Tips for Testing

- **Start Simple**: Begin with `simple-person.xml` to validate basic functionality
- **Progress Gradually**: Move to more complex files as basic features work
- **Test Edge Cases**: Modify XML files to test error handling
- **Validate Output**: Always check that the JSON output matches expectations
- **Performance Test**: Use larger XML files to test performance under load
