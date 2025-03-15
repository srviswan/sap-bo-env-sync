# SAP Business Objects Sync Tool

A Spring Boot application that synchronizes SAP Business Objects 4.3 content (reports, universes, connections, folders) between different environments using the SAP BO REST API. The tool now includes enhanced WebI-specific features, improved error handling, and multithreading for better performance.

## Features

- Synchronize reports, universes, connections, and folder structures between SAP BO environments
- WebI-specific operations including document refresh, export, and status monitoring
- Multithreaded processing for improved performance and efficiency
- Enhanced error handling with detailed error messages and global exception handling
- Asynchronous API endpoints for non-blocking operations
- Batch processing for efficient synchronization of large numbers of objects
- Scheduled synchronization with configurable cron expression
- Web UI for manual synchronization and monitoring
- REST API for programmatic access and integration
- Retry mechanism for handling transient errors
- Detailed logging for troubleshooting

## Requirements

- Java 11 or higher
- Maven 3.6 or higher
- SAP Business Objects 4.3 with REST API enabled

## Configuration

Configure the application in `src/main/resources/application.yml`:

```yaml
sap:
  bo:
    source:
      url: http://source-bo-server:6405/biprws  # Source BO server URL
      username: admin                           # Source BO username
      password: ${SOURCE_BO_PASSWORD}           # Source BO password (use environment variable)
      auth-type: secEnterprise                  # Authentication type
    target:
      url: http://target-bo-server:6405/biprws  # Target BO server URL
      username: admin                           # Target BO username
      password: ${TARGET_BO_PASSWORD}           # Target BO password (use environment variable)
      auth-type: secEnterprise                  # Authentication type

sync:
  schedule:
    enabled: false                              # Enable/disable scheduled sync
    cron: "0 0 1 * * ?"                         # Default: Run daily at 1 AM
  objects:
    reports: true                               # Sync reports
    universes: true                             # Sync universes
    connections: true                           # Sync connections
    folders: true                               # Sync folder structure
  batch-size: 10                                # Number of objects to process in a batch
  retry:
    max-attempts: 3                             # Max retry attempts for failed operations
    delay: 5000                                 # Delay between retries in milliseconds

# Thread pool configuration
app:
  thread-pool:
    core-size: 5                                # Core number of threads
    max-size: 10                                # Maximum number of threads
    queue-capacity: 25                          # Queue capacity before rejecting
    thread-name-prefix: sap-bo-sync-            # Thread name prefix
```

## Building the Application

```bash
mvn clean package
```

## Running the Application

```bash
# Set environment variables for passwords
export SOURCE_BO_PASSWORD=your_source_password
export TARGET_BO_PASSWORD=your_target_password

# Run the application
java -jar target/sap-bo-sync-tool-0.0.1-SNAPSHOT.jar
```

Or using Maven:

```bash
mvn spring-boot:run
```

## Using the Web UI

Access the web UI at http://localhost:8080

The UI provides options to:
- Synchronize all objects at once
- Selectively synchronize folders, connections, universes, or reports
- View synchronization logs

## REST API Endpoints

### Synchronization Endpoints

- `POST /api/sync/all?syncFolders=true` - Synchronize all objects
- `POST /api/sync/folders` - Synchronize folder structure
- `POST /api/sync/connections` - Synchronize connections
- `POST /api/sync/universes` - Synchronize universes
- `POST /api/sync/reports` - Synchronize reports

### Object Retrieval Endpoints

- `GET /api/sync/source/folders` - Get folders from source environment
- `GET /api/sync/source/connections` - Get connections from source environment
- `GET /api/sync/source/universes` - Get universes from source environment
- `GET /api/sync/source/reports` - Get reports from source environment

### WebI Document Endpoints

- `POST /api/sync/target/webi/{documentId}/refresh` - Refresh a WebI document
- `POST /api/sync/target/webi/{documentId}/export?format=PDF` - Export a WebI document to the specified format
- `GET /api/sync/target/webi/{documentId}/export/{jobId}` - Get the status of an export job

## Architecture

The application follows a layered architecture:

1. **Controller Layer**: REST endpoints for synchronization operations
2. **Service Layer**: Business logic for synchronization
   - Synchronous service implementation for direct operations
   - Asynchronous service implementation for non-blocking operations
3. **Client Layer**: Communication with SAP BO REST API
   - Enhanced error handling and retry mechanisms
   - Support for WebI-specific operations
4. **Model Layer**: Object representations of SAP BO entities
5. **Scheduler**: Scheduled synchronization jobs
6. **Thread Pool**: Configurable thread pool for parallel processing
7. **Exception Handling**: Global exception handler for consistent error responses

## License

This project is licensed under the MIT License - see the LICENSE file for details.
