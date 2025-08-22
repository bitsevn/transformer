package com.bitsevn.transformer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.service.ConfigurationService;
import com.bitsevn.transformer.service.MongoConfigurationService;

@RestController
@RequestMapping("/api/configs")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;
    
    @Autowired
    private MongoConfigurationService mongoConfigurationService;

    // ========================================
    // File-based Configuration Management APIs
    // ========================================
    
    /**
     * Get available configuration names
     */
    @GetMapping
    public ResponseEntity<List<String>> getAvailableConfigs() {
        List<String> configNames = configurationService.getCachedConfigurationNames();
        return ResponseEntity.ok(configNames);
    }
    
    /**
     * Get a specific configuration
     */
    @GetMapping("/{configName}")
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
    @PostMapping("/{configName}")
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
    @DeleteMapping("/{configName}")
    public ResponseEntity<?> removeConfiguration(@PathVariable String configName) {
        configurationService.removeCachedConfiguration(configName);
        return ResponseEntity.ok(Map.of("message", "Configuration removed from cache"));
    }
    
    /**
     * Clear all cached configurations
     */
    @DeleteMapping
    public ResponseEntity<?> clearAllConfigurations() {
        configurationService.clearCache();
        return ResponseEntity.ok(Map.of("message", "All configurations cleared from cache"));
    }
    
    // ========================================
    // MongoDB Configuration Management APIs
    // ========================================
    
    /**
     * Save a new configuration to MongoDB
     */
    @PostMapping("/mongo")
    public ResponseEntity<?> saveConfiguration(@RequestBody TransformationConfig config) {
        try {
            if (config.getName() == null || config.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Configuration name is required"));
            }
            
            if (mongoConfigurationService.configurationExists(config.getName())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Configuration with name '" + config.getName() + "' already exists"));
            }
            
            boolean saved = mongoConfigurationService.saveConfiguration(config);
            if (saved) {
                return ResponseEntity.ok(Map.of(
                    "message", "Configuration saved successfully",
                    "name", config.getName(),
                    "id", config.getName() // Using name as identifier
                ));
            } else {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Failed to save configuration"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to save configuration: " + e.getMessage()));
        }
    }
    
    /**
     * Get all configurations from MongoDB
     */
    @GetMapping("/mongo")
    public ResponseEntity<?> getAllMongoConfigurations() {
        try {
            List<TransformationConfig> configs = mongoConfigurationService.loadAllConfigurations();
            return ResponseEntity.ok(Map.of(
                "count", configs.size(),
                "configurations", configs
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to load configurations: " + e.getMessage()));
        }
    }
    
    /**
     * Get a specific configuration from MongoDB by name
     */
    @GetMapping("/mongo/{configName}")
    public ResponseEntity<?> getMongoConfiguration(@PathVariable String configName) {
        try {
            TransformationConfig config = mongoConfigurationService.loadConfiguration(configName);
            if (config != null) {
                return ResponseEntity.ok(config);
            } else {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Configuration '" + configName + "' not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to load configuration: " + e.getMessage()));
        }
    }
    
    /**
     * Get a configuration from MongoDB by ID
     */
    @GetMapping("/mongo/id/{configId}")
    public ResponseEntity<?> getMongoConfigurationById(@PathVariable String configId) {
        try {
            TransformationConfig config = mongoConfigurationService.loadConfigurationById(configId);
            if (config != null) {
                return ResponseEntity.ok(config);
            } else {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Configuration with ID '" + configId + "' not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to load configuration: " + e.getMessage()));
        }
    }
    
    /**
     * Update an existing configuration in MongoDB
     */
    @PutMapping("/mongo/{configName}")
    public ResponseEntity<?> updateMongoConfiguration(
            @PathVariable String configName,
            @RequestBody TransformationConfig updatedConfig) {
        try {
            if (!mongoConfigurationService.configurationExists(configName)) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Configuration '" + configName + "' not found"));
            }
            
            // Ensure the name in the path matches the configuration
            updatedConfig.setName(configName);
            
            boolean updated = mongoConfigurationService.updateConfiguration(configName, updatedConfig);
            if (updated) {
                return ResponseEntity.ok(Map.of(
                    "message", "Configuration updated successfully",
                    "name", configName
                ));
            } else {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Failed to update configuration"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to update configuration: " + e.getMessage()));
        }
    }
    
    /**
     * Delete a configuration from MongoDB
     */
    @DeleteMapping("/mongo/{configName}")
    public ResponseEntity<?> deleteMongoConfiguration(@PathVariable String configName) {
        try {
            if (!mongoConfigurationService.configurationExists(configName)) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Configuration '" + configName + "' not found"));
            }
            
            boolean deleted = mongoConfigurationService.deleteConfiguration(configName);
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "message", "Configuration deleted successfully",
                    "name", configName
                ));
            } else {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Failed to delete configuration"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to delete configuration: " + e.getMessage()));
        }
    }
    
    /**
     * Check if a configuration exists in MongoDB
     */
    @GetMapping("/mongo/{configName}/exists")
    public ResponseEntity<?> checkConfigurationExists(@PathVariable String configName) {
        try {
            boolean exists = mongoConfigurationService.configurationExists(configName);
            return ResponseEntity.ok(Map.of(
                "name", configName,
                "exists", exists
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to check configuration existence: " + e.getMessage()));
        }
    }
    
    /**
     * Get configuration count from MongoDB
     */
    @GetMapping("/mongo/count")
    public ResponseEntity<?> getConfigurationCount() {
        try {
            long count = mongoConfigurationService.getConfigurationCount();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to get configuration count: " + e.getMessage()));
        }
    }
    
    /**
     * Find configurations by version
     */
    @GetMapping("/mongo/version/{version}")
    public ResponseEntity<?> findConfigurationsByVersion(@PathVariable String version) {
        try {
            List<TransformationConfig> configs = mongoConfigurationService.findConfigurationsByVersion(version);
            return ResponseEntity.ok(Map.of(
                "version", version,
                "count", configs.size(),
                "configurations", configs
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to find configurations by version: " + e.getMessage()));
        }
    }
    
    /**
     * Find configurations by description containing text
     */
    @GetMapping("/mongo/search/description")
    public ResponseEntity<?> findConfigurationsByDescription(@RequestParam String description) {
        try {
            List<TransformationConfig> configs = mongoConfigurationService.findConfigurationsByDescription(description);
            return ResponseEntity.ok(Map.of(
                "searchTerm", description,
                "count", configs.size(),
                "configurations", configs
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to search configurations by description: " + e.getMessage()));
        }
    }
    
    /**
     * Find configurations with array mappings
     */
    @GetMapping("/mongo/with-array-mappings")
    public ResponseEntity<?> findConfigurationsWithArrayMappings() {
        try {
            List<TransformationConfig> configs = mongoConfigurationService.findConfigurationsWithArrayMappings();
            return ResponseEntity.ok(Map.of(
                "count", configs.size(),
                "configurations", configs
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to find configurations with array mappings: " + e.getMessage()));
        }
    }
    
    /**
     * Find configurations with nested property mappings
     */
    @GetMapping("/mongo/with-nested-mappings")
    public ResponseEntity<?> findConfigurationsWithNestedMappings() {
        try {
            List<TransformationConfig> configs = mongoConfigurationService.findConfigurationsWithNestedMappings();
            return ResponseEntity.ok(Map.of(
                "count", configs.size(),
                "configurations", configs
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to find configurations with nested mappings: " + e.getMessage()));
        }
    }
    
    /**
     * Find configurations by XML path pattern
     */
    @GetMapping("/mongo/search/xml-path")
    public ResponseEntity<?> findConfigurationsByXmlPathPattern(@RequestParam String xmlPathPattern) {
        try {
            List<TransformationConfig> configs = mongoConfigurationService.findConfigurationsByXmlPathPattern(xmlPathPattern);
            return ResponseEntity.ok(Map.of(
                "xmlPathPattern", xmlPathPattern,
                "count", configs.size(),
                "configurations", configs
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to search configurations by XML path pattern: " + e.getMessage()));
        }
    }
    
    /**
     * Load configuration from JSON string and save to MongoDB
     */
    @PostMapping("/mongo/from-json")
    public ResponseEntity<?> saveConfigurationFromJson(@RequestBody Map<String, String> request) {
        try {
            String jsonConfig = request.get("jsonConfig");
            if (jsonConfig == null || jsonConfig.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "JSON configuration is required"));
            }
            
            TransformationConfig config = mongoConfigurationService.loadConfigurationFromJson(jsonConfig);
            if (config == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid JSON configuration format"));
            }
            
            if (config.getName() == null || config.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Configuration name is required in JSON"));
            }
            
            if (mongoConfigurationService.configurationExists(config.getName())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Configuration with name '" + config.getName() + "' already exists"));
            }
            
            boolean saved = mongoConfigurationService.saveConfiguration(config);
            if (saved) {
                return ResponseEntity.ok(Map.of(
                    "message", "Configuration loaded from JSON and saved successfully",
                    "name", config.getName()
                ));
            } else {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Failed to save configuration"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to process JSON configuration: " + e.getMessage()));
        }
    }
    
    /**
     * Load multiple configurations from JSON array and save to MongoDB
     */
    @PostMapping("/mongo/from-json-array")
    public ResponseEntity<?> saveConfigurationsFromJsonArray(@RequestBody Map<String, String> request) {
        try {
            String jsonConfigs = request.get("jsonConfigs");
            if (jsonConfigs == null || jsonConfigs.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "JSON configurations array is required"));
            }
            
            List<TransformationConfig> configs = mongoConfigurationService.loadConfigurationsFromJson(jsonConfigs);
            if (configs.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No valid configurations found in JSON array"));
            }
            
            int savedCount = 0;
            int skippedCount = 0;
            
            for (TransformationConfig config : configs) {
                if (config.getName() != null && !config.getName().trim().isEmpty()) {
                    if (!mongoConfigurationService.configurationExists(config.getName())) {
                        if (mongoConfigurationService.saveConfiguration(config)) {
                            savedCount++;
                        }
                    } else {
                        skippedCount++;
                    }
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "message", "Configurations processed successfully",
                "totalProcessed", configs.size(),
                "saved", savedCount,
                "skipped", skippedCount
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to process JSON configurations array: " + e.getMessage()));
        }
    }
    
    /**
     * Refresh configuration from MongoDB (clear cache and reload)
     */
    @PostMapping("/mongo/{configName}/refresh")
    public ResponseEntity<?> refreshConfiguration(@PathVariable String configName) {
        try {
            TransformationConfig config = mongoConfigurationService.refreshConfiguration(configName);
            if (config != null) {
                return ResponseEntity.ok(Map.of(
                    "message", "Configuration refreshed successfully",
                    "name", configName,
                    "configuration", config
                ));
            } else {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Configuration '" + configName + "' not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to refresh configuration: " + e.getMessage()));
        }
    }
    
    /**
     * Refresh all configurations from MongoDB
     */
    @PostMapping("/mongo/refresh-all")
    public ResponseEntity<?> refreshAllConfigurations() {
        try {
            List<TransformationConfig> configs = mongoConfigurationService.refreshAllConfigurations();
            return ResponseEntity.ok(Map.of(
                "message", "All configurations refreshed successfully",
                "count", configs.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to refresh all configurations: " + e.getMessage()));
        }
    }
    
    /**
     * Get all cached configuration names
     */
    @GetMapping("/mongo/cached")
    public ResponseEntity<?> getCachedConfigurationNames() {
        try {
            List<String> cachedNames = mongoConfigurationService.getCachedConfigurationNames();
            return ResponseEntity.ok(Map.of(
                "count", cachedNames.size(),
                "cachedConfigurations", cachedNames
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to get cached configuration names: " + e.getMessage()));
        }
    }
    
    /**
     * Clear all cached configurations
     */
    @DeleteMapping("/mongo/cached")
    public ResponseEntity<?> clearMongoCache() {
        try {
            mongoConfigurationService.clearCache();
            return ResponseEntity.ok(Map.of("message", "MongoDB configuration cache cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to clear cache: " + e.getMessage()));
        }
    }
}
