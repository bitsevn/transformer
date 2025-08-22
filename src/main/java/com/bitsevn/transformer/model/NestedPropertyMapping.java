package com.bitsevn.transformer.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NestedPropertyMapping {
    
    @JsonProperty("xmlPath")
    private String xmlPath;
    
    @JsonProperty("jsonPath")
    private String jsonPath;
    
    @JsonProperty("properties")
    private List<PropertyFieldMapping> properties;
    
    @JsonProperty("dataType")
    private String dataType;
    
    @JsonProperty("defaultValue")
    private Object defaultValue;
    
    @JsonProperty("transform")
    private String transform;
    
    // Constructors
    public NestedPropertyMapping() {}
    
    public NestedPropertyMapping(String xmlPath, String jsonPath) {
        this.xmlPath = xmlPath;
        this.jsonPath = jsonPath;
    }
    
    public NestedPropertyMapping(String xmlPath, String jsonPath, List<PropertyFieldMapping> properties) {
        this.xmlPath = xmlPath;
        this.jsonPath = jsonPath;
        this.properties = properties;
    }
    
    // Getters and Setters
    public String getXmlPath() {
        return xmlPath;
    }
    
    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
    }
    
    public String getJsonPath() {
        return jsonPath;
    }
    
    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }
    
    public List<PropertyFieldMapping> getProperties() {
        return properties;
    }
    
    public void setProperties(List<PropertyFieldMapping> properties) {
        this.properties = properties;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public Object getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public String getTransform() {
        return transform;
    }
    
    public void setTransform(String transform) {
        this.transform = transform;
    }
    
    @Override
    public String toString() {
        return "NestedPropertyMapping{" +
                "xmlPath='" + xmlPath + '\'' +
                ", jsonPath='" + jsonPath + '\'' +
                ", properties=" + properties +
                ", dataType='" + dataType + '\'' +
                ", defaultValue=" + defaultValue +
                ", transform='" + transform + '\'' +
                '}';
    }
}
