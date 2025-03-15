package com.sap.bo.sync.service.impl;

import com.sap.bo.sync.exception.SapBoApiException;
import com.sap.bo.sync.service.AsyncSapBoService;
import com.sap.bo.sync.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the asynchronous SAP BO service
 */
@Service
public class AsyncSapBoServiceImpl implements AsyncSapBoService {

    private static final Logger log = LoggerFactory.getLogger(AsyncSapBoServiceImpl.class);
    
    private final SyncService syncService;
    
    public AsyncSapBoServiceImpl(SyncService syncService) {
        this.syncService = syncService;
    }

    @Async
    @Override
    public CompletableFuture<Integer> asyncSyncAll(boolean forceUpdate) {
        log.info("Starting asynchronous full synchronization with forceUpdate={}", forceUpdate);
        
        try {
            int result = syncService.syncAll(forceUpdate);
            log.info("Asynchronous full synchronization completed successfully");
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Error during asynchronous full synchronization", e);
            CompletableFuture<Integer> future = new CompletableFuture<>();
            future.completeExceptionally(new SapBoApiException("Error during asynchronous full synchronization", e));
            return future;
        }
    }

    @Async
    @Override
    public CompletableFuture<Integer> asyncSyncReports(List<String> reportIds, Map<String, String> options) {
        log.info("Starting asynchronous report synchronization: {}", reportIds != null ? reportIds : "all");
        
        try {
            int result = syncService.syncReports(reportIds, options);
            log.info("Asynchronous report synchronization completed successfully");
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Error during asynchronous report synchronization", e);
            CompletableFuture<Integer> future = new CompletableFuture<>();
            future.completeExceptionally(new SapBoApiException("Error during asynchronous report synchronization", e));
            return future;
        }
    }

    @Async
    @Override
    public CompletableFuture<Integer> asyncSyncUniverses(List<String> universeIds, Map<String, String> options) {
        log.info("Starting asynchronous universe synchronization: {}", universeIds != null ? universeIds : "all");
        
        try {
            int result = syncService.syncUniverses(universeIds, options);
            log.info("Asynchronous universe synchronization completed successfully");
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Error during asynchronous universe synchronization", e);
            CompletableFuture<Integer> future = new CompletableFuture<>();
            future.completeExceptionally(new SapBoApiException("Error during asynchronous universe synchronization", e));
            return future;
        }
    }

    @Async
    @Override
    public CompletableFuture<Integer> asyncSyncConnections(List<String> connectionIds, Map<String, String> options) {
        log.info("Starting asynchronous connection synchronization: {}", connectionIds != null ? connectionIds : "all");
        
        try {
            int result = syncService.syncConnections(connectionIds, options);
            log.info("Asynchronous connection synchronization completed successfully");
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Error during asynchronous connection synchronization", e);
            CompletableFuture<Integer> future = new CompletableFuture<>();
            future.completeExceptionally(new SapBoApiException("Error during asynchronous connection synchronization", e));
            return future;
        }
    }
}
