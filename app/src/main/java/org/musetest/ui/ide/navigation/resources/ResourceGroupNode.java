package org.musetest.ui.ide.navigation.resources;

import org.musetest.core.*;
import org.musetest.core.resource.*;
import org.musetest.core.resource.types.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ResourceGroupNode extends ResourceTreeNode
    {
    ResourceGroupNode(MuseProject project, ResourceType type)
        {
        super(project);
        _project.addResourceListener(new ProjectResourceListener()
            {
            @Override
            public void resourceAdded(ResourceToken added)
                {
                final ResourceType added_type = added.getType();
                if (added_type.equals(_type)
                    || added_type.isSubtype() && ((ResourceSubtype)added_type).isSubtypeOf(_type))
                    addChild(added);
                }

            @Override
            public void resourceRemoved(ResourceToken removed)
                {
                for (ResourceTreeNode node : _children)
                    if (node instanceof ResourceNode && ((ResourceNode)node).getResourceToken().equals(removed))
                        {
                        removeChild(removed);
                        return;
                        }
                }
            });
        _type = type;
        }

    public void setProject(MuseProject project)
        {
        _project = project;
        }

    public ResourceType getType()
        {
        return _type;
        }

    @Override
    public String getTreeLabel()
        {
        return _type.getName() + "s";
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
            {
            _children = new ArrayList<>();
            List<ResourceToken> resources = _project.getResourceStorage().findResources(new ResourceQueryParameters(_type));
            resources.sort(Comparator.comparing(ResourceInfo::getId));
            for (ResourceToken resource : resources)
                _children.add(new ResourceNode(resource, _project));
            }
        return _children;
        }

    private void addChild(ResourceToken new_child)
        {
        List<ResourceTreeNode> children = getChildren();
        int add_index = 0;
        for (int i = 0; i < children.size(); i++)
            {
            String new_id = new_child.getId();
            ResourceNode child_node = (ResourceNode) children.get(i);
            if (new_id.compareTo(child_node.getResourceToken().getId()) > 0)
                add_index++;
            else
                break;
            }
        ResourceNode new_node = new ResourceNode(new_child, _project);
        _children.add(add_index, new_node);
        for (ResourceTreeNodeListener listener : _listeners)
            listener.childAdded(add_index, new_node);
        }

    private void removeChild(ResourceToken remove_child)
        {
        List<ResourceTreeNode> children = getChildren();
        for (int i = 0; i < children.size(); i++)
            {
            String remove_id = remove_child.getId();
            ResourceNode child_node = (ResourceNode) children.get(i);
            if (child_node.getResourceToken().getId().equals(remove_id))
                {
                _children.remove(i);
                for (ResourceTreeNodeListener listener : _listeners)
                    listener.childRemoved(i, child_node);
                return;
                }
            }
        }

    private final ResourceType _type;
    private List<ResourceTreeNode> _children;
    }


