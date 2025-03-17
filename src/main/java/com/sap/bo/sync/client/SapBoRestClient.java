package com.sap.bo.sync.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bo.sync.config.SapBoProperties;
import com.sap.bo.sync.exception.SapBoApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * REST client for communicating with SAP Business Objects API
 */
@Component
@Profile("!mock")
public class SapBoRestClient {

    private static final Logger log = LoggerFactory.getLogger(SapBoRestClient.class);
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // Authentication token cache
    private final Map<String, String> authTokens = new HashMap<>();
    
    public SapBoRestClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Get or create authentication token for the specified environment
     */
    public String getAuthToken(SapBoProperties.BoEnvironment environment) {
        String cacheKey = environment.getUrl() + ":" + environment.getUsername();
        
        if (authTokens.containsKey(cacheKey)) {
            return authTokens.get(cacheKey);
        }
        
        log.info("Authenticating to SAP BO at {}", environment.getUrl());
        
        try {
            // Create authentication request
            Map<String, String> authRequest = new HashMap<>();
            authRequest.put("userName", environment.getUsername());
            authRequest.put("password", environment.getPassword());
            authRequest.put("auth", environment.getAuthType());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(authRequest), headers);
            
            // Make authentication request
            ResponseEntity<String> response = restTemplate.exchange(
                    environment.getUrl() + "/logon/long",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            // Parse response
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            String authToken = responseJson.path("logonToken").asText();
            
            if (authToken == null || authToken.isEmpty()) {
                throw new SapBoApiException("Failed to obtain authentication token");
            }
            
            // Cache the token
            authTokens.put(cacheKey, authToken);
            
            return authToken;
        } catch (RestClientException | JsonProcessingException e) {
            throw new SapBoApiException("Error authenticating to SAP BO API", e);
        }
    }
    
    /**
     * Make a GET request to the SAP BO API
     */
    public <T> T get(SapBoProperties.BoEnvironment environment, String path, Class<T> responseType) {
        String authToken = getAuthToken(environment);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-SAP-LogonToken", authToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<T> response = restTemplate.exchange(
                    environment.getUrl() + path,
                    HttpMethod.GET,
                    entity,
                    responseType
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            throw new SapBoApiException("Error making GET request to SAP BO API: " + path, e);
        }
    }
    
    /**
     * Make a POST request to the SAP BO API
     */
    public <T> T post(SapBoProperties.BoEnvironment environment, String path, Object requestBody, Class<T> responseType) {
        String authToken = getAuthToken(environment);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-SAP-LogonToken", authToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            ResponseEntity<T> response = restTemplate.exchange(
                    environment.getUrl() + path,
                    HttpMethod.POST,
                    entity,
                    responseType
            );
            
            return response.getBody();
        } catch (RestClientException | JsonProcessingException e) {
            throw new SapBoApiException("Error making POST request to SAP BO API: " + path, e);
        }
    }
    
    /**
     * Make a PUT request to the SAP BO API
     */
    public <T> T put(SapBoProperties.BoEnvironment environment, String path, Object requestBody, Class<T> responseType) {
        String authToken = getAuthToken(environment);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-SAP-LogonToken", authToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            ResponseEntity<T> response = restTemplate.exchange(
                    environment.getUrl() + path,
                    HttpMethod.PUT,
                    entity,
                    responseType
            );
            
            return response.getBody();
        } catch (RestClientException | JsonProcessingException e) {
            throw new SapBoApiException("Error making PUT request to SAP BO API: " + path, e);
        }
    }
    
    /**
     * Make a DELETE request to the SAP BO API
     */
    public void delete(SapBoProperties.BoEnvironment environment, String path) {
        String authToken = getAuthToken(environment);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-SAP-LogonToken", authToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            restTemplate.exchange(
                    environment.getUrl() + path,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
        } catch (RestClientException e) {
            throw new SapBoApiException("Error making DELETE request to SAP BO API: " + path, e);
        }
    }
    
    /**
     * Download binary content from the SAP BO API
     */
    public byte[] downloadContent(SapBoProperties.BoEnvironment environment, String path) {
        String authToken = getAuthToken(environment);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-SAP-LogonToken", authToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    environment.getUrl() + path,
                    HttpMethod.GET,
                    entity,
                    byte[].class
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            throw new SapBoApiException("Error downloading content from SAP BO API: " + path, e);
        }
    }
}
