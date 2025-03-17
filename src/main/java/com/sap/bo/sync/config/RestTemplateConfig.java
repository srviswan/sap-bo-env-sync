package com.sap.bo.sync.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Configuration for RestTemplate with configurable SSL validation
 * 
 * This class provides a RestTemplate bean that can be configured to either validate
 * SSL certificates (for production) or skip validation (for development/testing).
 * The behavior is controlled by the 'sap.bo.ssl.validate' property.
 */
@Configuration
public class RestTemplateConfig {

    @Autowired
    private SslProperties sslProperties;
    
    @Bean
    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // Configure SSL context based on validation setting
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        
        if (!sslProperties.isSslValidate()) {
            // If validation is disabled, trust all certificates
            sslContextBuilder.loadTrustMaterial(null, TrustAllStrategy.INSTANCE);
        }
        
        SSLContext sslContext = sslContextBuilder.build();
        
        // Configure hostname verification based on validation setting
        HostnameVerifier hostnameVerifier = sslProperties.isSslValidate() ? 
                SSLConnectionSocketFactory.getDefaultHostnameVerifier() : 
                NoopHostnameVerifier.INSTANCE;
        
        // Create SSL socket factory with appropriate hostname verification
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                sslContext, hostnameVerifier);
        
        // Configure timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(sslProperties.getConnectionTimeout())
                .setSocketTimeout(sslProperties.getSocketTimeout())
                .build();
        
        // Build the client with our custom SSL settings
        HttpClient httpClient = HttpClientBuilder.create()
                .setSSLSocketFactory(socketFactory)
                .setDefaultRequestConfig(requestConfig)
                .build();
        
        // Create request factory with our custom client
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        
        return new RestTemplate(requestFactory);
    }
}
