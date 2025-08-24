package com.bitsevn.transformer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.repository.TransformationConfigRepository;

/**
 * Service class for managing TransformationConfig business logic
 * Acts as an intermediary between controllers and repository
 */
@Service
public class TransformationConfigService {

    @Autowired
    private TransformationConfigRepository repository;

    /**
     * Create a new TransformationConfig
     */
    public TransformationConfig createConfiguration(TransformationConfig config) {
        // Check if configuration with same name already exists
        if (repository.existsByName(config.getName())) {
            throw new IllegalArgumentException("Configuration with name '" + config.getName() + "' already exists");
        }
        
        return repository.save(config);
    }

    /**
     * Get all TransformationConfig entities
     */
    public List<TransformationConfig> getAllConfigurations() {
        return repository.findAll();
    }

    /**
     * Get TransformationConfig by name
     */
    public TransformationConfig getConfigurationByName(String name) {
        Optional<TransformationConfig> configOpt = repository.findByName(name);
        if (configOpt.isPresent()) {
            return configOpt.get();
        } else {
            throw new ConfigurationNotFoundException("Configuration with name '" + name + "' not found");
        }
    }

    /**
     * Get TransformationConfig by ID
     */
    public TransformationConfig getConfigurationById(String id) {
        Optional<TransformationConfig> configOpt = repository.findById(id);
        if (configOpt.isPresent()) {
            return configOpt.get();
        } else {
            throw new ConfigurationNotFoundException("Configuration with ID '" + id + "' not found");
        }
    }

    /**
     * Get TransformationConfig by version
     */
    public List<TransformationConfig> getConfigurationsByVersion(String version) {
        return repository.findByVersion(version);
    }

    /**
     * Search configurations by name containing text
     */
    public List<TransformationConfig> searchConfigurationsByName(String query) {
        return repository.findByNameContainingIgnoreCase(query);
    }

    /**
     * Search configurations by description containing text
     */
    public List<TransformationConfig> searchConfigurationsByDescription(String query) {
        return repository.findByDescriptionContainingIgnoreCase(query);
    }

    /**
     * Get configurations with specific mapping types
     */
    public List<TransformationConfig> getConfigurationsByMappingType(String mappingType) {
        return repository.findByMappingTypeWithChildren(mappingType);
    }

    /**
     * Update TransformationConfig by name
     */
    public TransformationConfig updateConfigurationByName(String name, TransformationConfig updatedConfig) {
        TransformationConfig existingConfig = getConfigurationByName(name);
        
        // Preserve the ID and ensure name consistency
        updatedConfig.setId(existingConfig.getId());
        updatedConfig.setName(name);
        
        return repository.save(updatedConfig);
    }

    /**
     * Update TransformationConfig by ID
     */
    public TransformationConfig updateConfigurationById(String id, TransformationConfig updatedConfig) {
        // Verify the configuration exists
        getConfigurationById(id);
        
        // Preserve the ID and update other fields
        updatedConfig.setId(id);
        
        return repository.save(updatedConfig);
    }

    /**
     * Delete TransformationConfig by name
     */
    public void deleteConfigurationByName(String name) {
        if (repository.existsByName(name)) {
            repository.deleteByName(name);
        } else {
            throw new ConfigurationNotFoundException("Configuration with name '" + name + "' not found");
        }
    }

    /**
     * Delete TransformationConfig by ID
     */
    public void deleteConfigurationById(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new ConfigurationNotFoundException("Configuration with ID '" + id + "' not found");
        }
    }

    /**
     * Check if configuration exists by name
     */
    public boolean configurationExistsByName(String name) {
        return repository.existsByName(name);
    }

    /**
     * Get configuration statistics
     */
    public ConfigurationStats getConfigurationStats() {
        long totalConfigs = repository.count();
        long arrayConfigs = repository.findConfigurationsWithArrayMappings().size();
        long objectConfigs = repository.findConfigurationsWithObjectMappings().size();
        long singleConfigs = repository.findConfigurationsWithSingleMappings().size();
        
        return new ConfigurationStats(totalConfigs, arrayConfigs, objectConfigs, singleConfigs);
    }

    /**
     * Get configurations with array mappings
     */
    public List<TransformationConfig> getConfigurationsWithArrayMappings() {
        return repository.findConfigurationsWithArrayMappings();
    }

    /**
     * Get configurations with object mappings
     */
    public List<TransformationConfig> getConfigurationsWithObjectMappings() {
        return repository.findConfigurationsWithObjectMappings();
    }

    /**
     * Get configurations with single mappings
     */
    public List<TransformationConfig> getConfigurationsWithSingleMappings() {
        return repository.findConfigurationsWithSingleMappings();
    }

    /**
     * Find configurations by XML path pattern
     */
    public List<TransformationConfig> findByXmlPathPattern(String xmlPathPattern) {
        return repository.findByXmlPathPattern(xmlPathPattern);
    }

    /**
     * Find configurations by JSON path pattern
     */
    public List<TransformationConfig> findByJsonPathPattern(String jsonPathPattern) {
        return repository.findByJsonPathPattern(jsonPathPattern);
    }

    /**
     * Find configurations with transformations
     */
    public List<TransformationConfig> findConfigurationsWithTransformations() {
        return repository.findConfigurationsWithTransformations();
    }

    /**
     * Find configurations with default values
     */
    public List<TransformationConfig> findConfigurationsWithDefaultValues() {
        return repository.findConfigurationsWithDefaultValues();
    }

    /**
     * Find configurations by version and description
     */
    public List<TransformationConfig> findByVersionAndDescriptionContainingIgnoreCase(String version, String description) {
        return repository.findByVersionAndDescriptionContainingIgnoreCase(version, description);
    }

    /**
     * Count configurations by version
     */
    public long countByVersion(String version) {
        return repository.countByVersion(version);
    }

    // ==================== INNER CLASSES ====================

    /**
     * Statistics data class for configuration counts
     */
    public static class ConfigurationStats {
        private final long totalConfigurations;
        private final long arrayMappings;
        private final long objectMappings;
        private final long singleMappings;

        public ConfigurationStats(long totalConfigurations, long arrayMappings, long objectMappings, long singleMappings) {
            this.totalConfigurations = totalConfigurations;
            this.arrayMappings = arrayMappings;
            this.objectMappings = objectMappings;
            this.singleMappings = singleMappings;
        }

        public long getTotalConfigurations() { return totalConfigurations; }
        public long getArrayMappings() { return arrayMappings; }
        public long getObjectMappings() { return objectMappings; }
        public long getSingleMappings() { return singleMappings; }
    }

    /**
     * Custom exception for configuration not found
     */
    public static class ConfigurationNotFoundException extends RuntimeException {
        public ConfigurationNotFoundException(String message) {
            super(message);
        }
    }
}
