package com.bitsevn.transformer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.service.ConfigurationService;
import com.bitsevn.transformer.service.XmlToJsonTransformer;

@SpringBootTest
public class TestDataIntegrationTest {

    @Autowired
    private XmlToJsonTransformer transformer;

    @Autowired
    private ConfigurationService configurationService;

    private String simplePersonXml;
    private String simpleOrderXml;
    private String companyEmployeesXml;
    private String companyOfficesXml;
    private String companyProjectsXml;
    private String complexOrderXml;
    private String libraryBooksXml;

    @BeforeEach
    void setUp() throws Exception {
        // Load all test XML files
        simplePersonXml = loadXmlFile("test-data/simple-person.xml");
        simpleOrderXml = loadXmlFile("test-data/simple-order.xml");
        companyEmployeesXml = loadXmlFile("test-data/company-employees.xml");
        companyOfficesXml = loadXmlFile("test-data/company-offices.xml");
        companyProjectsXml = loadXmlFile("test-data/company-projects.xml");
        complexOrderXml = loadXmlFile("test-data/complex-order.xml");
        libraryBooksXml = loadXmlFile("test-data/library-books.xml");
    }

    private String loadXmlFile(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    @Test
    void testSimplePersonTransformation() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(simplePersonXml, config);
        assertNotNull(result, "Transformation result should not be null");

        // Verify basic property mappings
        assertTrue(result.contains("\"fullName\" : \"John Doe\""), "Should map person/name to fullName");
        assertTrue(result.contains("\"age\" : 30"), "Should map person/age to age");
        assertTrue(result.contains("\"emailAddress\" : \"john.doe@example.com\""), "Should map person/email to emailAddress");
        assertTrue(result.contains("\"isActive\" : true"), "Should map person/active to isActive");
        assertTrue(result.contains("\"annualSalary\" : 75000"), "Should map person/salary to annualSalary");
        assertTrue(result.contains("\"dateOfBirth\" : \"1994-05-15\""), "Should map person/birthDate to dateOfBirth");

        // Verify data type conversions
        assertTrue(result.contains("\"age\" : 30"), "Age should be converted to integer");
        assertTrue(result.contains("\"isActive\" : true"), "Active should be converted to boolean");
        assertTrue(result.contains("\"annualSalary\" : 75000"), "Salary should be converted to integer");
    }

    @Test
    void testSimpleOrderTransformation() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-order-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(simpleOrderXml, config);
        assertNotNull(result, "Transformation result should not be null");

