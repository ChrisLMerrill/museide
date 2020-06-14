package org.museautomation.ui.ide.navigation.resources;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.resource.types.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ResourceTypeGroupNode extends ResourceGroupNode
    {
    ResourceTypeGroupNode(MuseProject project, ResourceType type)
        {
        super(project);
        _type = type;
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
    protected List<ResourceTreeNode> getInitialChildList()
        {
        List<ResourceToken<MuseResource>> resources = _project.getResourceStorage().findResources(new ResourceQueryParameters(_type));
        resources.sort(Comparator.comparing(ResourceInfo::getId));

        List<ResourceTreeNode> children = new ArrayList<>();
        for (ResourceToken<MuseResource> resource : resources)
            children.add(new ResourceNode(resource, _project));
        return children;
        }

    @Override
    protected ResourceNode resourceAddedToProject(ResourceToken<MuseResource> added)
        {
        final ResourceType added_type = added.getType();
        if (added_type.equals(_type)
            || added_type.isSubtype() && ((ResourceSubtype)added_type).isSubtypeOf(_type))
            return new ResourceNode(added, getProject());
        else
            return null;
        }

    @Override
    protected ResourceNode resourceRemovedFromProject(ResourceToken<MuseResource> removed)
        {
        for (ResourceTreeNode node : getChildren())
            if (node instanceof ResourceNode && ((ResourceNode)node).getResourceToken().equals(removed))
                return (ResourceNode) node;
        return null;
        }

    private final ResourceType _type;
    }


