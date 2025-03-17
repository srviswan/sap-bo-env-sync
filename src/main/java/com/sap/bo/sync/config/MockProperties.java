package com.sap.bo.sync.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration properties for the mock environment
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mock")
@Profile("mock")
public class MockProperties {
    
    private DataRefreshProperties dataRefresh = new DataRefreshProperties();
    
    /**
     * Configuration for mock data refresh properties
     */
    @Data
    public static class DataRefreshProperties {
        private boolean enabled = true;
        private int interval = 3600; // Default: 1 hour in seconds
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public int getInterval() {
            return interval;
        }
        
        public void setInterval(int interval) {
            this.interval = interval;
        }
    }
}
