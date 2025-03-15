package com.sap.bo.sync.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the Report class
 */
public class ReportTest {

    @Test
    public void testReportProperties() {
        // Arrange
        Report report = new Report();
        String id = "report123";
        String name = "Sales Report";
        String universeId = "universe456";
        String universeName = "Sales Universe";
        String content = "Report content";
        String format = "PDF";
        Long size = 1024L;
        String schedule = "Daily";
        String lastRun = "2025-03-15T19:00:00Z";
        String status = "Completed";
        
        // Act
        report.setId(id);
        report.setName(name);
        report.setUniverseId(universeId);
        report.setUniverseName(universeName);
        report.setContent(content);
        report.setFormat(format);
        report.setSize(size);
        report.setSchedule(schedule);
        report.setLastRun(lastRun);
        report.setStatus(status);
        
        // Assert
        assertEquals(id, report.getId());
        assertEquals(name, report.getName());
        assertEquals(universeId, report.getUniverseId());
        assertEquals(universeName, report.getUniverseName());
        assertEquals(content, report.getContent());
        assertEquals(format, report.getFormat());
        assertEquals(size, report.getSize());
        assertEquals(schedule, report.getSchedule());
        assertEquals(lastRun, report.getLastRun());
        assertEquals(status, report.getStatus());
    }
    
    @Test
    public void testReportInheritance() {
        // Arrange
        Report report = new Report();
        String id = "report123";
        String cuid = "CUID123";
        String description = "Report description";
        String type = "Report";
        
        // Act
        report.setId(id);
        report.setCuid(cuid);
        report.setDescription(description);
        report.setType(type);
        
        // Assert
        assertEquals(id, report.getId());
        assertEquals(cuid, report.getCuid());
        assertEquals(description, report.getDescription());
        assertEquals(type, report.getType());
    }
}
