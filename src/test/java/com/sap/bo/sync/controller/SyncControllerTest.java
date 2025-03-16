package com.sap.bo.sync.controller;

import com.sap.bo.sync.model.SapBoObject;
import com.sap.bo.sync.scheduler.SyncScheduler;
import com.sap.bo.sync.service.SapBoService;
import com.sap.bo.sync.service.SapBoServiceFactory;
import com.sap.bo.sync.service.SyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the SyncController class
 */
@ExtendWith(MockitoExtension.class)
public class SyncControllerTest {

    @Mock
    private SyncService syncService;
    
    @Mock
    private SyncScheduler syncScheduler;

    @Mock
    private SapBoService sapBoService;
    
    @Mock
    private SapBoServiceFactory serviceFactory;

    @InjectMocks
    private SyncController syncController;

    @Test
    public void testSyncAll() {
        // Arrange
        when(syncScheduler.triggerSync(false)).thenReturn(CompletableFuture.completedFuture(5));

        // Act
        ResponseEntity<Map<String, Object>> response = syncController.triggerSync(false);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("started", body.get("status"));
        assertEquals("Synchronization started successfully", body.get("message"));
    }

    // Note: There is no syncFolders method in SyncController, so removing this test

    @Test
    public void testSyncReports() {
        // Arrange
        List<String> reportIds = null;
        Map<String, String> options = new HashMap<>();
        options.put("forceUpdate", "false");
        when(syncService.syncReports(eq(reportIds), eq(options))).thenReturn(3);

        // Act
        ResponseEntity<Map<String, Object>> response = syncController.syncReports(false, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(3, body.get("count"));
    }

    @Test
    public void testSyncUniverses() {
        // Arrange
        List<String> universeIds = null;
        Map<String, String> options = new HashMap<>();
        options.put("forceUpdate", "false");
        when(syncService.syncUniverses(eq(universeIds), eq(options))).thenReturn(2);

        // Act
        ResponseEntity<Map<String, Object>> response = syncController.syncUniverses(false, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.get("count"));
    }

    @Test
    public void testSyncConnections() {
        // Arrange
        List<String> connectionIds = null;
        Map<String, String> options = new HashMap<>();
        options.put("forceUpdate", "false");
        when(syncService.syncConnections(eq(connectionIds), eq(options))).thenReturn(1);

        // Act
        ResponseEntity<Map<String, Object>> response = syncController.syncConnections(false, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.get("count"));
    }

    @Test
    public void testSearch() {
        // Arrange
        List<SapBoObject> searchResults = new ArrayList<>();
        SapBoObject result = new SapBoObject();
        searchResults.add(result);
        
        when(serviceFactory.getSourceService()).thenReturn(sapBoService);
        when(sapBoService.search(eq("test"), any(), eq(null), eq(null))).thenReturn(searchResults);
        
        // Act
        ResponseEntity<List<SapBoObject>> response = syncController.search("test", null);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<SapBoObject> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
    }
}
