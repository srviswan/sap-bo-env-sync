package com.sap.bo.sync.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the Universe class
 */
public class UniverseTest {

    @Test
    public void testUniverseProperties() {
        // Arrange
        Universe universe = new Universe();
        String id = "universe123";
        String name = "Sales Universe";
        String connectionId = "conn456";
        String connectionName = "Sales DB Connection";
        String dataSourceType = "SQL";
        String content = "Universe content";
        
        // Act
        universe.setId(id);
        universe.setName(name);
        universe.setConnectionId(connectionId);
        universe.setConnectionName(connectionName);
        universe.setDataSourceType(dataSourceType);
        universe.setContent(content);
        
        // Assert
        assertEquals(id, universe.getId());
        assertEquals(name, universe.getName());
        assertEquals(connectionId, universe.getConnectionId());
        assertEquals(connectionName, universe.getConnectionName());
        assertEquals(dataSourceType, universe.getDataSourceType());
        assertEquals(content, universe.getContent());
    }
    
    @Test
    public void testUniverseObjects() {
        // Arrange
        Universe universe = new Universe();
        List<Universe.UniverseObject> objects = new ArrayList<>();
        
        Universe.UniverseObject dimension = new Universe.UniverseObject();
        dimension.setId("dim1");
        dimension.setName("Customer");
        dimension.setType("Dimension");
        dimension.setDescription("Customer dimension");
        dimension.setSql("SELECT * FROM customers");
        
        Universe.UniverseObject measure = new Universe.UniverseObject();
        measure.setId("meas1");
        measure.setName("Revenue");
        measure.setType("Measure");
        measure.setDescription("Revenue measure");
        measure.setSql("SUM(sales.amount)");
        
        objects.add(dimension);
        objects.add(measure);
        
        // Act
        universe.setObjects(objects);
        
        // Assert
        assertNotNull(universe.getObjects());
        assertEquals(2, universe.getObjects().size());
        
        Universe.UniverseObject retrievedDimension = universe.getObjects().get(0);
        assertEquals("dim1", retrievedDimension.getId());
        assertEquals("Customer", retrievedDimension.getName());
        assertEquals("Dimension", retrievedDimension.getType());
        assertEquals("Customer dimension", retrievedDimension.getDescription());
        assertEquals("SELECT * FROM customers", retrievedDimension.getSql());
        
        Universe.UniverseObject retrievedMeasure = universe.getObjects().get(1);
        assertEquals("meas1", retrievedMeasure.getId());
        assertEquals("Revenue", retrievedMeasure.getName());
        assertEquals("Measure", retrievedMeasure.getType());
        assertEquals("Revenue measure", retrievedMeasure.getDescription());
        assertEquals("SUM(sales.amount)", retrievedMeasure.getSql());
    }
}
