package com.sap.bo.sync.service;

import com.sap.bo.sync.model.Connection;
import com.sap.bo.sync.model.Report;
import com.sap.bo.sync.model.SapBoObject;
import com.sap.bo.sync.model.Universe;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Service interface for interacting with SAP Business Objects
 */
public interface SapBoService {

    /**
     * Get a list of folders
     * @param path Optional path to get folders from
     * @return List of folders
     */
    List<SapBoObject> getFolders(String path);
    
    /**
     * Get a list of reports
     * @param folderId Optional folder ID to get reports from
     * @param modifiedAfter Optional date to filter reports modified after this date
     * @param options Additional options for filtering
     * @return List of reports
     */
    List<Report> getReports(String folderId, Date modifiedAfter, Map<String, String> options);
    
    /**
     * Get a specific report by ID
     * @param reportId Report ID
     * @return Report object
     */
    Report getReport(String reportId);
    
    /**
     * Get report content
     * @param reportId Report ID
     * @return Report content as byte array
     */
    byte[] getReportContent(String reportId);
    
    /**
     * Create or update a report
     * @param report Report to create or update
     * @return Created or updated report
     */
    Report saveReport(Report report);
    
    /**
     * Get a list of universes
     * @param folderId Optional folder ID to get universes from
     * @param modifiedAfter Optional date to filter universes modified after this date
     * @param options Additional options for filtering
     * @return List of universes
     */
    List<Universe> getUniverses(String folderId, Date modifiedAfter, Map<String, String> options);
    
    /**
     * Get a specific universe by ID
     * @param universeId Universe ID
     * @return Universe object
     */
    Universe getUniverse(String universeId);
    
    /**
     * Create or update a universe
     * @param universe Universe to create or update
     * @return Created or updated universe
     */
    Universe saveUniverse(Universe universe);
    
    /**
     * Get a list of connections
     * @param modifiedAfter Optional date to filter connections modified after this date
     * @param options Additional options for filtering
     * @return List of connections
     */
    List<Connection> getConnections(Date modifiedAfter, Map<String, String> options);
    
    /**
     * Get a specific connection by ID
     * @param connectionId Connection ID
     * @return Connection object
     */
    Connection getConnection(String connectionId);
    
    /**
     * Create or update a connection
     * @param connection Connection to create or update
     * @return Created or updated connection
     */
    Connection saveConnection(Connection connection);
    
    /**
     * Search for objects by name or other criteria
     * @param query Search query
     * @param objectTypes Types of objects to search for
     * @param modifiedAfter Optional date to filter objects modified after this date
     * @param options Additional options for filtering
     * @return List of matching objects
     */
    List<SapBoObject> search(String query, List<String> objectTypes, Date modifiedAfter, Map<String, String> options);
    
    /**
     * Get dependent objects for a specific universe
     * @param universeId Universe ID to get dependencies for
     * @param dependencyTypes Types of dependencies to retrieve (reports, connections, etc.)
     * @return List of dependent objects
     */
    List<SapBoObject> getDependencies(String universeId, List<String> dependencyTypes);
    
    /**
     * Get server configuration details
     * @param configType Type of configuration to retrieve (e.g., "server", "cluster", "database")
     * @param options Additional options for filtering
     * @return Server configuration as JSON
     */
    JsonNode getServerConfig(String configType, Map<String, String> options);
    
    /**
     * Get cluster configuration details
     * @param clusterId Optional cluster ID to get configuration for
     * @param options Additional options for filtering
     * @return Cluster configuration as JSON
     */
    JsonNode getClusterConfig(String clusterId, Map<String, String> options);
}
