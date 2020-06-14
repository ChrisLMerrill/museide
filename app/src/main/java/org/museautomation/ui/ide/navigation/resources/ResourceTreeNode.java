package org.museautomation.ui.ide.navigation.resources;

import javafx.scene.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class ResourceTreeNode
    {
    public ResourceTreeNode(MuseProject project)
	    {
	    _project = project;
	    }

    public Node getCustomUI() { return null; }
    public abstract String getTreeLabel();
    public abstract String getTreeIconGlyphName();
    public abstract List<ResourceTreeNode> getChildren();

    public void addChildListener(ResourceTreeNodeListener listener)
        {
        if (_listeners.contains(listener))
            return;
        _listeners.add(listener);
        }

    public void removeChildListener(ResourceTreeNodeListener listener)
        {
        _listeners.remove(listener);
        }

    public ResourceTreeNode findResourceNode(ResourceToken<MuseResource> token)
        {
        for (ResourceTreeNode child : getChildren())
            {
            ResourceTreeNode node = child.findResourceNode(token);
            if (node != null)
                return node;
            }
        return null;
        }

    protected List<ResourceTreeNodeListener> _listeners = new ArrayList<>();

    protected MuseProject _project;
    }


