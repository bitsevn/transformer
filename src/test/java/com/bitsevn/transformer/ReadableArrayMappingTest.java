package com.bitsevn.transformer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.bitsevn.transformer.model.PropertyMapping;
import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.service.XmlToJsonTransformer;

@SpringBootTest
public class ReadableArrayMappingTest {

    private XmlToJsonTransformer transformer;
    private TransformationConfig config;

    @BeforeEach
    void setUp() {
        transformer = new XmlToJsonTransformer();
        
        // Create configuration for readable nested property mapping
        config = new TransformationConfig();
        config.setName("Readable Array Mapping Test");
        config.setVersion("1.0");
        
        PropertyMapping companyNameMapping = new PropertyMapping("company/name", "companyName", "string");
        config.setPropertyMappings(Arrays.asList(companyNameMapping));
    }

    @Test
    void testReadableEmployeeMapping() throws Exception {
        // Test readable employee mapping configuration
        config.setNestedPropertyMappings(Arrays.asList(
            createEmployeeMapping()
        ));
        
        String xmlInput = "<company><name>TechCorp</name><employees><employee><id>EMP001</id><name>John Doe</name><position>Developer</position><salary>85000</salary><department><deptId>DEV</deptId><deptName>Development</deptName></department></employee></employees></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"companyName\" : \"TechCorp\""));
        assertTrue(result.contains("\"mappedEmployees\" : ["));
        assertTrue(result.contains("\"employeeId\" : \"EMP001\""));
        assertTrue(result.contains("\"fullName\" : \"John Doe\""));
        assertTrue(result.contains("\"jobTitle\" : \"Developer\""));
        assertTrue(result.contains("\"annualSalary\" : 85000"));
        assertTrue(result.contains("\"deptCode\" : \"DEV\""));
        assertTrue(result.contains("\"departmentName\" : \"Development\""));
    }

    @Test
    void testReadableOfficeMapping() throws Exception {
        // Test readable office mapping configuration
        config.setNestedPropertyMappings(Arrays.asList(
            createOfficeMapping()
        ));
        
        String xmlInput = "<company><name>TechCorp</name><offices><office><officeId>OFF001</officeId><address><location>123 Main St</location></address><city>New York</city><country>US</country><phone>555-0123</phone></office></offices></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"mappedOffices\" : ["));
        assertTrue(result.contains("\"id\" : \"OFF001\""));
        assertTrue(result.contains("\"streetAddress\" : \"123 Main St\""));
        assertTrue(result.contains("\"cityName\" : \"New York\""));
        assertTrue(result.contains("\"countryCode\" : \"US\""));
        assertTrue(result.contains("\"contactNumber\" : \"555-0123\""));
    }

    @Test
    void testReadableProjectMapping() throws Exception {
        // Test readable project mapping configuration
        config.setNestedPropertyMappings(Arrays.asList(
            createProjectMapping()
        ));
        
        String xmlInput = "<company><name>TechCorp</name><projects><project><projectId>PRJ001</projectId><projectName>Website Redesign</projectName><startDate>2024-01-01</startDate><deadline>2024-06-30</deadline><status>active</status></project></projects></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"mappedProjects\" : ["));
        assertTrue(result.contains("\"id\" : \"PRJ001\""));
        assertTrue(result.contains("\"name\" : \"Website Redesign\""));
        assertTrue(result.contains("\"start\" : \"2024-01-01\""));
        assertTrue(result.contains("\"dueDate\" : \"2024-06-30\""));
        assertTrue(result.contains("\"projectStatus\" : \"ACTIVE\""));
    }

    @Test
    void testMultipleReadableMappings() throws Exception {
        // Test multiple readable mappings in the same configuration
        config.setNestedPropertyMappings(Arrays.asList(
            createEmployeeMapping(),
            createOfficeMapping(),
            createProjectMapping()
        ));
        
        String xmlInput = "<company><name>TechCorp</name><employees><employee><id>EMP001</id><name>John Doe</name><position>Developer</position></employee></employees><offices><office><officeId>OFF001</officeId><city>New York</city></office></offices><projects><project><projectId>PRJ001</projectId><projectName>Website Redesign</projectName></project></projects></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"mappedEmployees\" : ["));
        assertTrue(result.contains("\"mappedOffices\" : ["));
        assertTrue(result.contains("\"mappedProjects\" : ["));
        assertTrue(result.contains("\"employeeId\" : \"EMP001\""));
        assertTrue(result.contains("\"id\" : \"OFF001\""));
        assertTrue(result.contains("\"id\" : \"PRJ001\""));
    }

    @Test
    void testMixedMappingApproaches() throws Exception {
        // Test mixing readable mappings with traditional array mappings
        config.setNestedPropertyMappings(Arrays.asList(
            createEmployeeMapping()
        ));
        
        // Add traditional array mapping
        config.setArrayMappings(java.util.Map.of(
            "company/offices/office", "simpleOffices"
        ));
        
        String xmlInput = "<company><name>TechCorp</name><employees><employee><id>EMP001</id><name>John Doe</name><position>Developer</position></employee></employees><offices><office><officeId>OFF001</officeId><city>New York</city></office></offices></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"mappedEmployees\" : ["));
        assertTrue(result.contains("\"simpleOffices\" : ["));
        assertTrue(result.contains("\"employeeId\" : \"EMP001\""));
        assertTrue(result.contains("\"officeId\" : \"OFF001\""));
    }

    // Helper methods to create mapping configurations
    private com.bitsevn.transformer.model.NestedPropertyMapping createEmployeeMapping() {
        com.bitsevn.transformer.model.NestedPropertyMapping mapping = new com.bitsevn.transformer.model.NestedPropertyMapping();
        mapping.setXmlPath("company/employees/employee");
        mapping.setJsonPath("mappedEmployees");
        
        com.bitsevn.transformer.model.PropertyFieldMapping idMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("id", "employeeId", "string");
        idMapping.setRequired(true);
        
        com.bitsevn.transformer.model.PropertyFieldMapping nameMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("name", "fullName", "string");
        nameMapping.setRequired(true);
        nameMapping.setTransform("trim");
        
        com.bitsevn.transformer.model.PropertyFieldMapping positionMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("position", "jobTitle", "string");
        com.bitsevn.transformer.model.PropertyFieldMapping salaryMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("salary", "annualSalary", "integer");
        com.bitsevn.transformer.model.PropertyFieldMapping deptIdMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("department/deptId", "deptCode", "string");
        com.bitsevn.transformer.model.PropertyFieldMapping deptNameMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("department/deptName", "departmentName", "string");
        
        mapping.setProperties(Arrays.asList(idMapping, nameMapping, positionMapping, salaryMapping, deptIdMapping, deptNameMapping));
        return mapping;
    }

    private com.bitsevn.transformer.model.NestedPropertyMapping createOfficeMapping() {
        com.bitsevn.transformer.model.NestedPropertyMapping mapping = new com.bitsevn.transformer.model.NestedPropertyMapping();
        mapping.setXmlPath("company/offices/office");
        mapping.setJsonPath("mappedOffices");
        
        com.bitsevn.transformer.model.PropertyFieldMapping officeIdMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("officeId", "id", "string");
        officeIdMapping.setRequired(true);
        
        com.bitsevn.transformer.model.PropertyFieldMapping locationMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("address/location", "streetAddress", "string");
        com.bitsevn.transformer.model.PropertyFieldMapping cityMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("city", "cityName", "string");
        
        com.bitsevn.transformer.model.PropertyFieldMapping countryMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("country", "countryCode", "string");
        countryMapping.setDefaultValue("US");
        
        com.bitsevn.transformer.model.PropertyFieldMapping phoneMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("phone", "contactNumber", "string");
        
        mapping.setProperties(Arrays.asList(officeIdMapping, locationMapping, cityMapping, countryMapping, phoneMapping));
        return mapping;
    }

    private com.bitsevn.transformer.model.NestedPropertyMapping createProjectMapping() {
        com.bitsevn.transformer.model.NestedPropertyMapping mapping = new com.bitsevn.transformer.model.NestedPropertyMapping();
        mapping.setXmlPath("company/projects/project");
        mapping.setJsonPath("mappedProjects");
        
        com.bitsevn.transformer.model.PropertyFieldMapping projectIdMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("projectId", "id", "string");
        projectIdMapping.setRequired(true);
        
        com.bitsevn.transformer.model.PropertyFieldMapping projectNameMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("projectName", "name", "string");
        projectNameMapping.setRequired(true);
        
        com.bitsevn.transformer.model.PropertyFieldMapping startDateMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("startDate", "start", "date");
        com.bitsevn.transformer.model.PropertyFieldMapping deadlineMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("deadline", "dueDate", "date");
        
        com.bitsevn.transformer.model.PropertyFieldMapping statusMapping = new com.bitsevn.transformer.model.PropertyFieldMapping("status", "projectStatus", "string");
        statusMapping.setDefaultValue("ACTIVE");
        statusMapping.setTransform("uppercase");
        
        mapping.setProperties(Arrays.asList(projectIdMapping, projectNameMapping, startDateMapping, deadlineMapping, statusMapping));
        return mapping;
    }
}
