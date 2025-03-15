package com.sap.bo.sync.service.impl;

import com.sap.bo.sync.config.SapBoProperties;
import com.sap.bo.sync.exception.SapBoApiException;
import com.sap.bo.sync.model.Connection;
import com.sap.bo.sync.model.Report;
import com.sap.bo.sync.model.SapBoObject;
import com.sap.bo.sync.model.Universe;
import com.sap.bo.sync.service.SapBoService;
import com.sap.bo.sync.service.SapBoServiceFactory;
import com.sap.bo.sync.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Implementation of the sync service for SAP BO objects
 */
@Service
public class SyncServiceImpl implements SyncService {
    
    private static final Logger log = LoggerFactory.getLogger(SyncServiceImpl.class);

    private final SapBoServiceFactory serviceFactory;
    private final SapBoProperties sapBoProperties;

    public SyncServiceImpl(SapBoServiceFactory serviceFactory, SapBoProperties sapBoProperties) {
        this.serviceFactory = serviceFactory;
        this.sapBoProperties = sapBoProperties;
    }

    @Override
    public int syncAll(boolean forceUpdate) {
        log.info("Starting full synchronization with forceUpdate={}", forceUpdate);
        
        int totalCount = 0;
        
        try {
            // Sync folders first to ensure proper structure
            totalCount += syncFolders(null);
            
            // Sync connections
            Map<String, String> options = new HashMap<>();
            options.put("forceUpdate", String.valueOf(forceUpdate));
            
            totalCount += syncConnections(null, options);
            totalCount += syncUniverses(null, options);
            totalCount += syncReports(null, options);
            
            log.info("Full synchronization completed. Total objects synchronized: {}", totalCount);
            return totalCount;
        } catch (Exception e) {
            log.error("Error during full synchronization", e);
            throw new SapBoApiException("Error during full synchronization", e);
        }
    }

    @Override
    public int syncFolders(String path) {
        log.info("Synchronizing folders from path: {}", path);
        
        SapBoService sourceService = serviceFactory.getSourceService();
        SapBoService targetService = serviceFactory.getTargetService();
        
        try {
            List<SapBoObject> sourceFolders = sourceService.getFolders(path);
            log.info("Found {} folders in source environment", sourceFolders.size());
            
            int syncCount = 0;
            for (SapBoObject folder : sourceFolders) {
                try {
                    // Check if folder exists in target
                    List<SapBoObject> targetFolders = targetService.search(folder.getName(), Collections.singletonList("folder"));
                    boolean exists = targetFolders.stream()
                            .anyMatch(tf -> tf.getName().equals(folder.getName()) && 
                                    (StringUtils.isBlank(folder.getParentId()) || 
                                     StringUtils.equals(tf.getParentId(), folder.getParentId())));
                    
                    if (!exists) {
                        // Create folder in target
                        log.info("Creating folder: {}", folder.getName());
                        // Implementation would depend on actual API
                        syncCount++;
                    }
                    
                    // Recursively sync subfolders
                    if (folder.getChildren() != null && !folder.getChildren().isEmpty()) {
                        String subPath = StringUtils.isBlank(path) ? folder.getName() : path + "/" + folder.getName();
                        syncCount += syncFolders(subPath);
                    }
                } catch (Exception e) {
                    log.error("Error synchronizing folder: {}", folder.getName(), e);
                }
            }
            
            log.info("Folder synchronization completed. Total folders synchronized: {}", syncCount);
            return syncCount;
        } catch (Exception e) {
            log.error("Error during folder synchronization", e);
            throw new SapBoApiException("Error during folder synchronization", e);
        }
    }

