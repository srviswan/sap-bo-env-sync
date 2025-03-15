package com.sap.bo.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a SAP Business Objects Report
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report extends SapBoObject {
    
    @JsonProperty("universeId")
    private String universeId;
    
    @JsonProperty("universeName")
    private String universeName;
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("format")
    private String format;
    
    @JsonProperty("size")
    private Long size;
    
    @JsonProperty("schedule")
    private String schedule;
    
    @JsonProperty("lastRun")
    private String lastRun;
    
    @JsonProperty("status")
    private String status;
    
    // Explicit getters to ensure they're available during compilation
    public String getUniverseId() {
        return universeId;
    }
    
    public String getUniverseName() {
        return universeName;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getFormat() {
        return format;
    }
    
    public Long getSize() {
        return size;
    }
    
    public String getSchedule() {
        return schedule;
    }
    
    public String getLastRun() {
        return lastRun;
    }
    
    public String getStatus() {
        return status;
    }
}
