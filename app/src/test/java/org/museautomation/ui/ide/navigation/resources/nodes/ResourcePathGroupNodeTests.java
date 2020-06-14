package org.museautomation.ui.ide.navigation.resources.nodes;

import org.junit.jupiter.api.*;
import org.museautomation.core.*;
import org.museautomation.core.mocks.*;
import org.museautomation.core.project.*;
import org.museautomation.core.resource.storage.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourcePathGroupNodeTests
    {
    @Test
    public void noPaths() throws IOException
        {
        SimpleProject project = new SimpleProject();
        MuseResource resource1 = new MockTask();
        resource1.setId("resX");
        project.getResourceStorage().addResource(resource1);
        MuseResource resource2 = new MockTask();
        resource2.setId("resA");
        project.getResourceStorage().addResource(resource2);

        ResourceTreeNode node = new ResourcePathProjectNodeFactory().createNode(project);
        Assertions.assertTrue(node instanceof ResourcePathGroupNode);
        ResourcePathGroupNode root = (ResourcePathGroupNode) node;
        Assertions.assertEquals(2, root.getChildren().size());

        // verify sorted by name
        Assertions.assertEquals("resA", root.getChildren().get(0).getTreeLabel());
        Assertions.assertEquals("resX", root.getChildren().get(1).getTreeLabel());
        }

    @Test
    public void singlePath() throws IOException
        {
        SimpleProject project = new SimpleProject();
        MuseResource resource1 = new MockTask();
        resource1.setId("resX");
        resource1.metadata().setMetadataField(FolderIntoMemoryResourceStorage.PATH_ATTRIBUTE_NAME, "path1");
        project.getResourceStorage().addResource(resource1);
        MuseResource resource2 = new MockTask();
        resource2.setId("resA");
        resource2.metadata().setMetadataField(FolderIntoMemoryResourceStorage.PATH_ATTRIBUTE_NAME, "path1");
        project.getResourceStorage().addResource(resource2);

        ResourceTreeNode node = new ResourcePathProjectNodeFactory().createNode(project);

        Assertions.assertTrue(node instanceof ResourcePathGroupNode);
        ResourcePathGroupNode root = (ResourcePathGroupNode) node;
        Assertions.assertEquals(1, root.getChildren().size());

        ResourcePathGroupNode path1_node = (ResourcePathGroupNode) root.getChildren().get(0);
        Assertions.assertEquals(2, path1_node.getChildren().size());
        Assertions.assertEquals("path1", path1_node.getTreeLabel());

        // verify sorted by name
        Assertions.assertEquals("resA", path1_node.getChildren().get(0).getTreeLabel());
        Assertions.assertEquals("resX", path1_node.getChildren().get(1).getTreeLabel());
        }

    @Test
    public void twoPaths() throws IOException
        {
        SimpleProject project = new SimpleProject();
        MuseResource resource1 = new MockTask();
        resource1.setId("resX");
        resource1.metadata().setMetadataField(FolderIntoMemoryResourceStorage.PATH_ATTRIBUTE_NAME, "path1");
        project.getResourceStorage().addResource(resource1);
        MuseResource resource2 = new MockTask();
        resource2.setId("resA");
        resource2.metadata().setMetadataField(FolderIntoMemoryResourceStorage.PATH_ATTRIBUTE_NAME, "path2");
        project.getResourceStorage().addResource(resource2);

        ResourceTreeNode node = new ResourcePathProjectNodeFactory().createNode(project);
        Assertions.assertTrue(node instanceof ResourcePathGroupNode);

        ResourcePathGroupNode root = (ResourcePathGroupNode) node;
        Assertions.assertEquals(2, root.getChildren().size());

        ResourcePathGroupNode path1_node = (ResourcePathGroupNode) root.getChildren().get(0);
        Assertions.assertEquals(1, path1_node.getChildren().size());
        Assertions.assertEquals("path1", path1_node.getTreeLabel());
        Assertions.assertEquals("resX", path1_node.getChildren().get(0).getTreeLabel());

        ResourcePathGroupNode path2_node = (ResourcePathGroupNode) root.getChildren().get(1);
        Assertions.assertEquals(1, path2_node.getChildren().size());
        Assertions.assertEquals("path2", path2_node.getTreeLabel());
        Assertions.assertEquals("resA", path2_node.getChildren().get(0).getTreeLabel());
        }

    @Test
    public void subPath() throws IOException
        {
        SimpleProject project = new SimpleProject();
        MuseResource resource1 = new MockTask();
        resource1.setId("resX");
        resource1.metadata().setMetadataField(FolderIntoMemoryResourceStorage.PATH_ATTRIBUTE_NAME, "path1");
        project.getResourceStorage().addResource(resource1);
        MuseResource resource2 = new MockTask();
        resource2.setId("resA");
        resource2.metadata().setMetadataField(FolderIntoMemoryResourceStorage.PATH_ATTRIBUTE_NAME, "path1/path2");
        project.getResourceStorage().addResource(resource2);

        ResourceTreeNode node = new ResourcePathProjectNodeFactory().createNode(project);

        Assertions.assertTrue(node instanceof ResourcePathGroupNode);
        ResourcePathGroupNode root = (ResourcePathGroupNode) node;
        Assertions.assertEquals(1, root.getChildren().size());

        ResourcePathGroupNode path1_node = (ResourcePathGroupNode) root.getChildren().get(0);
        Assertions.assertEquals(2, path1_node.getChildren().size());
        Assertions.assertEquals("path1", path1_node.getTreeLabel());
        Assertions.assertEquals("resX", path1_node.getChildren().get(1).getTreeLabel());

        ResourcePathGroupNode path2_node = (ResourcePathGroupNode) path1_node.getChildren().get(0);
        Assertions.assertEquals(1, path2_node.getChildren().size());
        Assertions.assertEquals("path2", path2_node.getTreeLabel());
        Assertions.assertEquals("resA", path2_node.getChildren().get(0).getTreeLabel());
        }

    @Test
    public void deeperSubPath() throws IOException
        {
        SimpleProject project = new SimpleProject();
        MuseResource resource1 = new MockTask();
        resource1.setId("resX");
        resource1.metadata().setMetadataField(FolderIntoMemoryResourceStorage.PATH_ATTRIBUTE_NAME, "path1");
        project.getResourceStorage().addResource(resource1);
        MuseResource resource2 = new MockTask();
        resource2.setId("resA");
        resource2.metadata().setMetadataField(FolderIntoMemoryResourceStorage.PATH_ATTRIBUTE_NAME, "path1/path2/path3");
        project.getResourceStorage().addResource(resource2);

        ResourceTreeNode node = new ResourcePathProjectNodeFactory().createNode(project);

        Assertions.assertTrue(node instanceof ResourcePathGroupNode);
        ResourcePathGroupNode root = (ResourcePathGroupNode) node;

        Assertions.assertEquals(1, root.getChildren().size());
        ResourcePathGroupNode path1_node = (ResourcePathGroupNode) root.getChildren().get(0);
        Assertions.assertEquals("path1", path1_node.getTreeLabel());
        Assertions.assertEquals(2, path1_node.getChildren().size());
        Assertions.assertEquals("resX", path1_node.getChildren().get(1).getTreeLabel());

        ResourcePathGroupNode path2_node = (ResourcePathGroupNode) path1_node.getChildren().get(0);
        Assertions.assertEquals(1, path2_node.getChildren().size());
        Assertions.assertEquals("path2", path2_node.getTreeLabel());
        Assertions.assertEquals("path3", path2_node.getChildren().get(0).getTreeLabel());

        ResourcePathGroupNode path3_node = (ResourcePathGroupNode) path2_node.getChildren().get(0);
        Assertions.assertEquals(1, path3_node.getChildren().size());
        Assertions.assertEquals("path3", path3_node.getTreeLabel());
        Assertions.assertEquals("resA", path3_node.getChildren().get(0).getTreeLabel());
        }

    @Test
    public void notapplicableRelationship1()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[] {"path1"});
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.NOTAPPLICABLE, node.getRelationship(new String[0]));
        }

    @Test
    public void notapplicableRelationship2()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[] {"path1"});
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.NOTAPPLICABLE, node.getRelationship(new String[] {"path2"}));
        }

    @Test
    public void notapplicableRelationship3()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[] {"path1","path2"});
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.NOTAPPLICABLE, node.getRelationship(new String[] {"path2","path1"}));
        }

    @Test
    public void thisRelationship1()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[0]);
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.THIS, node.getRelationship(new String[0]));
        }

    @Test
    public void thisRelationship2()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[] {"p1"});
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.THIS, node.getRelationship(new String[]{"p1"}));
        }

    @Test
    public void thisRelationship3()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[] {"p1","p2"});
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.THIS, node.getRelationship(new String[]{"p1","p2"}));
        }

    @Test
    public void childRelationship1()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[0]);
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.CHILD, node.getRelationship(new String[] {"path1"}));
        }

    @Test
    public void childRelationship2()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[]{"path1"});
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.CHILD, node.getRelationship(new String[] {"path1","path2"}));
        }

    @Test
    public void childRelationship3()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[]{"path1","p2"});
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.CHILD, node.getRelationship(new String[] {"path1","p2","xyz"}));
        }

    @Test
    public void descendentRelationship1()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[0]);
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.DESCENDENT, node.getRelationship(new String[] {"path1","p2"}));
        }

    @Test
    public void descendentRelationship2()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[0]);
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.DESCENDENT, node.getRelationship(new String[] {"path1","p2","xyz"}));
        }

    @Test
    public void descendentRelationship3()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[]{"path1"});
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.DESCENDENT, node.getRelationship(new String[] {"path1","p2","xyz"}));
        }

    @Test
    public void descendentRelationship4()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[]{"path1"});
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.DESCENDENT, node.getRelationship(new String[] {"path1","p2","xyz","abc"}));
        }

    @Test
    public void descendentRelationship5()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[]{"path1","p2"});
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.DESCENDENT, node.getRelationship(new String[] {"path1","p2","xyz","abc"}));
        }

    @Test
    public void descendentRelationship6()
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(PROJECT, new String[]{"path1","p2"});
        Assertions.assertEquals(ResourcePathGroupNode.PathRelationship.DESCENDENT, node.getRelationship(new String[] {"path1","p2","xyz","abc","def"}));
        }

    private final static MuseProject PROJECT = new SimpleProject();
    }