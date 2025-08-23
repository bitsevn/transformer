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

@RestController
@RequestMapping("/api/configs/file")
public class FileConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

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
}
