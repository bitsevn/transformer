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
public class ConfigurationService {
    
    private final ObjectMapper objectMapper;
    private final Map<String, TransformationConfig> configCache;
    
    public ConfigurationService() {
        this.objectMapper = new ObjectMapper();
        this.configCache = new ConcurrentHashMap<>();
    }
    
    /**
     * Load transformation configuration from classpath
     */
    public TransformationConfig loadConfiguration(String configName) throws IOException {
        if (configCache.containsKey(configName)) {
            return configCache.get(configName);
        }
        
        String configPath = "configs/" + configName + ".json";
        Resource resource = new ClassPathResource(configPath);
        
        if (!resource.exists()) {
            throw new IOException("Configuration file not found: " + configPath);
        }
        
        try (InputStream inputStream = resource.getInputStream()) {
            TransformationConfig config = objectMapper.readValue(inputStream, TransformationConfig.class);
            configCache.put(configName, config);
            return config;
        }
    }
    
    /**
     * Load transformation configuration from JSON string
     */
    public TransformationConfig loadConfigurationFromJson(String jsonConfig) throws IOException {
        return objectMapper.readValue(jsonConfig, TransformationConfig.class);
    }
    
    /**
     * Load multiple configurations from a JSON array
     */
    public List<TransformationConfig> loadConfigurationsFromJson(String jsonConfigs) throws IOException {
        CollectionType listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, TransformationConfig.class);
        return objectMapper.readValue(jsonConfigs, listType);
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
}
