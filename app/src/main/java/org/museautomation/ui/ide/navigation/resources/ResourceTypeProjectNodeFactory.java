package org.museautomation.ui.ide.navigation.resources;

import org.museautomation.core.*;
import org.museautomation.core.resource.types.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceTypeProjectNodeFactory implements ProjectNodeFactory
    {
    @Override
    public ProjectNode createProjectNode(MuseProject project)
        {
        // create the nodes
        List<ResourceGroupNode> nodes = new ArrayList<>();
        for (ResourceType type : project.getResourceTypes().getPrimary())
            nodes.add(new ResourceTypeGroupNode(project, type));

        // sort the nodes
        nodes.sort(Comparator.comparing(ResourceGroupNode::getTreeLabel));

        return new ProjectNode(project, nodes);
        }
    }