# SAP BO Sync Tool - Mock Testing Environment

This document provides instructions on how to use the mock testing environment for the SAP BO Sync Tool. The mock environment allows you to test the functionality of the tool without requiring actual SAP Business Objects servers or APIs.

## Overview

The mock environment simulates SAP BO servers with predefined data that contains intentional differences between source and target environments. This allows you to test the configuration comparison functionality and see how the tool identifies and reports differences.

## Features

- Mock implementation of the SAP BO REST client
- Predefined server and cluster configurations with known differences
- Mock data for reports, universes, and connections
- Special controller for mock environment information and management

## How to Run the Mock Environment

To run the application with the mock profile, use the following command:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mock
```

This will start the application with the mock configuration, using simulated data instead of connecting to actual SAP BO servers.

## Testing Endpoints

Once the application is running with the mock profile, you can test the following endpoints:

### Mock Environment Information

```
GET /api/mock/health
GET /api/mock/info
```

These endpoints provide information about the mock environment and confirm that it's working properly.

### Configuration Comparison Endpoints

1. Compare Server Configurations:
   ```
   GET /api/sync/compare/server?configType=server&includeDetails=true
   ```

2. Compare Cluster Configurations:
   ```
   GET /api/sync/compare/cluster?includeDetails=true
   ```
   
   With specific cluster ID:
   ```
   GET /api/sync/compare/cluster?clusterId=cluster1&includeDetails=true
   ```

3. Compare Custom Configurations:
   ```
   GET /api/sync/compare/configs?env1=source&env2=target&configType=server&includeDetails=true
   ```
   
   For cluster configurations:
   ```
   GET /api/sync/compare/configs?env1=source&env2=target&configType=cluster&clusterId=cluster1&includeDetails=true
   ```

## Expected Results

The mock environment is configured with the following differences between source and target environments:

### Server Configuration Differences
- `maxConnections`: 100 (source) vs 150 (target)
- `sessionTimeout`: 30 (source) vs 45 (target)

### Cluster Configuration Differences
- Node1 status: "running" (source) vs "stopped" (target)
- Node2 port: 6410 (source) vs 6411 (target)

### Reports Differences
- Report1 description: Different in source and target
- Report1 lastModified: Different timestamps
- Report2: Present in source, missing in target

### Universes Differences
- Universe2: Present in source, missing in target
- Universe3: Missing in source, present in target

### Connections Differences
- Connection1 server: "oracle-sales-db" (source) vs "oracle-sales-db-new" (target)
- Connection1 lastModified: Different timestamps

## Customizing Mock Data

If you need to customize the mock data, you can modify the `MockSapBoRestClient.java` file to add or change the predefined configurations.

## Switching Back to Regular Mode

To switch back to the regular mode that connects to actual SAP BO servers, simply run the application without the mock profile:

```bash
mvn spring-boot:run
```

This will use the standard application.properties configuration and attempt to connect to the configured SAP BO servers.
