package com.sap.bo.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Represents a SAP Business Objects Universe
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Universe extends SapBoObject {
    
    @JsonProperty("connectionId")
    private String connectionId;
    
    @JsonProperty("connectionName")
    private String connectionName;
    
    @JsonProperty("dataSourceType")
    private String dataSourceType;
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("objects")
    private List<UniverseObject> objects;
    
    // Explicit getters to ensure they're available during compilation
    public String getConnectionId() {
        return connectionId;
    }
    
    public String getConnectionName() {
        return connectionName;
    }
    
    public String getDataSourceType() {
        return dataSourceType;
    }
    
    public String getContent() {
        return content;
    }
    
    public List<UniverseObject> getObjects() {
        return objects;
    }
    
    /**
     * Represents an object in a Universe (dimension, measure, etc.)
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UniverseObject {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("sql")
        private String sql;
        
        // Explicit getters
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getType() {
            return type;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getSql() {
            return sql;
        }
    }
}
