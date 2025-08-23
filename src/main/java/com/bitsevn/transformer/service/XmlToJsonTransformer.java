package com.bitsevn.transformer.service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.bitsevn.transformer.model.NestedPropertyMapping;
import com.bitsevn.transformer.model.PropertyFieldMapping;
import com.bitsevn.transformer.model.PropertyMapping;
import com.bitsevn.transformer.model.TransformationConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class XmlToJsonTransformer {
    
    private final ObjectMapper objectMapper;
    private final DocumentBuilderFactory documentBuilderFactory;
    
    public XmlToJsonTransformer() {
        this.objectMapper = new ObjectMapper();
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilderFactory.setNamespaceAware(true);
    }
    
    /**
     * Transform XML to JSON based on configuration
     */
    public String transformXmlToJson(String xmlInput, TransformationConfig config) throws Exception {
        Document document = parseXml(xmlInput);
        ObjectNode rootNode = objectMapper.createObjectNode();
        
        try {
            // Process property mappings
            if (config.getPropertyMappings() != null) {
                for (PropertyMapping mapping : config.getPropertyMappings()) {
                    Object value = extractValueFromXml(document, mapping, config);
                    if (value != null) {
                        setJsonValue(rootNode, mapping.getJsonPath(), value);
                    }
                }
            }
            
            // Handle arrays with enhanced property mapping support
            if (config.getArrayMappings() != null) {
                for (Map.Entry<String, String> arrayMapping : config.getArrayMappings().entrySet()) {
                    String xmlPath = arrayMapping.getKey();
                    String jsonPath = arrayMapping.getValue();
                    
                    try {
                        // Check if this is a complex array mapping with property definitions
                        if (xmlPath.contains("|")) {
                            // Complex array mapping with property definitions
                            List<Object> arrayValues = extractComplexArrayFromXml(document, xmlPath, config);
                            if (!arrayValues.isEmpty()) {
                                setJsonValue(rootNode, jsonPath, arrayValues);
                            }
                        } else {
                            // Simple array mapping (existing behavior)
                            List<Object> arrayValues = extractArrayFromXml(document, xmlPath, config);
                            if (!arrayValues.isEmpty()) {
                                setJsonValue(rootNode, jsonPath, arrayValues);
                            }
                        }
                    } catch (Exception e) {
                        // Log error but continue processing other mappings
                        System.err.println("Error processing array mapping " + xmlPath + ": " + e.getMessage());
                    }
                }
            }
            
            // Handle nested property mappings (new structured approach)
            if (config.getNestedPropertyMappings() != null) {
                for (NestedPropertyMapping nestedMapping : config.getNestedPropertyMappings()) {
                    try {
                        List<Object> arrayValues = extractNestedPropertyArrayFromXml(document, nestedMapping, config);
                        if (!arrayValues.isEmpty()) {
                            setJsonValue(rootNode, nestedMapping.getJsonPath(), arrayValues);
                        }
                    } catch (Exception e) {
                        // Log error but continue processing other mappings
                        System.err.println("Error processing nested property mapping " + nestedMapping.getXmlPath() + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Error during XML to JSON transformation: " + e.getMessage(), e);
        }
        
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }
    
    /**
     * Extract complex array values from XML with property-to-property mapping
     * Format: "parent/child|prop1:jsonProp1,prop2:jsonProp2"
     */
    private List<Object> extractComplexArrayFromXml(Document document, String complexXmlPath, TransformationConfig config) {
        List<Object> arrayValues = new ArrayList<>();
        
        try {
            // Split the path into XML path and property mappings
            String[] parts = complexXmlPath.split("\\|");
            if (parts.length != 2) {
                System.err.println("Invalid complex XML path format: " + complexXmlPath);
                return arrayValues;
            }
            
            String xmlPath = parts[0];
            String propertyMappings = parts[1];
            
            System.out.println("DEBUG: Processing complex array path: " + xmlPath + " with mappings: " + propertyMappings);
            
            // Parse property mappings (format: "prop1:jsonProp1,prop2:jsonProp2")
            Map<String, String> propMappings = parsePropertyMappings(propertyMappings);
            
            // Get array elements
            NodeList nodes = evaluateXPath(document, xmlPath);
            System.out.println("DEBUG: Found " + nodes.getLength() + " nodes for complex path: " + xmlPath);
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Object mappedObject = createMappedObject((Element) node, propMappings, config);
                    if (mappedObject != null) {
                        arrayValues.add(mappedObject);
                    }
                }
            }
            
            System.out.println("DEBUG: Created " + arrayValues.size() + " mapped objects for complex path: " + xmlPath);
        } catch (Exception e) {
            System.err.println("Error extracting complex array from XML path " + complexXmlPath + ": " + e.getMessage());
        }
        
        return arrayValues;
    }
    
    /**
     * Parse property mappings string into a map
     * Format: "prop1:jsonProp1,prop2:jsonProp2"
     */
    private Map<String, String> parsePropertyMappings(String propertyMappings) {
        Map<String, String> mappings = new HashMap<>();
        
        if (propertyMappings == null || propertyMappings.trim().isEmpty()) {
            return mappings;
        }
        
        String[] pairs = propertyMappings.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.trim().split(":");
            if (keyValue.length == 2) {
                mappings.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        
        return mappings;
    }
    
    /**
     * Convert PropertyFieldMapping list to a Map for compatibility
     */
    private Map<String, String> convertToPropertyMappingsMap(List<PropertyFieldMapping> propertyFieldMappings) {
        Map<String, String> mappings = new HashMap<>();
        
        if (propertyFieldMappings == null || propertyFieldMappings.isEmpty()) {
            return mappings;
        }
        
        for (PropertyFieldMapping fieldMapping : propertyFieldMappings) {
            mappings.put(fieldMapping.getXmlField(), fieldMapping.getJsonField());
        }
        
        return mappings;
    }
    
    /**
     * Create a mapped object from XML element based on property mappings
     */
    private Object createMappedObject(Element element, Map<String, String> propMappings, TransformationConfig config) {
        if (propMappings.isEmpty()) {
            // No mappings, return the element as is
            return extractNodeValue(element, config);
        }
        
        ObjectNode objectNode = objectMapper.createObjectNode();
        
        for (Map.Entry<String, String> mapping : propMappings.entrySet()) {
            String xmlProperty = mapping.getKey();
            String jsonProperty = mapping.getValue();
            
            // Extract value from the element
            Object value = extractPropertyFromElement(element, xmlProperty, config);
            if (value != null) {
                setJsonValue(objectNode, jsonProperty, value);
            }
        }
        
        return objectNode;
    }
    
    /**
     * Extract a specific property value from an XML element
     */
    private Object extractPropertyFromElement(Element element, String propertyName, TransformationConfig config) {
        try {
            // Check if it's a direct child element
            NodeList children = element.getElementsByTagName(propertyName);
            if (children.getLength() > 0) {
                Node child = children.item(0);
                return extractNodeValue(child, config);
            }
            
            // Check if it's an attribute
            if (element.hasAttribute(propertyName)) {
                return element.getAttribute(propertyName);
            }
            
            // Check if it's a nested path (e.g., "address/street")
            if (propertyName.contains("/")) {
                String[] pathParts = propertyName.split("/");
                Node currentNode = element;
                
                for (String part : pathParts) {
                    if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element currentElement = (Element) currentNode;
                        NodeList partNodes = currentElement.getElementsByTagName(part);
                        if (partNodes.getLength() > 0) {
                            currentNode = partNodes.item(0);
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
                
                return extractNodeValue(currentNode, config);
            }
            
        } catch (Exception e) {
            // Log error
        }
        
        return null;
    }
    
    /**
     * Extract value from XML based on property mapping
     */
    private Object extractValueFromXml(Document document, PropertyMapping mapping, TransformationConfig config) {
        try {
            String xmlPath = mapping.getXmlPath();
            NodeList nodes = evaluateXPath(document, xmlPath);
            
            if (nodes.getLength() == 0) {
                return getDefaultValue(mapping, config);
            }
            
            Node node = nodes.item(0);
            String value = getNodeValue(node);
            
            if (value == null || value.trim().isEmpty()) {
                return getDefaultValue(mapping, config);
            }
            
            // Apply transformations if specified
            if (mapping.getTransform() != null && config.getTransformations() != null) {
                String transformRule = config.getTransformations().get(mapping.getTransform());
                if (transformRule != null) {
                    value = applyTransformation(value, transformRule);
                }
            }
            
            // Convert to appropriate data type
            return convertToDataType(value, mapping.getDataType());
            
        } catch (Exception e) {
            // Log error and return default value
            return getDefaultValue(mapping, config);
        }
    }
    
    /**
     * Extract all elements with the same name as an array
     * Useful for explicit array mapping scenarios
     */
    private List<Object> extractAllElementsByName(Document document, String elementName, TransformationConfig config) {
        List<Object> elements = new ArrayList<>();
        try {
            NodeList nodes = document.getElementsByTagName(elementName);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                Object value = extractNodeValue(node, config);
                if (value != null) {
                    elements.add(value);
                }
            }
        } catch (Exception e) {
            // Log error
        }
        return elements;
    }

    /**
     * Extract array values from XML (simple array handling)
     */
    private List<Object> extractArrayFromXml(Document document, String xmlPath, TransformationConfig config) {
        List<Object> arrayValues = new ArrayList<>();
        try {
            NodeList nodes = evaluateXPath(document, xmlPath);
            System.out.println("DEBUG: Found " + nodes.getLength() + " nodes for path: " + xmlPath);
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                Object value = extractNodeValue(node, config);
                if (value != null) {
                    arrayValues.add(value);
                }
            }
            System.out.println("DEBUG: Extracted " + arrayValues.size() + " values for path: " + xmlPath);
        } catch (Exception e) {
            System.err.println("Error extracting array from XML path " + xmlPath + ": " + e.getMessage());
        }
        return arrayValues;
    }

    /**
     * Extract array values from XML for nested property mappings
     */
    private List<Object> extractNestedPropertyArrayFromXml(Document document, NestedPropertyMapping nestedMapping, TransformationConfig config) {
        List<Object> arrayValues = new ArrayList<>();
        try {
            String xmlPath = nestedMapping.getXmlPath();
            System.out.println("DEBUG: Processing nested property array path: " + xmlPath);
            
            NodeList nodes = evaluateXPath(document, xmlPath);
            System.out.println("DEBUG: Found " + nodes.getLength() + " nodes for nested property path: " + xmlPath);
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Object mappedObject = createMappedObjectFromFieldMappings((Element) node, nestedMapping.getProperties(), config);
                    if (mappedObject != null) {
                        arrayValues.add(mappedObject);
                    }
                }
            }
            
            System.out.println("DEBUG: Created " + arrayValues.size() + " mapped objects for nested property path: " + xmlPath);
        } catch (Exception e) {
            System.err.println("Error extracting nested property array from XML path " + nestedMapping.getXmlPath() + ": " + e.getMessage());
        }
        return arrayValues;
    }

    /**
     * Create mapped object using PropertyFieldMapping list, honoring dataType, transform, and defaultValue
     */
    private Object createMappedObjectFromFieldMappings(Element element, List<PropertyFieldMapping> fieldMappings, TransformationConfig config) {
        if (fieldMappings == null || fieldMappings.isEmpty()) {
            return extractNodeValue(element, config);
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        for (PropertyFieldMapping fieldMapping : fieldMappings) {
            String xmlField = fieldMapping.getXmlField();
            String jsonField = fieldMapping.getJsonField();
            Object raw = extractPropertyFromElement(element, xmlField, config);
            String valueStr = raw == null ? null : raw.toString();
            if (valueStr == null || valueStr.trim().isEmpty()) {
                Object def = fieldMapping.getDefaultValue();
                if (def != null) {
                    Object coerced = convertToDataType(def.toString(), fieldMapping.getDataType());
                    setJsonValue(objectNode, jsonField, coerced);
                }
                continue;
            }
            // Apply transformation
            String transformKey = fieldMapping.getTransform();
            if (transformKey != null) {
                String rule = transformKey;
                if (config.getTransformations() != null && config.getTransformations().containsKey(transformKey)) {
                    rule = config.getTransformations().get(transformKey);
                }
                valueStr = applyTransformation(valueStr, rule);
            }
            Object finalValue = convertToDataType(valueStr, fieldMapping.getDataType());
            setJsonValue(objectNode, jsonField, finalValue);
        }
        return objectNode;
    }
    
    /**
     * Extract value from a single XML node
     * Enhanced to better handle complex elements and preserve structure
     */
    private Object extractNodeValue(Node node, TransformationConfig config) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            if (element.hasChildNodes()) {
                NodeList children = element.getChildNodes();
                if (children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE) {
                    return children.item(0).getNodeValue();
                } else {
                    // Complex element, create object
                    ObjectNode objectNode = objectMapper.createObjectNode();
                    for (int i = 0; i < children.getLength(); i++) {
                        Node child = children.item(i);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            String childName = child.getNodeName();
                            Object childValue = extractNodeValue(child, config);
                            if (childValue != null) {
                                // Handle arrays - if we have multiple children with the same name
                                if (objectNode.has(childName)) {
                                    // Convert to array if we encounter duplicate names
                                    if (objectNode.get(childName).isArray()) {
                                        ((ArrayNode) objectNode.get(childName)).add(objectMapper.valueToTree(childValue));
                                    } else {
                                        // Convert existing single value to array
                                        ArrayNode arrayNode = objectMapper.createArrayNode();
                                        arrayNode.add(objectNode.get(childName));
                                        arrayNode.add(objectMapper.valueToTree(childValue));
                                        objectNode.set(childName, arrayNode);
                                    }
                                } else {
                                    objectNode.set(childName, objectMapper.valueToTree(childValue));
                                }
                            }
                        }
                    }
                    return objectNode;
                }
            }
        }
        return getNodeValue(node);
    }
    
    /**
     * Get node value based on node type
     */
    private String getNodeValue(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            return node.getNodeValue();
        } else if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            if (element.hasAttribute("value")) {
                return element.getAttribute("value");
            }
            // Get text content
            return element.getTextContent();
        }
        return null;
    }
    
    /**
     * Convert string value to appropriate data type
     */
    private Object convertToDataType(String value, String dataType) {
        if (dataType == null || dataType.isEmpty()) {
            return value;
        }
        
        try {
            switch (dataType.toLowerCase()) {
                case "integer":
                case "int":
                    return Integer.parseInt(value);
                case "long":
                    return Long.parseLong(value);
                case "double":
                case "float":
                    return Double.parseDouble(value);
                case "bigdecimal":
                    return new BigDecimal(value);
                case "biginteger":
                    return new BigInteger(value);
                case "boolean":
                case "bool":
                    return Boolean.parseBoolean(value);
                case "date":
                    return LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
                case "datetime":
                    return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
                case "string":
                default:
                    return value;
            }
        } catch (Exception e) {
            // Return original value if conversion fails
            return value;
        }
    }
    
    /**
     * Apply transformation rule to value
     */
    private String applyTransformation(String value, String transformRule) {
        if (transformRule == null || transformRule.isEmpty()) {
            return value;
        }
        
        // Simple transformation examples
        if ("uppercase".equals(transformRule)) {
            return value.toUpperCase();
        } else if ("lowercase".equals(transformRule)) {
            return value.toLowerCase();
        } else if ("trim".equals(transformRule)) {
            return value.trim();
        } else if (transformRule.startsWith("replace:")) {
            String[] parts = transformRule.substring(8).split("->");
            if (parts.length == 2) {
                return value.replace(parts[0], parts[1]);
            }
        }
        
        return value;
    }
    
    /**
     * Get default value for mapping
     */
    private Object getDefaultValue(PropertyMapping mapping, TransformationConfig config) {
        if (mapping.getDefaultValue() != null) {
            return convertToDataType(mapping.getDefaultValue(), mapping.getDataType());
        }
        
        if (config.getDefaultValues() != null && config.getDefaultValues().containsKey(mapping.getJsonPath())) {
            return config.getDefaultValues().get(mapping.getJsonPath());
        }
        
        return null;
    }
    
    /**
     * Set value in JSON object using dot notation path
     */
    private void setJsonValue(ObjectNode rootNode, String jsonPath, Object value) {
        String[] pathParts = jsonPath.split("\\.");
        ObjectNode currentNode = rootNode;
        
        for (int i = 0; i < pathParts.length - 1; i++) {
            String part = pathParts[i];
            if (!currentNode.has(part)) {
                currentNode.putObject(part);
            }
            currentNode = (ObjectNode) currentNode.get(part);
        }
        
        String lastPart = pathParts[pathParts.length - 1];
        if (value instanceof String) {
            currentNode.put(lastPart, (String) value);
        } else if (value instanceof Integer) {
            currentNode.put(lastPart, (Integer) value);
        } else if (value instanceof Long) {
            currentNode.put(lastPart, (Long) value);
        } else if (value instanceof Double) {
            currentNode.put(lastPart, (Double) value);
        } else if (value instanceof Boolean) {
            currentNode.put(lastPart, (Boolean) value);
        } else if (value instanceof JsonNode) {
            currentNode.set(lastPart, (JsonNode) value);
        } else if (value instanceof List) {
            currentNode.set(lastPart, objectMapper.valueToTree(value));
        } else {
            currentNode.put(lastPart, value.toString());
        }
    }
    
    /**
     * Parse XML string to Document
     */
    private Document parseXml(String xmlInput) throws Exception {
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        InputSource source = new InputSource(new StringReader(xmlInput));
        return builder.parse(source);
    }
    
    /**
     * Simple XPath evaluation (for basic path expressions)
     * Now properly handles array scenarios by returning all matching elements
     */
    private NodeList evaluateXPath(Document document, String xpath) {
        // This is a simplified XPath implementation
        // In production, you might want to use a proper XPath library
        if (xpath.startsWith("/")) {
            xpath = xpath.substring(1);
        }
        
        String[] pathParts = xpath.split("/");
        Node currentNode = document;
        
        // If this is a simple path (no array indexing), return all matching elements
        if (pathParts.length == 1 && !pathParts[0].contains("[")) {
            return getChildElements(currentNode, pathParts[0]);
        }
        
        // For complex paths, navigate to the parent and return all children
        for (int i = 0; i < pathParts.length - 1; i++) {
            String part = pathParts[i];
            if (part.isEmpty()) continue;
            
            if (part.contains("[")) {
                // Handle array indexing like "item[0]"
                String elementName = part.substring(0, part.indexOf("["));
                String indexStr = part.substring(part.indexOf("[") + 1, part.indexOf("]"));
                int index = Integer.parseInt(indexStr);
                
                NodeList children = getChildElements(currentNode, elementName);
                if (children.getLength() > index) {
                    currentNode = children.item(index);
                } else {
                    return new EmptyNodeList();
                }
            } else {
                NodeList children = getChildElements(currentNode, part);
                if (children.getLength() > 0) {
                    currentNode = children.item(0);
                } else {
                    return new EmptyNodeList();
                }
            }
        }
        
        // For the last part, return all matching elements if no indexing
        String lastPart = pathParts[pathParts.length - 1];
        if (lastPart.contains("[")) {
            // Handle array indexing for the last part
            String elementName = lastPart.substring(0, lastPart.indexOf("["));
            String indexStr = lastPart.substring(lastPart.indexOf("[") + 1, lastPart.indexOf("]"));
            int index = Integer.parseInt(indexStr);
            
            NodeList children = getChildElements(currentNode, elementName);
            if (children.getLength() > index) {
                return new SingleNodeList(children.item(index));
            } else {
                return new EmptyNodeList();
            }
        } else {
            // Return all matching elements for array processing
            return getChildElements(currentNode, lastPart);
        }
    }
    
    /**
     * Get child elements by tag name
     * Enhanced to properly find all matching children within a specific context
     */
    private NodeList getChildElements(Node parent, String tagName) {
        if (parent.getNodeType() == Node.DOCUMENT_NODE) {
            return ((Document) parent).getElementsByTagName(tagName);
        } else if (parent.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) parent;
            // Use getElementsByTagName to get all descendants, then filter to direct children
            NodeList allDescendants = element.getElementsByTagName(tagName);
            List<Node> directChildren = new ArrayList<>();
            
            for (int i = 0; i < allDescendants.getLength(); i++) {
                Node descendant = allDescendants.item(i);
                // Check if this is a direct child of the current element
                if (descendant.getParentNode() == element) {
                    directChildren.add(descendant);
                }
            }
            
            return new DynamicNodeList(directChildren);
        }
        return new EmptyNodeList();
    }
    
    /**
     * Simple NodeList implementations for XPath evaluation
     */
    private static class EmptyNodeList implements NodeList {
        @Override
        public Node item(int index) { return null; }
        @Override
        public int getLength() { return 0; }
    }
    
    private static class SingleNodeList implements NodeList {
        private final Node node;
        
        public SingleNodeList(Node node) {
            this.node = node;
        }
        
        @Override
        public Node item(int index) {
            return index == 0 ? node : null;
        }
        
        @Override
        public int getLength() {
            return 1;
        }
    }
    
    /**
     * Dynamic NodeList implementation for handling variable-sized collections
     */
    private static class DynamicNodeList implements NodeList {
        private final List<Node> nodes;
        
        public DynamicNodeList(List<Node> nodes) {
            this.nodes = nodes;
        }
        
        @Override
        public Node item(int index) {
            return (index >= 0 && index < nodes.size()) ? nodes.get(index) : null;
        }
        
        @Override
        public int getLength() {
            return nodes.size();
        }
    }
}
