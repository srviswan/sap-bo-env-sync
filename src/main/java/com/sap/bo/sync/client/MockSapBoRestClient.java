package com.sap.bo.sync.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sap.bo.sync.config.SapBoProperties;
import com.sap.bo.sync.exception.SapBoApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of the SapBoRestClient for testing without actual SAP BO servers
 */
@Component
@Profile("mock")
public class MockSapBoRestClient extends SapBoRestClient {

    private static final Logger log = LoggerFactory.getLogger(MockSapBoRestClient.class);
    
    private final ObjectMapper objectMapper;
    private final Map<String, JsonNode> mockData = new HashMap<>();
    
    // Environment identifiers for mock data
    private static final String SOURCE_ENV = "source";
    private static final String TARGET_ENV = "target";
    
    public MockSapBoRestClient(@org.springframework.beans.factory.annotation.Qualifier("mockRestTemplate") RestTemplate mockRestTemplate, ObjectMapper objectMapper) {
        super(mockRestTemplate, objectMapper);
        this.objectMapper = objectMapper;
        initializeMockData();
    }
    
    /**
     * Initialize mock data for different environments and endpoints
     */
    private void initializeMockData() {
        // Create mock server configurations
        mockData.put(SOURCE_ENV + "_server_config", createSourceServerConfig());
        mockData.put(TARGET_ENV + "_server_config", createTargetServerConfig());
        
        // Create mock cluster configurations
        mockData.put(SOURCE_ENV + "_cluster_config", createSourceClusterConfig());
        mockData.put(TARGET_ENV + "_cluster_config", createTargetClusterConfig());
        
        // Create mock reports data
        mockData.put(SOURCE_ENV + "_reports", createSourceReports());
        mockData.put(TARGET_ENV + "_reports", createTargetReports());
        
        // Create mock universes data
        mockData.put(SOURCE_ENV + "_universes", createSourceUniverses());
        mockData.put(TARGET_ENV + "_universes", createTargetUniverses());
        
        // Create mock connections data
        mockData.put(SOURCE_ENV + "_connections", createSourceConnections());
        mockData.put(TARGET_ENV + "_connections", createTargetConnections());
    }
    
