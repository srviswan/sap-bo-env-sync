package com.sap.bo.sync.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bo.sync.client.SapBoRestClient;
import com.sap.bo.sync.config.SapBoProperties;
import com.sap.bo.sync.exception.SapBoApiException;
import com.sap.bo.sync.model.Connection;
import com.sap.bo.sync.model.Report;
import com.sap.bo.sync.model.SapBoObject;
import com.sap.bo.sync.model.Universe;
import com.sap.bo.sync.service.SapBoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the SAP Business Objects service
 */
@Service
public class SapBoServiceImpl implements SapBoService {

    private static final Logger log = LoggerFactory.getLogger(SapBoServiceImpl.class);

    private final SapBoRestClient restClient;
    private final SapBoProperties sapBoProperties;
    private final ObjectMapper objectMapper;
    
    // Environment to use for this service instance
    private SapBoProperties.BoEnvironment environment;

    /**
     * Constructor that uses the source environment by default
     */
    public SapBoServiceImpl(SapBoRestClient restClient, SapBoProperties sapBoProperties, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.sapBoProperties = sapBoProperties;
        this.objectMapper = objectMapper;
        // Default to source environment with null check
        this.environment = new SapBoProperties.BoEnvironment(); // Create default environment
        if (sapBoProperties != null && sapBoProperties.getSource() != null) {
            this.environment = sapBoProperties.getSource();
        }
    }
    
    /**
     * Constructor that uses the specified environment
     */
    public SapBoServiceImpl(SapBoRestClient restClient, SapBoProperties sapBoProperties, ObjectMapper objectMapper, 
                           SapBoProperties.BoEnvironment environment) {
        this.restClient = restClient;
        this.sapBoProperties = sapBoProperties;
        this.objectMapper = objectMapper;
        this.environment = environment;
    }

