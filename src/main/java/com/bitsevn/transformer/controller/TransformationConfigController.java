package com.bitsevn.transformer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.bitsevn.transformer.service.TransformationConfigService;

/**
 * REST Controller for managing TransformationConfig entities in MongoDB
 * Provides comprehensive CRUD operations and search capabilities
 * Exception handling is managed globally by GlobalExceptionHandler
 */
@RestController
@RequestMapping("/api/transformation-configs")
public class TransformationConfigController {

    @Autowired
    private TransformationConfigService transformationConfigService;

    /**
     * Create a new TransformationConfig
     * POST /api/transformation-configs
     */
    @PostMapping
    public ResponseEntity<?> createConfiguration(@RequestBody TransformationConfig config) {
        try {
            TransformationConfig savedConfig = transformationConfigService.createConfiguration(config);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "success", true,
                    "message", "Configuration created successfully",
                    "config", Map.of(
                        "id", savedConfig.getId(),
                        "name", savedConfig.getName(),
                        "version", savedConfig.getVersion(),
                        "description", savedConfig.getDescription()
                    )
                ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "suggestion", "Use PUT /api/transformation-configs/{name} to update existing configuration"
                ));
        }
    }

    /**
     * Get all TransformationConfig entities
     * GET /api/transformation-configs
     */
    @GetMapping
    public ResponseEntity<?> getAllConfigurations() {
        List<TransformationConfig> configs = transformationConfigService.getAllConfigurations();
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "totalCount", configs.size(),
            "configs", configs.stream()
                .map(config -> Map.of(
                    "id", config.getId(),
                    "name", config.getName(),
                    "version", config.getVersion(),
                    "description", config.getDescription(),
                    "mappingsCount", config.getUnifiedMappings() != null ? config.getUnifiedMappings().size() : 0
                ))
                .toList()
        ));
    }

    /**
     * Get TransformationConfig by name
     * GET /api/transformation-configs/name/{name}
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getConfigurationByName(@PathVariable String name) {
        try {
            TransformationConfig config = transformationConfigService.getConfigurationByName(name);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "config", config
            ));
        } catch (TransformationConfigService.ConfigurationNotFoundException e) {
            throw e; // Let GlobalExceptionHandler handle this
        }
    }

    /**
     * Get TransformationConfig by ID
     * GET /api/transformation-configs/id/{id}
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getConfigurationById(@PathVariable String id) {
        try {
            TransformationConfig config = transformationConfigService.getConfigurationById(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "config", config
            ));
        } catch (TransformationConfigService.ConfigurationNotFoundException e) {
            throw e; // Let GlobalExceptionHandler handle this
        }
    }

    /**
     * Get TransformationConfig by version
     * GET /api/transformation-configs/version/{version}
     */
    @GetMapping("/version/{version}")
    public ResponseEntity<?> getConfigurationsByVersion(@PathVariable String version) {
        List<TransformationConfig> configs = transformationConfigService.getConfigurationsByVersion(version);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "version", version,
            "totalCount", configs.size(),
            "configs", configs.stream()
                .map(config -> Map.of(
                    "id", config.getId(),
                    "name", config.getName(),
                    "version", config.getVersion(),
                    "description", config.getDescription(),
                    "mappingsCount", config.getUnifiedMappings() != null ? config.getUnifiedMappings().size() : 0
                ))
                .toList()
        ));
    }

    /**
     * Search configurations by name containing text
     * GET /api/transformation-configs/search/name?q={query}
     */
    @GetMapping("/search/name")
    public ResponseEntity<?> searchConfigurationsByName(@RequestParam String q) {
        List<TransformationConfig> configs = transformationConfigService.searchConfigurationsByName(q);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "searchQuery", q,
            "totalCount", configs.size(),
            "configs", configs.stream()
                .map(config -> Map.of(
                    "id", config.getId(),
                    "name", config.getName(),
                    "version", config.getVersion(),
                    "description", config.getDescription(),
                    "mappingsCount", config.getUnifiedMappings() != null ? config.getUnifiedMappings().size() : 0
                ))
                .toList()
        ));
    }

    /**
     * Search configurations by description containing text
     * GET /api/transformation-configs/search/description?q={query}
     */
    @GetMapping("/search/description")
    public ResponseEntity<?> searchConfigurationsByDescription(@RequestParam String q) {
        List<TransformationConfig> configs = transformationConfigService.searchConfigurationsByDescription(q);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "searchQuery", q,
            "totalCount", configs.size(),
            "configs", configs.stream()
                .map(config -> Map.of(
                    "id", config.getId(),
                    "name", config.getName(),
                    "version", config.getVersion(),
                    "description", config.getDescription(),
                    "mappingsCount", config.getUnifiedMappings() != null ? config.getUnifiedMappings().size() : 0
                ))
                .toList()
        ));
    }

    /**
     * Get configurations with specific mapping types
     * GET /api/transformation-configs/type/{mappingType}
     */
    @GetMapping("/type/{mappingType}")
    public ResponseEntity<?> getConfigurationsByMappingType(@PathVariable String mappingType) {
        List<TransformationConfig> configs = transformationConfigService.getConfigurationsByMappingType(mappingType);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "mappingType", mappingType,
            "totalCount", configs.size(),
            "configs", configs.stream()
                .map(config -> Map.of(
                    "id", config.getId(),
                    "name", config.getName(),
                    "version", config.getVersion(),
                    "description", config.getDescription(),
                    "mappingsCount", config.getUnifiedMappings() != null ? config.getUnifiedMappings().size() : 0
                ))
                .toList()
        ));
    }

    /**
     * Update TransformationConfig by name
     * PUT /api/transformation-configs/{name}
     */
    @PutMapping("/{name}")
    public ResponseEntity<?> updateConfiguration(@PathVariable String name, @RequestBody TransformationConfig updatedConfig) {
        try {
            TransformationConfig savedConfig = transformationConfigService.updateConfigurationByName(name, updatedConfig);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Configuration updated successfully",
                "config", Map.of(
                    "id", savedConfig.getId(),
                    "name", savedConfig.getName(),
                    "version", savedConfig.getVersion(),
                    "description", savedConfig.getDescription()
                )
            ));
        } catch (TransformationConfigService.ConfigurationNotFoundException e) {
            throw e; // Let GlobalExceptionHandler handle this
        }
    }

    /**
     * Update TransformationConfig by ID
     * PUT /api/transformation-configs/id/{id}
     */
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateConfigurationById(@PathVariable String id, @RequestBody TransformationConfig updatedConfig) {
        try {
            TransformationConfig savedConfig = transformationConfigService.updateConfigurationById(id, updatedConfig);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Configuration updated successfully",
                "config", Map.of(
                    "id", savedConfig.getId(),
                    "name", savedConfig.getName(),
                    "version", savedConfig.getVersion(),
                    "description", savedConfig.getDescription()
                )
            ));
        } catch (TransformationConfigService.ConfigurationNotFoundException e) {
            throw e; // Let GlobalExceptionHandler handle this
        }
    }

    /**
     * Delete TransformationConfig by name
     * DELETE /api/transformation-configs/{name}
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteConfigurationByName(@PathVariable String name) {
        try {
            transformationConfigService.deleteConfigurationByName(name);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Configuration '" + name + "' deleted successfully"
            ));
        } catch (TransformationConfigService.ConfigurationNotFoundException e) {
            throw e; // Let GlobalExceptionHandler handle this
        }
    }

    /**
     * Delete TransformationConfig by ID
     * DELETE /api/transformation-configs/id/{id}
     */
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteConfigurationById(@PathVariable String id) {
        try {
            transformationConfigService.deleteConfigurationById(id);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Configuration with ID '" + id + "' deleted successfully"
            ));
        } catch (TransformationConfigService.ConfigurationNotFoundException e) {
            throw e; // Let GlobalExceptionHandler handle this
        }
    }

    /**
     * Get configuration statistics
     * GET /api/transformation-configs/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getConfigurationStats() {
        TransformationConfigService.ConfigurationStats stats = transformationConfigService.getConfigurationStats();
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "statistics", Map.of(
                "totalConfigurations", stats.getTotalConfigurations(),
                "arrayMappings", stats.getArrayMappings(),
                "objectMappings", stats.getObjectMappings(),
                "singleMappings", stats.getSingleMappings()
            )
        ));
    }

    /**
     * Check if configuration exists by name
     * GET /api/transformation-configs/{name}/exists
     */
    @GetMapping("/{name}/exists")
    public ResponseEntity<?> checkConfigurationExists(@PathVariable String name) {
        boolean exists = transformationConfigService.configurationExistsByName(name);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "name", name,
            "exists", exists,
            "message", exists ? "Configuration found" : "Configuration not found"
        ));
    }
}
