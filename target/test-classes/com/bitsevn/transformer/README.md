# XML to JSON Transformation Test Suite

This directory contains comprehensive tests for the XML to JSON transformation service using all the test data files and configurations.

## 🧪 Test Suite Overview

The test suite is designed to validate the complete functionality of the XML to JSON transformation service, covering:

- **Basic Transformation Tests**: Core functionality validation
- **Edge Case Tests**: Error handling and boundary conditions
- **Performance Tests**: Scalability and performance characteristics
- **Integration Tests**: End-to-end system validation

## 📁 Test Files

### 1. `TestDataIntegrationTest.java`
**Purpose**: Main integration tests for all XML files and configurations
**Coverage**:
- Simple person transformation with data type conversion
- Simple order transformation with nested JSON structure
- Company employees with array handling (inline + readable)
- Company offices with complex nested property mapping
- Company projects with attribute extraction
- Complex order with deep nested structures
- Library books with complex metadata handling

**Key Features**:
- Tests all 7 test data files
- Validates all 7 configuration files
- Tests property name differences and depth variations
- Validates array mapping approaches
- Tests XML attribute extraction
- Verifies transformation rules and default values

### 2. `TestDataEdgeCaseTest.java`
**Purpose**: Edge case and error handling validation
**Coverage**:
- Empty XML handling
- Missing fields handling
- Large numbers handling
- Special characters handling
- Malformed XML error handling
- Null and empty input validation
- Configuration validation
- XML with only attributes
- Mixed content handling
- CDATA sections
- Namespace handling

**Key Features**:
- Tests error scenarios gracefully
- Validates boundary conditions
- Ensures robust error handling
- Tests XML parsing edge cases

### 3. `TestDataPerformanceTest.java`
**Purpose**: Performance and scalability testing
**Coverage**:
- Large dataset performance (100 employees, 50 projects, 200 books)
- Memory usage optimization
- Concurrent transformation handling
- Repeated transformation performance
- Performance consistency validation

**Key Features**:
- Generates large XML datasets dynamically
- Measures transformation time and memory usage
- Tests concurrent execution
- Validates performance thresholds

### 4. `TestDataTestSuite.java`
**Purpose**: Test suite runner and documentation
**Coverage**:
- Test suite setup validation
- Configuration and data file validation
- Comprehensive documentation
- Troubleshooting guide

## 🚀 Running the Tests

### Prerequisites
- Java 17 or higher
- Spring Boot 3.x
- Maven 3.6+
- Sufficient memory (recommended: 2GB+)

### Complete Test Suite
```bash
# Run all tests
mvn test

# Run specific test suite
mvn test -Dtest=TestDataTestSuite
```

### Individual Test Categories
```bash
# Basic integration tests
mvn test -Dtest=TestDataIntegrationTest

# Edge case tests
mvn test -Dtest=TestDataEdgeCaseTest

# Performance tests
mvn test -Dtest=TestDataPerformanceTest
```

### Specific Test Methods
```bash
# Run specific test method
mvn test -Dtest=TestDataIntegrationTest#testSimplePersonTransformation

# Run multiple specific tests
mvn test -Dtest=TestDataIntegrationTest#testSimplePersonTransformation,testSimpleOrderTransformation
```

### Test with Debug Logging
```bash
# Enable debug logging
mvn test -Dlogging.level.com.bitsevn.transformer=DEBUG
```

## 📊 Test Coverage

### Test Data Files Covered
1. **`simple-person.xml`** - Basic person data with different data types
2. **`simple-order.xml`** - Simple order with nested structure creation
3. **`company-employees.xml`** - Company data with employee arrays
4. **`company-offices.xml`** - Company office locations with addresses
5. **`company-projects.xml`** - Company projects with attributes
6. **`complex-order.xml`** - Advanced e-commerce order
7. **`library-books.xml`** - Library catalog with metadata

### Configuration Files Tested
1. **`simple-person-config.json`** - Basic property mapping
2. **`simple-order-config.json`** - Nested JSON structure
3. **`company-employees-config.json`** - Array mapping approaches
4. **`company-offices-config.json`** - Complex nested mapping
5. **`company-projects-config.json`** - Attribute extraction
6. **`complex-order-config.json`** - Deep nested structures
7. **`library-books-config.json`** - Complex metadata mapping

### Functionality Validated
- ✅ Property mapping with different names
- ✅ Depth variations (flat to nested, nested to flat)
- ✅ Array handling (simple, inline, readable)
- ✅ XML attribute extraction
- ✅ Data type conversion and validation
- ✅ Transformation rules (uppercase, lowercase, trim)
- ✅ Default values and error handling
- ✅ Nested property mapping
- ✅ Performance with large datasets
- ✅ Concurrent transformation handling
- ✅ Memory usage optimization

## 🔍 Test Scenarios

### Basic Transformation Scenarios
1. **Simple Person**: Basic property mapping with data type conversion
2. **Simple Order**: Nested JSON structure creation
3. **Company Employees**: Array mapping with multiple approaches
4. **Company Offices**: Complex nested property mapping
5. **Company Projects**: Attribute extraction and project mapping
6. **Complex Order**: Deep nested structure mapping
7. **Library Books**: Complex metadata and classification

