package com.bitsevn.transformer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bitsevn.transformer.service.ConfigurationService;
import com.bitsevn.transformer.service.MongoConfigurationService;

@RestController
@RequestMapping("/api/configs")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;
    
    @Autowired
    private MongoConfigurationService mongoConfigurationService;

    /**
     * Get overall configuration statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getConfigurationStats() {
        try {
            List<String> fileConfigs = configurationService.getCachedConfigurationNames();
            long mongoCount = mongoConfigurationService.getConfigurationCount();
            List<String> mongoCached = mongoConfigurationService.getCachedConfigurationNames();
            
            return ResponseEntity.ok(Map.of(
                "fileBased", Map.of(
                    "cachedCount", fileConfigs.size(),
                    "cachedNames", fileConfigs
                ),
                "mongoBased", Map.of(
                    "totalCount", mongoCount,
                    "cachedCount", mongoCached.size(),
                    "cachedNames", mongoCached
                ),
                "totalCached", fileConfigs.size() + mongoCached.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to get configuration statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "message", "Configuration service is running",
            "endpoints", Map.of(
                "fileBased", "/api/configs/file",
                "mongoBased", "/api/configs/mongo"
            )
        ));
    }
}
