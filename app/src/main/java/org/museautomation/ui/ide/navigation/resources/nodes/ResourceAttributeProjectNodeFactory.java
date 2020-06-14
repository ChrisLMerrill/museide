package org.museautomation.ui.ide.navigation.resources.nodes;

import org.museautomation.core.*;
import org.museautomation.core.resource.types.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceAttributeProjectNodeFactory implements ResourceTreeNodeFactory
    {
    @Override
    public ResourceTreeNode createNode(MuseProject project)
        {
        // create the nodes
        List<ResourceTreeNode> nodes = new ArrayList<>();
        for (ResourceType type : project.getResourceTypes().getPrimary())
            nodes.add(new ResourceTypeGroupNode(project, type));

        // sort the nodes
        nodes.sort(Comparator.comparing(ResourceTreeNode::getTreeLabel));

        return new ResourceTypeRootNode(project, nodes);
        }
    }