        // Verify nested JSON structure creation
        assertTrue(result.contains("\"orderNumber\" : \"ORD-001\""), "Should map order/orderId to orderNumber");
        assertTrue(result.contains("\"customer\" : {"), "Should create customer object");
        assertTrue(result.contains("\"fullName\" : \"Jane Smith\""), "Should map customerName to customer.fullName");
        assertTrue(result.contains("\"orderDetails\" : {"), "Should create orderDetails object");
        assertTrue(result.contains("\"orderDate\" : \"2024-01-15\""), "Should map orderDate to orderDetails.orderDate");
        assertTrue(result.contains("\"financial\" : {"), "Should create financial object");
        assertTrue(result.contains("\"totalAmount\" : 99.99"), "Should map totalAmount to financial.totalAmount");
        assertTrue(result.contains("\"currency\" : \"USD\""), "Should use default currency value");
        assertTrue(result.contains("\"status\" : \"PENDING\""), "Should use default status value");
    }

    @Test
    void testCompanyEmployeesTransformation() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("company-employees-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(companyEmployeesXml, config);
        assertNotNull(result, "Transformation result should not be null");

        // Verify basic property mappings
        assertTrue(result.contains("\"companyName\" : \"TechCorp Solutions\""), "Should map company/name to companyName");
        assertTrue(result.contains("\"businessType\" : \"Technology\""), "Should map company/industry to businessType");
        assertTrue(result.contains("\"foundedYear\" : 2010"), "Should map company/founded to foundedYear");

        // Verify array mappings
        assertTrue(result.contains("\"allEmployees\""), "Should extract all employees");
        assertTrue(result.contains("\"mappedEmployees\""), "Should create mapped employees");
        assertTrue(result.contains("\"allSkills\""), "Should extract all skills");

        // Verify nested property mappings
        assertTrue(result.contains("\"detailedEmployees\""), "Should create detailed employees");
        assertTrue(result.contains("\"employeeId\" : \"EMP001\""), "Should map id to employeeId");
        assertTrue(result.contains("\"fullName\" : \"John Doe\""), "Should map name to fullName");
        assertTrue(result.contains("\"jobTitle\" : \"Senior Developer\""), "Should map position to jobTitle");
        assertTrue(result.contains("\"annualSalary\" : 85000"), "Should map salary to annualSalary");
        assertTrue(result.contains("\"deptCode\" : \"DEV\""), "Should map department/deptId to deptCode");
        assertTrue(result.contains("\"departmentName\" : \"Development\""), "Should map department/deptName to departmentName");
    }

    @Test
    void testCompanyOfficesTransformation() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("company-offices-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(companyOfficesXml, config);
        assertNotNull(result, "Transformation result should not be null");

        // Verify basic property mappings
        assertTrue(result.contains("\"companyName\" : \"GlobalTech Inc\""), "Should map company/name to companyName");
        assertTrue(result.contains("\"businessType\" : \"Software Development\""), "Should map company/industry to businessType");

        // Verify nested property mappings for offices
        assertTrue(result.contains("\"officeLocations\""), "Should create office locations array");
        assertTrue(result.contains("\"locationId\" : \"OFF001\""), "Should map officeId to locationId");
        assertTrue(result.contains("\"streetAddress\" : \"123 Main Street\""), "Should map address/street to streetAddress");
        assertTrue(result.contains("\"cityName\" : \"New York\""), "Should map address/city to cityName");
        assertTrue(result.contains("\"stateCode\" : \"NY\""), "Should map address/state to stateCode");
        assertTrue(result.contains("\"postalCode\" : \"10001\""), "Should map address/zipCode to postalCode");
        assertTrue(result.contains("\"countryCode\" : \"USA\""), "Should use default country code");
        assertTrue(result.contains("\"phoneNumber\" : \"+1-555-0123\""), "Should map contact/phone to phoneNumber");
        assertTrue(result.contains("\"emailAddress\" : \"nyc@globaltech.com\""), "Should map contact/email to emailAddress");
        assertTrue(result.contains("\"maxCapacity\" : 150"), "Should map capacity to maxCapacity");
        assertTrue(result.contains("\"timeZone\" : \"EST\""), "Should map timezone to timeZone");

        // Verify international address handling (London office)
        assertTrue(result.contains("\"cityName\" : \"London\""), "Should handle international cities");
        assertTrue(result.contains("\"postalCode\" : \"SW1A 1AA\""), "Should handle different postal code formats");
    }

    @Test
    void testCompanyProjectsTransformation() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("company-projects-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(companyProjectsXml, config);
        assertNotNull(result, "Transformation result should not be null");

        // Verify basic property mappings
        assertTrue(result.contains("\"companyName\" : \"InnovationCorp\""), "Should map company/name to companyName");
        assertTrue(result.contains("\"businessType\" : \"Consulting\""), "Should map company/industry to businessType");

        // Verify nested property mappings for projects
        assertTrue(result.contains("\"projectPortfolio\""), "Should create project portfolio array");
        assertTrue(result.contains("\"projectCode\" : \"PRJ001\""), "Should map projectId to projectCode");
        assertTrue(result.contains("\"projectName\" : \"E-commerce Platform Redesign\""), "Should map name to projectName");
        assertTrue(result.contains("\"projectDescription\""), "Should map description to projectDescription");
        assertTrue(result.contains("\"startDate\" : \"2024-01-01\""), "Should map startDate to startDate");
        assertTrue(result.contains("\"endDate\" : \"2024-06-30\""), "Should map deadline to endDate");
        assertTrue(result.contains("\"projectBudget\" : 500000"), "Should map budget to projectBudget");

        // Verify array mappings
        assertTrue(result.contains("\"projectSummary\""), "Should create project summary array");
        assertTrue(result.contains("\"allTeamMembers\""), "Should extract all team members");
        assertTrue(result.contains("\"allTechnologies\""), "Should extract all technologies");
    }

    @Test
    void testComplexOrderTransformation() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("complex-order-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(complexOrderXml, config);
        assertNotNull(result, "Transformation result should not be null");

        // Verify root attribute mappings
        assertTrue(result.contains("\"orderNumber\" : \"ORD-2024-001\""), "Should map @orderId to orderNumber");
        assertTrue(result.contains("\"orderType\" : \"ONLINE\""), "Should map @orderType to orderType");
        assertTrue(result.contains("\"priorityLevel\" : \"HIGH\""), "Should map @priority to priorityLevel");

        // Verify customer information mapping
        assertTrue(result.contains("\"customerInfo\""), "Should create customer info object");
        assertTrue(result.contains("\"customerNumber\" : \"CUST-001\""), "Should map customerId to customerNumber");
        assertTrue(result.contains("\"firstName\" : \"John\""), "Should map personalInfo/firstName to firstName");
        assertTrue(result.contains("\"lastName\" : \"Doe\""), "Should map personalInfo/lastName to lastName");
        assertTrue(result.contains("\"emailAddress\" : \"john.doe@example.com\""), "Should map email with lowercase transform");
        assertTrue(result.contains("\"phoneNumber\" : \"+1-555-0123\""), "Should map phone to phoneNumber");
        assertTrue(result.contains("\"birthDate\" : \"1985-03-15\""), "Should map dateOfBirth to birthDate");

        // Verify address mapping
        assertTrue(result.contains("\"customerAddresses\""), "Should create customer addresses array");
        assertTrue(result.contains("\"addressType\" : \"BILLING\""), "Should map @type to addressType");
        assertTrue(result.contains("\"isPrimary\" : true"), "Should map @primary to isPrimary");
        assertTrue(result.contains("\"streetAddress\" : \"123 Main Street\""), "Should map street to streetAddress");
        assertTrue(result.contains("\"cityName\" : \"New York\""), "Should map city to cityName");
        assertTrue(result.contains("\"stateCode\" : \"NY\""), "Should map state to stateCode");
        assertTrue(result.contains("\"postalCode\" : \"10001\""), "Should map zipCode to postalCode");
        assertTrue(result.contains("\"countryCode\" : \"USA\""), "Should use default country code");

        // Verify item mapping
        assertTrue(result.contains("\"orderItems\""), "Should create order items array");
        assertTrue(result.contains("\"itemCode\" : \"ITEM-001\""), "Should map @itemId to itemCode");
        assertTrue(result.contains("\"itemCategory\" : \"ELECTRONICS\""), "Should map @category to itemCategory");
        assertTrue(result.contains("\"brandName\" : \"TechCorp\""), "Should map @brand to brandName");
        assertTrue(result.contains("\"itemName\" : \"Smartphone Pro Max\""), "Should map name to itemName");
        assertTrue(result.contains("\"itemDescription\""), "Should map description to itemDescription");
        assertTrue(result.contains("\"itemQuantity\" : 1"), "Should map quantity to itemQuantity");
        assertTrue(result.contains("\"warrantyMonths\" : 24"), "Should map warranty to warrantyMonths");

        // Verify array mappings
        assertTrue(result.contains("\"orderSummary\""), "Should create order summary");
        assertTrue(result.contains("\"financialSummary\""), "Should create financial summary");
        assertTrue(result.contains("\"orderUpdates\""), "Should create order updates");
    }

    @Test
    void testLibraryBooksTransformation() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("library-books-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(libraryBooksXml, config);
        assertNotNull(result, "Transformation result should not be null");

        // Verify root attribute mappings
        assertTrue(result.contains("\"libraryName\" : \"Central Public Library\""), "Should map @name to libraryName");
        assertTrue(result.contains("\"libraryLocation\" : \"Downtown\""), "Should map @location to libraryLocation");
        assertTrue(result.contains("\"establishedYear\" : 1980"), "Should map @established to establishedYear");

        // Verify book catalog mapping
        assertTrue(result.contains("\"bookCatalog\""), "Should create book catalog array");
        assertTrue(result.contains("\"isbn\" : \"978-0-7475-3269-9\""), "Should map @isbn to isbn");
        assertTrue(result.contains("\"bookGenre\" : \"FANTASY\""), "Should map @genre to bookGenre");
        assertTrue(result.contains("\"isAvailable\" : true"), "Should map @available to isAvailable");
        assertTrue(result.contains("\"bookTitle\" : \"Harry Potter and the Philosopher's Stone\""), "Should map title to bookTitle");
        assertTrue(result.contains("\"authorFirstName\" : \"J.K.\""), "Should map author/firstName to authorFirstName");
        assertTrue(result.contains("\"authorLastName\" : \"Rowling\""), "Should map author/lastName to authorLastName");
        assertTrue(result.contains("\"authorNationality\" : \"British\""), "Should map author/nationality to authorNationality");
        assertTrue(result.contains("\"authorBirthYear\" : 1965"), "Should map author/birthYear to authorBirthYear");
        assertTrue(result.contains("\"publisherName\" : \"Bloomsbury\""), "Should map publication/publisher to publisherName");
        assertTrue(result.contains("\"publicationDate\" : \"1997-06-26\""), "Should map publication/publicationDate to publicationDate");
        assertTrue(result.contains("\"editionNumber\" : \"1st\""), "Should map publication/edition to editionNumber");
        assertTrue(result.contains("\"pageCount\" : 223"), "Should map publication/pages to pageCount");
        assertTrue(result.contains("\"deweyDecimal\" : \"823.914\""), "Should map classification/deweyDecimal to deweyDecimal");
        assertTrue(result.contains("\"totalCopies\" : 5"), "Should map availability/totalCopies to totalCopies");
        assertTrue(result.contains("\"availableCopies\" : 3"), "Should map availability/availableCopies to availableCopies");
        assertTrue(result.contains("\"reservedCopies\" : 2"), "Should map availability/reservedCopies to reservedCopies");

        // Verify array mappings
        assertTrue(result.contains("\"allSubjects\""), "Should extract all subjects");
        assertTrue(result.contains("\"allReviews\""), "Should extract all reviews");
        assertTrue(result.contains("\"genreStatistics\""), "Should extract genre statistics");
        assertTrue(result.contains("\"circulationData\""), "Should extract circulation data");
    }

    @Test
    void testAllConfigurationsAreValid() throws Exception {
        // Verify all configuration files can be loaded
        List<String> configNames = List.of(
            "simple-person-config",
            "simple-order-config",
            "company-employees-config",
            "company-offices-config",
            "company-projects-config",
            "complex-order-config",
            "library-books-config"
        );

        for (String configName : configNames) {
            TransformationConfig config = configurationService.loadConfiguration(configName);
            assertNotNull(config, "Configuration " + configName + " should be loaded");
            assertNotNull(config.getName(), "Configuration " + configName + " should have a name");
            assertNotNull(config.getPropertyMappings(), "Configuration " + configName + " should have property mappings");
        }
    }

    @Test
    void testDataTypeConversions() throws Exception {
        // Test with simple person config to verify data type conversions
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        
        String result = transformer.transformXmlToJson(simplePersonXml, config);
        
        // Verify integer conversion
        assertTrue(result.contains("\"age\" : 30"), "Age should be converted to integer");
        assertTrue(result.contains("\"annualSalary\" : 75000"), "Salary should be converted to integer");
        
        // Verify boolean conversion
        assertTrue(result.contains("\"isActive\" : true"), "Active should be converted to boolean");
        
        // Verify date conversion
        assertTrue(result.contains("\"dateOfBirth\" : \"1994-05-15\""), "Birth date should be converted to string");
    }

    @Test
    void testTransformationRules() throws Exception {
        // Test transformation rules with simple person config
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        
        String result = transformer.transformXmlToJson(simplePersonXml, config);
        
        // Verify lowercase transformation for email
        assertTrue(result.contains("\"emailAddress\" : \"john.doe@example.com\""), "Email should be transformed to lowercase");
        
        // Test with simple order config for uppercase transformation
        config = configurationService.loadConfiguration("simple-order-config");
        result = transformer.transformXmlToJson(simpleOrderXml, config);
        
        // Verify uppercase transformation for status
        assertTrue(result.contains("\"status\" : \"PENDING\""), "Status should be transformed to uppercase");
    }

    @Test
    void testDefaultValues() throws Exception {
        // Test default values with simple order config
        TransformationConfig config = configurationService.loadConfiguration("simple-order-config");
        
        String result = transformer.transformXmlToJson(simpleOrderXml, config);
        
        // Verify default values are applied
        assertTrue(result.contains("\"currency\" : \"USD\""), "Should use default currency value");
        assertTrue(result.contains("\"status\" : \"PENDING\""), "Should use default status value");
        
        // Test with company offices config
        config = configurationService.loadConfiguration("company-offices-config");
        result = transformer.transformXmlToJson(companyOfficesXml, config);
        
        // Verify default country code
        assertTrue(result.contains("\"countryCode\" : \"USA\""), "Should use default country code");
    }
}
