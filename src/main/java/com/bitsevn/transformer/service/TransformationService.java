package com.bitsevn.transformer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Unified XML to JSON Transformer Service
 * Handles XML to JSON transformation using a unified mapping configuration
 */
@Service
public class TransformationService {

    private final ObjectMapper objectMapper;

    public TransformationService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Transform XML to JSON using unified mapping configuration
     * 
     * @param xmlInput XML string to transform
     * @param configMap Configuration map containing unified mappings
     * @return JSON string result
     */
    public String transformXmlToJson(String xmlInput, Map<String, Object> configMap) throws Exception {
        try {
            // Parse XML
            Document xmlDoc = parseXml(xmlInput);
            
            // Extract unified mappings from config
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> unifiedMappings = (List<Map<String, Object>>) configMap.get("unifiedMappings");
            
            if (unifiedMappings == null || unifiedMappings.isEmpty()) {
                throw new IllegalArgumentException("No unified mappings found in configuration");
            }
            
            // Create root JSON object
            ObjectNode rootJson = objectMapper.createObjectNode();
            
            // Process each mapping
            for (Map<String, Object> mapping : unifiedMappings) {
                processMapping(xmlDoc, mapping, rootJson);
            }
            
            // Apply transformations if specified
            applyTransformations(rootJson, configMap);
            
            // Apply default values if specified
            applyDefaultValues(rootJson, configMap);
            
            return objectMapper.writeValueAsString(rootJson);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform XML to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Process a single mapping configuration
     */
    @SuppressWarnings("unchecked")
    private void processMapping(Document xmlDoc, Map<String, Object> mapping, ObjectNode rootJson) {
        String xmlPath = (String) mapping.get("xmlPath");
        String jsonPath = (String) mapping.get("jsonPath");
        String type = (String) mapping.get("type");
        Boolean isArray = (Boolean) mapping.get("isArray");
        List<Map<String, Object>> children = (List<Map<String, Object>>) mapping.get("children");
        
        if (xmlPath == null || jsonPath == null) {
            return; // Skip invalid mappings
        }
        
        // Evaluate XPath and get nodes
        List<Node> nodes = evaluateXPath(xmlDoc, xmlPath);
        
        if (nodes.isEmpty()) {
            return; // No matching nodes found
        }
        
        // Process based on type
        if ("single".equals(type)) {
            processSingleMapping(nodes.get(0), jsonPath, rootJson);
        } else if ("array".equals(type) || Boolean.TRUE.equals(isArray)) {
            processArrayMapping(nodes, jsonPath, rootJson);
        } else if ("object".equals(type)) {
            processObjectMapping(nodes.get(0), jsonPath, children, rootJson);
        }
    }

    /**
     * Process single value mapping
     */
    private void processSingleMapping(Node node, String jsonPath, ObjectNode rootJson) {
        String value = extractNodeValue(node);
        if (value != null) {
            setJsonValue(rootJson, jsonPath, value);
        }
    }

    /**
     * Process array mapping
     */
    private void processArrayMapping(List<Node> nodes, String jsonPath, ObjectNode rootJson) {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        
        for (Node node : nodes) {
            String value = extractNodeValue(node);
            if (value != null) {
                arrayNode.add(value);
            }
        }
        
        if (arrayNode.size() > 0) {
            setJsonValue(rootJson, jsonPath, arrayNode);
        }
    }

    /**
     * Process object mapping with nested children
     */
    @SuppressWarnings("unchecked")
    private void processObjectMapping(Node node, String jsonPath, List<Map<String, Object>> children, ObjectNode rootJson) {
        if (children == null || children.isEmpty()) {
            // Simple object mapping - extract all child elements
            Map<String, Object> objectData = extractChildElements(node);
            if (!objectData.isEmpty()) {
                setJsonValue(rootJson, jsonPath, objectData);
            }
        } else {
            // Complex object mapping with defined children
            ObjectNode objectNode = objectMapper.createObjectNode();
            
            for (Map<String, Object> childMapping : children) {
                String childXmlPath = (String) childMapping.get("xmlPath");
                String childJsonPath = (String) childMapping.get("childJsonPath");
                String childType = (String) childMapping.get("type");
                Boolean childIsArray = (Boolean) childMapping.get("isArray");
                List<Map<String, Object>> grandChildren = (List<Map<String, Object>>) childMapping.get("children");
                
                if (childXmlPath == null) continue;
                
                // Evaluate child XPath relative to current node
                List<Node> childNodes = evaluateXPath(node, childXmlPath);
                
                if (childNodes.isEmpty()) continue;
                
                if ("single".equals(childType)) {
                    String value = extractNodeValue(childNodes.get(0));
                    if (value != null) {
                        objectNode.put(childJsonPath != null ? childJsonPath : childXmlPath, value);
                    }
                } else if ("array".equals(childType) || Boolean.TRUE.equals(childIsArray)) {
                    ArrayNode childArray = objectMapper.createArrayNode();
                    for (Node childNode : childNodes) {
                        String value = extractNodeValue(childNode);
                        if (value != null) {
                            childArray.add(value);
                        }
                    }
                    if (childArray.size() > 0) {
                        objectNode.set(childJsonPath != null ? childJsonPath : childXmlPath, childArray);
                    }
                } else if ("object".equals(childType)) {
                    if (grandChildren != null && !grandChildren.isEmpty()) {
                        processObjectMapping(childNodes.get(0), null, grandChildren, objectNode);
                    } else {
                        Map<String, Object> childObjectData = extractChildElements(childNodes.get(0));
                        if (!childObjectData.isEmpty()) {
                            ObjectNode childObjectNode = objectMapper.createObjectNode();
                            for (Map.Entry<String, Object> entry : childObjectData.entrySet()) {
                                childObjectNode.put(entry.getKey(), entry.getValue().toString());
                            }
                            objectNode.set(childJsonPath != null ? childJsonPath : childXmlPath, childObjectNode);
                        }
                    }
                }
            }
            
            if (objectNode.size() > 0) {
                setJsonValue(rootJson, jsonPath, objectNode);
            }
        }
    }

    /**
     * Extract all direct child elements from a node
     */
    private Map<String, Object> extractChildElements(Node parentNode) {
        Map<String, Object> children = new HashMap<>();
        
        if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) parentNode;
            NodeList childNodes = element.getChildNodes();
            
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    String childName = child.getNodeName();
                    String childValue = extractNodeValue(child);
                    
                    if (children.containsKey(childName)) {
                        // Convert to array if duplicate child names
                        Object existing = children.get(childName);
                        if (existing instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<Object> list = (List<Object>) existing;
                            list.add(childValue);
                        } else {
                            List<Object> list = new ArrayList<>();
                            list.add(existing);
                            list.add(childValue);
                            children.put(childName, list);
                        }
                    } else {
                        children.put(childName, childValue);
                    }
                }
            }
        }
        
        return children;
    }

    /**
     * Evaluate XPath-like expression and return matching nodes from Document
     */
    private List<Node> evaluateXPath(Document xmlDoc, String xpath) {
        List<Node> result = new ArrayList<>();
        
        if (xpath == null || xpath.trim().isEmpty()) {
            return result;
        }
        
        // Simple XPath evaluation for common patterns
        if (xpath.equals("/")) {
            result.add(xmlDoc.getDocumentElement());
        } else if (xpath.startsWith("/")) {
            // Absolute path
            String[] parts = xpath.substring(1).split("/");
            Node current = xmlDoc.getDocumentElement();
            
            for (String part : parts) {
                if (current == null) break;
                
                if (part.contains("*")) {
                    // Wildcard - get all children
                    if (current.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) current;
                        NodeList children = element.getChildNodes();
                        for (int i = 0; i < children.getLength(); i++) {
                            Node child = children.item(i);
                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                result.add(child);
                            }
                        }
                        return result;
                    }
                } else {
                    // Specific element name
                    if (current.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) current;
                        NodeList children = element.getElementsByTagName(part);
                        if (children.getLength() > 0) {
                            current = children.item(0);
                        } else {
                            current = null;
                        }
                    } else {
                        current = null;
                    }
                }
            }
            
            if (current != null) {
                result.add(current);
            }
        } else {
            // Relative path - search from root
            NodeList elements = xmlDoc.getElementsByTagName(xpath);
            for (int i = 0; i < elements.getLength(); i++) {
                result.add(elements.item(i));
            }
        }
        
        return result;
    }

    /**
     * Evaluate XPath-like expression and return matching nodes from a specific Node
     */
    private List<Node> evaluateXPath(Node parentNode, String xpath) {
        List<Node> result = new ArrayList<>();
        
        if (xpath == null || xpath.trim().isEmpty() || parentNode == null) {
            return result;
        }
        
        // For relative paths from a specific node, search within that node's children
        if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) parentNode;
            NodeList children = element.getElementsByTagName(xpath);
            for (int i = 0; i < children.getLength(); i++) {
                result.add(children.item(i));
            }
        }
        
        return result;
    }

    /**
     * Extract text value from a node
     */
    private String extractNodeValue(Node node) {
        if (node == null) return null;
        
        if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE) {
            return node.getNodeValue().trim();
        } else if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            NodeList textNodes = element.getChildNodes();
            
            StringBuilder value = new StringBuilder();
            for (int i = 0; i < textNodes.getLength(); i++) {
                Node child = textNodes.item(i);
                if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
                    value.append(child.getNodeValue());
                }
            }
            
            return value.toString().trim();
        }
        
        return null;
    }

    /**
     * Set value in JSON object using dot notation path
     */
    private void setJsonValue(ObjectNode rootJson, String jsonPath, Object value) {
        if (jsonPath == null || jsonPath.trim().isEmpty()) {
            return;
        }
        
        String[] parts = jsonPath.split("\\.");
        ObjectNode current = rootJson;
        
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (!current.has(part)) {
                current.putObject(part);
            }
            current = (ObjectNode) current.get(part);
        }
        
        String lastPart = parts[parts.length - 1];
        if (value instanceof String) {
            current.put(lastPart, (String) value);
        } else if (value instanceof Integer) {
            current.put(lastPart, (Integer) value);
        } else if (value instanceof Long) {
            current.put(lastPart, (Long) value);
        } else if (value instanceof Double) {
            current.put(lastPart, (Double) value);
        } else if (value instanceof Boolean) {
            current.put(lastPart, (Boolean) value);
        } else if (value instanceof JsonNode) {
            current.set(lastPart, (JsonNode) value);
        } else {
            current.put(lastPart, value.toString());
        }
    }

    /**
     * Apply transformations to JSON result
     */
    @SuppressWarnings("unchecked")
    private void applyTransformations(ObjectNode rootJson, Map<String, Object> configMap) {
        Map<String, String> transformations = (Map<String, String>) configMap.get("transformations");
        if (transformations == null) return;
        
        // Apply transformations based on configuration
        for (Map.Entry<String, String> entry : transformations.entrySet()) {
            String fieldName = entry.getKey();
            String transformation = entry.getValue();
            
            if (rootJson.has(fieldName)) {
                JsonNode fieldValue = rootJson.get(fieldName);
                if (fieldValue.isTextual()) {
                    String value = fieldValue.asText();
                    String transformedValue = applyTransformation(value, transformation);
                    rootJson.put(fieldName, transformedValue);
                }
            }
        }
    }

    /**
     * Apply a specific transformation to a value
     */
    private String applyTransformation(String value, String transformation) {
        if (value == null) return null;
        
        switch (transformation.toLowerCase()) {
            case "uppercase":
                return value.toUpperCase();
            case "lowercase":
                return value.toLowerCase();
            case "trim":
                return value.trim();
            case "capitalize":
                if (value.length() > 0) {
                    return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
                }
                return value;
            default:
                return value;
        }
    }

    /**
     * Apply default values to JSON result
     */
    @SuppressWarnings("unchecked")
    private void applyDefaultValues(ObjectNode rootJson, Map<String, Object> configMap) {
        Map<String, Object> defaultValues = (Map<String, Object>) configMap.get("defaultValues");
        if (defaultValues == null) return;
        
        for (Map.Entry<String, Object> entry : defaultValues.entrySet()) {
            String fieldName = entry.getKey();
            Object defaultValue = entry.getValue();
            
            if (!rootJson.has(fieldName)) {
                if (defaultValue instanceof String) {
                    rootJson.put(fieldName, (String) defaultValue);
                } else if (defaultValue instanceof Integer) {
                    rootJson.put(fieldName, (Integer) defaultValue);
                } else if (defaultValue instanceof Long) {
                    rootJson.put(fieldName, (Long) defaultValue);
                } else if (defaultValue instanceof Double) {
                    rootJson.put(fieldName, (Double) defaultValue);
                } else if (defaultValue instanceof Boolean) {
                    rootJson.put(fieldName, (Boolean) defaultValue);
                } else {
                    rootJson.put(fieldName, defaultValue.toString());
                }
            }
        }
    }

    /**
     * Parse XML string to Document
     */
    private Document parseXml(String xmlInput) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        
        java.io.StringReader reader = new java.io.StringReader(xmlInput);
        javax.xml.transform.Source source = new javax.xml.transform.stream.StreamSource(reader);
        
        javax.xml.transform.dom.DOMResult result = new javax.xml.transform.dom.DOMResult();
        javax.xml.transform.Transformer transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, result);
        
        return (Document) result.getNode();
    }
}
