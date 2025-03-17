package com.sap.bo.sync.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Service interface for synchronizing SAP Business Objects between environments
 */
public interface SyncService {

    /**
     * Synchronize all objects between source and target environments
     * @param forceUpdate If true, update objects even if they already exist
     * @return Number of objects synchronized
     */
    int syncAll(boolean forceUpdate);
    
    /**
     * Synchronize folders between source and target environments
     * @param path Optional path to synchronize from
     * @return Number of folders synchronized
     */
    int syncFolders(String path);
    
    /**
     * Synchronize reports between source and target environments
     * @param reportIds List of report IDs to synchronize, or null for all
     * @param options Additional options for synchronization
     * @return Number of reports synchronized
     */
    int syncReports(List<String> reportIds, Map<String, String> options);
    
    /**
     * Synchronize universes between source and target environments
     * @param universeIds List of universe IDs to synchronize, or null for all
     * @param options Additional options for synchronization
     * @return Number of universes synchronized
     */
    int syncUniverses(List<String> universeIds, Map<String, String> options);
    
    /**
     * Synchronize connections between source and target environments
     * @param connectionIds List of connection IDs to synchronize, or null for all
     * @param options Additional options for synchronization
     * @return Number of connections synchronized
     */
    int syncConnections(List<String> connectionIds, Map<String, String> options);
    
    /**
     * Synchronize connections between source and target environments with timestamp filter
     * @param modifiedAfter Only synchronize connections modified after this date
     * @param options Additional options for synchronization
     * @return Number of connections synchronized
     */
    int syncConnections(Date modifiedAfter, Map<String, String> options);
    
    /**
     * Synchronize reports between source and target environments with folder and timestamp filters
     * @param folderIds List of folder IDs to synchronize reports from, or null for all
     * @param modifiedAfter Only synchronize reports modified after this date
     * @param options Additional options for synchronization
     * @return Number of reports synchronized
     */
    int syncReports(List<String> folderIds, Date modifiedAfter, Map<String, String> options);
    
    /**
     * Synchronize universes between source and target environments with folder and timestamp filters
     * @param folderIds List of folder IDs to synchronize universes from, or null for all
     * @param modifiedAfter Only synchronize universes modified after this date
     * @param options Additional options for synchronization
     * @return Number of universes synchronized
     */
    int syncUniverses(List<String> folderIds, Date modifiedAfter, Map<String, String> options);
    
    /**
     * Synchronize dependencies of a universe between source and target environments
     * @param universeId ID of the universe to synchronize dependencies for
     * @param dependencyTypes List of dependency types to synchronize, or null for all
     * @return Number of dependencies synchronized
     */
    int syncUniverseDependencies(String universeId, List<String> dependencyTypes);
    
    /**
     * Perform incremental synchronization between source and target environments
     * @param modifiedAfter Only synchronize objects modified after this date
     * @param folderIds List of folder IDs to synchronize from, or null for all
     * @param forceUpdate If true, update objects even if they already exist
     * @return Number of objects synchronized
     */
    int syncIncremental(Date modifiedAfter, List<String> folderIds, boolean forceUpdate);
    
    /**
     * Compare server configurations between source and target environments
     * @param configType Type of configuration to compare (e.g., "server", "cluster", "database")
     * @param options Additional options for filtering
     * @return Comparison result as JSON
     */
    JsonNode compareServerConfigs(String configType, Map<String, String> options);
    
    /**
     * Compare cluster configurations between source and target environments
     * @param clusterId Optional cluster ID to compare configuration for
     * @param options Additional options for filtering
     * @return Comparison result as JSON
     */
    JsonNode compareClusterConfigs(String clusterId, Map<String, String> options);
    
    /**
     * Compare configurations between two custom environments
     * @param env1 First environment name
     * @param env2 Second environment name
     * @param configType Type of configuration to compare
     * @param options Additional options for filtering
     * @return Comparison result as JSON
     */
    JsonNode compareConfigs(String env1, String env2, String configType, Map<String, String> options);
}
