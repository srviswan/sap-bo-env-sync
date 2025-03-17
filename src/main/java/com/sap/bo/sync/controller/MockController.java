package com.sap.bo.sync.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for mock testing operations
 * Only available in the mock profile
 */
@RestController
@RequestMapping("/mock")
@Profile("mock")
public class MockController {

    private static final Logger log = LoggerFactory.getLogger(MockController.class);
    
    private final ObjectMapper objectMapper;
    
    public MockController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Health check endpoint to verify the mock controller is working
     */
    @GetMapping("/health")
    public ResponseEntity<JsonNode> health() {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("status", "UP");
        response.put("message", "Mock controller is running");
        
        log.info("Mock health check requested");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get information about the mock environment
     */
    @GetMapping("/info")
    public ResponseEntity<JsonNode> info() {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("profile", "mock");
        response.put("description", "This is a mock environment for testing SAP BO Sync Tool");
        
        ObjectNode environments = objectMapper.createObjectNode();
        
        ObjectNode sourceEnv = objectMapper.createObjectNode();
        sourceEnv.put("name", "source");
        sourceEnv.put("url", "http://source-bo-server:6405/biprws");
        sourceEnv.put("username", "Administrator");
        
        ObjectNode targetEnv = objectMapper.createObjectNode();
        targetEnv.put("name", "target");
        targetEnv.put("url", "http://target-bo-server:6405/biprws");
        targetEnv.put("username", "Administrator");
        
        environments.set("source", sourceEnv);
        environments.set("target", targetEnv);
        
        response.set("environments", environments);
        
        ObjectNode availableEndpoints = objectMapper.createObjectNode();
        availableEndpoints.put("/sync/compare/server", "Compare server configurations");
        availableEndpoints.put("/sync/compare/cluster", "Compare cluster configurations");
        availableEndpoints.put("/sync/compare/configs", "Compare custom configurations");
        
        response.set("availableEndpoints", availableEndpoints);
        
        log.info("Mock info requested");
        
        return ResponseEntity.ok(response);
    }
}
