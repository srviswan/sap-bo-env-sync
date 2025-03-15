package com.sap.bo.sync.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous service interface for SAP Business Objects synchronization
 */
public interface AsyncSapBoService {

    /**
     * Asynchronously synchronize all objects between environments
     * @param forceUpdate If true, update objects even if they already exist
     * @return CompletableFuture with the number of objects synchronized
     */
    CompletableFuture<Integer> asyncSyncAll(boolean forceUpdate);
    
    /**
     * Asynchronously synchronize reports between environments
     * @param reportIds List of report IDs to synchronize, or null for all
     * @param options Additional options for synchronization
     * @return CompletableFuture with the number of reports synchronized
     */
    CompletableFuture<Integer> asyncSyncReports(List<String> reportIds, Map<String, String> options);
    
    /**
     * Asynchronously synchronize universes between environments
     * @param universeIds List of universe IDs to synchronize, or null for all
     * @param options Additional options for synchronization
     * @return CompletableFuture with the number of universes synchronized
     */
    CompletableFuture<Integer> asyncSyncUniverses(List<String> universeIds, Map<String, String> options);
    
    /**
     * Asynchronously synchronize connections between environments
     * @param connectionIds List of connection IDs to synchronize, or null for all
     * @param options Additional options for synchronization
     * @return CompletableFuture with the number of connections synchronized
     */
    CompletableFuture<Integer> asyncSyncConnections(List<String> connectionIds, Map<String, String> options);
}
