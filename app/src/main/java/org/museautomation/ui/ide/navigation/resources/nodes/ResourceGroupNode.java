package org.museautomation.ui.ide.navigation.resources.nodes;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class ResourceGroupNode extends ResourceTreeNode
    {
    public ResourceGroupNode(MuseProject project)
        {
        super(project);
        }

    @Override
    public boolean notifyResourceAdded(ResourceToken<MuseResource> to_add)
        {
        boolean added = resourceAddedToProject(to_add);
        if (added)
            return true;
        for (ResourceTreeNode child : getChildren())
            if (child.notifyResourceAdded(to_add))
                return true;
        return false;
        }

    @Override
    public boolean notifyResourceRemoved(ResourceToken<MuseResource> to_remove)
        {
        boolean removed = resourceRemovedFromProject(to_remove);
        if (removed)
            return true;
        for (ResourceTreeNode child : getChildren())
            if (child.notifyResourceRemoved(to_remove))
                return true;
        return false;
        }

    protected abstract boolean resourceAddedToProject(ResourceToken<MuseResource> added);
    protected abstract boolean resourceRemovedFromProject(ResourceToken<MuseResource> removed);

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

    protected void addChild(ResourceTreeNode new_node)
        {
        List<ResourceTreeNode> children = getChildren();
        int add_index = 0;
        for (int i = 0; i < children.size(); i++)
            {
            ResourceTreeNode child_node = children.get(i);
            if (new_node.getTreeLabel().compareTo(child_node.getTreeLabel()) > 0)
                add_index++;
            else
                break;
            }
        if (_children == null)
            _children = new ArrayList<>();
        _children.add(add_index, new_node);
        for (ResourceTreeNodeListener listener : _listeners)
            listener.childAdded(add_index, new_node);
        }

    protected void removeChild(ResourceTreeNode remove_node)
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