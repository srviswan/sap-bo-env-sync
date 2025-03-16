package com.sap.bo.sync.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the SapBoProperties class
 */
public class SapBoPropertiesTest {

    @Test
    public void testSapBoProperties() {
        // Arrange & Act
        SapBoProperties properties = new SapBoProperties();
        properties.setSource(new SapBoProperties.BoEnvironment());
        properties.setTarget(new SapBoProperties.BoEnvironment());
        properties.setSync(new SapBoProperties.SyncProperties());
        
        // Assert
        assertNotNull(properties.getSource());
        assertNotNull(properties.getTarget());
        assertNotNull(properties.getSync());
    }
    
    @Test
    public void testBoEnvironmentProperties() {
        // Arrange & Act
        SapBoProperties.BoEnvironment environment = new SapBoProperties.BoEnvironment();
        String url = "http://example.com/bo";
        String username = "admin";
        String password = "password";
        String authType = "secEnterprise";
        
        environment.setUrl(url);
        environment.setUsername(username);
        environment.setPassword(password);
        environment.setAuthType(authType);
        
        // Assert
        assertEquals(url, environment.getUrl());
        assertEquals(username, environment.getUsername());
        assertEquals(password, environment.getPassword());
        assertEquals(authType, environment.getAuthType());
    }
    
    @Test
    public void testSyncProperties() {
        // Arrange & Act
        SapBoProperties.SyncProperties syncProps = new SapBoProperties.SyncProperties();
        boolean enabled = true;
        boolean forceUpdate = false;
        int batchSize = 100;
        SapBoProperties.ScheduleProperties schedule = new SapBoProperties.ScheduleProperties();
        
        syncProps.setEnabled(enabled);
        syncProps.setForceUpdate(forceUpdate);
        syncProps.setBatchSize(batchSize);
        syncProps.setSchedule(schedule);
        
        // Assert
        assertEquals(enabled, syncProps.isEnabled());
        assertEquals(forceUpdate, syncProps.isForceUpdate());
        assertEquals(batchSize, syncProps.getBatchSize());
        assertNotNull(syncProps.getSchedule());
    }
    
    @Test
    public void testScheduleProperties() {
        // Arrange
        SapBoProperties.ScheduleProperties scheduleProps = new SapBoProperties.ScheduleProperties();
        String cron = "0 0 * * * *";
        
        // Act
        scheduleProps.setCron(cron);
        
        // Assert
        assertEquals(cron, scheduleProps.getCron());
    }
}
