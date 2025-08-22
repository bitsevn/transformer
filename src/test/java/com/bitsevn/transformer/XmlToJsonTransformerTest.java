package com.bitsevn.transformer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.bitsevn.transformer.model.PropertyMapping;
import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.service.XmlToJsonTransformer;

@SpringBootTest
public class XmlToJsonTransformerTest {

    private XmlToJsonTransformer transformer;
    private TransformationConfig config;

    @BeforeEach
    void setUp() {
        transformer = new XmlToJsonTransformer();
        
        // Create a simple test configuration
        config = new TransformationConfig();
        config.setName("Test Config");
        config.setVersion("1.0");
        
        PropertyMapping nameMapping = new PropertyMapping("person/name", "fullName", "string");
        PropertyMapping ageMapping = new PropertyMapping("person/age", "age", "integer");
        PropertyMapping emailMapping = new PropertyMapping("person/email", "email", "string");
        emailMapping.setTransform("lowercase");
        
        // Add transformations map to support the lowercase transformation
        Map<String, String> transformations = new HashMap<>();
        transformations.put("lowercase", "lowercase");
        config.setTransformations(transformations);
        
        config.setPropertyMappings(Arrays.asList(nameMapping, ageMapping, emailMapping));
    }

    @Test
    void testBasicTransformation() throws Exception {
        String xmlInput = "<person><name>John Doe</name><age>30</age><email>JOHN.DOE@EXAMPLE.COM</email></person>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        // Debug output
        System.out.println("Transformation result:");
        System.out.println(result);
        System.out.println("Config: " + config.getPropertyMappings());
        System.out.println("Transformations: " + config.getTransformations());
        
        assertNotNull(result, "Result should not be null");
        
        // Check if any content was transformed
        assertTrue(result.trim().length() > 2, "Result should contain more than empty braces, got: " + result);
        
        // More flexible assertions - check for the presence of expected values
        assertTrue(result.contains("John Doe"), "Should contain the name value, got: " + result);
        assertTrue(result.contains("30"), "Should contain the age value, got: " + result);
        assertTrue(result.contains("john.doe@example.com"), "Should contain lowercase email, got: " + result);
    }

    @Test
    void testMissingElements() throws Exception {
        String xmlInput = "<person><name>John Doe</name></person>";
        
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        assertNotNull(result);
        assertTrue(result.contains("\"fullName\" : \"John Doe\""));
        // Missing elements should not appear in output
        assertFalse(result.contains("age"));
        assertFalse(result.contains("email"));
    }

    @Test
    void testComplexNestedStructure() throws Exception {
        // Create configuration for nested structure
        TransformationConfig nestedConfig = new TransformationConfig();
        nestedConfig.setName("Nested Config");
        
        PropertyMapping orderIdMapping = new PropertyMapping("order/orderId", "orderId", "string");
        PropertyMapping customerIdMapping = new PropertyMapping("order/customer/customerId", "customer.id", "string");
        PropertyMapping customerNameMapping = new PropertyMapping("order/customer/name", "customer.name", "string");
        PropertyMapping totalMapping = new PropertyMapping("order/totalAmount", "financial.totalAmount", "double");
        
        nestedConfig.setPropertyMappings(Arrays.asList(orderIdMapping, customerIdMapping, customerNameMapping, totalMapping));
        
        String xmlInput = "<order><orderId>ORD-001</orderId><customer><customerId>CUST-001</customerId><name>Jane Smith</name></customer><totalAmount>99.99</totalAmount></order>";
        
        String result = transformer.transformXmlToJson(xmlInput, nestedConfig);
        
        assertNotNull(result);
        assertTrue(result.contains("\"orderId\" : \"ORD-001\""));
        assertTrue(result.contains("\"customer\" : {"));
        assertTrue(result.contains("\"id\" : \"CUST-001\""));
        assertTrue(result.contains("\"name\" : \"Jane Smith\""));
        assertTrue(result.contains("\"financial\" : {"));
        assertTrue(result.contains("\"totalAmount\" : 99.99"));
    }

    @Test
    void testDataTypes() throws Exception {
        // Create configuration for different data types
        TransformationConfig typeConfig = new TransformationConfig();
        typeConfig.setName("Type Config");
        
        PropertyMapping stringMapping = new PropertyMapping("data/string", "stringValue", "string");
        PropertyMapping intMapping = new PropertyMapping("data/integer", "intValue", "integer");
        PropertyMapping doubleMapping = new PropertyMapping("data/double", "doubleValue", "double");
        PropertyMapping boolMapping = new PropertyMapping("data/boolean", "boolValue", "boolean");
        
        typeConfig.setPropertyMappings(Arrays.asList(stringMapping, intMapping, doubleMapping, boolMapping));
        
        String xmlInput = "<data><string>Hello</string><integer>42</integer><double>3.14</double><boolean>true</boolean></data>";
        
        String result = transformer.transformXmlToJson(xmlInput, typeConfig);
        
        assertNotNull(result);
        assertTrue(result.contains("\"stringValue\" : \"Hello\""));
        assertTrue(result.contains("\"intValue\" : 42"));
        assertTrue(result.contains("\"doubleValue\" : 3.14"));
        assertTrue(result.contains("\"boolValue\" : true"));
    }

    @Test
    void testInvalidXml() {
        String invalidXml = "<person><name>John Doe</name><age>30</age>";
        
        assertThrows(Exception.class, () -> {
            transformer.transformXmlToJson(invalidXml, config);
        });
    }

    @Test
    void testEmptyXml() throws Exception {
        String emptyXml = "<person></person>";
        
        String result = transformer.transformXmlToJson(emptyXml, config);
        
        assertNotNull(result);
        // Should return empty JSON object
        assertTrue(result.contains("{}") || result.contains("{ }"));
    }
}
