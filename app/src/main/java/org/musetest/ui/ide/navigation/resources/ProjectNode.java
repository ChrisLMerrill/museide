package org.musetest.ui.ide.navigation.resources;

import org.musetest.core.*;
import org.musetest.core.resource.types.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProjectNode extends ResourceTreeNode
    {
    ProjectNode(MuseProject project)
        {
        super(project);

        // create the nodes
        List<ResourceGroupNode> nodes = new ArrayList<>();
        for (ResourceType type : _project.getResourceTypes().getPrimary())
            nodes.add(new ResourceGroupNode(_project, type));

        // sort the nodes
        nodes.sort(Comparator.comparing(ResourceGroupNode::getTreeLabel));

        // add to children
        _children.addAll(nodes);
        }

    @Override
    public String getTreeLabel()
        {
        return _project.getName();
        }

    @Override
    public String getTreeIconGlyphName()
        {
        return "FA:FOLDER_ALT";
        }

    @Override
    public List<ResourceTreeNode> getChildren()
        {
        return _children;
        }

    private List<ResourceTreeNode> _children = new ArrayList<>();
    }


