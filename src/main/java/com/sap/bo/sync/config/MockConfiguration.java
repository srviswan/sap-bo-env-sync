package com.sap.bo.sync.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for mock environment
 */
@Configuration
@Profile("mock")
public class MockConfiguration {

    /**
     * Create a RestTemplate bean for mock environment
     * This is needed because the SapBoRestClient requires it, but our mock implementation won't use it
     */
    @Bean("mockRestTemplate")
    public RestTemplate mockRestTemplate() {
        return new RestTemplate();
    }
    
    /**
     * Create an ObjectMapper bean for JSON processing
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
