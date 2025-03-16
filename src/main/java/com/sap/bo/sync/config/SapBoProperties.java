package com.sap.bo.sync.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for SAP Business Objects environments
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sap.bo")
public class SapBoProperties {
    
    private BoEnvironment source;
    private BoEnvironment target;
    private SyncProperties sync = new SyncProperties();
    
    // Explicit getters and setters to ensure they're available during compilation
    public BoEnvironment getSource() {
        return source;
    }
    
    public void setSource(BoEnvironment source) {
        this.source = source;
    }
    
    public BoEnvironment getTarget() {
        return target;
    }
    
    public void setTarget(BoEnvironment target) {
        this.target = target;
    }
    
    public SyncProperties getSync() {
        return sync;
    }
    
    public void setSync(SyncProperties sync) {
        this.sync = sync;
    }
    
    /**
     * Configuration for a single BO environment
     */
    @Data
    public static class BoEnvironment {
        private String url;
        private String username;
        private String password;
        private String authType;
        
        // Explicit getters and setters
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getAuthType() {
            return authType;
        }
        
        public void setAuthType(String authType) {
            this.authType = authType;
        }
    }
    
    /**
     * Configuration for synchronization properties
     */
    @Data
    public static class SyncProperties {
        private boolean enabled = true;
        private boolean forceUpdate = false;
        private ScheduleProperties schedule = new ScheduleProperties();
        private ObjectsProperties objects = new ObjectsProperties();
        private int batchSize = 10; // Default batch size
        
        // Explicit getters and setters
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public boolean isForceUpdate() {
            return forceUpdate;
        }
        
        public void setForceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }
        
        public ScheduleProperties getSchedule() {
            return schedule;
        }
        
        public void setSchedule(ScheduleProperties schedule) {
            this.schedule = schedule;
        }
        
        public ObjectsProperties getObjects() {
            return objects;
        }
        
        public void setObjects(ObjectsProperties objects) {
            this.objects = objects;
        }
        
        public int getBatchSize() {
            return batchSize;
        }
        
        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }
    }
    
    /**
     * Configuration for schedule properties
     */
    @Data
    public static class ScheduleProperties {
        private boolean enabled = false;
        private String cron = "0 0 1 * * ?"; // Default: 1 AM daily
        
        // Explicit getters and setters
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getCron() {
            return cron;
        }
        
        public void setCron(String cron) {
            this.cron = cron;
        }
    }
    
    /**
     * Configuration for object synchronization properties
     */
    @Data
    public static class ObjectsProperties {
        private boolean reports = true;
        private boolean universes = true;
        private boolean connections = true;
        private boolean folders = true;
        
        // Explicit getters and setters
        public boolean isReports() {
            return reports;
        }
        
        public void setReports(boolean reports) {
            this.reports = reports;
        }
        
        public boolean isUniverses() {
            return universes;
        }
        
        public void setUniverses(boolean universes) {
            this.universes = universes;
        }
        
        public boolean isConnections() {
            return connections;
        }
        
        public void setConnections(boolean connections) {
            this.connections = connections;
        }
        
        public boolean isFolders() {
            return folders;
        }
        
        public void setFolders(boolean folders) {
            this.folders = folders;
        }
    }
}
