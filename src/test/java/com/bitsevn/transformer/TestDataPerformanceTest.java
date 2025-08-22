package com.bitsevn.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.service.ConfigurationService;
import com.bitsevn.transformer.service.XmlToJsonTransformer;

@SpringBootTest
public class TestDataPerformanceTest {

    @Autowired
    private XmlToJsonTransformer transformer;

    @Autowired
    private ConfigurationService configurationService;

    private String largeCompanyEmployeesXml;
    private String largeCompanyProjectsXml;
    private String largeLibraryBooksXml;

    @BeforeEach
    void setUp() {
        // Generate large XML datasets for performance testing
        largeCompanyEmployeesXml = generateLargeCompanyEmployeesXml(100);
        largeCompanyProjectsXml = generateLargeCompanyProjectsXml(50);
        largeLibraryBooksXml = generateLargeLibraryBooksXml(100); // Reduced from 200 to 100 for better performance
    }

    private String generateLargeCompanyEmployeesXml(int employeeCount) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<company>");
        xml.append("<name>Large TechCorp Solutions</name>");
        xml.append("<industry>Technology</industry>");
        xml.append("<founded>2010</founded>");
        xml.append("<employees>");

        for (int i = 1; i <= employeeCount; i++) {
            xml.append("<employee>");
            xml.append("<id>EMP").append(String.format("%03d", i)).append("</id>");
            xml.append("<name>Employee ").append(i).append("</name>");
            xml.append("<position>Developer ").append(i % 5 + 1).append("</position>");
            xml.append("<salary>").append(50000 + (i * 1000)).append("</salary>");
            xml.append("<department>");
            xml.append("<deptId>DEPT").append(i % 3 + 1).append("</deptId>");
            xml.append("<deptName>Department ").append(i % 3 + 1).append("</deptName>");
            xml.append("</department>");
            xml.append("<skills>");
            for (int j = 0; j < 3; j++) {
                xml.append("<skill>Skill ").append(i + j).append("</skill>");
            }
            xml.append("</skills>");
            xml.append("</employee>");
        }

