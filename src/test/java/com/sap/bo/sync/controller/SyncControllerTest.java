package com.sap.bo.sync.controller;

import com.sap.bo.sync.model.SapBoObject;
import com.sap.bo.sync.service.SapBoService;
import com.sap.bo.sync.service.SyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the SyncController class
 */
@ExtendWith(MockitoExtension.class)
public class SyncControllerTest {

    @Mock
    private SyncService syncService;

    @Mock
    private SapBoService sapBoService;

    @InjectMocks
    private SyncController syncController;

    @Test
    public void testSyncAll() {
        // Arrange
        when(syncService.syncAll()).thenReturn(CompletableFuture.completedFuture(true));

        // Act
        ResponseEntity<String> response = syncController.syncAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sync started successfully", response.getBody());
    }

    @Test
    public void testSyncFolders() {
        // Arrange
        when(syncService.syncFolders()).thenReturn(CompletableFuture.completedFuture(true));

        // Act
        ResponseEntity<String> response = syncController.syncFolders();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Folder sync started successfully", response.getBody());
    }

    @Test
    public void testSyncReports() {
        // Arrange
        when(syncService.syncReports()).thenReturn(CompletableFuture.completedFuture(true));

        // Act
        ResponseEntity<String> response = syncController.syncReports();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reports sync started successfully", response.getBody());
    }

    @Test
    public void testSyncUniverses() {
        // Arrange
        when(syncService.syncUniverses()).thenReturn(CompletableFuture.completedFuture(true));

        // Act
        ResponseEntity<String> response = syncController.syncUniverses();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Universes sync started successfully", response.getBody());
    }

    @Test
    public void testSyncConnections() {
        // Arrange
        when(syncService.syncConnections()).thenReturn(CompletableFuture.completedFuture(true));

        // Act
        ResponseEntity<String> response = syncController.syncConnections();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Connections sync started successfully", response.getBody());
    }

    @Test
    public void testGetFolders() {
        // Arrange
        List<SapBoObject> folders = new ArrayList<>();
        SapBoObject folder = new SapBoObject();
        folder.setId("folder1");
        folder.setName("Test Folder");
        folders.add(folder);

        when(sapBoService.getFolders(true)).thenReturn(folders);

        // Act
        ResponseEntity<List<SapBoObject>> response = syncController.getFolders();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("folder1", response.getBody().get(0).getId());
        assertEquals("Test Folder", response.getBody().get(0).getName());
    }
}
