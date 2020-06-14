package org.museautomation.ui.ide.navigation.resources;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
abstract class ResourceGroupNode extends ResourceTreeNode
    {
    ResourceGroupNode(MuseProject project)
        {
        super(project);
        }

    @Override
    public boolean notifyResourceAdded(ResourceToken<MuseResource> added)
        {
        ResourceNode node = resourceAddedToProject(added);
        if (node != null)
            {
            addChild(node);
            return true;
            }
        for (ResourceTreeNode child : getChildren())
            {
            boolean is_added = child.notifyResourceAdded(added);
            if (is_added)
                return true;
            }
        return false;
        }

    @Override
    public boolean notifyResourceRemoved(ResourceToken<MuseResource> removed)
        {
        ResourceNode node = resourceRemovedFromProject(removed);
        if (node != null)
            {
            removeChild(node);
            return true;
            }
        for (ResourceTreeNode child : getChildren())
            {
            boolean is_removed = child.notifyResourceRemoved(removed);
            if (is_removed)
                return true;
            }
        return false;
        }

    protected abstract ResourceNode resourceAddedToProject(ResourceToken<MuseResource> added);
    protected abstract ResourceNode resourceRemovedFromProject(ResourceToken<MuseResource> removed);

    public void setProject(MuseProject project)
        {
        _project = project;
        }

    public MuseProject getProject()
        {
        return _project;
        }

    @Override
    public String getTreeIconGlyphName()
        {
        return "FA:FOLDER_ALT";
        }

    @Override
    public List<ResourceTreeNode> getChildren()
        {
        if (_children == null)
            _children = getInitialChildList();
        return _children;
        }

    protected abstract List<ResourceTreeNode> getInitialChildList();

    private void addChild(ResourceNode new_node)
        {
        List<ResourceTreeNode> children = getChildren();
        int add_index = 0;
        for (int i = 0; i < children.size(); i++)
            {
            ResourceNode child_node = (ResourceNode) children.get(i);
            if (new_node.getTreeLabel().compareTo(child_node.getTreeLabel()) > 0)
                add_index++;
            else
                break;
            }
        _children.add(add_index, new_node);
        for (ResourceTreeNodeListener listener : _listeners)
            listener.childAdded(add_index, new_node);
        }

    private void removeChild(ResourceNode remove_node)
        {
        List<ResourceTreeNode> children = getChildren();
        for (int i = 0; i < children.size(); i++)
            {
            ResourceNode child_node = (ResourceNode) children.get(i);
            if (child_node == remove_node)
                {
                _children.remove(i);
                for (ResourceTreeNodeListener listener : _listeners)
                    listener.childRemoved(i, child_node);
                return;
                }
            }
        }

    private List<ResourceTreeNode> _children;
    }