    @Override
    @Retryable(value = {SapBoApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public int syncReports(List<String> reportIds, Map<String, String> options) {
        log.info("Synchronizing reports: {}", reportIds != null ? reportIds : "all");
        
        SapBoService sourceService = serviceFactory.getSourceService();
        SapBoService targetService = serviceFactory.getTargetService();
        
        boolean forceUpdate = options != null && "true".equals(options.get("forceUpdate"));
        
        try {
            List<Report> sourceReports;
            if (reportIds != null && !reportIds.isEmpty()) {
                sourceReports = new ArrayList<>();
                for (String id : reportIds) {
                    sourceReports.add(sourceService.getReport(id));
                }
            } else {
                sourceReports = sourceService.getReports(null);
            }
            
            log.info("Found {} reports in source environment", sourceReports.size());
            
            int syncCount = 0;
            int batchSize = sapBoProperties.getSync().getBatchSize();
            List<List<Report>> batches = createBatches(sourceReports, batchSize);
            
            for (List<Report> batch : batches) {
                syncCount += processBatch(batch, targetService, forceUpdate);
            }
            
            log.info("Report synchronization completed. Total reports synchronized: {}", syncCount);
            return syncCount;
        } catch (Exception e) {
            log.error("Error during report synchronization", e);
            throw new SapBoApiException("Error during report synchronization", e);
        }
    }

    @Override
    @Retryable(value = {SapBoApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public int syncUniverses(List<String> universeIds, Map<String, String> options) {
        log.info("Synchronizing universes: {}", universeIds != null ? universeIds : "all");
        
        SapBoService sourceService = serviceFactory.getSourceService();
        SapBoService targetService = serviceFactory.getTargetService();
        
        boolean forceUpdate = options != null && "true".equals(options.get("forceUpdate"));
        
        try {
            List<Universe> sourceUniverses;
            if (universeIds != null && !universeIds.isEmpty()) {
                sourceUniverses = new ArrayList<>();
                for (String id : universeIds) {
                    sourceUniverses.add(sourceService.getUniverse(id));
                }
            } else {
                sourceUniverses = sourceService.getUniverses(null);
            }
            
            log.info("Found {} universes in source environment", sourceUniverses.size());
            
            int syncCount = 0;
            for (Universe universe : sourceUniverses) {
                try {
                    // Check if universe exists in target
                    List<SapBoObject> targetUniverses = targetService.search(universe.getName(), Collections.singletonList("universe"));
                    boolean exists = targetUniverses.stream()
                            .anyMatch(tu -> tu.getName().equals(universe.getName()));
                    
                    if (!exists || forceUpdate) {
                        // Create or update universe in target
                        log.info("{} universe: {}", exists ? "Updating" : "Creating", universe.getName());
                        targetService.saveUniverse(universe);
                        syncCount++;
                    }
                } catch (Exception e) {
                    log.error("Error synchronizing universe: {}", universe.getName(), e);
                }
            }
            
            log.info("Universe synchronization completed. Total universes synchronized: {}", syncCount);
            return syncCount;
        } catch (Exception e) {
            log.error("Error during universe synchronization", e);
            throw new SapBoApiException("Error during universe synchronization", e);
        }
    }

    @Override
    @Retryable(value = {SapBoApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public int syncConnections(List<String> connectionIds, Map<String, String> options) {
        log.info("Synchronizing connections: {}", connectionIds != null ? connectionIds : "all");
        
        SapBoService sourceService = serviceFactory.getSourceService();
        SapBoService targetService = serviceFactory.getTargetService();
        
        boolean forceUpdate = options != null && "true".equals(options.get("forceUpdate"));
        
        try {
            List<Connection> sourceConnections;
            if (connectionIds != null && !connectionIds.isEmpty()) {
                sourceConnections = new ArrayList<>();
                for (String id : connectionIds) {
                    sourceConnections.add(sourceService.getConnection(id));
                }
            } else {
                sourceConnections = sourceService.getConnections();
            }
            
            log.info("Found {} connections in source environment", sourceConnections.size());
            
            int syncCount = 0;
            for (Connection connection : sourceConnections) {
                try {
                    // Check if connection exists in target
                    List<SapBoObject> targetConnections = targetService.search(connection.getName(), Collections.singletonList("connection"));
                    boolean exists = targetConnections.stream()
                            .anyMatch(tc -> tc.getName().equals(connection.getName()));
                    
                    if (!exists || forceUpdate) {
                        // Create or update connection in target
                        log.info("{} connection: {}", exists ? "Updating" : "Creating", connection.getName());
                        targetService.saveConnection(connection);
                        syncCount++;
                    }
                } catch (Exception e) {
                    log.error("Error synchronizing connection: {}", connection.getName(), e);
                }
            }
            
            log.info("Connection synchronization completed. Total connections synchronized: {}", syncCount);
            return syncCount;
        } catch (Exception e) {
            log.error("Error during connection synchronization", e);
            throw new SapBoApiException("Error during connection synchronization", e);
        }
    }
    
    /**
     * Process a batch of reports asynchronously
     */
    @Async
    private int processBatch(List<Report> batch, SapBoService targetService, boolean forceUpdate) {
        log.debug("Processing batch of {} reports", batch.size());
        
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        
        for (Report report : batch) {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    // Check if report exists in target
                    List<SapBoObject> targetReports = targetService.search(report.getName(), Collections.singletonList("report"));
                    boolean exists = targetReports.stream()
                            .anyMatch(tr -> tr.getName().equals(report.getName()));
                    
                    if (!exists || forceUpdate) {
                        // Create or update report in target
                        log.info("{} report: {}", exists ? "Updating" : "Creating", report.getName());
                        targetService.saveReport(report);
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    log.error("Error synchronizing report: {}", report.getName(), e);
                    return false;
                }
            });
            
            futures.add(future);
        }
        
        try {
            // Wait for all futures to complete
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0])
            );
            
            allFutures.get(); // Wait for all to complete
            
            // Count successful syncs
            return (int) futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Boolean::booleanValue)
                    .count();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error processing batch", e);
            return 0;
        }
    }
    
    /**
     * Create batches from a list
     */
    private <T> List<List<T>> createBatches(List<T> items, int batchSize) {
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        
        int totalItems = items.size();
        int totalBatches = (totalItems + batchSize - 1) / batchSize; // Ceiling division
        
        return new ArrayList<>(
                items.stream()
                        .collect(Collectors.groupingBy(item -> items.indexOf(item) / batchSize))
                        .values()
        );
    }
}
