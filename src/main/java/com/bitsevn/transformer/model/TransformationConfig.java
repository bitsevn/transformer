package com.bitsevn.transformer.model;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model class representing a transformation configuration
 * Supports hierarchical mapping structures with nested children
 */
@Document(collection = "transformation_configs")
public class TransformationConfig {

    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    @JsonProperty("description")
    private String description;

    @JsonProperty("unifiedMappings")
    private List<UnifiedMapping> unifiedMappings;

    @JsonProperty("transformations")
    private Map<String, String> transformations;

    @JsonProperty("defaultValues")
    private Map<String, Object> defaultValues;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    // Default constructor
    public TransformationConfig() {}

    // Constructor with required fields
    public TransformationConfig(String name, String version, String description) {
        this.name = name;
        this.version = version;
        this.description = description;
    }

    // Constructor with all fields
    public TransformationConfig(String name, String version, String description, 
                              List<UnifiedMapping> unifiedMappings, 
                              Map<String, String> transformations,
                              Map<String, Object> defaultValues, 
                              Map<String, Object> metadata) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.unifiedMappings = unifiedMappings;
        this.transformations = transformations;
        this.defaultValues = defaultValues;
        this.metadata = metadata;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<UnifiedMapping> getUnifiedMappings() {
        return unifiedMappings;
    }

    public void setUnifiedMappings(List<UnifiedMapping> unifiedMappings) {
        this.unifiedMappings = unifiedMappings;
    }

    public Map<String, String> getTransformations() {
        return transformations;
    }

    public void setTransformations(Map<String, String> transformations) {
        this.transformations = transformations;
    }

    public Map<String, Object> getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(Map<String, Object> defaultValues) {
        this.defaultValues = defaultValues;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "TransformationConfig{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", unifiedMappings=" + unifiedMappings +
                ", transformations=" + transformations +
                ", defaultValues=" + defaultValues +
                ", metadata=" + metadata +
                '}';
    }

    /**
     * Inner class representing a unified mapping configuration
     */
    public static class UnifiedMapping {
        @JsonProperty("xmlPath")
        private String xmlPath;

        @JsonProperty("jsonPath")
        private String jsonPath;

        @JsonProperty("type")
        private String type;

        @JsonProperty("isArray")
        private Boolean isArray;

        @JsonProperty("children")
        private List<UnifiedMapping> children;

        @JsonProperty("dataType")
        private String dataType;

        @JsonProperty("required")
        private Boolean required;

        @JsonProperty("defaultValue")
        private Object defaultValue;

        @JsonProperty("transform")
        private String transform;

        @JsonProperty("description")
        private String description;

        // Default constructor
        public UnifiedMapping() {}

        // Constructor with required fields
        public UnifiedMapping(String xmlPath, String jsonPath, String type) {
            this.xmlPath = xmlPath;
            this.jsonPath = jsonPath;
            this.type = type;
        }

        // Constructor with all fields
        public UnifiedMapping(String xmlPath, String jsonPath, String type, Boolean isArray,
                            List<UnifiedMapping> children, String dataType, Boolean required,
                            Object defaultValue, String transform, String description) {
            this.xmlPath = xmlPath;
            this.jsonPath = jsonPath;
            this.type = type;
            this.isArray = isArray;
            this.children = children;
            this.dataType = dataType;
            this.required = required;
            this.defaultValue = defaultValue;
            this.transform = transform;
            this.description = description;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getIsArray() {
            return isArray;
        }

        public void setIsArray(Boolean isArray) {
            this.isArray = isArray;
        }

        public List<UnifiedMapping> getChildren() {
            return children;
        }

        public void setChildren(List<UnifiedMapping> children) {
            this.children = children;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "UnifiedMapping{" +
                    "xmlPath='" + xmlPath + '\'' +
                    ", jsonPath='" + jsonPath + '\'' +
                    ", type='" + type + '\'' +
                    ", isArray=" + isArray +
                    ", children=" + children +
                    ", dataType='" + dataType + '\'' +
                    ", required=" + required +
                    ", defaultValue=" + defaultValue +
                    ", transform='" + transform + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}
