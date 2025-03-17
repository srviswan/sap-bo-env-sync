package com.sap.bo.sync.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for SSL settings
 */
@Configuration
@ConfigurationProperties(prefix = "sap.bo")
public class SslProperties {
    
    /**
     * Whether to validate SSL certificates
     */
    private boolean sslValidate = true;
    
    /**
     * Connection timeout in milliseconds
     */
    private int connectionTimeout = 30000;
    
    /**
     * Socket timeout in milliseconds
     */
    private int socketTimeout = 60000;
    
    /**
     * Get SSL validation setting
     */
    public boolean isSslValidate() {
        return sslValidate;
    }
    
    /**
     * Set SSL validation setting
     */
    public void setSslValidate(boolean sslValidate) {
        this.sslValidate = sslValidate;
    }
    
    /**
     * Get connection timeout
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    
    /**
     * Set connection timeout
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    /**
     * Get socket timeout
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }
    
    /**
     * Set socket timeout
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
}
