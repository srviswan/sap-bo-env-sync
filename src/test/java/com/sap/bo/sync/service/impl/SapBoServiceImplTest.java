package com.sap.bo.sync.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bo.sync.client.SapBoRestClient;
import com.sap.bo.sync.config.SapBoProperties;
import com.sap.bo.sync.model.Connection;
import com.sap.bo.sync.model.Report;
import com.sap.bo.sync.model.SapBoObject;
import com.sap.bo.sync.model.Universe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.HttpMethod; // Not used

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the SapBoServiceImpl class
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class SapBoServiceImplTest {

    @Mock
    private SapBoRestClient restClient;

    @Mock
    private SapBoProperties sapBoProperties;
    
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SapBoServiceImpl sapBoService;

    private SapBoProperties.BoEnvironment sourceEnv;

    @BeforeEach
    public void setUp() {
        sourceEnv = new SapBoProperties.BoEnvironment();
        sourceEnv.setUrl("http://source.example.com/bo");
        sourceEnv.setUsername("sourceUser");
        sourceEnv.setPassword("sourcePass");
        sourceEnv.setAuthType("secEnterprise");

        when(sapBoProperties.getSource()).thenReturn(sourceEnv);
    }

    @Test
    public void testGetFolders() throws Exception {
        // Arrange
        String folderJson = "{\"entries\": [{\"id\": \"folder1\", \"name\": \"Folder 1\", \"type\": \"Folder\"}, {\"id\": \"folder2\", \"name\": \"Folder 2\", \"type\": \"Folder\"}]}";
        
        ObjectMapper realMapper = new ObjectMapper();
        JsonNode rootNode = realMapper.readTree(folderJson);
        JsonNode entriesNode = rootNode.path("entries"); // Used for test setup
        
        List<SapBoObject> folders = new ArrayList<>();
        SapBoObject folder1 = new SapBoObject();
        folder1.setId("folder1");
        folder1.setName("Folder 1");
        folder1.setType("Folder");
        
        SapBoObject folder2 = new SapBoObject();
        folder2.setId("folder2");
        folder2.setName("Folder 2");
        folder2.setType("Folder");
        
        folders.add(folder1);
        folders.add(folder2);
        
        when(restClient.get(any(SapBoProperties.BoEnvironment.class), anyString(), eq(String.class)))
            .thenReturn(folderJson);
        when(objectMapper.readTree(anyString())).thenReturn(rootNode);
        when(objectMapper.treeToValue(any(JsonNode.class), eq(SapBoObject.class)))
            .thenReturn(folder1, folder2);
        when(objectMapper.readValue(anyString(), eq(SapBoObject.class)))
            .thenReturn(folder1, folder2);
        
        // Act
        List<SapBoObject> result = sapBoService.getFolders("/Public");
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    
    @Test
    public void testGetReports() throws Exception {
        // Arrange
        String reportsJson = "{\"entries\": [{\"id\": \"report1\", \"name\": \"Report 1\", \"type\": \"Report\", \"universeId\": \"universe1\"}, {\"id\": \"report2\", \"name\": \"Report 2\", \"type\": \"Report\", \"universeId\": \"universe2\"}]}";
        
        ObjectMapper realMapper = new ObjectMapper();
        JsonNode rootNode = realMapper.readTree(reportsJson);
        JsonNode entriesNode = rootNode.path("entries"); // Used for test setup
        
        List<Report> reports = new ArrayList<>();
        Report report1 = new Report();
        report1.setId("report1");
        report1.setName("Report 1");
        report1.setType("Report");
        report1.setUniverseId("universe1");
        
        Report report2 = new Report();
        report2.setId("report2");
        report2.setName("Report 2");
        report2.setType("Report");
        report2.setUniverseId("universe2");
        
        reports.add(report1);
        reports.add(report2);
        
        when(restClient.get(any(SapBoProperties.BoEnvironment.class), anyString(), eq(String.class)))
            .thenReturn(reportsJson);
        when(objectMapper.readTree(anyString())).thenReturn(rootNode);
        when(objectMapper.treeToValue(any(JsonNode.class), eq(Report.class)))
            .thenReturn(report1, report2);
        when(objectMapper.readValue(anyString(), eq(Report.class)))
            .thenReturn(report1, report2);
        
        // Act
        List<Report> result = sapBoService.getReports("folder1", null, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    
    @Test
    public void testGetReport() throws Exception {
        // Arrange
        String reportJson = "{\"id\": \"report1\", \"name\": \"Report 1\", \"type\": \"Report\", \"universeId\": \"universe1\"}";
        
        Report report = new Report();
        report.setId("report1");
        report.setName("Report 1");
        report.setType("Report");
        report.setUniverseId("universe1");
        
        when(restClient.get(any(SapBoProperties.BoEnvironment.class), anyString(), eq(String.class)))
            .thenReturn(reportJson);
        when(objectMapper.readValue(anyString(), eq(Report.class))).thenReturn(report);
        
        // Act
        Report result = sapBoService.getReport("report1");
        
        // Assert
        assertNotNull(result);
        assertEquals("report1", result.getId());
    }
    
    @Test
    public void testGetUniverses() throws Exception {
        // Arrange
        String universesJson = "{\"entries\": [{\"id\": \"universe1\", \"name\": \"Universe 1\", \"type\": \"Universe\", \"connectionId\": \"conn1\"}, {\"id\": \"universe2\", \"name\": \"Universe 2\", \"type\": \"Universe\", \"connectionId\": \"conn2\"}]}";
        
        ObjectMapper realMapper = new ObjectMapper();
        JsonNode rootNode = realMapper.readTree(universesJson);
        JsonNode entriesNode = rootNode.path("entries"); // Used for test setup
        
        List<Universe> universes = new ArrayList<>();
        Universe universe1 = new Universe();
        universe1.setId("universe1");
        universe1.setName("Universe 1");
        universe1.setType("Universe");
        universe1.setConnectionId("conn1");
        
        Universe universe2 = new Universe();
        universe2.setId("universe2");
        universe2.setName("Universe 2");
        universe2.setType("Universe");
        universe2.setConnectionId("conn2");
        
        universes.add(universe1);
        universes.add(universe2);
        
        when(restClient.get(any(SapBoProperties.BoEnvironment.class), anyString(), eq(String.class)))
            .thenReturn(universesJson);
        when(objectMapper.readTree(anyString())).thenReturn(rootNode);
        when(objectMapper.treeToValue(any(JsonNode.class), eq(Universe.class)))
            .thenReturn(universe1, universe2);
        when(objectMapper.readValue(anyString(), eq(Universe.class)))
            .thenReturn(universe1, universe2);
        
        // Act
        List<Universe> result = sapBoService.getUniverses("folder1", null, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    
    @Test
    public void testGetConnections() throws Exception {
        // Arrange
        String connectionsJson = "{\"entries\": [{\"id\": \"conn1\", \"name\": \"Connection 1\", \"type\": \"JDBC\", \"server\": \"server1.example.com\"}, {\"id\": \"conn2\", \"name\": \"Connection 2\", \"type\": \"JDBC\", \"server\": \"server2.example.com\"}]}";
        
        ObjectMapper realMapper = new ObjectMapper();
        JsonNode rootNode = realMapper.readTree(connectionsJson);
        JsonNode entriesNode = rootNode.path("entries"); // Used for test setup
        
        List<Connection> connections = new ArrayList<>();
        Connection conn1 = new Connection();
        conn1.setId("conn1");
        conn1.setName("Connection 1");
        conn1.setType("JDBC");
        conn1.setServer("server1.example.com");
        
        Connection conn2 = new Connection();
        conn2.setId("conn2");
        conn2.setName("Connection 2");
        conn2.setType("JDBC");
        conn2.setServer("server2.example.com");
        
        connections.add(conn1);
        connections.add(conn2);
        
        when(restClient.get(any(SapBoProperties.BoEnvironment.class), anyString(), eq(String.class)))
            .thenReturn(connectionsJson);
        when(objectMapper.readTree(anyString())).thenReturn(rootNode);
        when(objectMapper.treeToValue(any(JsonNode.class), eq(Connection.class)))
            .thenReturn(conn1, conn2);
        when(objectMapper.readValue(anyString(), eq(Connection.class)))
            .thenReturn(conn1, conn2);
        
        // Act
        List<Connection> result = sapBoService.getConnections(null, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    
    @Test
    public void testGetReportContent() {
        // Arrange
        String reportId = "report1";
        byte[] reportContent = "Report content data".getBytes();
        
        when(restClient.downloadContent(any(SapBoProperties.BoEnvironment.class), anyString()))
            .thenReturn(reportContent);
        
        // Act
        byte[] result = sapBoService.getReportContent(reportId);
        
        // Assert
        assertNotNull(result);
        assertEquals(new String(reportContent), new String(result));
    }
}
