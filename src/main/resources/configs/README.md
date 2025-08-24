# Unified XML to JSON Transformation Configuration Files

This directory contains configuration files for transforming XML data to JSON using the new unified transformation system.

## Configuration Files Overview

### `unified-company-employees-config.json`
**Purpose**: Transform company employee data using the new unified hierarchical mapping system
**Test Data**: `../test-data/company-employees-test.xml`
**Features**:
- Hierarchical nested mappings with children
- Support for single values, arrays, and structured objects
- Unlimited levels of nesting
- Consistent mapping properties at all levels

**Key Mappings**:
- **Company Info**: `company/name` → `companyName` (single)
- **Employees Array**: `company/employees/employee` → `mappedEmployees` (array with children)
  - `id` → `employeeId` (single)
  - `name` → `fullName` (single with trim transform)
  - `department` → `departmentInfo` (object with children)
    - `deptId` → `deptCode` (single)
    - `deptName` → `departmentName` (single)
  - `skills` → `employeeSkills` (array with children)
    - `skill` → `skillName` (single)
- **Departments Array**: `company/departments/department` → `departments` (array with children)
  - `manager` → `managerInfo` (object with children)
    - `name` → `managerName` (single)
    - `email` → `managerEmail` (single)

## New Unified Configuration Structure

### Mapping Types

1. **Single Mappings** (`"type": "single"`)
   - Extract simple values from XML
   - Support data type conversion
   - Can apply transformations
   - Example: `company/name` → `companyName`

2. **Array Mappings** (`"type": "array"`)
   - Handle collections of XML elements
   - Set `"isArray": true` for array processing
   - Can contain children for structured arrays
   - Example: `company/employees/employee` → `employees`

3. **Object Mappings** (`"type": "object"`)
   - Create structured JSON objects
   - Must contain children for field definitions
   - Support nested hierarchies
   - Example: `department` → `departmentInfo`

### Hierarchical Children

Instead of flat field mappings, the new system uses hierarchical children:

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

### Benefits of the New Structure

1. **True Nesting**: Support for unlimited levels of nesting
2. **Consistent Interface**: Same mapping properties at all levels
3. **Flexible Organization**: Group related mappings logically
4. **Easier Maintenance**: Clear hierarchy makes configurations easier to understand
5. **Reusable Patterns**: Common mapping patterns can be defined once and reused

### Configuration Properties

Each mapping supports these properties:
- **xmlPath**: XML element path (e.g., `company/name`)
- **jsonPath**: JSON output path (e.g., `companyName`)
- **type**: Mapping type (`single`, `array`, `object`)
- **isArray**: Boolean flag for array processing
- **children**: List of nested mappings
- **dataType**: Target data type (`string`, `integer`, `boolean`, etc.)
- **required**: Required field flag
- **defaultValue**: Field-specific default value
- **transform**: Transformation to apply
- **description**: Field description

### Global Configuration

- **transformations**: Global transformation rules
- **defaultValues**: Global default values
- **metadata**: Additional configuration metadata

## Migration from Old System

The old three-mapping system has been replaced:
- **Before**: `propertyMappings`, `arrayMappings`, `nestedPropertyMappings`
- **After**: Single `unifiedMappings` array with hierarchical children

### Old vs New Structure

**Old (Flat Fields)**:
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

**New (Hierarchical Children)**:
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

## File Structure

- **Configuration Files**: JSON files with unified mapping structure
- **Test Data**: XML files for testing transformations
- **Documentation**: This README and related guides

## Usage

1. **Create Configuration**: Define your unified mappings in a JSON file
2. **Place in Configs**: Put the file in this `configs/` directory
3. **Use in Application**: Reference by filename (without .json extension)
4. **Transform XML**: Use the `UnifiedXmlToJsonTransformer` service

## Future Enhancements

- **Schema Validation**: JSON schema validation for configurations
- **Template Library**: Pre-built configuration templates
- **Visual Editor**: GUI for creating and editing configurations
- **Version Control**: Configuration versioning and rollback
- **Testing Framework**: Automated configuration testing
