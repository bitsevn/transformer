package com.bitsevn.transformer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.service.ConfigurationService;
import com.bitsevn.transformer.service.XmlToJsonTransformer;

@SpringBootTest
public class TestDataEdgeCaseTest {

    @Autowired
    private XmlToJsonTransformer transformer;

    @Autowired
    private ConfigurationService configurationService;

    private String emptyXml;
    private String malformedXml;
    private String missingFieldsXml;
    private String largeNumbersXml;
    private String specialCharactersXml;

    @BeforeEach
    void setUp() {
        // Create edge case XML data
        emptyXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person></person>";
        malformedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person><name>John Doe<age>30</age>";
        missingFieldsXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person><name>John Doe</name></person>";
        largeNumbersXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person><name>John Doe</name><age>999999999</age><salary>999999999999</salary></person>";
        specialCharactersXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person><name>John &amp; Jane Doe</name><email>john.doe@example.com</email><description>Special chars: &lt;&gt;&quot;&apos;</description></person>";
    }

    @Test
    void testEmptyXmlHandling() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(emptyXml, config);
        assertNotNull(result, "Transformation result should not be null");

        // Should include defaults where configured
        assertTrue(result.contains("\"isActive\" : false"), "Should use default value for isActive");
        assertTrue(result.contains("\"annualSalary\" : 0"), "Should use default value for annualSalary");
    }

    @Test
    void testMissingFieldsHandling() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(missingFieldsXml, config);
        assertNotNull(result, "Transformation result should not be null");

        // Should handle missing fields gracefully; present values mapped, defaults applied
        assertTrue(result.contains("\"fullName\" : \"John Doe\""), "Should map existing name field");
        assertTrue(result.contains("\"isActive\" : false"), "Should use default value for isActive");
        assertTrue(result.contains("\"annualSalary\" : 0"), "Should use default value for annualSalary");
    }

    @Test
    void testLargeNumbersHandling() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(largeNumbersXml, config);
        assertNotNull(result, "Transformation result should not be null");

        // Should handle large numbers correctly
        assertTrue(result.contains("\"fullName\" : \"John Doe\""), "Should map name correctly");
        assertTrue(result.contains("\"age\" : 999999999"), "Should handle large age value");
        assertTrue(
            result.contains("\"annualSalary\" : 999999999999") ||
            result.contains("\"annualSalary\" : \"999999999999\""),
            "Should handle large salary value"
        );
    }

    @Test
    void testSpecialCharactersHandling() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(specialCharactersXml, config);
        assertNotNull(result, "Transformation result should not be null");

        // Should handle special characters correctly
        assertTrue(result.contains("\"fullName\" : \"John & Jane Doe\""), "Should handle ampersand in name");
        assertTrue(result.contains("\"emailAddress\" : \"john.doe@example.com\""), "Should handle email with special chars");
        // Note: The description field is not mapped in the config, so it won't appear in output
    }

    @Test
    void testMalformedXmlHandling() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        // Should throw exception for malformed XML
        assertThrows(Exception.class, () -> {
            transformer.transformXmlToJson(malformedXml, config);
        }, "Should throw exception for malformed XML");
    }

    @Test
    void testNullXmlInput() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        // Should handle null XML input
        assertThrows(Exception.class, () -> {
            transformer.transformXmlToJson(null, config);
        }, "Should throw exception for null XML input");
    }

    @Test
    void testEmptyStringXmlInput() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        // Should handle empty string XML input
        assertThrows(Exception.class, () -> {
            transformer.transformXmlToJson("", config);
        }, "Should throw exception for empty string XML input");
    }

    @Test
    void testWhitespaceOnlyXmlInput() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        // Should handle whitespace-only XML input
        assertThrows(Exception.class, () -> {
            transformer.transformXmlToJson("   \n\t   ", config);
        }, "Should throw exception for whitespace-only XML input");
    }

    @Test
    void testInvalidConfigurationName() throws Exception {
        // Should handle invalid configuration name gracefully
        assertThrows(IOException.class, () -> {
            configurationService.loadConfiguration("non-existent-config");
        }, "Should throw IOException for non-existent configuration");
    }

    @Test
    void testConfigurationWithNoMappings() throws Exception {
        // Test with a minimal configuration that has no property mappings
        TransformationConfig minimalConfig = new TransformationConfig();
        minimalConfig.setName("Minimal Config");
        minimalConfig.setVersion("1.0");
        // Initialize empty lists to prevent NullPointerException
        minimalConfig.setPropertyMappings(new ArrayList<>());
        minimalConfig.setArrayMappings(new HashMap<>());
        minimalConfig.setNestedPropertyMappings(new ArrayList<>());

        String simplePersonXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person><name>John Doe</name><age>30</age><email>john.doe@example.com</email></person>";
        
        String result = transformer.transformXmlToJson(simplePersonXml, minimalConfig);
        assertNotNull(result, "Transformation result should not be null");
        
        // Should return empty JSON object since no mappings are defined
        // The pretty printer formats output, so check for basic structure
        assertNotNull(result, "Result should not be null");
        assertTrue(result.trim().length() > 0, "Result should not be empty string");
        
        // Verify no actual property mappings occurred
        assertFalse(result.contains("\"fullName\""), "Should not contain fullName property");
        assertFalse(result.contains("\"age\""), "Should not contain age property");
        assertFalse(result.contains("\"emailAddress\""), "Should not contain emailAddress property");
        
        // Should be a valid JSON structure (contains braces)
        assertTrue(result.contains("{") && result.contains("}"), "Should contain JSON object braces");
    }

    @Test
    void testXmlWithOnlyAttributes() throws Exception {
        String xmlWithOnlyAttributes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person id=\"123\" type=\"customer\"></person>";
        
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(xmlWithOnlyAttributes, config);
        assertNotNull(result, "Transformation result should not be null");

        // Should not error on only attributes; no specific fields required
    }

    @Test
    void testXmlWithMixedContent() throws Exception {
        String xmlWithMixedContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person><name>John Doe</name>Some text content<age>30</age></person>";
        
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(xmlWithMixedContent, config);
        assertNotNull(result, "Transformation result should not be null");

        // Should handle mixed content (elements and text)
        assertTrue(result.contains("\"fullName\" : \"John Doe\""), "Should map name correctly");
        assertTrue(result.contains("\"age\" : 30"), "Should map age correctly");
        // Missing email field is acceptable; ensure no crash
    }

    @Test
    void testXmlWithCDATA() throws Exception {
        String xmlWithCDATA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person><name><![CDATA[John & Jane Doe]]></name><age>30</age></person>";
        
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(xmlWithCDATA, config);
        assertNotNull(result, "Transformation result should not be null");

        // Should handle CDATA sections correctly
        assertTrue(result.contains("\"fullName\" : \"John & Jane Doe\""), "Should handle CDATA content correctly");
        assertTrue(result.contains("\"age\" : 30"), "Should map age correctly");
    }

    @Test
    void testXmlWithNamespaces() throws Exception {
        String xmlWithNamespaces = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns:person xmlns:ns=\"http://example.com\"><ns:name>John Doe</ns:name><ns:age>30</ns:age></ns:person>";
        
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        String result = transformer.transformXmlToJson(xmlWithNamespaces, config);
        assertNotNull(result, "Transformation result should not be null");

        // Should handle namespaced XML (though current config doesn't map namespaced paths)
        // This test verifies the transformer doesn't crash on namespaced XML
        assertNotNull(result, "Should handle namespaced XML without crashing");
    }
}
