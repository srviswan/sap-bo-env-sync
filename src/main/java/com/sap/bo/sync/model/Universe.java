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
    
    // Explicit getters and setters to ensure they're available during compilation
    public String getConnectionId() {
        return connectionId;
    }
    
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getConnectionName() {
        return connectionName;
    }
    
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }
    
    public String getDataSourceType() {
        return dataSourceType;
    }
    
    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public List<UniverseObject> getObjects() {
        return objects;
    }
    
    public void setObjects(List<UniverseObject> objects) {
        this.objects = objects;
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
        
        // Explicit getters and setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getSql() {
            return sql;
        }
        
        public void setSql(String sql) {
            this.sql = sql;
        }
    }
}
