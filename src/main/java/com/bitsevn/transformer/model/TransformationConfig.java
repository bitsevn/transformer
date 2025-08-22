package com.bitsevn.transformer.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransformationConfig {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("version")
    private String version;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("propertyMappings")
    private List<PropertyMapping> propertyMappings;
    
    @JsonProperty("arrayMappings")
    private Map<String, String> arrayMappings;
    
    @JsonProperty("nestedPropertyMappings")
    private List<NestedPropertyMapping> nestedPropertyMappings;
    
    @JsonProperty("typeConversions")
    private Map<String, String> typeConversions;
    
    @JsonProperty("defaultValues")
    private Map<String, Object> defaultValues;
    
    @JsonProperty("transformations")
    private Map<String, String> transformations;
    
    // Constructors
    public TransformationConfig() {}
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<PropertyMapping> getPropertyMappings() {
        return propertyMappings;
    }
    
    public void setPropertyMappings(List<PropertyMapping> propertyMappings) {
        this.propertyMappings = propertyMappings;
    }
    
    public Map<String, String> getArrayMappings() {
        return arrayMappings;
    }
    
    public void setArrayMappings(Map<String, String> arrayMappings) {
        this.arrayMappings = arrayMappings;
    }
    
    public List<NestedPropertyMapping> getNestedPropertyMappings() {
        return nestedPropertyMappings;
    }
    
    public void setNestedPropertyMappings(List<NestedPropertyMapping> nestedPropertyMappings) {
        this.nestedPropertyMappings = nestedPropertyMappings;
    }
    
    public Map<String, String> getTypeConversions() {
        return typeConversions;
    }
    
    public void setTypeConversions(Map<String, String> typeConversions) {
        this.typeConversions = typeConversions;
    }
    
    public Map<String, Object> getDefaultValues() {
        return defaultValues;
    }
    
    public void setDefaultValues(Map<String, Object> defaultValues) {
        this.defaultValues = defaultValues;
    }
    
    public Map<String, String> getTransformations() {
        return transformations;
    }
    
    public void setTransformations(Map<String, String> transformations) {
        this.transformations = transformations;
    }
    
    @Override
    public String toString() {
        return "TransformationConfig{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", propertyMappings=" + propertyMappings +
                ", arrayMappings=" + arrayMappings +
                ", nestedPropertyMappings=" + nestedPropertyMappings +
                ", typeConversions=" + typeConversions +
                ", defaultValues=" + defaultValues +
                ", transformations=" + transformations +
                '}';
    }
}
