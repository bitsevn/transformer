# Unified XML to JSON Transformer Guide

## Overview

The `UnifiedXmlToJsonTransformer` is an improved transformer that replaces the previous three separate mapping types (`propertyMappings`, `arrayMappings`, `nestedPropertyMappings`) with a single, unified mapping system that allows any level of nesting and complex transformations.

## Key Benefits

1. **Single Configuration**: One mapping system instead of three separate ones
2. **Unlimited Nesting**: Support for any level of nested field mappings
3. **Flexible Array Handling**: Automatic array detection and processing
4. **Type Safety**: Built-in data type conversion and validation
5. **Transformations**: Support for custom and built-in transformations
6. **Default Values**: Configurable default values for missing data
7. **Hierarchical Structure**: True nested mappings with children instead of flat fields

## Configuration Structure

### Basic Structure

```json
{
  "name": "Configuration Name",
  "version": "2.0",
  "description": "Description of the configuration",
  "unifiedMappings": [
    // Array of mapping objects
  ],
  "transformations": {
    // Custom transformation rules
  },
  "defaultValues": {
    // Global default values
  }
}
```

### Mapping Object Structure

Each mapping in the `unifiedMappings` array has the following structure:

```json
{
  "xmlPath": "path/to/xml/element",
  "jsonPath": "path.to.json.field",
  "type": "single|array|object",
  "isArray": true|false,
  "dataType": "string|integer|double|boolean|date|datetime",
  "transform": "transformation_rule",
  "defaultValue": "default_value",
  "required": true|false,
  "children": [
    // Array of child mappings for nested structures
  ]
}
```

## Mapping Types

### 1. Single Value Mappings

For extracting single values from XML:

```json
{
  "xmlPath": "company/name",
  "jsonPath": "companyName",
  "type": "single",
  "dataType": "string",
  "required": true
}
```

### 2. Array Mappings

For extracting arrays of elements:

```json
{
  "xmlPath": "company/employees/employee",
  "jsonPath": "allEmployees",
  "type": "array",
  "isArray": true
}
```

### 3. Structured Array Mappings

For extracting arrays with specific field mappings:

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
      "dataType": "string",
      "required": true
    },
    {
      "xmlPath": "name",
      "jsonPath": "fullName",
      "dataType": "string",
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
          "dataType": "string"
        },
        {
          "xmlPath": "deptName",
          "jsonPath": "departmentName",
          "dataType": "string"
        }
      ]
    }
  ]
}
```

### 4. Object Mappings

For creating structured objects with nested children:

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

## Hierarchical Children Structure

The new system uses hierarchical children instead of flat fields, providing:

### Benefits of Children Structure

1. **True Nesting**: Each child can have its own children, creating unlimited nesting levels
2. **Consistent Interface**: Same properties available at all levels
3. **Logical Grouping**: Related mappings can be grouped together
4. **Easier Maintenance**: Clear hierarchy makes configurations easier to understand
5. **Reusable Patterns**: Common structures can be defined once and reused

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

### Children vs Old Fields

**Old System (Fields)**:
```json
{
  "xmlPath": "company/employees/employee",
  "jsonPath": "mappedEmployees",
  "type": "array",
  "fields": [
    {"xmlField": "id", "jsonField": "employeeId"},
    {"xmlField": "name", "jsonField": "fullName"}
  ]
}
```

**New System (Children)**:
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
      "xmlPath": "name",
      "jsonPath": "fullName",
      "type": "single"
    }
  ]
}
```

## XML Path Syntax

### Basic Paths

- **Simple**: `company/name`
- **Nested**: `company/employees/employee/id`
- **Array**: `company/employees/employee` (for array processing)

### Path Examples

```json
{
  "xmlPath": "company/name",           // Company name
  "xmlPath": "company/founded",        // Founded year
  "xmlPath": "company/employees/employee", // Employee array
  "xmlPath": "company/departments/department/manager/name" // Nested manager name
}
```

## JSON Path Syntax

### Basic Paths

- **Simple**: `companyName`
- **Nested**: `employee.department.name`
- **Array**: `employees` (for array output)

### Path Examples

```json
{
  "jsonPath": "companyName",           // Root level
  "jsonPath": "employeeInfo",          // Object level
  "jsonPath": "employee.department",   // Nested object
  "jsonPath": "employees",             // Array level
  "jsonPath": "metadata.lastUpdated"   // Deep nested
}
```

## Data Types

### Supported Types

- **string**: Text values (default)
- **integer**: Whole numbers
- **long**: Large whole numbers
- **double**: Decimal numbers
- **bigdecimal**: Precise decimal numbers
- **biginteger**: Large precise whole numbers
- **boolean**: True/false values
- **date**: Date values
- **datetime**: Date and time values

### Type Conversion Examples

```json
{
  "xmlPath": "company/founded",
  "jsonPath": "foundedYear",
  "dataType": "integer"
}

{
  "xmlPath": "company/metadata/active",
  "jsonPath": "isActive",
  "dataType": "boolean"
}

{
  "xmlPath": "company/metadata/lastUpdated",
  "jsonPath": "lastUpdated",
  "dataType": "datetime"
}
```

## Transformations

### Built-in Transformations

