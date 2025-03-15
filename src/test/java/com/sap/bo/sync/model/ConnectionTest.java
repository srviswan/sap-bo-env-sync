package com.sap.bo.sync.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the Connection class
 */
public class ConnectionTest {

    @Test
    public void testConnectionProperties() {
        // Arrange
        Connection connection = new Connection();
        String id = "conn123";
        String name = "Sales DB Connection";
        String type = "JDBC";
        String dataSourceType = "SQL";
        String server = "db.example.com";
        String database = "sales_db";
        String username = "admin";
        String authentication = "Basic";
        String status = "Active";
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("port", "1433");
        parameters.put("timeout", "30");
        
        // Act
        connection.setId(id);
        connection.setName(name);
        connection.setType(type);
        connection.setDataSourceType(dataSourceType);
        connection.setServer(server);
        connection.setDatabase(database);
        connection.setUsername(username);
        connection.setAuthentication(authentication);
        connection.setParameters(parameters);
        connection.setStatus(status);
        
        // Assert
        assertEquals(id, connection.getId());
        assertEquals(name, connection.getName());
        assertEquals(type, connection.getType());
        assertEquals(dataSourceType, connection.getDataSourceType());
        assertEquals(server, connection.getServer());
        assertEquals(database, connection.getDatabase());
        assertEquals(username, connection.getUsername());
        assertEquals(authentication, connection.getAuthentication());
        assertEquals(status, connection.getStatus());
        
        assertNotNull(connection.getParameters());
        assertEquals(2, connection.getParameters().size());
        assertEquals("1433", connection.getParameters().get("port"));
        assertEquals("30", connection.getParameters().get("timeout"));
    }
    
    @Test
    public void testConnectionInheritance() {
        // Arrange
        Connection connection = new Connection();
        String id = "conn123";
        String cuid = "CUID123";
        String description = "Connection description";
        
        // Act
        connection.setId(id);
        connection.setCuid(cuid);
        connection.setDescription(description);
        
        // Assert
        assertEquals(id, connection.getId());
        assertEquals(cuid, connection.getCuid());
        assertEquals(description, connection.getDescription());
    }
}
