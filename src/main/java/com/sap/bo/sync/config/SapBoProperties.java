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
    
    // Explicit getters to ensure they're available during compilation
    public BoEnvironment getSource() {
        return source;
    }
    
    public BoEnvironment getTarget() {
        return target;
    }
    
    public SyncProperties getSync() {
        return sync;
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
        
        // Explicit getters
        public String getUrl() {
            return url;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public String getAuthType() {
            return authType;
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
        
        // Explicit getters
        public boolean isEnabled() {
            return enabled;
        }
        
        public boolean isForceUpdate() {
            return forceUpdate;
        }
        
        public ScheduleProperties getSchedule() {
            return schedule;
        }
        
        public ObjectsProperties getObjects() {
            return objects;
        }
        
        public int getBatchSize() {
            return batchSize;
        }
    }
    
    /**
     * Configuration for schedule properties
     */
    @Data
    public static class ScheduleProperties {
        private boolean enabled = false;
        private String cron = "0 0 1 * * ?"; // Default: 1 AM daily
        
        // Explicit getters
        public boolean isEnabled() {
            return enabled;
        }
        
        public String getCron() {
            return cron;
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
        
        // Explicit getters
        public boolean isReports() {
            return reports;
        }
        
        public boolean isUniverses() {
            return universes;
        }
        
        public boolean isConnections() {
            return connections;
        }
        
        public boolean isFolders() {
            return folders;
        }
    }
}