    @Override
    public List<SapBoObject> getFolders(String path) {
        log.debug("Getting folders from path: {}", path);
        
        String endpoint = "/infostore/folders";
        if (StringUtils.isNotBlank(path)) {
            endpoint += "?path=" + path;
        }
        
        try {
            String response = restClient.get(environment, endpoint, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode entriesNode = rootNode.path("entries");
            
            List<SapBoObject> folders = new ArrayList<>();
            if (entriesNode.isArray()) {
                for (JsonNode entryNode : entriesNode) {
                    SapBoObject folder = objectMapper.treeToValue(entryNode, SapBoObject.class);
                    folders.add(folder);
                }
            }
            
            return folders;
        } catch (Exception e) {
            throw new SapBoApiException("Error getting folders", e);
        }
    }

    @Override
    public List<Report> getReports(String folderId) {
        log.debug("Getting reports from folder: {}", folderId);
        
        String endpoint = "/infostore/reports";
        if (StringUtils.isNotBlank(folderId)) {
            endpoint += "?folderId=" + folderId;
        }
        
        try {
            String response = restClient.get(environment, endpoint, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode entriesNode = rootNode.path("entries");
            
            List<Report> reports = new ArrayList<>();
            if (entriesNode.isArray()) {
                for (JsonNode entryNode : entriesNode) {
                    Report report = objectMapper.treeToValue(entryNode, Report.class);
                    reports.add(report);
                }
            }
            
            return reports;
        } catch (Exception e) {
            throw new SapBoApiException("Error getting reports", e);
        }
    }

    @Override
    public Report getReport(String reportId) {
        log.debug("Getting report with ID: {}", reportId);
        
        if (StringUtils.isBlank(reportId)) {
            throw new SapBoApiException("Report ID cannot be empty");
        }
        
        String endpoint = "/infostore/reports/" + reportId;
        
        try {
            String response = restClient.get(environment, endpoint, String.class);
            return objectMapper.readValue(response, Report.class);
        } catch (Exception e) {
            throw new SapBoApiException("Error getting report", e);
        }
    }

    @Override
    public byte[] getReportContent(String reportId) {
        log.debug("Getting content for report with ID: {}", reportId);
        
        if (StringUtils.isBlank(reportId)) {
            throw new SapBoApiException("Report ID cannot be empty");
        }
        
        String endpoint = "/infostore/reports/" + reportId + "/content";
        
        try {
            return restClient.downloadContent(environment, endpoint);
        } catch (Exception e) {
            throw new SapBoApiException("Error getting report content", e);
        }
    }

    @Override
    public Report saveReport(Report report) {
        if (report == null) {
            throw new SapBoApiException("Report cannot be null");
        }
        
        try {
            String endpoint;
            HttpMethod method;
            
            if (StringUtils.isBlank(report.getId())) {
                // Create new report
                log.debug("Creating new report: {}", report.getName());
                endpoint = "/infostore/reports";
                method = HttpMethod.POST;
            } else {
                // Update existing report
                log.debug("Updating report with ID: {}", report.getId());
                endpoint = "/infostore/reports/" + report.getId();
                method = HttpMethod.PUT;
            }
            
            String response;
            if (method == HttpMethod.POST) {
                response = restClient.post(environment, endpoint, report, String.class);
            } else {
                response = restClient.put(environment, endpoint, report, String.class);
            }
            
            return objectMapper.readValue(response, Report.class);
        } catch (Exception e) {
            throw new SapBoApiException("Error saving report", e);
        }
    }

    @Override
    public List<Universe> getUniverses(String folderId) {
        log.debug("Getting universes from folder: {}", folderId);
        
        String endpoint = "/infostore/universes";
        if (StringUtils.isNotBlank(folderId)) {
            endpoint += "?folderId=" + folderId;
        }
        
        try {
            String response = restClient.get(environment, endpoint, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode entriesNode = rootNode.path("entries");
            
            List<Universe> universes = new ArrayList<>();
            if (entriesNode.isArray()) {
                for (JsonNode entryNode : entriesNode) {
                    Universe universe = objectMapper.treeToValue(entryNode, Universe.class);
                    universes.add(universe);
                }
            }
            
            return universes;
        } catch (Exception e) {
            throw new SapBoApiException("Error getting universes", e);
        }
    }

    @Override
    public Universe getUniverse(String universeId) {
        log.debug("Getting universe with ID: {}", universeId);
        
        if (StringUtils.isBlank(universeId)) {
            throw new SapBoApiException("Universe ID cannot be empty");
        }
        
        String endpoint = "/infostore/universes/" + universeId;
        
        try {
            String response = restClient.get(environment, endpoint, String.class);
            return objectMapper.readValue(response, Universe.class);
        } catch (Exception e) {
            throw new SapBoApiException("Error getting universe", e);
        }
    }

    @Override
    public Universe saveUniverse(Universe universe) {
        if (universe == null) {
            throw new SapBoApiException("Universe cannot be null");
        }
        
        try {
            String endpoint;
            HttpMethod method;
            
            if (StringUtils.isBlank(universe.getId())) {
                // Create new universe
                log.debug("Creating new universe: {}", universe.getName());
                endpoint = "/infostore/universes";
                method = HttpMethod.POST;
            } else {
                // Update existing universe
                log.debug("Updating universe with ID: {}", universe.getId());
                endpoint = "/infostore/universes/" + universe.getId();
                method = HttpMethod.PUT;
            }
            
            String response;
            if (method == HttpMethod.POST) {
                response = restClient.post(environment, endpoint, universe, String.class);
            } else {
                response = restClient.put(environment, endpoint, universe, String.class);
            }
            
            return objectMapper.readValue(response, Universe.class);
        } catch (Exception e) {
            throw new SapBoApiException("Error saving universe", e);
        }
    }

    @Override
    public List<Connection> getConnections() {
        log.debug("Getting all connections");
        
        String endpoint = "/infostore/connections";
        
        try {
            String response = restClient.get(environment, endpoint, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode entriesNode = rootNode.path("entries");
            
            List<Connection> connections = new ArrayList<>();
            if (entriesNode.isArray()) {
                for (JsonNode entryNode : entriesNode) {
                    Connection connection = objectMapper.treeToValue(entryNode, Connection.class);
                    connections.add(connection);
                }
            }
            
            return connections;
        } catch (Exception e) {
            throw new SapBoApiException("Error getting connections", e);
        }
    }

    @Override
    public Connection getConnection(String connectionId) {
        log.debug("Getting connection with ID: {}", connectionId);
        
        if (StringUtils.isBlank(connectionId)) {
            throw new SapBoApiException("Connection ID cannot be empty");
        }
        
        String endpoint = "/infostore/connections/" + connectionId;
        
        try {
            String response = restClient.get(environment, endpoint, String.class);
            return objectMapper.readValue(response, Connection.class);
        } catch (Exception e) {
            throw new SapBoApiException("Error getting connection", e);
        }
    }

    @Override
    public Connection saveConnection(Connection connection) {
        if (connection == null) {
            throw new SapBoApiException("Connection cannot be null");
        }
        
        try {
            String endpoint;
            HttpMethod method;
            
            if (StringUtils.isBlank(connection.getId())) {
                // Create new connection
                log.debug("Creating new connection: {}", connection.getName());
                endpoint = "/infostore/connections";
                method = HttpMethod.POST;
            } else {
                // Update existing connection
                log.debug("Updating connection with ID: {}", connection.getId());
                endpoint = "/infostore/connections/" + connection.getId();
                method = HttpMethod.PUT;
            }
            
            String response;
            if (method == HttpMethod.POST) {
                response = restClient.post(environment, endpoint, connection, String.class);
            } else {
                response = restClient.put(environment, endpoint, connection, String.class);
            }
            
            return objectMapper.readValue(response, Connection.class);
        } catch (Exception e) {
            throw new SapBoApiException("Error saving connection", e);
        }
    }

    @Override
    public List<SapBoObject> search(String query, List<String> objectTypes) {
        log.debug("Searching for objects with query: {} and types: {}", query, objectTypes);
        
        if (StringUtils.isBlank(query)) {
            throw new SapBoApiException("Search query cannot be empty");
        }
        
        String endpoint = "/infostore/search?query=" + query;
        
        if (objectTypes != null && !objectTypes.isEmpty()) {
            endpoint += "&types=" + String.join(",", objectTypes);
        }
        
        try {
            String response = restClient.get(environment, endpoint, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode entriesNode = rootNode.path("entries");
            
            List<SapBoObject> results = new ArrayList<>();
            if (entriesNode.isArray()) {
                for (JsonNode entryNode : entriesNode) {
                    SapBoObject object = objectMapper.treeToValue(entryNode, SapBoObject.class);
                    results.add(object);
                }
            }
            
            return results;
        } catch (Exception e) {
            throw new SapBoApiException("Error searching for objects", e);
        }
    }
    
    // Helper enum for HTTP methods
    private enum HttpMethod {
        GET, POST, PUT, DELETE
    }
}