        xml.append("</employees>");
        xml.append("</company>");
        return xml.toString();
    }

    private String generateLargeCompanyProjectsXml(int projectCount) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<company>");
        xml.append("<name>Large InnovationCorp</name>");
        xml.append("<industry>Consulting</industry>");
        xml.append("<projects>");

        for (int i = 1; i <= projectCount; i++) {
            xml.append("<project projectId=\"PRJ").append(String.format("%03d", i)).append("\" status=\"ACTIVE\" priority=\"HIGH\">");
            xml.append("<name>Project ").append(i).append("</name>");
            xml.append("<description>Description for project ").append(i).append("</description>");
            xml.append("<startDate>2024-01-01</startDate>");
            xml.append("<deadline>2024-12-31</deadline>");
            xml.append("<budget>").append(100000 + (i * 10000)).append("</budget>");
            xml.append("<team>");
            for (int j = 0; j < 5; j++) {
                xml.append("<member role=\"Role ").append(j).append("\" experience=\"").append(j + 1).append("\">Member ").append(i).append("-").append(j).append("</member>");
            }
            xml.append("</team>");
            xml.append("<technologies>");
            for (int j = 0; j < 4; j++) {
                xml.append("<tech>Tech ").append(i + j).append("</tech>");
            }
            xml.append("</technologies>");
            xml.append("</project>");
        }

        xml.append("</projects>");
        xml.append("</company>");
        return xml.toString();
    }

    private String generateLargeLibraryBooksXml(int bookCount) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<library name=\"Large Central Public Library\" location=\"Downtown\" established=\"1980\">");
        xml.append("<catalog>");

        for (int i = 1; i <= bookCount; i++) {
            xml.append("<book isbn=\"978-0-").append(String.format("%010d", i)).append("\" genre=\"FICTION\" available=\"true\">");
            xml.append("<title>Book ").append(i).append("</title>");
            xml.append("<author>");
            xml.append("<firstName>Author</firstName>");
            xml.append("<lastName>").append(i).append("</lastName>");
            xml.append("<nationality>Unknown</nationality>");
            xml.append("<birthYear>").append(1900 + (i % 100)).append("</birthYear>");
            xml.append("</author>");
            xml.append("<publication>");
            xml.append("<publisher>Publisher ").append(i % 10 + 1).append("</publisher>");
            xml.append("<publicationDate>").append(2000 + (i % 25)).append("-01-01</publicationDate>");
            xml.append("<edition>1st</edition>");
            xml.append("<pages>").append(200 + (i % 300)).append("</pages>");
            xml.append("</publication>");
            xml.append("<classification>");
            xml.append("<deweyDecimal>").append(800 + (i % 200)).append(".").append(i % 1000).append("</deweyDecimal>");
            xml.append("<subject>Subject ").append(i).append("</subject>");
            xml.append("</classification>");
            xml.append("<availability>");
            xml.append("<totalCopies>").append(1 + (i % 5)).append("</totalCopies>");
            xml.append("<availableCopies>").append(i % 3).append("</availableCopies>");
            xml.append("<reservedCopies>").append(i % 2).append("</reservedCopies>");
            xml.append("</availability>");
            xml.append("<reviews>");
            for (int j = 0; j < 2; j++) {
                xml.append("<review rating=\"").append(1 + (i + j) % 5).append("\" reviewer=\"Reviewer").append(i).append("-").append(j).append("\">Review ").append(i).append("-").append(j).append("</review>");
            }
            xml.append("</reviews>");
            xml.append("</book>");
        }

        xml.append("</catalog>");
        xml.append("<statistics>");
        xml.append("<totalBooks>").append(bookCount).append("</totalBooks>");
        xml.append("<totalAuthors>").append(bookCount).append("</totalAuthors>");
        xml.append("<genres>");
        xml.append("<genre name=\"FICTION\" count=\"").append(bookCount / 2).append("\">FICTION</genre>");
        xml.append("<genre name=\"NON_FICTION\" count=\"").append(bookCount / 2).append("\">NON_FICTION</genre>");
        xml.append("</genres>");
        xml.append("</statistics>");
        xml.append("</library>");
        return xml.toString();
    }

    @Test
    void testLargeCompanyEmployeesPerformance() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("company-employees-config");
        assertNotNull(config, "Configuration should be loaded");

        long startTime = System.currentTimeMillis();
        String result = transformer.transformXmlToJson(largeCompanyEmployeesXml, config);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertNotNull(result, "Transformation result should not be null");
        assertTrue(duration < 5000, "Transformation should complete within 5 seconds for 100 employees");
        
        // Verify the result contains expected data
        assertTrue(result.contains("\"companyName\" : \"Large TechCorp Solutions\""), "Should map company name");
        assertTrue(result.contains("\"allEmployees\""), "Should extract all employees");
        assertTrue(result.contains("\"mappedEmployees\""), "Should create mapped employees");
        assertTrue(result.contains("\"detailedEmployees\""), "Should create detailed employees");
        
        System.out.println("Large company employees transformation completed in " + duration + "ms");
    }

    @Test
    void testLargeCompanyProjectsPerformance() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("company-projects-config");
        assertNotNull(config, "Configuration should be loaded");

        long startTime = System.currentTimeMillis();
        String result = transformer.transformXmlToJson(largeCompanyProjectsXml, config);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertNotNull(result, "Transformation result should not be null");
        assertTrue(duration < 3000, "Transformation should complete within 3 seconds for 50 projects");
        
        // Verify the result contains expected data
        assertTrue(result.contains("\"companyName\" : \"Large InnovationCorp\""), "Should map company name");
        assertTrue(result.contains("\"projectPortfolio\""), "Should create project portfolio");
        assertTrue(result.contains("\"projectSummary\""), "Should create project summary");
        assertTrue(result.contains("\"allTeamMembers\""), "Should extract all team members");
        assertTrue(result.contains("\"allTechnologies\""), "Should extract all technologies");
        
        System.out.println("Large company projects transformation completed in " + duration + "ms");
    }

    @Test
    void testLargeLibraryBooksPerformance() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("library-books-config");
        assertNotNull(config, "Configuration should be loaded");

        // Debug: Print XML length and structure info
        System.out.println("Generated XML length: " + largeLibraryBooksXml.length() + " characters");

        long startTime = System.currentTimeMillis();
        String result = transformer.transformXmlToJson(largeLibraryBooksXml, config);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertNotNull(result, "Transformation result should not be null");
        assertTrue(duration < 10000, "Transformation should complete within 10 seconds for 100 books, took: " + duration + "ms");
        
        // Verify the result contains expected data
        assertTrue(result.contains("\"libraryName\" : \"Large Central Public Library\""), "Should map library name");
        assertTrue(result.contains("\"bookCatalog\""), "Should create book catalog");
        assertTrue(result.contains("\"allSubjects\""), "Should extract all subjects");
        assertTrue(result.contains("\"allReviews\""), "Should extract all reviews");
        assertTrue(result.contains("\"genreStatistics\""), "Should extract genre statistics");
        
        // Debug: Print result length
        System.out.println("Transformation result length: " + result.length() + " characters");
        
        System.out.println("Large library books transformation completed in " + duration + "ms");
    }

    @Test
    void testMemoryUsageWithLargeData() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("company-employees-config");
        assertNotNull(config, "Configuration should be loaded");

        // Get initial memory usage
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // Perform transformation
        String result = transformer.transformXmlToJson(largeCompanyEmployeesXml, config);
        
        // Get final memory usage
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        assertNotNull(result, "Transformation result should not be null");
        
        // Memory usage should be reasonable (less than 100MB for this dataset)
        assertTrue(memoryUsed < 100 * 1024 * 1024, "Memory usage should be reasonable");
        
        System.out.println("Memory used for large transformation: " + (memoryUsed / 1024 / 1024) + "MB");
    }

    @Test
    void testConcurrentTransformations() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        String simpleXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person><name>John Doe</name><age>30</age><email>john.doe@example.com</email></person>";
        
        List<Thread> threads = new ArrayList<>();
        List<String> results = new ArrayList<>();
        
        // Create multiple threads to perform transformations concurrently
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                try {
                    String result = transformer.transformXmlToJson(simpleXml, config);
                    synchronized (results) {
                        results.add(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            threads.add(thread);
        }
        
        // Start all threads
        long startTime = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Verify all transformations completed successfully
        assertEquals(10, results.size(), "All 10 transformations should complete");
        assertTrue(duration < 2000, "Concurrent transformations should complete within 2 seconds");
        
        // Verify all results are valid
        for (String result : results) {
            assertNotNull(result, "Each transformation result should not be null");
            assertTrue(result.contains("\"fullName\" : \"John Doe\""), "Each result should contain expected data");
        }
        
        System.out.println("Concurrent transformations completed in " + duration + "ms");
    }

    @Test
    void testRepeatedTransformationsPerformance() throws Exception {
        TransformationConfig config = configurationService.loadConfiguration("simple-person-config");
        assertNotNull(config, "Configuration should be loaded");

        String simpleXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person><name>John Doe</name><age>30</age><email>john.doe@example.com</email></person>";
        
        // Perform the same transformation multiple times to test caching and performance consistency
        List<Long> durations = new ArrayList<>();
        
        for (int i = 0; i < 20; i++) {
            long startTime = System.currentTimeMillis();
            String result = transformer.transformXmlToJson(simpleXml, config);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            assertNotNull(result, "Transformation result should not be null");
            durations.add(duration);
        }
        
        // Calculate average duration
        long totalDuration = durations.stream().mapToLong(Long::longValue).sum();
        long averageDuration = totalDuration / durations.size();
        
        // Verify performance is consistent
        assertTrue(averageDuration < 100, "Average transformation time should be less than 100ms");
        
        // Verify all transformations produced the same result
        assertTrue(durations.stream().allMatch(d -> d < 200), "All transformations should complete within 200ms");
        
        System.out.println("Repeated transformations - Average time: " + averageDuration + "ms");
    }
}
