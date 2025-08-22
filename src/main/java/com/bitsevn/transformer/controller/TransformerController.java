package com.bitsevn.transformer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.service.ConfigurationService;
import com.bitsevn.transformer.service.XmlToJsonTransformer;

@RestController
@RequestMapping("/api")
public class TransformerController {

    @Autowired
    private XmlToJsonTransformer transformer;
    
    @Autowired
    private ConfigurationService configurationService;

    @GetMapping("/health")
    public String health() {
        return "Transformer service is running!";
    }
    
    /**
     * Transform XML to JSON using a named configuration
     */
    @PostMapping("/transform/{configName}")
    public ResponseEntity<?> transformXmlToJson(
            @PathVariable String configName,
            @RequestBody String xmlInput) {
        try {
            TransformationConfig config = configurationService.loadConfiguration(configName);
            String jsonOutput = transformer.transformXmlToJson(xmlInput, config);
            return ResponseEntity.ok(Map.of("result", jsonOutput));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Transformation failed: " + e.getMessage()));
        }
    }
    
    /**
     * Transform XML to JSON using inline configuration
     */
    @PostMapping("/transform")
    public ResponseEntity<?> transformXmlToJsonWithConfig(
            @RequestBody Map<String, Object> request) {
        try {
            String xmlInput = (String) request.get("xml");
            String jsonConfig = (String) request.get("config");
            
            if (xmlInput == null || jsonConfig == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Both 'xml' and 'config' fields are required"));
            }
            
            TransformationConfig config = configurationService.loadConfigurationFromJson(jsonConfig);
            String jsonOutput = transformer.transformXmlToJson(xmlInput, config);
            return ResponseEntity.ok(Map.of("result", jsonOutput));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Transformation failed: " + e.getMessage()));
        }
    }
    
    /**
     * Get available configuration names
     */
    @GetMapping("/configs")
    public ResponseEntity<List<String>> getAvailableConfigs() {
        List<String> configNames = configurationService.getCachedConfigurationNames();
        return ResponseEntity.ok(configNames);
    }
    
    /**
     * Get a specific configuration
     */
    @GetMapping("/configs/{configName}")
    public ResponseEntity<?> getConfiguration(@PathVariable String configName) {
        try {
            TransformationConfig config = configurationService.loadConfiguration(configName);
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Cache a new configuration
     */
    @PostMapping("/configs/{configName}")
    public ResponseEntity<?> cacheConfiguration(
            @PathVariable String configName,
            @RequestBody TransformationConfig config) {
        try {
            configurationService.cacheConfiguration(configName, config);
            return ResponseEntity.ok(Map.of("message", "Configuration cached successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to cache configuration: " + e.getMessage()));
        }
    }
    
    /**
     * Remove a configuration from cache
     */
    @DeleteMapping("/configs/{configName}")
    public ResponseEntity<?> removeConfiguration(@PathVariable String configName) {
        configurationService.removeCachedConfiguration(configName);
        return ResponseEntity.ok(Map.of("message", "Configuration removed from cache"));
    }
    
    /**
     * Clear all cached configurations
     */
    @DeleteMapping("/configs")
    public ResponseEntity<?> clearAllConfigurations() {
        configurationService.clearCache();
        return ResponseEntity.ok(Map.of("message", "All configurations cleared from cache"));
    }
}
