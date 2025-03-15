package com.sap.bo.sync.controller;

import com.sap.bo.sync.model.SapBoObject;
import com.sap.bo.sync.scheduler.SyncScheduler;
import com.sap.bo.sync.service.SapBoService;
import com.sap.bo.sync.service.SapBoServiceFactory;
import com.sap.bo.sync.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * REST controller for synchronization operations
 */
@RestController
@RequestMapping("/sync")
public class SyncController {

    private static final Logger log = LoggerFactory.getLogger(SyncController.class);
    
    private final SyncService syncService;
    private final SapBoServiceFactory serviceFactory;
    private final SyncScheduler syncScheduler;
    
    public SyncController(SyncService syncService, SapBoServiceFactory serviceFactory, SyncScheduler syncScheduler) {
        this.syncService = syncService;
        this.serviceFactory = serviceFactory;
        this.syncScheduler = syncScheduler;
    }
    
    /**
     * Get synchronization status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSyncStatus() {
        return ResponseEntity.ok(syncScheduler.getSyncStatus());
    }
    
    /**
     * Trigger full synchronization
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> triggerSync(
            @RequestParam(value = "forceUpdate", defaultValue = "false") boolean forceUpdate) {
        
        log.info("Triggering full synchronization with forceUpdate={}", forceUpdate);
        
        CompletableFuture<Integer> future = syncScheduler.triggerSync(forceUpdate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "started");
        response.put("message", "Synchronization started successfully");
        
        return ResponseEntity.accepted().body(response);
    }
    
    /**
     * Synchronize reports
     */
    @PostMapping("/reports")
    public ResponseEntity<Map<String, Object>> syncReports(
            @RequestParam(value = "forceUpdate", defaultValue = "false") boolean forceUpdate,
            @RequestBody(required = false) List<String> reportIds) {
        
        log.info("Triggering report synchronization for {} with forceUpdate={}", 
                reportIds != null ? reportIds.size() + " reports" : "all reports", forceUpdate);
        
        Map<String, String> options = new HashMap<>();
        options.put("forceUpdate", String.valueOf(forceUpdate));
        
        int count = syncService.syncReports(reportIds, options);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("count", count);
        response.put("message", "Synchronized " + count + " reports");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Synchronize universes
     */
    @PostMapping("/universes")
    public ResponseEntity<Map<String, Object>> syncUniverses(
            @RequestParam(value = "forceUpdate", defaultValue = "false") boolean forceUpdate,
            @RequestBody(required = false) List<String> universeIds) {
        
        log.info("Triggering universe synchronization for {} with forceUpdate={}", 
                universeIds != null ? universeIds.size() + " universes" : "all universes", forceUpdate);
        
        Map<String, String> options = new HashMap<>();
        options.put("forceUpdate", String.valueOf(forceUpdate));
        
        int count = syncService.syncUniverses(universeIds, options);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("count", count);
        response.put("message", "Synchronized " + count + " universes");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Synchronize connections
     */
    @PostMapping("/connections")
    public ResponseEntity<Map<String, Object>> syncConnections(
            @RequestParam(value = "forceUpdate", defaultValue = "false") boolean forceUpdate,
            @RequestBody(required = false) List<String> connectionIds) {
        
        log.info("Triggering connection synchronization for {} with forceUpdate={}", 
                connectionIds != null ? connectionIds.size() + " connections" : "all connections", forceUpdate);
        
        Map<String, String> options = new HashMap<>();
        options.put("forceUpdate", String.valueOf(forceUpdate));
        
        int count = syncService.syncConnections(connectionIds, options);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("count", count);
        response.put("message", "Synchronized " + count + " connections");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Search for objects in source environment
     */
    @GetMapping("/search")
    public ResponseEntity<List<SapBoObject>> search(
            @RequestParam("query") String query,
            @RequestParam(value = "types", required = false) List<String> objectTypes) {
        
        log.info("Searching for objects with query: {} and types: {}", query, objectTypes);
        
        SapBoService sourceService = serviceFactory.getSourceService();
        List<SapBoObject> results = sourceService.search(query, objectTypes);
        
        return ResponseEntity.ok(results);
    }
}
