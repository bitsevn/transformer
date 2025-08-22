# XML to JSON Transformation Configuration Files

This directory contains configuration files for transforming various XML test data files to JSON using the XML to JSON Transformer service.

## Configuration Files Overview

### 1. `simple-person-config.json`
**Purpose**: Transform simple person XML data
**Test Data**: `../test-data/simple-person.xml`
**Features**:
- Basic property mapping with different names
- Data type conversion (string, integer, boolean, date)
- Transformation rules (lowercase for email)
- Default values for optional fields

**Key Mappings**:
- `person/name` → `fullName`
- `person/email` → `emailAddress` (with lowercase transform)
- `person/active` → `isActive`
- `person/salary` → `annualSalary`

### 2. `simple-order-config.json`
**Purpose**: Transform simple order XML data
**Test Data**: `../test-data/simple-order.xml`
**Features**:
- Nested JSON structure creation
- Business logic grouping
- Default values for business rules

**Key Mappings**:
- `order/orderId` → `orderNumber`
- `order/customerName` → `customer.fullName`
- `order/totalAmount` → `financial.totalAmount`
- `order/status` → `orderDetails.status` (with uppercase transform)

### 3. `company-employees-config.json`
**Purpose**: Transform company employee data with array handling
**Test Data**: `../test-data/company-employees.xml`
**Features**:
- Multiple array mapping approaches
- Both inline and readable nested property mappings
- Complex nested structure handling

**Key Mappings**:
- **Inline**: `company/employees/employee|id:employeeId,name:fullName,position:jobTitle,salary:annualSalary` → `mappedEmployees`
- **Readable**: Detailed employee mapping with department information
- **Simple**: `company/employees/employee` → `allEmployees`
- **Skills**: `company/employees/employee/skills/skill` → `allSkills`

### 4. `company-offices-config.json`
**Purpose**: Transform company office location data
**Test Data**: `../test-data/company-offices.xml`
**Features**:
- Complex nested property mapping
- International address handling
- Contact information mapping

**Key Mappings**:
- `company/offices/office` → `officeLocations`
- `address/street` → `streetAddress`
- `address/city` → `cityName`
- `contact/phone` → `phoneNumber`
- `capacity` → `maxCapacity`

### 5. `company-projects-config.json`
**Purpose**: Transform company project data with attribute extraction
**Test Data**: `../test-data/company-projects.xml`
**Features**:
- XML attribute extraction
- Project metadata mapping
- Team and technology arrays

**Key Mappings**:
- **Attributes**: `@projectId` → `projectCode`, `@status` → `projectStatus`
- **Inline**: `company/projects/project|projectId:projectCode,status:projectStatus,priority:priorityLevel` → `projectSummary`
- **Nested**: Detailed project information with dates and budgets

### 6. `complex-order-config.json`
**Purpose**: Transform complex e-commerce order data
**Test Data**: `../test-data/complex-order.xml`
**Features**:
- Deep nested structure mapping
- XML attribute extraction
- Complex business object transformation
- Multiple address types

**Key Mappings**:
- **Root Attributes**: `@orderId` → `orderNumber`, `@orderType` → `orderType`
- **Customer**: Complex customer structure with personal info and addresses
- **Items**: Product information with specifications and pricing
- **Addresses**: Billing and shipping address handling

### 7. `library-books-config.json`
**Purpose**: Transform library catalog data
**Test Data**: `../test-data/library-books.xml`
**Features**:
- Complex metadata handling
- Classification system mapping
- Author and publication information
- Availability tracking

**Key Mappings**:
- **Root Attributes**: `@name` → `libraryName`, `@established` → `establishedYear`
- **Books**: Comprehensive book information with authors, publishers, and classification
- **Arrays**: Subjects, reviews, genre statistics, and circulation data

## Usage Examples

### Basic Transformation
```bash
# Transform simple person data
curl -X POST http://localhost:8080/api/transform/simple-person-config \
  -H "Content-Type: text/plain" \
  -d @../test-data/simple-person.xml
```

### Array Mapping
```bash
# Transform company employees with array handling
curl -X POST http://localhost:8080/api/transform/company-employees-config \
  -H "Content-Type: text/plain" \
  -d @../test-data/company-employees.xml
```

### Complex Nested Structures
```bash
# Transform complex order data
curl -X POST http://localhost:8080/api/transform/complex-order-config \
  -H "Content-Type: text/plain" \
  -d @../test-data/complex-order.xml
```

## Configuration Patterns

### 1. Property Name Differences
All configurations demonstrate how XML property names can differ from JSON property names:
- `person/name` → `fullName`
- `order/orderId` → `orderNumber`
- `company/industry` → `businessType`

### 2. Depth Differences
Configurations show how to map XML depth to different JSON structures:
- **Flat to Nested**: `order/customerName` → `customer.fullName`
- **Nested to Flat**: `company/employees/employee/name` → `fullName`
- **Complex Nesting**: `order/customer/addresses/address/street` → `streetAddress`

### 3. Array Handling
Multiple approaches for array transformation:
- **Simple**: Extract entire elements
- **Inline**: Property-to-property mapping with pipe syntax
- **Readable**: Structured nested property mapping

### 4. Attribute Extraction
XML attributes are mapped using `@` prefix:
- `@orderId` → `orderNumber`
- `@isbn` → `isbn`
- `@status` → `projectStatus`

### 5. Data Type Conversion
Automatic type conversion with explicit data types:
- `string`, `integer`, `double`, `boolean`, `date`
- Default values for missing data
- Transformation rules (uppercase, lowercase, trim)

## Testing Strategy

### 1. Start Simple
Begin with `simple-person-config.json` to validate basic functionality:
- Property mapping
- Data type conversion
- Transformation rules

### 2. Progress to Arrays
Move to `company-employees-config.json` for array handling:
- Simple array extraction
- Inline property mapping
- Readable nested mapping

### 3. Test Complex Structures
Use `complex-order-config.json` for advanced scenarios:
- Deep nesting
- Attribute extraction
- Complex business objects

### 4. Validate Edge Cases
Test with modified XML data:
- Missing elements
- Empty arrays
- Invalid data types

## Configuration Best Practices

### 1. Naming Conventions
- Use descriptive JSON property names
- Group related properties logically
- Maintain consistency across configurations

### 2. Data Type Handling
- Specify explicit data types for validation
- Use appropriate default values
- Handle missing data gracefully

### 3. Transformation Rules
- Apply transformations consistently
- Use uppercase for status fields
- Use lowercase for email addresses
- Trim whitespace for text fields

### 4. Array Mapping
- Choose the right approach for your use case
- Use inline mapping for simple scenarios
- Use readable mapping for complex scenarios
- Consider performance for large arrays

### 5. Error Handling
- Mark required fields appropriately
- Provide meaningful default values
- Handle missing nested structures

## Performance Considerations

- **Simple mappings** are fastest for basic transformations
- **Inline array mapping** is efficient for property conversion
- **Readable nested mapping** provides clarity but may be slower
- **Deep nesting** can impact performance with large XML files

## Troubleshooting

### Common Issues
1. **Missing elements**: Check required field settings
2. **Data type errors**: Verify data type specifications
3. **Array mapping failures**: Check array mapping syntax
4. **Nested path errors**: Validate XML path expressions

### Debug Tips
- Enable debug logging in application.properties
- Test with smaller XML samples first
- Validate configuration JSON syntax
- Check XML path expressions carefully
