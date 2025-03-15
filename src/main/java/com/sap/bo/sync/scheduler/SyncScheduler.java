package com.sap.bo.sync.scheduler;

import com.sap.bo.sync.config.SapBoProperties;
import com.sap.bo.sync.service.AsyncSapBoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Scheduler for periodic synchronization tasks
 */
@Component
public class SyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(SyncScheduler.class);
    
    private final AsyncSapBoService asyncSapBoService;
    private final SapBoProperties sapBoProperties;
    
    // Track the last sync time for reporting
    private LocalDateTime lastSyncTime;
    private int lastSyncCount = 0;
    private boolean syncInProgress = false;
    
    public SyncScheduler(AsyncSapBoService asyncSapBoService, SapBoProperties sapBoProperties) {
        this.asyncSapBoService = asyncSapBoService;
        this.sapBoProperties = sapBoProperties;
    }
    
    /**
     * Scheduled task to synchronize all objects
     */
    @Scheduled(cron = "${sap.bo.sync.schedule.cron:0 0 * * * ?}")
    public void scheduledSync() {
        if (!sapBoProperties.getSync().isEnabled()) {
            log.debug("Scheduled synchronization is disabled");
            return;
        }
        
        if (syncInProgress) {
            log.warn("Previous synchronization is still in progress, skipping this run");
            return;
        }
        
        log.info("Starting scheduled synchronization at {}", LocalDateTime.now());
        syncInProgress = true;
        
        try {
            boolean forceUpdate = sapBoProperties.getSync().isForceUpdate();
            
            CompletableFuture<Integer> future = asyncSapBoService.asyncSyncAll(forceUpdate);
            
            future.whenComplete((count, throwable) -> {
                syncInProgress = false;
                
                if (throwable != null) {
                    log.error("Scheduled synchronization failed", throwable);
                } else {
                    lastSyncTime = LocalDateTime.now();
                    lastSyncCount = count;
                    log.info("Scheduled synchronization completed at {}. Objects synchronized: {}", 
                            lastSyncTime, count);
                }
            });
        } catch (Exception e) {
            syncInProgress = false;
            log.error("Error starting scheduled synchronization", e);
        }
    }
    
    /**
     * Manually trigger synchronization
     * @param forceUpdate If true, update objects even if they already exist
     * @return CompletableFuture with the number of objects synchronized
     */
    public CompletableFuture<Integer> triggerSync(boolean forceUpdate) {
        if (syncInProgress) {
            log.warn("Synchronization is already in progress");
            CompletableFuture<Integer> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException("Synchronization is already in progress"));
            return future;
        }
        
        log.info("Manually triggering synchronization at {}", LocalDateTime.now());
        syncInProgress = true;
        
        CompletableFuture<Integer> future = asyncSapBoService.asyncSyncAll(forceUpdate);
        
        future.whenComplete((count, throwable) -> {
            syncInProgress = false;
            
            if (throwable == null) {
                lastSyncTime = LocalDateTime.now();
                lastSyncCount = count;
                log.info("Manual synchronization completed at {}. Objects synchronized: {}", 
                        lastSyncTime, count);
            }
        });
        
        return future;
    }
    
    /**
     * Get the status of the synchronization
     * @return Map containing status information
     */
    public Map<String, Object> getSyncStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", sapBoProperties.getSync().isEnabled());
        status.put("inProgress", syncInProgress);
        status.put("lastSyncTime", lastSyncTime);
        status.put("lastSyncCount", lastSyncCount);
        status.put("schedule", sapBoProperties.getSync().getSchedule());
        
        return status;
    }
}
