package com.bitsevn.transformer.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bitsevn.transformer.model.TransformationConfig;
import com.bitsevn.transformer.service.ConfigurationService;
import com.bitsevn.transformer.service.MongoConfigurationService;
import com.bitsevn.transformer.service.XmlToJsonTransformer;

@RestController
@RequestMapping("/api")
public class TransformerController {

    @Autowired
    private XmlToJsonTransformer transformer;
    
    @Autowired
    private ConfigurationService configurationService;
    
    @Autowired
    private MongoConfigurationService mongoConfigurationService;

    /**
     * Transform XML to JSON using a named configuration from files
     */
    @PostMapping("/transform/{configName}")
    public ResponseEntity<?> transformXmlToJson(
            @PathVariable String configName,
            @RequestBody String xmlInput) {
        try {
            TransformationConfig config = configurationService.loadConfiguration(configName);
            String jsonOutput = transformer.transformXmlToJson(xmlInput, config);
            return ResponseEntity.ok(Map.of("result", jsonOutput));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Transformation failed: " + e.getMessage()));
        }
    }
    
    /**
     * Transform XML to JSON using inline configuration
     */
    @PostMapping("/transform")
    public ResponseEntity<?> transformXmlToJsonWithConfig(
            @RequestBody Map<String, Object> request) {
        try {
            String xmlInput = (String) request.get("xml");
            String jsonConfig = (String) request.get("config");
            
            if (xmlInput == null || jsonConfig == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Both 'xml' and 'config' fields are required"));
            }
            
            TransformationConfig config = configurationService.loadConfigurationFromJson(jsonConfig);
            String jsonOutput = transformer.transformXmlToJson(xmlInput, config);
            return ResponseEntity.ok(Map.of("result", jsonOutput));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Transformation failed: " + e.getMessage()));
        }
    }
    
    /**
     * Transform XML to JSON using MongoDB configuration
     */
    @PostMapping("/mongo/transform/{configName}")
    public ResponseEntity<?> transformXmlToJsonWithMongoConfig(
            @PathVariable String configName,
            @RequestBody String xmlInput) {
        try {
            TransformationConfig config = mongoConfigurationService.loadConfiguration(configName);
            if (config == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Configuration '" + configName + "' not found in MongoDB"));
            }
            
            String jsonOutput = transformer.transformXmlToJson(xmlInput, config);
            return ResponseEntity.ok(Map.of(
                "configName", configName,
                "result", jsonOutput
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Transformation failed: " + e.getMessage()));
        }
    }
}
