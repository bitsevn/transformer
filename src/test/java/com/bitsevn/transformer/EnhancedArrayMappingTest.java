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
public class EnhancedArrayMappingTest {

    private XmlToJsonTransformer transformer;
    private TransformationConfig config;

    @BeforeEach
    void setUp() {
        transformer = new XmlToJsonTransformer();
        
        // Create configuration for enhanced array mapping
        config = new TransformationConfig();
        config.setName("Enhanced Array Mapping Test");
        config.setVersion("1.0");
        
        PropertyMapping companyNameMapping = new PropertyMapping("company/name", "companyName", "string");
        config.setPropertyMappings(Arrays.asList(companyNameMapping));
    }

    @Test
    void testBasicPropertyMapping() throws Exception {
        // Test basic property mapping in arrays
        config.setArrayMappings(java.util.Map.of(
            "company/employees/employee|id:employeeId,name:fullName,position:jobTitle", "mappedEmployees"
        ));
        
        String xmlInput = "<company><name>TechCorp</name><employees><employee><id>EMP001</id><name>John Doe</name><position>Developer</position></employee></employees></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"companyName\" : \"TechCorp\""));
        assertTrue(result.contains("\"mappedEmployees\" : ["));
        assertTrue(result.contains("\"employeeId\" : \"EMP001\""));
        assertTrue(result.contains("\"fullName\" : \"John Doe\""));
        assertTrue(result.contains("\"jobTitle\" : \"Developer\""));
    }

    @Test
    void testNestedPropertyMapping() throws Exception {
        // Test nested property mapping in arrays
        config.setArrayMappings(java.util.Map.of(
            "company/offices/office|officeId:id,address/location:location,city:cityName", "mappedOffices"
        ));
        
        String xmlInput = "<company><name>TechCorp</name><offices><office><officeId>OFF001</officeId><address><location>123 Main St</location></address><city>New York</city></office></offices></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"mappedOffices\" : ["));
        assertTrue(result.contains("\"id\" : \"OFF001\""));
        assertTrue(result.contains("\"location\" : \"123 Main St\""));
        assertTrue(result.contains("\"cityName\" : \"New York\""));
    }

    @Test
    void testAttributeExtraction() throws Exception {
        // Test attribute extraction in arrays
        config.setArrayMappings(java.util.Map.of(
            "company/projects/project|projectId:id,status:projectStatus", "mappedProjects"
        ));
        
        String xmlInput = "<company><name>TechCorp</name><projects><project projectId=\"PRJ001\" status=\"ACTIVE\"><name>Website Redesign</name></project></projects></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"mappedProjects\" : ["));
        assertTrue(result.contains("\"id\" : \"PRJ001\""));
        assertTrue(result.contains("\"projectStatus\" : \"ACTIVE\""));
    }

    @Test
    void testComplexNestedMapping() throws Exception {
        // Test complex nested mapping with multiple levels
        config.setArrayMappings(java.util.Map.of(
            "company/employees/employee|id:employeeId,name:fullName,position:jobTitle,salary:annualSalary,department/deptId:deptCode,department/deptName:departmentName", "mappedEmployees"
        ));
        
        String xmlInput = "<company><name>TechCorp</name><employees><employee><id>EMP001</id><name>John Doe</name><position>Senior Developer</position><salary>85000</salary><department><deptId>DEV</deptId><deptName>Development</deptName></department></employee></employees></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"mappedEmployees\" : ["));
        assertTrue(result.contains("\"employeeId\" : \"EMP001\""));
        assertTrue(result.contains("\"fullName\" : \"John Doe\""));
        assertTrue(result.contains("\"jobTitle\" : \"Senior Developer\""));
        // Inline enhanced mapping doesn't carry data types; values are strings
        assertTrue(result.contains("\"annualSalary\" : \"85000\""));
        assertTrue(result.contains("\"deptCode\" : \"DEV\""));
        assertTrue(result.contains("\"departmentName\" : \"Development\""));
    }

    @Test
    void testMultipleArrayMappings() throws Exception {
        // Test multiple array mappings in the same configuration
        config.setArrayMappings(java.util.Map.of(
            "company/employees/employee|id:employeeId,name:fullName", "mappedEmployees",
            "company/departments/department|deptId:departmentId,deptName:name", "mappedDepartments",
            "company/offices/office|officeId:id,city:cityName", "mappedOffices"
        ));
        
        String xmlInput = "<company><name>TechCorp</name><employees><employee><id>EMP001</id><name>John Doe</name></employee></employees><departments><department><deptId>DEV</deptId><deptName>Development</deptName></department></departments><offices><office><officeId>OFF001</officeId><city>New York</city></office></offices></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"mappedEmployees\" : ["));
        assertTrue(result.contains("\"mappedDepartments\" : ["));
        assertTrue(result.contains("\"mappedOffices\" : ["));
        assertTrue(result.contains("\"employeeId\" : \"EMP001\""));
        assertTrue(result.contains("\"departmentId\" : \"DEV\""));
        assertTrue(result.contains("\"id\" : \"OFF001\""));
    }

    @Test
    void testMixedArrayMappings() throws Exception {
        // Test mixing simple and enhanced array mappings
        config.setArrayMappings(java.util.Map.of(
            "company/employees/employee", "employees", // Simple mapping
            "company/employees/employee|id:employeeId,name:fullName,position:jobTitle", "mappedEmployees" // Enhanced mapping
        ));
        
        String xmlInput = "<company><name>TechCorp</name><employees><employee><id>EMP001</id><name>John Doe</name><position>Developer</position></employee></employees></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"employees\" : ["));
        assertTrue(result.contains("\"mappedEmployees\" : ["));
        // Simple mapping should contain the full element
        assertTrue(result.contains("\"id\" : \"EMP001\""));
        // Enhanced mapping should contain mapped properties
        assertTrue(result.contains("\"employeeId\" : \"EMP001\""));
        assertTrue(result.contains("\"fullName\" : \"John Doe\""));
        assertTrue(result.contains("\"jobTitle\" : \"Developer\""));
    }

    @Test
    void testEmptyArrayMapping() throws Exception {
        // Test empty array mapping
        config.setArrayMappings(java.util.Map.of(
            "company/employees/employee|id:employeeId,name:fullName", "mappedEmployees"
        ));
        
        String xmlInput = "<company><name>TechCorp</name><employees></employees></company>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"companyName\" : \"TechCorp\""));
        // Should not contain mappedEmployees if no employees exist
        assertTrue(!result.contains("\"mappedEmployees\"") || result.contains("\"mappedEmployees\" : []"));
    }
}
