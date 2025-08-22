package com.bitsevn.transformer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PropertyFieldMapping {
    
    @JsonProperty("xmlField")
    private String xmlField;
    
    @JsonProperty("jsonField")
    private String jsonField;
    
    @JsonProperty("dataType")
    private String dataType;
    
    @JsonProperty("defaultValue")
    private Object defaultValue;
    
    @JsonProperty("transform")
    private String transform;
    
    @JsonProperty("required")
    private boolean required;
    
    // Constructors
    public PropertyFieldMapping() {}
    
    public PropertyFieldMapping(String xmlField, String jsonField) {
        this.xmlField = xmlField;
        this.jsonField = jsonField;
    }
    
    public PropertyFieldMapping(String xmlField, String jsonField, String dataType) {
        this.xmlField = xmlField;
        this.jsonField = jsonField;
        this.dataType = dataType;
    }
    
    // Getters and Setters
    public String getXmlField() {
        return xmlField;
    }
    
    public void setXmlField(String xmlField) {
        this.xmlField = xmlField;
    }
    
    public String getJsonField() {
        return jsonField;
    }
    
    public void setJsonField(String jsonField) {
        this.jsonField = jsonField;
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
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    @Override
    public String toString() {
        return "PropertyFieldMapping{" +
                "xmlField='" + xmlField + '\'' +
                ", jsonField='" + jsonField + '\'' +
                ", dataType='" + dataType + '\'' +
                ", defaultValue=" + defaultValue +
                ", transform='" + transform + '\'' +
                ", required=" + required +
                '}';
    }
}
