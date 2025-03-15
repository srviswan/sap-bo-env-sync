package com.sap.bo.sync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the SAP Business Objects Synchronization Tool
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SapBoSyncToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(SapBoSyncToolApplication.class, args);
    }
}
