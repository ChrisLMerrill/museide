package org.museautomation.ui.ide.navigation.resources;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceNode extends ResourceTreeNode
    {
    public ResourceNode(ResourceToken resource, MuseProject project)
        {
        super(project);
        _token = resource;
        }

    @Override
    public String getTreeLabel()
        {
        return _token.getId();
        }

    @Override
    public String getTreeIconGlyphName()
        {
        return null;
        }

    @Override
    public List<ResourceTreeNode> getChildren()
        {
        return Collections.emptyList();
        }

    public ResourceToken getResourceToken()
        {
        return _token;
        }

    public MuseProject getProject()
        {
        return _project;
        }

    @Override
    public ResourceTreeNode findResourceNode(ResourceToken token)
        {
        if (token.getId().equals(_token.getId()))
            return this;
        return null;
        }

    private ResourceToken _token;
    }


