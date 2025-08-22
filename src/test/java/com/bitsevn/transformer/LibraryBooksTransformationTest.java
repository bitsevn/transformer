package com.bitsevn.transformer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.service.ConfigurationService;
import com.bitsevn.transformer.service.XmlToJsonTransformer;

@SpringBootTest
public class LibraryBooksTransformationTest {

    @Autowired
    private XmlToJsonTransformer transformer;

    @Autowired
    private ConfigurationService configurationService;

    @Test
    void testSimpleLibraryBooksTransformation() throws Exception {
        // Load configuration
        TransformationConfig config = configurationService.loadConfiguration("library-books-config");
        assertNotNull(config, "Configuration should be loaded");

        // Create simple test XML
        String simpleLibraryXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<library name=\"Test Library\" location=\"Downtown\" established=\"1980\">" +
                "<catalog>" +
                "<book isbn=\"978-0-1234567890\" genre=\"FICTION\" available=\"true\">" +
                "<title>Test Book</title>" +
                "<author>" +
                "<firstName>Test</firstName>" +
                "<lastName>Author</lastName>" +
                "<nationality>Unknown</nationality>" +
                "<birthYear>1950</birthYear>" +
                "</author>" +
                "<publication>" +
                "<publisher>Test Publisher</publisher>" +
                "<publicationDate>2020-01-01</publicationDate>" +
                "<edition>1st</edition>" +
                "<pages>300</pages>" +
                "</publication>" +
                "<classification>" +
                "<deweyDecimal>800.123</deweyDecimal>" +
                "<subject>Test Subject</subject>" +
                "</classification>" +
                "<availability>" +
                "<totalCopies>5</totalCopies>" +
                "<availableCopies>3</availableCopies>" +
                "<reservedCopies>1</reservedCopies>" +
                "</availability>" +
                "<reviews>" +
                "<review rating=\"4\" reviewer=\"TestReviewer1\">Great book!</review>" +
                "<review rating=\"5\" reviewer=\"TestReviewer2\">Excellent!</review>" +
                "</reviews>" +
                "</book>" +
                "</catalog>" +
                "<statistics>" +
                "<totalBooks>1</totalBooks>" +
                "<totalAuthors>1</totalAuthors>" +
                "<genres>" +
                "<genre name=\"FICTION\" count=\"1\">FICTION</genre>" +
                "</genres>" +
                "</statistics>" +
                "</library>";

        // Transform
        String result = transformer.transformXmlToJson(simpleLibraryXml, config);

        // Debug output
        System.out.println("=== Simple Library Books Transformation Test ===");
        System.out.println("XML Input length: " + simpleLibraryXml.length());
        System.out.println("Result: " + result);
        System.out.println("=== End Test ===");

        // Basic assertions
        assertNotNull(result, "Transformation result should not be null");
        assertTrue(result.trim().length() > 10, "Result should contain content");

        // Check expected mappings
        assertTrue(result.contains("\"libraryName\" : \"Test Library\""), 
                  "Should map library name, got: " + result);
        assertTrue(result.contains("\"bookCatalog\""), 
                  "Should create book catalog, got: " + result);
        assertTrue(result.contains("\"allSubjects\""), 
                  "Should extract all subjects, got: " + result);
        assertTrue(result.contains("\"allReviews\""), 
                  "Should extract all reviews, got: " + result);
        assertTrue(result.contains("\"genreStatistics\""), 
                  "Should extract genre statistics, got: " + result);
    }
}
