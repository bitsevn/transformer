package com.bitsevn.transformer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PropertyMapping {
    
    @JsonProperty("xmlPath")
    private String xmlPath;
    
    @JsonProperty("jsonPath")
    private String jsonPath;
    
    @JsonProperty("dataType")
    private String dataType;
    
    @JsonProperty("defaultValue")
    private String defaultValue;
    
    @JsonProperty("required")
    private boolean required;
    
    @JsonProperty("transform")
    private String transform;
    
    // Constructors
    public PropertyMapping() {}
    
    public PropertyMapping(String xmlPath, String jsonPath) {
        this.xmlPath = xmlPath;
        this.jsonPath = jsonPath;
    }
    
    public PropertyMapping(String xmlPath, String jsonPath, String dataType) {
        this.xmlPath = xmlPath;
        this.jsonPath = jsonPath;
        this.dataType = dataType;
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
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public String getTransform() {
        return transform;
    }
    
    public void setTransform(String transform) {
        this.transform = transform;
    }
    
    @Override
    public String toString() {
        return "PropertyMapping{" +
                "xmlPath='" + xmlPath + '\'' +
                ", jsonPath='" + jsonPath + '\'' +
                ", dataType='" + dataType + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", required=" + required +
                ", transform='" + transform + '\'' +
                '}';
    }
}
