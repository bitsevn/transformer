package com.bitsevn.transformer.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.bitsevn.transformer.model.TransformationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@Service
public class FileConfigurationService {
    
    private final ObjectMapper objectMapper;
    private final Map<String, TransformationConfig> transformationConfigCache;
    
    public FileConfigurationService() {
        this.objectMapper = new ObjectMapper();
        this.transformationConfigCache = new ConcurrentHashMap<>();
    }
    
    /**
     * Load transformation configuration from classpath
     * This method loads configurations in the new unified format as TransformationConfig
     */
    public TransformationConfig loadTransformationConfiguration(String configName) throws IOException {
        if (transformationConfigCache.containsKey(configName)) {
            return transformationConfigCache.get(configName);
        }
        
        String configPath = "configs/" + configName + ".json";
        Resource resource = new ClassPathResource(configPath);
        
        if (!resource.exists()) {
            throw new IOException("Configuration file not found: " + configPath);
        }
        
        try (InputStream inputStream = resource.getInputStream()) {
            TransformationConfig config = objectMapper.readValue(inputStream, TransformationConfig.class);
            transformationConfigCache.put(configName, config);
            return config;
        }
    }
    
    /**
     * Load transformation configuration from classpath with automatic .json extension handling
     */
    public TransformationConfig loadTransformationConfigurationAuto(String configName) throws IOException {
        // Ensure the file has .json extension
        if (!configName.endsWith(".json")) {
            configName = configName + ".json";
        }
        
        // Remove .json extension for cache key
        String cacheKey = configName.replace(".json", "");
        
        if (transformationConfigCache.containsKey(cacheKey)) {
            return transformationConfigCache.get(cacheKey);
        }
        
        String configPath = "configs/" + configName;
        Resource resource = new ClassPathResource(configPath);
        
        if (!resource.exists()) {
            throw new IOException("Configuration file not found: " + configPath);
        }
        
        try (InputStream inputStream = resource.getInputStream()) {
            TransformationConfig config = objectMapper.readValue(inputStream, TransformationConfig.class);
            transformationConfigCache.put(cacheKey, config);
            return config;
        }
    }
    
    /**
     * Load transformation configuration from JSON string
     */
    public TransformationConfig loadTransformationConfigurationFromJson(String jsonConfig) throws IOException {
        return objectMapper.readValue(jsonConfig, TransformationConfig.class);
    }
    
    /**
     * Load multiple transformation configurations from a JSON array
     */
    public List<TransformationConfig> loadTransformationConfigurationsFromJson(String jsonConfigs) throws IOException {
        CollectionType listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, TransformationConfig.class);
        return objectMapper.readValue(jsonConfigs, listType);
    }
    
    /**
     * Save transformation configuration to cache
     */
    public void cacheTransformationConfiguration(String name, TransformationConfig config) {
        transformationConfigCache.put(name, config);
    }
    
    /**
     * Get cached transformation configuration
     */
    public TransformationConfig getCachedTransformationConfiguration(String name) {
        return transformationConfigCache.get(name);
    }
    
    /**
     * Remove transformation configuration from cache
     */
    public void removeCachedTransformationConfiguration(String name) {
        transformationConfigCache.remove(name);
    }
    
    /**
     * Clear all cached configurations
     */
    public void clearCache() {
        transformationConfigCache.clear();
    }
    
    /**
     * Get all cached transformation configuration names
     */
    public List<String> getCachedConfigurationNames() {
        return List.copyOf(transformationConfigCache.keySet());
    }
    
    /**
     * Get all cached transformation configuration names
     */
    public List<String> getCachedTransformationConfigurationNames() {
        return List.copyOf(transformationConfigCache.keySet());
    }
    
    /**
     * Check if a transformation configuration file exists in the configs directory
     */
    public boolean transformationConfigurationExists(String configName) {
        // Handle both with and without .json extension
        String configPath = configName.endsWith(".json") ? 
            "configs/" + configName : 
            "configs/" + configName + ".json";
        Resource resource = new ClassPathResource(configPath);
        return resource.exists();
    }
    
    /**
     * Convert TransformationConfig to Map for backward compatibility
     * This method is useful when the transformer still expects a Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> convertToMap(TransformationConfig config) {
        return objectMapper.convertValue(config, Map.class);
    }
    
    /**
     * Convert Map to TransformationConfig
     * This method is useful for converting legacy configurations
     */
    public TransformationConfig convertFromMap(Map<String, Object> configMap) {
        return objectMapper.convertValue(configMap, TransformationConfig.class);
    }
}
