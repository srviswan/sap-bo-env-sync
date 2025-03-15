package com.sap.bo.sync.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the SapBoObject class
 */
public class SapBoObjectTest {

    @Test
    public void testSapBoObjectProperties() {
        // Arrange
        SapBoObject object = new SapBoObject();
        String id = "123";
        String cuid = "CUID123";
        String name = "Test Object";
        String description = "Test Description";
        String type = "Folder";
        String parentId = "456";
        String parentCuid = "CUID456";
        Date created = new Date();
        Date modified = new Date();
        String owner = "admin";
        String path = "/Public/Test";
        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        List<SapBoObject> children = new ArrayList<>();
        
        // Act
        object.setId(id);
        object.setCuid(cuid);
        object.setName(name);
        object.setDescription(description);
        object.setType(type);
        object.setParentId(parentId);
        object.setParentCuid(parentCuid);
        object.setCreated(created);
        object.setModified(modified);
        object.setOwner(owner);
        object.setPath(path);
        object.setProperties(properties);
        object.setChildren(children);
        
        // Assert
        assertEquals(id, object.getId());
        assertEquals(cuid, object.getCuid());
        assertEquals(name, object.getName());
        assertEquals(description, object.getDescription());
        assertEquals(type, object.getType());
        assertEquals(parentId, object.getParentId());
        assertEquals(parentCuid, object.getParentCuid());
        assertEquals(created, object.getCreated());
        assertEquals(modified, object.getModified());
        assertEquals(owner, object.getOwner());
        assertEquals(path, object.getPath());
        assertEquals(properties, object.getProperties());
        assertEquals(children, object.getChildren());
    }
    
    @Test
    public void testSapBoObjectChildrenManagement() {
        // Arrange
        SapBoObject parent = new SapBoObject();
        parent.setId("parent1");
        parent.setName("Parent Object");
        
        SapBoObject child1 = new SapBoObject();
        child1.setId("child1");
        child1.setName("Child 1");
        
        SapBoObject child2 = new SapBoObject();
        child2.setId("child2");
        child2.setName("Child 2");
        
        List<SapBoObject> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        
        // Act
        parent.setChildren(children);
        
        // Assert
        assertNotNull(parent.getChildren());
        assertEquals(2, parent.getChildren().size());
        assertEquals("child1", parent.getChildren().get(0).getId());
        assertEquals("Child 1", parent.getChildren().get(0).getName());
        assertEquals("child2", parent.getChildren().get(1).getId());
        assertEquals("Child 2", parent.getChildren().get(1).getName());
    }
}
