package org.museautomation.ui.ide.navigation.resources.nodes;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourcePathProjectNodeFactory implements ResourceTreeNodeFactory
    {
    @Override
    public String getName()
        {
        return "Path";
        }

    @Override
    public ResourceTreeNode createNode(MuseProject project)
        {
        ResourcePathGroupNode node = new ResourcePathGroupNode(project, new String[0]);
        List<ResourceToken<MuseResource>> resources = project.getResourceStorage().findResources(ResourceQueryParameters.forAllResources());
        for (ResourceToken<MuseResource> token : resources)
            node.notifyResourceAdded(token);
        return node;
        }
    }