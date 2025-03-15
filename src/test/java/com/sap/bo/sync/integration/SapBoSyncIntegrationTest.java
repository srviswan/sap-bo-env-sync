package com.sap.bo.sync.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bo.sync.SapBoSyncToolApplication;
import com.sap.bo.sync.model.Connection;
import com.sap.bo.sync.model.Report;
import com.sap.bo.sync.model.SapBoObject;
import com.sap.bo.sync.model.Universe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the SAP BO Sync Tool
 */
@SpringBootTest(classes = SapBoSyncToolApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SapBoSyncIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void testGetFoldersEndpoint() throws Exception {
        // Arrange
        List<SapBoObject> folders = new ArrayList<>();
        SapBoObject folder1 = new SapBoObject();
        folder1.setId("folder1");
        folder1.setName("Folder 1");
        folder1.setType("Folder");
        folder1.setPath("/Public/Folder 1");
        
        SapBoObject folder2 = new SapBoObject();
        folder2.setId("folder2");
        folder2.setName("Folder 2");
        folder2.setType("Folder");
        folder2.setPath("/Public/Folder 2");
        
        folders.add(folder1);
        folders.add(folder2);

        // Mock the REST API response
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(objectMapper.writeValueAsString(folders));

        // Act & Assert
        mockMvc.perform(get("/api/folders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("folder1")))
                .andExpect(jsonPath("$[0].name", is("Folder 1")))
                .andExpect(jsonPath("$[1].id", is("folder2")))
                .andExpect(jsonPath("$[1].name", is("Folder 2")));
    }

    @Test
    public void testGetReportsEndpoint() throws Exception {
        // Arrange
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

        // Mock the REST API response
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(objectMapper.writeValueAsString(reports));

        // Act & Assert
        mockMvc.perform(get("/api/reports")
                .param("folderId", "folder1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("report1")))
                .andExpect(jsonPath("$[0].name", is("Report 1")))
                .andExpect(jsonPath("$[1].id", is("report2")))
                .andExpect(jsonPath("$[1].name", is("Report 2")));
    }

    @Test
    public void testGetUniversesEndpoint() throws Exception {
        // Arrange
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

        // Mock the REST API response
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(objectMapper.writeValueAsString(universes));

        // Act & Assert
        mockMvc.perform(get("/api/universes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("universe1")))
                .andExpect(jsonPath("$[0].name", is("Universe 1")))
                .andExpect(jsonPath("$[1].id", is("universe2")))
                .andExpect(jsonPath("$[1].name", is("Universe 2")));
    }

    @Test
    public void testGetConnectionsEndpoint() throws Exception {
        // Arrange
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

        // Mock the REST API response
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(objectMapper.writeValueAsString(connections));

        // Act & Assert
        mockMvc.perform(get("/api/connections")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("conn1")))
                .andExpect(jsonPath("$[0].name", is("Connection 1")))
                .andExpect(jsonPath("$[1].id", is("conn2")))
                .andExpect(jsonPath("$[1].name", is("Connection 2")));
    }

    @Test
    public void testSyncAllEndpoint() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/sync/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Sync started successfully"))
                .andReturn();
    }

    @Test
    public void testSyncFoldersEndpoint() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/sync/folders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Folder sync started successfully"))
                .andReturn();
    }

    @Test
    public void testSyncReportsEndpoint() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/sync/reports")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Reports sync started successfully"))
                .andReturn();
    }
}
