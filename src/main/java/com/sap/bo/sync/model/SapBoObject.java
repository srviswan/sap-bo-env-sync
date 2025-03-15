package com.sap.bo.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Base class for SAP Business Objects objects
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SapBoObject {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("cuid")
    private String cuid;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("parentId")
    private String parentId;
    
    @JsonProperty("parentCuid")
    private String parentCuid;
    
    @JsonProperty("created")
    private Date created;
    
    @JsonProperty("modified")
    private Date modified;
    
    @JsonProperty("owner")
    private String owner;
    
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("properties")
    private Map<String, Object> properties;
    
    @JsonProperty("children")
    private List<SapBoObject> children;
    
    // Explicit getters to ensure they're available during compilation
    public String getId() {
        return id;
    }
    
    public String getCuid() {
        return cuid;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getType() {
        return type;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public String getParentCuid() {
        return parentCuid;
    }
    
    public Date getCreated() {
        return created;
    }
    
    public Date getModified() {
        return modified;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public String getPath() {
        return path;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public List<SapBoObject> getChildren() {
        return children;
    }
}
