package org.museautomation.ui.ide.navigation.resources.nodes;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.slf4j.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceTypeRootNode extends ResourceTreeNode
    {
    ResourceTypeRootNode(MuseProject project, List<ResourceTreeNode> nodes)
        {
        super(project);
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
    public boolean notifyResourceAdded(ResourceToken<MuseResource> added)
        {
        for (ResourceTreeNode child : _children)
            if (child.notifyResourceAdded(added))
                return true;
        LOG.error("Resource was added to project, but was not added to any ResourceGroupNodes: " + added.getId());
        return false;
        }

    @Override
    public boolean notifyResourceRemoved(ResourceToken<MuseResource> removed)
        {
        for (ResourceTreeNode child : _children)
            if (child.notifyResourceRemoved(removed))
                return true;
        LOG.error("Resource was removed from project, but was not removed from any ResourceGroupNodes: " + removed.getId());
        return false;
        }

    @Override
    public List<ResourceTreeNode> getChildren()
        {
        return _children;
        }

    private final List<ResourceTreeNode> _children = new ArrayList<>();

    final static Logger LOG = LoggerFactory.getLogger(ResourceTypeRootNode.class);
    }