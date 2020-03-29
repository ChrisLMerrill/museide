package org.museautomation.ui.steptree;

import javafx.application.*;
import javafx.scene.*;
import net.christophermerrill.FancyFxTree.*;
import org.museautomation.ui.step.*;
import org.museautomation.core.*;
import org.museautomation.core.context.*;
import org.museautomation.core.events.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.step.*;
import org.museautomation.core.step.descriptor.*;
import org.museautomation.core.step.events.*;
import org.museautomation.core.step.events.TypeChangeEvent;
import org.museautomation.core.util.*;
import org.museautomation.core.values.events.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.ui.extend.edit.step.*;
import org.museautomation.ui.extend.glyphs.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepConfigurationTreeNode implements MuseEventListener, InteractiveTaskStateListener
	{
	StepConfigurationTreeNode(StepEditContext edit_context, StepConfiguration step)
		{
		_edit_context = edit_context;
		_edit_context.getController().getBreakpoints().addBreakpointsListener(_breakpoints_listener);
		_edit_context.addShuttable(this::destroy);
		_edit_context.getController().addListener(this);
		if (!_edit_context.getController().getState().equals(InteractiveTaskState.IDLE))
			subscribeToExecutionContext();
		_step = step;
		_listener = new ChangeListener();
		_step.addChangeListener(_listener);
		_descriptor = _edit_context.getProject().getStepDescriptors().get(_step);

		loadChildren(_step.getChildren());
		}

	private void subscribeToExecutionContext()
		{
		_execution_context = (SteppedTaskExecutionContext) _edit_context.getController().getTestRunner().getExecutionContext();
		_execution_context.addEventListener(this);
		}

	private void unsubscribeFromExecutionContext()
		{
		if (_execution_context != null)
			{
			_execution_context.removeEventListener(this);
			_execution_context = null;
			}
		}

	private void loadChildren(List<StepConfiguration> children)
		{
		if (children != null)
			{
			int index = 0;
			StepEditContext edit_context = new ChildStepEditContext(_edit_context, _step);
			for (StepConfiguration child : children)
				{
				final StepConfigurationFacade child_facade = new StepConfigurationFacade(edit_context, child);
				_children.add(child_facade);
				if (_item_facade != null)
					_item_facade.addChild(child_facade, index);
				index++;
				}
			}
		}

	private void unloadChildren()
		{
		for (int i = _children.size() - 1; i >= 0; i--)
			{
			_item_facade.removeChild(i, _children.get(i));
			_children.remove(i);
			}
		}

	Node getCustomCellUI()
		{
		return null;
		}

	public FancyTreeCellEditor getCustomEditorUI(StepConfigurationFacade facade)
		{
		if (_editor == null)
			_editor = new StepCellEditor(_edit_context, facade, StepTreeDoubleClickModifiers.CURRENT._control_down);
		Platform.runLater(_editor::requestFocus);
		return _editor;
		}

	public void editStarting()
		{
		_editing = true;
		}

	public void editFinished()
		{
		if (_editor != null)
			{
			_editor.destroy();
			_editor = null;
			}
		_editing = false;
		}

	StepConfiguration getModelNode()
		{
		return _step;
		}

	public List<FancyTreeNodeFacade<StepConfiguration>> getChildren()
		{
		return _children;
		}

	List<String> getStyles()
		{
		return Collections.singletonList(_style);
		}

	void destroy()
		{
		Platform.runLater(() ->
			{
			if (_step == null)
				return; // already destroyed
			unsubscribeFromExecutionContext();
			_edit_context.getController().removeListener(this);
            _edit_context.getController().getBreakpoints().removeBreakpointsListener(_breakpoints_listener);
            _breakpoints_listener = null;
            
			_edit_context = null;
			if (_step != null)
				_step.removeChangeListener(_listener);
			_listener = null;
			if (_children != null)
				{
				_children.clear();
				_children = null;
				}
			_step = null;
			});
		}

	void setTreeItemFacade(FancyTreeItemFacade item_facade)
		{
		_item_facade = item_facade;
		}

	String getLabelText()
		{
		return StepCellRenderers.get(_edit_context.getProject()).findRenderer(_edit_context, _step).getStepLabel();
		}

	Node getIcon()
		{
		if (_state_icon != null)
			return _state_icon;

		if (_step_icon == null)
			{
			ColorDescriptor color = RgbColorDescriptor.BLACK;
			if (_edit_context.getController().getBreakpoints().isBreakpoint(_step))
			    color = RgbColorDescriptor.RED;
			_step_icon = StepGraphicBuilder.getInstance().getStepIcon(_descriptor, _edit_context.getProject(), color);
			_step_icon.getStyleClass().add("step-glyph");
			_step_icon.getStyleClass().add(Glyphs.getStyleName(_descriptor.getIconDescriptor(), null, null));
			}
		return _step_icon;
		}

	private void setStyle(String style)
		{
		if (style.equals(_style))
			return;
		_style = style;
		if (!_editing)
			_item_facade.refreshDisplay();
		}

	private boolean isStyle(String style)
		{
		return style.equals(_style);
		}

	@Override
	public void eventRaised(MuseEvent test_event)
		{
		// is this is an event about THIS step?
		if (test_event.hasAttribute(StepEventType.STEP_ID, _step.getStepId()))
			{
			switch (test_event.getTypeId())
				{
				case StartStepEventType.TYPE_ID:
					_state_icon = GraphicNodeBuilder.getInstance().getSpinner();
					setStyle(RUNNING_STYLE);
					break;
				case EndStepEventType.TYPE_ID:
					_state_icon = null;
					if (test_event.hasTag(StepEventType.INCOMPLETE))
						{
						_state_icon = GraphicNodeBuilder.getInstance().getEllipsis();
						setStyle(INCOMPLETE_STYLE);
						}
					else
						{
						setStyle(COMPLETE_STYLE);
						if (_loaded_indirect_children)
							{
							unloadChildren();
							_loaded_indirect_children = false;
							}
						}
					break;
				case DynamicStepLoadingEventType.TYPE_ID:
					// note: MUST do this immediately (not Platform.runLater() or it happens after the pause event, which is
					// required to get the next step highlighted in the UI correctly
					loadChildren(DynamicStepLoadingEventType.getLoadedSteps(test_event, _execution_context));
					_loaded_indirect_children = true;
					break;
				case PauseTaskEventType.TYPE_ID:
					_state_icon = Glyphs.create("FA:PAUSE");
					setStyle(PAUSED_STYLE);
					break;
				}
			}
		}

	@Override
	public void stateChanged(InteractiveTaskState state)
		{
		if (state.equals(InteractiveTaskState.STARTING))
			subscribeToExecutionContext();
		else if (state.equals(InteractiveTaskState.STOPPING))
			{
			unsubscribeFromExecutionContext();
			_state_icon = null;
			setStyle(DEFAULT_STYLE);

			if (_loaded_indirect_children)
				{
				for (int i = _children.size() - 1; i >= 0; i--)
					{
					final FancyTreeNodeFacade<StepConfiguration> child = _children.get(i);
					_item_facade.removeChild(i, child);
					child.destroy();
					}
				_children.clear();
				_loaded_indirect_children = false;
				}
			}
		}

	public StepEditContext getContext()
		{
		return _edit_context;
		}

	public boolean referencesExternalResource()
		{
		return _step.getType().equals(CallMacroStep.TYPE_ID)
			|| _step.getType().equals(CallFunction.TYPE_ID);
		}

	public boolean isInProgress()
		{
		return isStyle(RUNNING_STYLE) || isStyle(INCOMPLETE_STYLE);
		}


	private StepEditContext _edit_context;
	private StepConfiguration _step;
	private StepDescriptor _descriptor;
	private Node _step_icon;
	private Node _state_icon = null;
	private boolean _loaded_indirect_children = false;
	private SteppedTaskExecutionContext _execution_context = null;
	private boolean _editing = false;
	private StepCellEditor _editor = null;

	private List<FancyTreeNodeFacade<StepConfiguration>> _children = new ArrayList<>();
	private FancyTreeItemFacade _item_facade;
	private ChangeListener _listener;

	private Breakpoints.BreakpointsListener _breakpoints_listener = new Breakpoints.BreakpointsListener()
        {
        @Override
        public void breakpointChanged(StepConfiguration step)
            {
            if (step.equals(_step))
                {
                _step_icon = null;
                _item_facade.refreshDisplay();
                }
            }
        };

	private String _style = DEFAULT_STYLE;
	public static final String DEFAULT_STYLE = "step-default";
	public static final String RUNNING_STYLE = "step-running";
	public static final String COMPLETE_STYLE = "step-complete";
	public static final String INCOMPLETE_STYLE = "step-incomplete";
	public static final String PAUSED_STYLE = "step-paused";

	class ChangeListener extends StepChangeObserver
		{
		@Override
		protected void childAdded(StepConfiguration child, int index)
			{
			FancyTreeNodeFacade<StepConfiguration> child_facade = new StepConfigurationFacade(new ChildStepEditContext(_edit_context, _step), child);
			_children.add(index, child_facade);
			_item_facade.addChild(child_facade, index);
			}

		@Override
		protected void childRemoved(StepConfiguration child, int index)
			{
			FancyTreeNodeFacade<StepConfiguration> child_facade = _children.get(index);
			if (!(child_facade.getModelNode() == child))
				throw new IllegalArgumentException("The child at the index doesn't match the child to be removed. This is a bug in the maintenance of the child list.");
			_children.remove(index);
			_item_facade.removeChild(index, child_facade);
			}

		@Override
		public void changeEventRaised(ChangeEvent event)
			{
			if (event instanceof SourceChangedEvent ||
				event instanceof NamedSourceChangedEvent)
				{
				if (!_editing)
					_item_facade.refreshDisplay();
				}
			else
				super.changeEventRaised(event);
			}

		@Override
		protected void typeChanged(TypeChangeEvent event, String old_type, String new_type)
			{
			_descriptor = _edit_context.getProject().getStepDescriptors().get(_step);
			_step_icon = null;
			if (!_editing)
				_item_facade.refreshDisplay();
			}

		@Override
		protected void metadataChanged(MetadataChangeEvent event, String name, Object old_value, Object new_value)
			{
			if (!_editing)
				_item_facade.refreshDisplay();
			}
		}
	}