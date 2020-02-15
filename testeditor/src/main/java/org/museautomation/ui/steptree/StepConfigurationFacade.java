package org.museautomation.ui.steptree;

import javafx.scene.*;
import net.christophermerrill.FancyFxTree.*;
import org.museautomation.core.step.*;
import org.museautomation.ui.extend.edit.step.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepConfigurationFacade implements FancyTreeNodeFacade<StepConfiguration>
	{
	StepConfigurationFacade(StepEditContext context, StepConfiguration step)
		{
		_node = new StepConfigurationTreeNode(context, step);
		}

	private StepConfigurationFacade(StepConfigurationTreeNode node)
		{
		_node = node;
		}

	@Override
	public Node getCustomCellUI()
		{
		return _node.getCustomCellUI();
		}

	@Override
	public FancyTreeCellEditor getCustomEditorUI()
		{
		return _node.getCustomEditorUI(this);
		}

	@Override
	public void editStarting()
		{
		_node.editStarting();
		}

	@Override
	public void editFinished()
		{
		_node.editFinished();
		}

	@Override
	public StepConfiguration getModelNode()
		{
		return _node.getModelNode();
		}

	@Override
	public List<FancyTreeNodeFacade<StepConfiguration>> getChildren()
		{
		return _node.getChildren();
		}

	@Override
	public FancyTreeNodeFacade<StepConfiguration> copyAndDestroy()
		{
		return new StepConfigurationFacade(_node);
		}

	@Override
	public List<String> getStyles()
		{
		return _node.getStyles();
		}

	@Override
	public void destroy()
		{
		_node.destroy();
		}

	@Override
	public void setTreeItemFacade(FancyTreeItemFacade item_facade)
		{
		_node.setTreeItemFacade(item_facade);
		}

	@Override
	public String getLabelText()
		{
		return _node.getLabelText();
		}

	@Override
	public Node getIcon()
		{
		return _node.getIcon();
		}

	@Override
	public void setLabelText(String new_value)
		{
		// no-op: using custom editor.
		}

	public boolean referencesExternalResource()
		{
		return _node.referencesExternalResource();
		}

	public boolean isInProgress()
		{
		return _node.isInProgress();
		}

	private final StepConfigurationTreeNode _node;
	}