    @Override
    public String getAuthToken(SapBoProperties.BoEnvironment environment) {
        // Return a dummy token for mock environment
        return "mock-auth-token-" + environment.getUrl().hashCode();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(SapBoProperties.BoEnvironment environment, String endpoint, Class<T> responseType) {
        log.debug("Mock GET request to endpoint: {}", endpoint);
        
        String envId = getEnvironmentId(environment);
        JsonNode result = null;
        
        if (endpoint.contains("/servers")) {
            result = mockData.get(envId + "_server_config");
        } else if (endpoint.contains("/clusters")) {
            result = mockData.get(envId + "_cluster_config");
        } else if (endpoint.contains("/reports")) {
            result = mockData.get(envId + "_reports");
        } else if (endpoint.contains("/universes")) {
            result = mockData.get(envId + "_universes");
        } else if (endpoint.contains("/connections")) {
            result = mockData.get(envId + "_connections");
        }
        
        if (result == null) {
            throw new SapBoApiException("Unsupported mock endpoint: " + endpoint);
        }
        
        // Try to convert the result to the requested type if possible
        if (JsonNode.class.isAssignableFrom(responseType)) {
            return (T) result;
        } else {
            try {
                return objectMapper.convertValue(result, responseType);
            } catch (Exception e) {
                log.warn("Could not convert result to {}, returning as JsonNode", responseType.getName());
                return (T) result;
            }
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T post(SapBoProperties.BoEnvironment environment, String endpoint, Object requestBody, Class<T> responseType) {
        log.debug("Mock POST request to endpoint: {} with body: {}", endpoint, requestBody);
        
        // For simplicity, we'll return the same responses as GET for now
        return get(environment, endpoint, responseType);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T put(SapBoProperties.BoEnvironment environment, String endpoint, Object requestBody, Class<T> responseType) {
        log.debug("Mock PUT request to endpoint: {} with body: {}", endpoint, requestBody);
        
        // Create a simple success response
        ObjectNode response = objectMapper.createObjectNode();
        response.put("status", "success");
        response.put("message", "Resource updated successfully");
        
        // Try to convert the response to the requested type if possible
        if (ObjectNode.class.isAssignableFrom(responseType)) {
            return (T) response;
        } else {
            try {
                return objectMapper.convertValue(response, responseType);
            } catch (Exception e) {
                log.warn("Could not convert response to {}, returning as ObjectNode", responseType.getName());
                return (T) response;
            }
        }
    }
    
    // If there's a delete method in the parent class, we need to implement it
    // This is a placeholder for any delete method that might exist
    public void delete(SapBoProperties.BoEnvironment environment, String endpoint) {
        log.debug("Mock DELETE request to endpoint: {}", endpoint);
        // Do nothing for delete operations in mock mode
    }
    
    /**
     * Get the environment identifier based on the environment URL
     */
    private String getEnvironmentId(SapBoProperties.BoEnvironment environment) {
        if (environment.getUrl().contains("source")) {
            return SOURCE_ENV;
        } else {
            return TARGET_ENV;
        }
    }
    
    /**
     * Create source server configuration with some sample data
     */
    private JsonNode createSourceServerConfig() {
        ObjectNode config = objectMapper.createObjectNode();
        config.put("serverName", "BO_SERVER_SRC");
        config.put("version", "4.3");
        config.put("hostname", "source-bo-server");
        config.put("port", 6400);
        
        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("maxConnections", 100);
        properties.put("sessionTimeout", 30);
        properties.put("authenticationMode", "Enterprise");
        
        config.set("properties", properties);
        
        return config;
    }
    
    /**
     * Create target server configuration with some differences from source
     */
    private JsonNode createTargetServerConfig() {
        ObjectNode config = objectMapper.createObjectNode();
        config.put("serverName", "BO_SERVER_TGT");
        config.put("version", "4.3");
        config.put("hostname", "target-bo-server");
        config.put("port", 6400);
        
        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("maxConnections", 150);  // Different from source
        properties.put("sessionTimeout", 45);   // Different from source
        properties.put("authenticationMode", "Enterprise");
        
        config.set("properties", properties);
        
        return config;
    }
    
    /**
     * Create source cluster configuration
     */
    private JsonNode createSourceClusterConfig() {
        ObjectNode config = objectMapper.createObjectNode();
        config.put("clusterId", "cluster1");
        
        ArrayNode nodes = objectMapper.createArrayNode();
        
        ObjectNode node1 = objectMapper.createObjectNode();
        node1.put("name", "node1");
        node1.put("status", "running");
        node1.put("hostname", "source-node1");
        node1.put("port", 6410);
        
        ObjectNode node2 = objectMapper.createObjectNode();
        node2.put("name", "node2");
        node2.put("status", "running");
        node2.put("hostname", "source-node2");
        node2.put("port", 6410);
        
        nodes.add(node1);
        nodes.add(node2);
        
        config.set("nodes", nodes);
        
        return config;
    }
    
    /**
     * Create target cluster configuration with some differences
     */
    private JsonNode createTargetClusterConfig() {
        ObjectNode config = objectMapper.createObjectNode();
        config.put("clusterId", "cluster1");
        
        ArrayNode nodes = objectMapper.createArrayNode();
        
        ObjectNode node1 = objectMapper.createObjectNode();
        node1.put("name", "node1");
        node1.put("status", "stopped");  // Different from source
        node1.put("hostname", "target-node1");
        node1.put("port", 6410);
        
        ObjectNode node2 = objectMapper.createObjectNode();
        node2.put("name", "node2");
        node2.put("status", "running");
        node2.put("hostname", "target-node2");
        node2.put("port", 6411);  // Different from source
        
        nodes.add(node1);
        nodes.add(node2);
        
        config.set("nodes", nodes);
        
        return config;
    }
    
    /**
     * Create source reports data
     */
    private JsonNode createSourceReports() {
        ArrayNode reports = objectMapper.createArrayNode();
        
        ObjectNode report1 = objectMapper.createObjectNode();
        report1.put("id", "report1");
        report1.put("name", "Sales Report");
        report1.put("description", "Monthly sales report");
        report1.put("type", "Webi");
        report1.put("path", "/Public/Sales");
        report1.put("lastModified", "2025-01-15T10:30:00Z");
        
        ObjectNode report2 = objectMapper.createObjectNode();
        report2.put("id", "report2");
        report2.put("name", "Inventory Report");
        report2.put("description", "Inventory status report");
        report2.put("type", "Crystal");
        report2.put("path", "/Public/Inventory");
        report2.put("lastModified", "2025-02-10T14:45:00Z");
        
        reports.add(report1);
        reports.add(report2);
        
        return reports;
    }
    
    /**
     * Create target reports data with some differences
     */
    private JsonNode createTargetReports() {
        ArrayNode reports = objectMapper.createArrayNode();
        
        ObjectNode report1 = objectMapper.createObjectNode();
        report1.put("id", "report1");
        report1.put("name", "Sales Report");
        report1.put("description", "Monthly sales report - Updated");  // Different from source
        report1.put("type", "Webi");
        report1.put("path", "/Public/Sales");
        report1.put("lastModified", "2025-03-05T09:15:00Z");  // Different from source
        
        // Report2 is missing in target
        
        reports.add(report1);
        
        return reports;
    }
    
    /**
     * Create source universes data
     */
    private JsonNode createSourceUniverses() {
        ArrayNode universes = objectMapper.createArrayNode();
        
        ObjectNode universe1 = objectMapper.createObjectNode();
        universe1.put("id", "universe1");
        universe1.put("name", "Sales Universe");
        universe1.put("description", "Universe for sales data");
        universe1.put("type", "UNX");
        universe1.put("path", "/Public/Universes/Sales");
        universe1.put("lastModified", "2025-01-20T11:30:00Z");
        
        ObjectNode universe2 = objectMapper.createObjectNode();
        universe2.put("id", "universe2");
        universe2.put("name", "HR Universe");
        universe2.put("description", "Universe for HR data");
        universe2.put("type", "UNX");
        universe2.put("path", "/Public/Universes/HR");
        universe2.put("lastModified", "2025-02-15T16:45:00Z");
        
        universes.add(universe1);
        universes.add(universe2);
        
        return universes;
    }
    
    /**
     * Create target universes data with some differences
     */
    private JsonNode createTargetUniverses() {
        ArrayNode universes = objectMapper.createArrayNode();
        
        ObjectNode universe1 = objectMapper.createObjectNode();
        universe1.put("id", "universe1");
        universe1.put("name", "Sales Universe");
        universe1.put("description", "Universe for sales data");
        universe1.put("type", "UNX");
        universe1.put("path", "/Public/Universes/Sales");
        universe1.put("lastModified", "2025-01-20T11:30:00Z");
        
        // Universe2 is missing in target
        
        // Additional universe in target
        ObjectNode universe3 = objectMapper.createObjectNode();
        universe3.put("id", "universe3");
        universe3.put("name", "Finance Universe");
        universe3.put("description", "Universe for finance data");
        universe3.put("type", "UNX");
        universe3.put("path", "/Public/Universes/Finance");
        universe3.put("lastModified", "2025-03-10T13:20:00Z");
        
        universes.add(universe1);
        universes.add(universe3);
        
        return universes;
    }
    
    /**
     * Create source connections data
     */
    private JsonNode createSourceConnections() {
        ArrayNode connections = objectMapper.createArrayNode();
        
        ObjectNode connection1 = objectMapper.createObjectNode();
        connection1.put("id", "conn1");
        connection1.put("name", "Oracle Sales DB");
        connection1.put("type", "JDBC");
        connection1.put("server", "oracle-sales-db");
        connection1.put("database", "SALES");
        connection1.put("lastModified", "2025-01-25T10:15:00Z");
        
        ObjectNode connection2 = objectMapper.createObjectNode();
        connection2.put("id", "conn2");
        connection2.put("name", "SQL Server HR DB");
        connection2.put("type", "JDBC");
        connection2.put("server", "sqlserver-hr-db");
        connection2.put("database", "HR");
        connection2.put("lastModified", "2025-02-20T14:30:00Z");
        
        connections.add(connection1);
        connections.add(connection2);
        
        return connections;
    }
    
    /**
     * Create target connections data with some differences
     */
    private JsonNode createTargetConnections() {
        ArrayNode connections = objectMapper.createArrayNode();
        
        ObjectNode connection1 = objectMapper.createObjectNode();
        connection1.put("id", "conn1");
        connection1.put("name", "Oracle Sales DB");
        connection1.put("type", "JDBC");
        connection1.put("server", "oracle-sales-db-new");  // Different from source
        connection1.put("database", "SALES");
        connection1.put("lastModified", "2025-03-15T09:45:00Z");  // Different from source
        
        ObjectNode connection2 = objectMapper.createObjectNode();
        connection2.put("id", "conn2");
        connection2.put("name", "SQL Server HR DB");
        connection2.put("type", "JDBC");
        connection2.put("server", "sqlserver-hr-db");
        connection2.put("database", "HR");
        connection2.put("lastModified", "2025-02-20T14:30:00Z");
        
        connections.add(connection1);
        connections.add(connection2);
        
        return connections;
    }
}
