package com.sap.bo.sync.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the SapBoProperties class
 */
public class SapBoPropertiesTest {

    @Test
    public void testSapBoProperties() {
        // Arrange
        SapBoProperties properties = new SapBoProperties();
        SapBoProperties.BoEnvironment source = new SapBoProperties.BoEnvironment();
        SapBoProperties.BoEnvironment target = new SapBoProperties.BoEnvironment();
        SapBoProperties.SyncProperties sync = new SapBoProperties.SyncProperties();
        
        // Act
        properties.setSource(source);
        properties.setTarget(target);
        properties.setSync(sync);
        
        // Assert
        assertNotNull(properties.getSource());
        assertNotNull(properties.getTarget());
        assertNotNull(properties.getSync());
    }
    
    @Test
    public void testBoEnvironmentProperties() {
        // Arrange
        SapBoProperties.BoEnvironment environment = new SapBoProperties.BoEnvironment();
        String url = "http://example.com/bo";
        String username = "admin";
        String password = "password";
        String authType = "secEnterprise";
        
        // Act
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
        // Arrange
        SapBoProperties.SyncProperties syncProps = new SapBoProperties.SyncProperties();
        boolean enabled = true;
        boolean forceUpdate = false;
        int batchSize = 100;
        SapBoProperties.ScheduleProperties schedule = new SapBoProperties.ScheduleProperties();
        
        // Act
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
