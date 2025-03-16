package com.sap.bo.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * Represents a SAP Business Objects Connection
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Connection extends SapBoObject {
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("dataSourceType")
    private String dataSourceType;
    
    @JsonProperty("server")
    private String server;
    
    @JsonProperty("database")
    private String database;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("authentication")
    private String authentication;
    
    @JsonProperty("parameters")
    private Map<String, String> parameters;
    
    @JsonProperty("status")
    private String status;
    
    // Explicit getters and setters to ensure they're available during compilation
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDataSourceType() {
        return dataSourceType;
    }
    
    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
    
    public String getServer() {
        return server;
    }
    
    public void setServer(String server) {
        this.server = server;
    }
    
    public String getDatabase() {
        return database;
    }
    
    public void setDatabase(String database) {
        this.database = database;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getAuthentication() {
        return authentication;
    }
    
    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
