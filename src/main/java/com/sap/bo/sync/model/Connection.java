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
    
    // Explicit getters to ensure they're available during compilation
    @Override
    public String getType() {
        return type;
    }
    
    public String getDataSourceType() {
        return dataSourceType;
    }
    
    public String getServer() {
        return server;
    }
    
    public String getDatabase() {
        return database;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getAuthentication() {
        return authentication;
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    public String getStatus() {
        return status;
    }
}