- **uppercase**: Convert to uppercase
- **lowercase**: Convert to lowercase
- **trim**: Remove leading/trailing whitespace
- **replace**: Custom replacement patterns

### Custom Transformations

```json
{
  "transformations": {
    "capitalize": "replace:^[a-z]|\\b[a-z]:${0:0:1}${0:1}",
    "cleanPhone": "replace:[^0-9+\\-()]:"
  }
}
```

### Applying Transformations

```json
{
  "xmlPath": "company/name",
  "jsonPath": "companyName",
  "transform": "uppercase"
}

{
  "xmlPath": "company/email",
  "jsonPath": "emailAddress",
  "transform": "lowercase"
}
```

## Default Values

### Field-level Defaults

```json
{
  "xmlPath": "company/industry",
  "jsonPath": "businessType",
  "defaultValue": "Technology"
}
```

### Global Defaults

```json
{
  "defaultValues": {
    "businessType": "Technology",
    "foundedYear": 2000,
    "isActive": true
  }
}
```

## Required Fields

### Field Requirements

```json
{
  "xmlPath": "company/name",
  "jsonPath": "companyName",
  "required": true
}

{
  "xmlPath": "company/industry",
  "jsonPath": "businessType",
  "required": false,
  "defaultValue": "Technology"
}
```

## Advanced Features

### Nested Object Creation

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
      "children": [
        {
          "xmlPath": "deptId",
          "jsonPath": "deptCode",
          "type": "single"
        }
      ]
    }
  ]
}
```

### Mixed Type Arrays

```json
{
  "xmlPath": "company/contacts/contact",
  "jsonPath": "contactInfo",
  "type": "array",
  "isArray": true,
  "children": [
    {
      "xmlPath": "type",
      "jsonPath": "contactType",
      "type": "single"
    },
    {
      "xmlPath": "value",
      "jsonPath": "contactValue",
      "type": "single"
    },
    {
      "xmlPath": "primary",
      "jsonPath": "isPrimary",
      "dataType": "boolean"
    }
  ]
}
```

## Migration from Old System

### Old vs New Structure

| Old System | New System |
|------------|------------|
| `propertyMappings` | `unifiedMappings` with `type: "single"` |
| `arrayMappings` | `unifiedMappings` with `type: "array"` |
| `nestedPropertyMappings` | `unifiedMappings` with `type: "object"` and `children` |
| `fields` array | `children` array with full mapping objects |

### Migration Steps

1. **Convert propertyMappings**:
   ```json
   // Old
   {"xmlPath": "x", "jsonPath": "y", "dataType": "string"}
   
   // New
   {"xmlPath": "x", "jsonPath": "y", "type": "single", "dataType": "string"}
   ```

2. **Convert arrayMappings**:
   ```json
   // Old
   {"company/employees/employee": "employees"}
   
   // New
   {"xmlPath": "company/employees/employee", "jsonPath": "employees", "type": "array", "isArray": true}
   ```

3. **Convert nestedPropertyMappings**:
   ```json
   // Old
   {"xmlPath": "x", "jsonPath": "y", "properties": [...]}
   
   // New
   {"xmlPath": "x", "jsonPath": "y", "type": "object", "children": [...]}
   ```

## Best Practices

### 1. Consistent Naming

- Use descriptive names for JSON paths
- Follow consistent naming conventions
- Use camelCase for JSON fields

### 2. Logical Grouping

- Group related mappings under objects
- Use meaningful object names
- Keep nesting levels reasonable

### 3. Error Handling

- Set appropriate default values
- Mark required fields clearly
- Use data type validation

### 4. Performance

- Avoid extremely deep nesting
- Use appropriate data types
- Cache frequently used configurations

## Troubleshooting

### Common Issues

1. **No Output for Arrays**
   - Ensure `isArray: true` is set
   - Check XML path contains correct element names
   - Verify array type is set correctly

2. **Missing Nested Fields**
   - Check XML path uses forward slashes (`/`)
   - Verify child mappings are properly defined
   - Ensure object type is set for structured mappings

3. **Data Type Conversion Errors**
   - Verify XML contains valid data for specified type
   - Use `defaultValue` for fallback values
   - Check transformation syntax

4. **Transformations Not Working**
   - Verify transformation name spelling
   - Check custom transformation syntax
   - Ensure transformation is defined in global transformations

### Debug Information

The transformer provides debug output for:
- Number of nodes found for each path
- Array processing results
- Field mapping success/failure
- Transformation application status

Check console output for detailed processing information.

## Examples

### Complete Configuration Example

```json
{
  "name": "Company Employees Configuration",
  "version": "2.0",
  "description": "Transform company employee data with hierarchical structure",
  "unifiedMappings": [
    {
      "xmlPath": "company/name",
      "jsonPath": "companyName",
      "type": "single",
      "dataType": "string",
      "required": true
    },
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
          "dataType": "string"
        },
        {
          "xmlPath": "department",
          "jsonPath": "departmentInfo",
          "type": "object",
          "children": [
            {
              "xmlPath": "deptId",
              "jsonPath": "deptCode",
              "type": "single"
            }
          ]
        }
      ]
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

This guide covers the complete unified transformation system with hierarchical children support, providing a powerful and flexible way to transform XML data to JSON with unlimited nesting capabilities.
