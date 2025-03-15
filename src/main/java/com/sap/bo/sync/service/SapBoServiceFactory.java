package com.sap.bo.sync.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bo.sync.client.SapBoRestClient;
import com.sap.bo.sync.config.SapBoProperties;
import com.sap.bo.sync.service.impl.SapBoServiceImpl;
import org.springframework.stereotype.Component;

/**
 * Factory for creating SapBoService instances for different environments
 */
@Component
public class SapBoServiceFactory {

    private final SapBoRestClient restClient;
    private final SapBoProperties sapBoProperties;
    private final ObjectMapper objectMapper;
    
    public SapBoServiceFactory(SapBoRestClient restClient, SapBoProperties sapBoProperties, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.sapBoProperties = sapBoProperties;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Get a service for the source environment
     * @return SapBoService for source environment
     */
    public SapBoService getSourceService() {
        return new SapBoServiceImpl(restClient, sapBoProperties, objectMapper, sapBoProperties.getSource());
    }
    
    /**
     * Get a service for the target environment
     * @return SapBoService for target environment
     */
    public SapBoService getTargetService() {
        return new SapBoServiceImpl(restClient, sapBoProperties, objectMapper, sapBoProperties.getTarget());
    }
    
    /**
     * Get a service for the specified environment
     * @param environment Environment to use
     * @return SapBoService for the specified environment
     */
    public SapBoService getService(SapBoProperties.BoEnvironment environment) {
        return new SapBoServiceImpl(restClient, sapBoProperties, objectMapper, environment);
    }
}
