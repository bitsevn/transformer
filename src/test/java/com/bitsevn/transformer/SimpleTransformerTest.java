package com.bitsevn.transformer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.bitsevn.transformer.model.PropertyMapping;
import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.service.XmlToJsonTransformer;

public class SimpleTransformerTest {

    @Test
    void testSimpleTransformation() throws Exception {
        // Create transformer
        XmlToJsonTransformer transformer = new XmlToJsonTransformer();
        
        // Create configuration
        TransformationConfig config = new TransformationConfig();
        config.setName("Simple Test Config");
        config.setVersion("1.0");
        
        // Create mappings
        PropertyMapping nameMapping = new PropertyMapping("person/name", "fullName", "string");
        PropertyMapping ageMapping = new PropertyMapping("person/age", "age", "integer");
        PropertyMapping emailMapping = new PropertyMapping("person/email", "email", "string");
        emailMapping.setTransform("lowercase");
        
        // Add transformations map
        Map<String, String> transformations = new HashMap<>();
        transformations.put("lowercase", "lowercase");
        config.setTransformations(transformations);
        
        config.setPropertyMappings(Arrays.asList(nameMapping, ageMapping, emailMapping));
        
        // Test XML
        String xmlInput = "<person><name>John Doe</name><age>30</age><email>JOHN.DOE@EXAMPLE.COM</email></person>";
        
        // Transform
        String result = transformer.transformXmlToJson(xmlInput, config);
        
        // Debug output
        System.out.println("=== Simple Transformer Test ===");
        System.out.println("XML Input: " + xmlInput);
        System.out.println("Result: " + result);
        System.out.println("Config mappings: " + config.getPropertyMappings());
        System.out.println("Config transformations: " + config.getTransformations());
        
        // Assertions
        assertNotNull(result, "Result should not be null");
        assertTrue(result.trim().length() > 2, "Result should contain content");
        
        // Check for expected content
        assertTrue(result.contains("John Doe"), "Should contain name");
        assertTrue(result.contains("30"), "Should contain age");
        assertTrue(result.contains("john.doe@example.com"), "Should contain lowercase email");
    }
}
