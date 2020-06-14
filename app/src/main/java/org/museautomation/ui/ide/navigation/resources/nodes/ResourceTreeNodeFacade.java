package org.museautomation.ui.ide.navigation.resources.nodes;

import javafx.scene.*;
import net.christophermerrill.FancyFxTree.*;
import org.museautomation.ui.extend.glyphs.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceTreeNodeFacade implements FancyTreeNodeFacade<ResourceTreeNode>
    {
    public ResourceTreeNodeFacade(ResourceTreeNode node)
        {
        _node = node;
        _listener = new ResourceTreeNodeListener()
	        {
	        @Override
	        public void childAdded(int index, ResourceTreeNode child)
		        {
		        _item.addChild(new ResourceTreeNodeFacade(child), index);
		        }

	        @Override
	        public void childRemoved(int index, ResourceTreeNode child)
		        {
		        _item.removeChild(index, null);
		        }
	        };
        _node.addChildListener(_listener);
        }

    @Override
    public FancyTreeNodeFacade<ResourceTreeNode> copyAndDestroy()
        {
        ResourceTreeNodeFacade copy = new ResourceTreeNodeFacade(_node);
        copy._children = _children;
        copy._item = _item;
        destroy();
        return copy;
        }

    @Override
    public List<FancyTreeNodeFacade<ResourceTreeNode>> getChildren()
        {
        if (_children == null)
            {
            _children = new ArrayList<>();
            for (ResourceTreeNode child : _node.getChildren())
                _children.add(new ResourceTreeNodeFacade(child));
            }
        return _children;
        }

    @Override
    public String getLabelText()
        {
        return _node.getTreeLabel();
        }

    @Override
    public Node getIcon()
        {
        String name = _node.getTreeIconGlyphName();
        if (name == null)
            return null;
        return Glyphs.create(name);
        }

    @Override
    public Node getCustomCellUI()
        {
        return _node.getCustomUI();
        }

    @Override
    public FancyTreeCellEditor getCustomEditorUI()
	    {
	    return null;  // no edits allowed
	    }

    @Override
    public void editStarting()
	    {
	    // no-op...no editing
	    }

    @Override
    public void editFinished()
	    {
	    // no-op...no editing
	    }

    @Override
    public void setLabelText(String new_value)
	    {
	    // No-op: no edits allowed
	    }

    @Override
    public ResourceTreeNode getModelNode()
        {
        return _node;
        }

    @Override
    public void setTreeItemFacade(FancyTreeItemFacade item)
        {
        _item = item;
        }

    @Override
    public void destroy()
        {
        _node.removeChildListener(_listener);
        _listener = null;
        }

    @Override
    public List<String> getStyles()
	    {
	    return Collections.emptyList();
	    }

    private final ResourceTreeNode _node;
    private List<FancyTreeNodeFacade<ResourceTreeNode>> _children;
    private FancyTreeItemFacade _item;
    private ResourceTreeNodeListener _listener;
    }