### Edge Case Scenarios
1. **Empty XML**: Handle XML with no content
2. **Missing Fields**: Graceful handling of missing data
3. **Large Numbers**: Handle extreme numeric values
4. **Special Characters**: XML entity handling
5. **Malformed XML**: Error handling for invalid XML
6. **Null Input**: Validation of null inputs
7. **Empty Strings**: Validation of empty inputs
8. **Whitespace Only**: Validation of whitespace inputs
9. **Invalid Configs**: Handling of missing configurations
10. **No Mappings**: Minimal configuration handling
11. **Attributes Only**: XML with only attributes
12. **Mixed Content**: Elements and text content
13. **CDATA Sections**: CDATA content handling
14. **Namespaces**: Namespaced XML handling

### Performance Scenarios
1. **Large Datasets**: 100+ employees, 50+ projects, 200+ books
2. **Memory Usage**: Monitor memory consumption
3. **Concurrent Execution**: Multiple simultaneous transformations
4. **Repeated Operations**: Performance consistency
5. **Scalability**: Performance with increasing data size

## 📈 Expected Results

### Basic Tests
- All property mappings should work correctly
- Data type conversions should be accurate
- Array mappings should extract proper data
- Nested structures should be created correctly
- Attributes should be extracted properly

### Edge Case Tests
- Missing fields should use default values
- Invalid XML should throw appropriate exceptions
- Null inputs should be handled gracefully
- Empty inputs should be validated properly

### Performance Tests
- Large dataset transformations should complete within time limits
- Memory usage should be reasonable (< 100MB for large datasets)
- Concurrent transformations should work correctly
- Performance should be consistent across runs

## 🛠️ Troubleshooting

### Common Issues

#### 1. Configuration Loading Failures
**Symptoms**: Tests fail with configuration not found errors
**Solutions**:
- Verify configuration files are in `src/main/resources/configs/`
- Check JSON syntax validity
- Ensure file names match configuration service expectations

#### 2. Test Data File Issues
**Symptoms**: Tests fail with file not found errors
**Solutions**:
- Verify XML files are in `src/main/resources/test-data/`
- Check XML syntax validity
- Ensure proper encoding (UTF-8)

#### 3. Performance Test Failures
**Symptoms**: Performance tests timeout or fail
**Solutions**:
- Increase JVM memory allocation: `-Xmx2g`
- Check system resources during test execution
- Verify timeout values are appropriate for test environment

#### 4. Integration Test Failures
**Symptoms**: Spring Boot context fails to load
**Solutions**:
- Verify Spring Boot dependencies are correct
- Check service dependencies are available
- Validate XML to JSON transformer configuration

#### 5. Edge Case Test Failures
**Symptoms**: Edge case tests fail unexpectedly
**Solutions**:
- Review error handling expectations
- Check exception types and messages
- Validate boundary condition handling

### Debug Information

#### Enable Debug Logging
```properties
# In application.properties or test properties
logging.level.com.bitsevn.transformer=DEBUG
logging.level.org.springframework.web=DEBUG
```

#### Check Test Output
- Review detailed error messages in test output
- Check transformation results for unexpected output
- Monitor memory usage during large dataset tests

#### Memory Issues
```bash
# Increase JVM memory for tests
mvn test -DargLine="-Xmx2g -Xms1g"
```

## 📝 Test Customization

### Adding New Test Data
1. Create XML file in `src/main/resources/test-data/`
2. Create configuration file in `src/main/resources/configs/`
3. Add test method in `TestDataIntegrationTest.java`
4. Update test suite documentation

### Modifying Test Scenarios
1. Update XML content in test methods
2. Modify expected assertions
3. Adjust performance thresholds
4. Update edge case scenarios

### Performance Tuning
1. Adjust dataset sizes in performance tests
2. Modify timeout values for different environments
3. Update memory usage thresholds
4. Customize concurrent test parameters

## 🎯 Best Practices

### Test Design
- Keep tests focused and specific
- Use descriptive test method names
- Include comprehensive assertions
- Handle both positive and negative scenarios

### Performance Testing
- Use realistic dataset sizes
- Set appropriate timeout values
- Monitor memory usage
- Test concurrent scenarios

### Error Handling
- Test both expected and unexpected errors
- Validate error messages and types
- Ensure graceful degradation
- Test boundary conditions

### Documentation
- Keep test documentation up to date
- Include troubleshooting information
- Document test scenarios and expected results
- Provide usage examples

## 📚 Additional Resources

### Related Documentation
- [Test Data Files](../main/resources/test-data/README.md)
- [Configuration Files](../main/resources/configs/README.md)
- [Main Application README](../../README.md)

### External Resources
- [JUnit 5 Documentation](https://junit.org/junit5/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Maven Testing](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)

### Support
- Check test output for detailed error messages
- Review transformation results for unexpected output
- Enable debug logging for additional information
- Monitor system resources during performance tests
