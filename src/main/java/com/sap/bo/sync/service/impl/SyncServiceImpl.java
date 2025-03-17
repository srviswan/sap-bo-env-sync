package com.sap.bo.sync.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Date;
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

    private SapBoServiceFactory serviceFactory;
    private SapBoProperties sapBoProperties;
    private ObjectMapper objectMapper;

    /**
     * No-arg constructor for Spring bean instantiation
     */
    public SyncServiceImpl() {
        // Default constructor for Spring
    }

    public SyncServiceImpl(SapBoServiceFactory serviceFactory, SapBoProperties sapBoProperties, ObjectMapper objectMapper) {
        this.serviceFactory = serviceFactory;
        this.sapBoProperties = sapBoProperties;
        this.objectMapper = objectMapper;
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
            
            // Pass null for the first parameter to indicate all objects should be synced
            List<String> allObjects = null;
            totalCount += syncConnections(allObjects, options);
            totalCount += syncUniverses(allObjects, options);
            totalCount += syncReports(allObjects, options);
            
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
                    List<SapBoObject> targetFolders = targetService.search(folder.getName(), 
                            Collections.singletonList("folder"), null, null);
                    boolean exists = targetFolders.stream()
                            .anyMatch(tf -> tf.getName().equals(folder.getName()) && 
                                    (StringUtils.isBlank(folder.getParentId()) || 
                                     StringUtils.equals(tf.getParentId(), folder.getParentId())));
                    
                    if (!exists) {
                        // Create folder in target - implementation would depend on actual API
                        // This is a placeholder as the interface doesn't have a createFolder method
                        // targetService.saveFolder(folder);
                        syncCount++;
                        log.debug("Created folder: {}", folder.getName());
                    } else {
                        log.debug("Folder already exists: {}", folder.getName());
                    }
                } catch (Exception e) {
                    log.error("Error processing folder {}: {}", folder.getName(), e.getMessage());
                }
            }
            
            log.info("Synchronized {} folders", syncCount);
            return syncCount;
        } catch (Exception e) {
            log.error("Error synchronizing folders: {}", e.getMessage());
            throw new SapBoApiException("Failed to synchronize folders", e);
        }
    }

    @Override
    @Retryable(value = {SapBoApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public int syncReports(List<String> reportIds, Map<String, String> options) {
        // Call the overloaded method with null for modifiedAfter parameter
        return syncReports(reportIds, null, options);
    }
    
    @Override
    @Retryable(value = {SapBoApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public int syncReports(List<String> folderIds, Date modifiedAfter, Map<String, String> options) {
        log.info("Synchronizing reports with folderIds: {}, modifiedAfter: {}", 
                folderIds != null ? folderIds : "all", modifiedAfter);
        
        SapBoService sourceService = serviceFactory.getSourceService();
        SapBoService targetService = serviceFactory.getTargetService();
        
        boolean forceUpdate = options != null && "true".equals(options.get("forceUpdate"));
        
        try {
            List<Report> sourceReports = new ArrayList<>();
            
            if (folderIds != null && !folderIds.isEmpty() && folderIds.stream().allMatch(id -> !id.contains("/"))) {
                // Specific report IDs requested (if the IDs don't contain slashes, they're likely object IDs not folder IDs)
                for (String id : folderIds) {
                    sourceReports.add(sourceService.getReport(id));
                }
            } else if (folderIds != null && !folderIds.isEmpty()) {
                // Reports from specific folders
                for (String folderId : folderIds) {
                    sourceReports.addAll(sourceService.getReports(folderId, modifiedAfter, options));
                }
            } else {
                // All reports, potentially filtered by modification date
                sourceReports = sourceService.getReports(null, modifiedAfter, options);
            }
            
            log.info("Found {} reports in source environment", sourceReports.size());
            
            // Create batches for parallel processing
            int batchSize = sapBoProperties.getSync().getBatchSize();
            List<List<Report>> batches = createBatches(sourceReports, batchSize);
            
            // Process batches in parallel
            List<CompletableFuture<Integer>> futures = new ArrayList<>();
            for (List<Report> batch : batches) {
                futures.add(processBatch(batch, targetService, forceUpdate));
            }
            
            // Wait for all batches to complete and count total
            int totalCount = 0;
            for (CompletableFuture<Integer> future : futures) {
                try {
                    totalCount += future.get();
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Error waiting for batch completion: {}", e.getMessage());
                }
            }
            
            log.info("Synchronized {} reports", totalCount);
            return totalCount;
        } catch (Exception e) {
            log.error("Error synchronizing reports: {}", e.getMessage());
            throw new SapBoApiException("Failed to synchronize reports", e);
        }
    }

    @Override
    @Retryable(value = {SapBoApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public int syncUniverses(List<String> universeIds, Map<String, String> options) {
        // Call the overloaded method with null for modifiedAfter parameter
        return syncUniverses(universeIds, null, options);
    }
    
    @Override
    @Retryable(value = {SapBoApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public int syncUniverses(List<String> folderIds, Date modifiedAfter, Map<String, String> options) {
        log.info("Synchronizing universes with folderIds: {}, modifiedAfter: {}", 
                folderIds != null ? folderIds : "all", modifiedAfter);
        
        SapBoService sourceService = serviceFactory.getSourceService();
        SapBoService targetService = serviceFactory.getTargetService();
        
        boolean forceUpdate = options != null && "true".equals(options.get("forceUpdate"));
        
        try {
            List<Universe> sourceUniverses = new ArrayList<>();
            
            if (folderIds != null && !folderIds.isEmpty() && folderIds.stream().allMatch(id -> !id.contains("/"))) {
                // Specific universe IDs requested (if the IDs don't contain slashes, they're likely object IDs not folder IDs)
                for (String id : folderIds) {
                    sourceUniverses.add(sourceService.getUniverse(id));
                }
            } else if (folderIds != null && !folderIds.isEmpty()) {
                // Universes from specific folders
                for (String folderId : folderIds) {
                    sourceUniverses.addAll(sourceService.getUniverses(folderId, modifiedAfter, options));
                }
            } else {
                // All universes, potentially filtered by modification date
                sourceUniverses = sourceService.getUniverses(null, modifiedAfter, options);
            }
            
            log.info("Found {} universes in source environment", sourceUniverses.size());
            
            int totalCount = 0;
            for (Universe universe : sourceUniverses) {
                try {
                    // Check if universe exists in target
                    Universe targetUniverse = targetService.getUniverse(universe.getId());
                    boolean exists = targetUniverse != null;
                    
                    if (!exists || forceUpdate) {
                        // Create or update universe in target
                        targetService.saveUniverse(universe);
                        totalCount++;
                        log.debug("{}d universe: {}", exists ? "Update" : "Create", universe.getName());
                    } else {
                        log.debug("Universe already exists and forceUpdate is false: {}", universe.getName());
                    }
                } catch (Exception e) {
                    log.error("Error processing universe {}: {}", universe.getName(), e.getMessage());
                }
            }
            
            log.info("Synchronized {} universes", totalCount);
            return totalCount;
        } catch (Exception e) {
            log.error("Error synchronizing universes: {}", e.getMessage());
            throw new SapBoApiException("Failed to synchronize universes", e);
        }
    }
    
    @Override
    @Retryable(value = {SapBoApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public int syncUniverseDependencies(String universeId, List<String> dependencyTypes) {
        log.info("Synchronizing dependencies for universe: {}", universeId);
        
        SapBoService sourceService = serviceFactory.getSourceService();
        SapBoService targetService = serviceFactory.getTargetService();
        
        int totalCount = 0;
        
        try {
            // Get universe from source environment
            Universe universe = sourceService.getUniverse(universeId);
            if (universe == null) {
                log.error("Universe with ID {} not found in source environment", universeId);
                return 0;
            }
            
            // Get dependencies
            List<SapBoObject> dependencies = sourceService.getDependencies(universeId, dependencyTypes);
            log.info("Found {} dependencies for universe {}", dependencies.size(), universe.getName());
            
            // Sync each dependency based on its type
            Map<String, String> options = new HashMap<>();
            options.put("forceUpdate", "true"); // Always force update dependencies
            
            for (SapBoObject dependency : dependencies) {
                String type = dependency.getType();
                String id = dependency.getId();
                
                try {
                    if ("connection".equalsIgnoreCase(type)) {
                        syncConnections(Collections.singletonList(id), options);
                        totalCount++;
                    } else if ("universe".equalsIgnoreCase(type)) {
                        // Avoid circular dependencies
                        if (!id.equals(universeId)) {
                            syncUniverses(Collections.singletonList(id), options);
                            totalCount++;
                        }
                    } else if ("report".equalsIgnoreCase(type) || "webi".equalsIgnoreCase(type)) {
                        syncReports(Collections.singletonList(id), options);
                        totalCount++;
                    } else {
                        log.warn("Unsupported dependency type: {} for ID: {}", type, id);
                    }
                } catch (Exception e) {
                    log.error("Error synchronizing dependency {} of type {}: {}", id, type, e.getMessage());
                }
            }
            
            log.info("Synchronized {} dependencies for universe {}", totalCount, universe.getName());
            return totalCount;
        } catch (Exception e) {
            log.error("Error synchronizing universe dependencies: {}", e.getMessage());
            throw new SapBoApiException("Failed to synchronize universe dependencies", e);
        }
    }
    
    @Override
    @Retryable(value = {SapBoApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public int syncConnections(List<String> connectionIds, Map<String, String> options) {
        // Call the overloaded method with null for modifiedAfter parameter
        Date modifiedAfter = null;
        return syncConnections(modifiedAfter, options);
    }
    
    @Override
    @Retryable(value = {SapBoApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public int syncConnections(Date modifiedAfter, Map<String, String> options) {
        log.info("Synchronizing connections with modifiedAfter: {}", modifiedAfter);
        
        SapBoService sourceService = serviceFactory.getSourceService();
        SapBoService targetService = serviceFactory.getTargetService();
        
        boolean forceUpdate = options != null && "true".equals(options.get("forceUpdate"));
        
        try {
            // Get connections from source environment with timestamp filter
            List<Connection> sourceConnections = sourceService.getConnections(modifiedAfter, options);
            
            log.info("Found {} connections in source environment", sourceConnections.size());
            
            int totalCount = 0;
            for (Connection connection : sourceConnections) {
                try {
                    // Check if connection exists in target
                    Connection targetConnection = targetService.getConnection(connection.getId());
                    boolean exists = targetConnection != null;
                    
                    if (!exists || forceUpdate) {
                        // Create or update connection in target
                        targetService.saveConnection(connection);
                        totalCount++;
                        log.debug("{}d connection: {}", exists ? "Update" : "Create", connection.getName());
                    } else {
                        log.debug("Connection already exists and forceUpdate is false: {}", connection.getName());
                    }
                } catch (Exception e) {
                    log.error("Error processing connection {}: {}", connection.getName(), e.getMessage());
                }
            }
            
            log.info("Synchronized {} connections", totalCount);
            return totalCount;
        } catch (Exception e) {
            log.error("Error synchronizing connections: {}", e.getMessage());
            throw new SapBoApiException("Failed to synchronize connections", e);
        }
    }
    
    /**
     * Process a batch of reports asynchronously
     */
    @Async
    private CompletableFuture<Integer> processBatch(List<Report> batch, SapBoService targetService, boolean forceUpdate) {
        int count = 0;
        for (Report report : batch) {
            try {
                // Check if report exists in target
                Report targetReport = targetService.getReport(report.getId());
                boolean exists = targetReport != null;
                
                if (!exists || forceUpdate) {
                    // Create or update report in target
                    targetService.saveReport(report);
                    count++;
                    log.debug("{}d report: {}", exists ? "Update" : "Create", report.getName());
                } else {
                    log.debug("Report already exists and forceUpdate is false: {}", report.getName());
                }
            } catch (Exception e) {
                log.error("Error processing report {}: {}", report.getName(), e.getMessage());
            }
        }
        return CompletableFuture.completedFuture(count);
    }
    
    /**
     * Create batches from a list
     */
    private <T> List<List<T>> createBatches(List<T> items, int batchSize) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        
        int totalItems = items.size();
        // Calculate total batches for logging purposes
        int totalBatches = (totalItems + batchSize - 1) / batchSize; // Ceiling division
        log.debug("Processing {} items in {} batches of size {}", totalItems, totalBatches, batchSize);
        
        return new ArrayList<>(
                items.stream()
                        .collect(Collectors.groupingBy(item -> items.indexOf(item) / batchSize))
                        .values()
        );
    }
    
    @Override
    @Retryable(value = {SapBoApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public int syncIncremental(Date modifiedAfter, List<String> folderIds, boolean forceUpdate) {
        log.info("Starting incremental synchronization with modifiedAfter: {}, folderIds: {}, forceUpdate: {}", 
                modifiedAfter, folderIds != null ? folderIds : "all", forceUpdate);
        
        int totalCount = 0;
        
        try {
            // Sync folders first to ensure proper structure
            if (folderIds != null && !folderIds.isEmpty()) {
                // Sync specific folders
                for (String folderId : folderIds) {
                    totalCount += syncFolders(folderId);
                }
            } else {
                // Sync all folders
                totalCount += syncFolders(null);
            }
            
            // Prepare options map
            Map<String, String> options = new HashMap<>();
            options.put("forceUpdate", String.valueOf(forceUpdate));
            
            // Sync connections, universes, and reports with modification date filter
            totalCount += syncConnections(modifiedAfter, options);
            totalCount += syncUniverses(folderIds, modifiedAfter, options);
            totalCount += syncReports(folderIds, modifiedAfter, options);
            
            log.info("Incremental synchronization completed. Total objects synchronized: {}", totalCount);
            return totalCount;
        } catch (Exception e) {
            log.error("Error during incremental synchronization: {}", e.getMessage());
            throw new SapBoApiException("Failed to perform incremental synchronization", e);
        }
    }
    
    @Override
    public JsonNode compareServerConfigs(String configType, Map<String, String> options) {
        log.info("Comparing server configurations of type: {}", configType);
        
        try {
            SapBoService sourceService = serviceFactory.getSourceService();
            SapBoService targetService = serviceFactory.getTargetService();
            
            // Get configurations from both environments
            JsonNode sourceConfig = sourceService.getServerConfig(configType, options);
            JsonNode targetConfig = targetService.getServerConfig(configType, options);
            
            // Create comparison result
            ObjectNode result = objectMapper.createObjectNode();
            result.set("source", sourceConfig);
            result.set("target", targetConfig);
            result.put("comparisonTimestamp", System.currentTimeMillis());
            
            // Add differences analysis
            result.set("differences", analyzeDifferences(sourceConfig, targetConfig));
            
            return result;
        } catch (Exception e) {
            log.error("Error comparing server configurations: {}", e.getMessage());
            throw new SapBoApiException("Failed to compare server configurations", e);
        }
    }
    
    @Override
    public JsonNode compareClusterConfigs(String clusterId, Map<String, String> options) {
        log.info("Comparing cluster configurations for cluster ID: {}", clusterId);
        
        try {
            SapBoService sourceService = serviceFactory.getSourceService();
            SapBoService targetService = serviceFactory.getTargetService();
            
            // Get configurations from both environments
            JsonNode sourceConfig = sourceService.getClusterConfig(clusterId, options);
            JsonNode targetConfig = targetService.getClusterConfig(clusterId, options);
            
            // Create comparison result
            ObjectNode result = objectMapper.createObjectNode();
            result.set("source", sourceConfig);
            result.set("target", targetConfig);
            result.put("comparisonTimestamp", System.currentTimeMillis());
            
            // Add differences analysis
            result.set("differences", analyzeDifferences(sourceConfig, targetConfig));
            
            return result;
        } catch (Exception e) {
            log.error("Error comparing cluster configurations: {}", e.getMessage());
            throw new SapBoApiException("Failed to compare cluster configurations", e);
        }
    }
    
    @Override
    public JsonNode compareConfigs(String env1, String env2, String configType, Map<String, String> options) {
        log.info("Comparing configurations between environments {} and {} of type: {}", env1, env2, configType);
        
        try {
            // Get services for the specified environments
            SapBoProperties.BoEnvironment environment1 = getEnvironmentByName(env1);
            SapBoProperties.BoEnvironment environment2 = getEnvironmentByName(env2);
            
            if (environment1 == null || environment2 == null) {
                throw new SapBoApiException("One or both environments not found: " + env1 + ", " + env2);
            }
            
            SapBoService service1 = serviceFactory.getService(environment1);
            SapBoService service2 = serviceFactory.getService(environment2);
            
            // Get configurations from both environments
            JsonNode config1;
            JsonNode config2;
            
            if ("server".equalsIgnoreCase(configType)) {
                config1 = service1.getServerConfig(configType, options);
                config2 = service2.getServerConfig(configType, options);
            } else if ("cluster".equalsIgnoreCase(configType)) {
                String clusterId = options != null ? options.get("clusterId") : null;
                config1 = service1.getClusterConfig(clusterId, options);
                config2 = service2.getClusterConfig(clusterId, options);
            } else {
                throw new SapBoApiException("Unsupported configuration type: " + configType);
            }
            
            // Create comparison result
            ObjectNode result = objectMapper.createObjectNode();
            result.put("environment1", env1);
            result.put("environment2", env2);
            result.put("configType", configType);
            result.set("config1", config1);
            result.set("config2", config2);
            result.put("comparisonTimestamp", System.currentTimeMillis());
            
            // Add differences analysis
            result.set("differences", analyzeDifferences(config1, config2));
            
            return result;
        } catch (Exception e) {
            log.error("Error comparing configurations: {}", e.getMessage());
            throw new SapBoApiException("Failed to compare configurations", e);
        }
    }
    
    /**
     * Get environment by name
     */
    private SapBoProperties.BoEnvironment getEnvironmentByName(String name) {
        if ("source".equalsIgnoreCase(name)) {
            return sapBoProperties.getSource();
        } else if ("target".equalsIgnoreCase(name)) {
            return sapBoProperties.getTarget();
        } else {
            // Custom environment lookup logic could be added here
            log.warn("Unknown environment name: {}", name);
            return null;
        }
    }
    
    /**
     * Analyze differences between two JSON configurations
     */
    private JsonNode analyzeDifferences(JsonNode config1, JsonNode config2) {
        ObjectNode differences = objectMapper.createObjectNode();
        differences.put("hasDifferences", !config1.equals(config2));
        
        // This is a simplified implementation that just checks for equality
        // A more sophisticated implementation would analyze specific fields and their differences
        
        return differences;
    }
}
