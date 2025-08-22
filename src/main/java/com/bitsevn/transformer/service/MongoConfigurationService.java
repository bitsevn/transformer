package com.bitsevn.transformer.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.repository.TransformationConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@Service
public class MongoConfigurationService {
    
    private final MongoTemplate mongoTemplate;
    private final TransformationConfigRepository repository;
    private final ObjectMapper objectMapper;
    private final Map<String, TransformationConfig> configCache;
    
    private static final String COLLECTION_NAME = "transformation_configs";
    
    @Autowired
    public MongoConfigurationService(MongoTemplate mongoTemplate, TransformationConfigRepository repository) {
        this.mongoTemplate = mongoTemplate;
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
        this.configCache = new ConcurrentHashMap<>();
    }
    
    /**
     * Load transformation configuration from MongoDB by name
     */
    public TransformationConfig loadConfiguration(String configName) {
        // Check cache first
        if (configCache.containsKey(configName)) {
            return configCache.get(configName);
        }
        
        try {
            // Use repository for better performance
            TransformationConfig config = repository.findByName(configName).orElse(null);
            
            if (config != null) {
                // Cache the configuration
                configCache.put(configName, config);
                return config;
            }
            
            return null;
        } catch (Exception e) {
            // Log error and return null
            System.err.println("Error loading configuration from MongoDB: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Load transformation configuration from MongoDB by ID
     */
    public TransformationConfig loadConfigurationById(String configId) {
        try {
            // Query MongoDB for configuration by ID
            Query query = new Query(Criteria.where("_id").is(configId));
            TransformationConfig config = mongoTemplate.findOne(query, TransformationConfig.class, COLLECTION_NAME);
            
            if (config != null) {
                // Cache the configuration by name
                configCache.put(config.getName(), config);
                return config;
            }
            
            return null;
        } catch (Exception e) {
            // Log error and return null
            System.err.println("Error loading configuration by ID from MongoDB: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Load all transformation configurations from MongoDB
     */
    public List<TransformationConfig> loadAllConfigurations() {
        try {
            List<TransformationConfig> configs = repository.findAll();
            
            // Cache all configurations
            for (TransformationConfig config : configs) {
                configCache.put(config.getName(), config);
            }
            
            return configs;
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Error loading all configurations from MongoDB: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Load transformation configurations by criteria (e.g., version, description)
     */
    public List<TransformationConfig> loadConfigurationsByCriteria(Map<String, Object> criteria) {
        try {
            Query query = new Query();
            
            // Build query from criteria map
            for (Map.Entry<String, Object> entry : criteria.entrySet()) {
                if (entry.getValue() != null) {
                    query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
                }
            }
            
            List<TransformationConfig> configs = mongoTemplate.find(query, TransformationConfig.class, COLLECTION_NAME);
            
            // Cache configurations
            for (TransformationConfig config : configs) {
                configCache.put(config.getName(), config);
            }
            
            return configs;
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Error loading configurations by criteria from MongoDB: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Load transformation configuration from JSON string (for backward compatibility)
     */
    public TransformationConfig loadConfigurationFromJson(String jsonConfig) {
        try {
            return objectMapper.readValue(jsonConfig, TransformationConfig.class);
        } catch (Exception e) {
            // Log error and return null
            System.err.println("Error parsing JSON configuration: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Load multiple configurations from a JSON array
     */
    public List<TransformationConfig> loadConfigurationsFromJson(String jsonConfigs) {
        try {
            CollectionType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, TransformationConfig.class);
            return objectMapper.readValue(jsonConfigs, listType);
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Error parsing JSON configurations array: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Save configuration to MongoDB and cache
     */
    public boolean saveConfiguration(TransformationConfig config) {
        try {
            // Save to MongoDB using repository
            TransformationConfig savedConfig = repository.save(config);
            
            if (savedConfig != null) {
                // Update cache
                configCache.put(config.getName(), savedConfig);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            // Log error and return false
            System.err.println("Error saving configuration to MongoDB: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update existing configuration in MongoDB
     */
    public boolean updateConfiguration(String configName, TransformationConfig updatedConfig) {
        try {
            // Find existing configuration
            Query query = new Query(Criteria.where("name").is(configName));
            TransformationConfig existingConfig = mongoTemplate.findOne(query, TransformationConfig.class, COLLECTION_NAME);
            
            if (existingConfig != null) {
                // Update the configuration
                updatedConfig.setName(configName); // Ensure name consistency
                TransformationConfig savedConfig = mongoTemplate.save(updatedConfig, COLLECTION_NAME);
                
                if (savedConfig != null) {
                    // Update cache
                    configCache.put(configName, savedConfig);
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            // Log error and return false
            System.err.println("Error updating configuration in MongoDB: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete configuration from MongoDB and cache
     */
    public boolean deleteConfiguration(String configName) {
        try {
            // Remove from cache first
            configCache.remove(configName);
            
            // Delete from MongoDB
            Query query = new Query(Criteria.where("name").is(configName));
            return mongoTemplate.remove(query, TransformationConfig.class, COLLECTION_NAME).getDeletedCount() > 0;
        } catch (Exception e) {
            // Log error and return false
            System.err.println("Error deleting configuration from MongoDB: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if configuration exists in MongoDB
     */
    public boolean configurationExists(String configName) {
        try {
            return repository.existsByName(configName);
        } catch (Exception e) {
            // Log error and return false
            System.err.println("Error checking configuration existence in MongoDB: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get configuration count from MongoDB
     */
    public long getConfigurationCount() {
        try {
            return repository.count();
        } catch (Exception e) {
            // Log error and return 0
            System.err.println("Error getting configuration count from MongoDB: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Save configuration to cache
     */
    public void cacheConfiguration(String name, TransformationConfig config) {
        configCache.put(name, config);
    }
    
    /**
     * Get cached configuration
     */
    public TransformationConfig getCachedConfiguration(String name) {
        return configCache.get(name);
    }
    
    /**
     * Remove configuration from cache
     */
    public void removeCachedConfiguration(String name) {
        configCache.remove(name);
    }
    
    /**
     * Clear all cached configurations
     */
    public void clearCache() {
        configCache.clear();
    }
    
    /**
     * Get all cached configuration names
     */
    public List<String> getCachedConfigurationNames() {
        return List.copyOf(configCache.keySet());
    }
    
    /**
     * Refresh configuration from MongoDB (clear cache and reload)
     */
    public TransformationConfig refreshConfiguration(String configName) {
        // Remove from cache
        configCache.remove(configName);
        
        // Reload from MongoDB
        return loadConfiguration(configName);
    }
    
    /**
     * Refresh all configurations from MongoDB
     */
    public List<TransformationConfig> refreshAllConfigurations() {
        // Clear cache
        clearCache();
        
        // Reload all from MongoDB
        return loadAllConfigurations();
    }
    
    /**
     * Find configurations by version
     */
    public List<TransformationConfig> findConfigurationsByVersion(String version) {
        try {
            List<TransformationConfig> configs = repository.findByVersion(version);
            
            // Cache configurations
            for (TransformationConfig config : configs) {
                configCache.put(config.getName(), config);
            }
            
            return configs;
        } catch (Exception e) {
            System.err.println("Error finding configurations by version: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Find configurations by description containing text
     */
    public List<TransformationConfig> findConfigurationsByDescription(String description) {
        try {
            List<TransformationConfig> configs = repository.findByDescriptionContainingIgnoreCase(description);
            
            // Cache configurations
            for (TransformationConfig config : configs) {
                configCache.put(config.getName(), config);
            }
            
            return configs;
        } catch (Exception e) {
            System.err.println("Error finding configurations by description: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Find configurations with array mappings
     */
    public List<TransformationConfig> findConfigurationsWithArrayMappings() {
        try {
            List<TransformationConfig> configs = repository.findConfigurationsWithArrayMappings();
            
            // Cache configurations
            for (TransformationConfig config : configs) {
                configCache.put(config.getName(), config);
            }
            
            return configs;
        } catch (Exception e) {
            System.err.println("Error finding configurations with array mappings: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Find configurations with nested property mappings
     */
    public List<TransformationConfig> findConfigurationsWithNestedMappings() {
        try {
            List<TransformationConfig> configs = repository.findConfigurationsWithNestedMappings();
            
            // Cache configurations
            for (TransformationConfig config : configs) {
                configCache.put(config.getName(), config);
            }
            
            return configs;
        } catch (Exception e) {
            System.err.println("Error finding configurations with nested mappings: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Find configurations by XML path pattern
     */
    public List<TransformationConfig> findConfigurationsByXmlPathPattern(String xmlPathPattern) {
        try {
            List<TransformationConfig> configs = repository.findByXmlPathPattern(xmlPathPattern);
            
            // Cache configurations
            for (TransformationConfig config : configs) {
                configCache.put(config.getName(), config);
            }
            
            return configs;
        } catch (Exception e) {
            System.err.println("Error finding configurations by XML path pattern: " + e.getMessage());
            return List.of();
        }
    }
}
