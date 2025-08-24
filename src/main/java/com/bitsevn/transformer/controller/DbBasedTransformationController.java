package com.bitsevn.transformer.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.service.DbConfigurationService;
import com.bitsevn.transformer.service.TransformationService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controller for testing and demonstrating the XML to JSON Transformer
 * Exception handling is managed globally by GlobalExceptionHandler
 */
@RestController
@RequestMapping("/api/transformation/db")
public class DbBasedTransformationController {

    @Autowired
    private TransformationService transformer;
    
    @Autowired
    private DbConfigurationService configurationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Transform XML to JSON using mapping configuration
     * Request body should contain the XML string to transform
     * Configuration is specified in the URL path
     */
    @PostMapping("/transform/{configName}")
    public ResponseEntity<?> transformXmlToJson(@PathVariable String configName, @RequestBody String xmlInput) {
        if (xmlInput == null || xmlInput.trim().isEmpty()) {
            throw new IllegalArgumentException("XML input is required");
        }
        
        if (configName == null || configName.trim().isEmpty()) {
            throw new IllegalArgumentException("Configuration file name is required");
        }
        
        try {
            // Load configuration using ConfigurationService
            TransformationConfig config = configurationService.loadTransformationConfigurationAuto(configName);
            
            // Convert to Map for the transformer service (for backward compatibility)
            Map<String, Object> configMap = configurationService.convertToMap(config);
            
            String result = transformer.transformXmlToJson(xmlInput, configMap);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "result", objectMapper.readValue(result, LinkedHashMap.class),
                "message", "XML transformed successfully using configuration: " + configName,
                "configUsed", configName,
                "configInfo", Map.of(
                    "name", config.getName(),
                    "version", config.getVersion(),
                    "description", config.getDescription(),
                    "mappingCount", config.getUnifiedMappings() != null ? config.getUnifiedMappings().size() : 0
                )
            ));
        } catch (Exception e) {
            // Wrap checked exceptions in RuntimeException for global handling
            throw new RuntimeException("Transformation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Health check endpoint
     */
    @PostMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "XML to JSON Transformer",
            "message", "Service is running and ready to process transformations",
            "configLocation", "classpath:configs/",
            "supportedFormats", "JSON configuration files",
            "modelTypes", "TransformationConfig"
        ));
    }
    
    /**
     * List available configuration files
     */
    @PostMapping("/configs")
    public ResponseEntity<?> listAvailableConfigs() {
        try {
            // Get cached transformation configuration names from ConfigurationService
            var transformationConfigs = configurationService.getCachedTransformationConfigurationNames();
            
            return ResponseEntity.ok(Map.of(
                "availableConfigs", Map.of(
                    "transformation", transformationConfigs
                ),
                "message", "Available configuration files in classpath:configs/",
                "note", "Use the configName parameter without .json extension",
                "totalTransformationConfigs", transformationConfigs.size()
            ));
        } catch (Exception e) {
            // Wrap checked exceptions in RuntimeException for global handling
            throw new RuntimeException("Failed to list configurations: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if a specific configuration exists
     */
    @PostMapping("/configs/{configName}/exists")
    public ResponseEntity<?> checkConfigExists(@PathVariable String configName) {
        try {
            boolean exists = configurationService.transformationConfigurationExists(configName);
            
            return ResponseEntity.ok(Map.of(
                "configName", configName,
                "exists", exists,
                "message", exists ? "Configuration found" : "Configuration not found"
            ));
        } catch (Exception e) {
            // Wrap checked exceptions in RuntimeException for global handling
            throw new RuntimeException("Failed to check configuration: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get configuration details
     */
    @PostMapping("/configs/{configName}/details")
    public ResponseEntity<?> getConfigDetails(@PathVariable String configName) {
        try {
            TransformationConfig config = configurationService.loadTransformationConfigurationAuto(configName);
            
            return ResponseEntity.ok(Map.of(
                "configName", configName,
                "config", Map.of(
                    "name", config.getName(),
                    "version", config.getVersion(),
                    "description", config.getDescription(),
                    "unifiedMappingsCount", config.getUnifiedMappings() != null ? config.getUnifiedMappings().size() : 0,
                    "transformationsCount", config.getTransformations() != null ? config.getTransformations().size() : 0,
                    "defaultValuesCount", config.getDefaultValues() != null ? config.getDefaultValues().size() : 0,
                    "metadataCount", config.getMetadata() != null ? config.getMetadata().size() : 0
                ),
                "message", "Configuration details retrieved successfully"
            ));
        } catch (Exception e) {
            // Wrap checked exceptions in RuntimeException for global handling
            throw new RuntimeException("Failed to get configuration details: " + e.getMessage(), e);
        }
    }
}
