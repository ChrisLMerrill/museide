package org.museautomation.ui.steptree;

import javafx.application.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.FancyFxTree.*;
import org.museautomation.core.*;
import org.museautomation.core.events.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.step.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.step.*;
import org.museautomation.ui.extend.javafx.*;
import org.slf4j.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepTree2
	{
	public StepTree2(MuseProject project, StepConfiguration step, UndoStack undo_stack, InteractiveTestController controller)
		{
		_step = step;
		_tree = new FancyTreeView<>(new StepTreeOperations(project, undo_stack, step, controller.getBreakpoints()));
		_tree.getStylesheets().add(getClass().getResource("StepTree.css").toExternalForm());
		_tree.getStylesheets().add(Styles.getDefaultTreeStyles());
		_tree.setPadding(new Insets(2));
		_context = new RootStepEditContext(project, undo_stack, controller);
		_tree.setRoot(new StepConfigurationFacade(_context, step));
		_tree.setEditable(true);
		if (step.getChildren() != null && step.getChildren().size() > 0)
			_tree.expandToMakeVisible(step.getChildren().get(0));
		_undo_stack = undo_stack;
		_controller = controller;
		TestStateListener state_listener = new TestStateListener();
		_controller.addListener(state_listener);
		}

	public Node getNode()
		{
		return _tree;
		}

	public UndoStack getUndoStack()
		{
		return _undo_stack;
		}

	public StepConfiguration getSingleSelection()
		{
		ObservableList<TreeItem<StepConfigurationFacade>> selected_items = _tree.getSelectionModel().getSelectedItems();
		return selected_items.get(0).getValue().getModelNode();
		}

	public StepConfiguration getSelectionParent()
		{
		ObservableList<TreeItem<StepConfigurationFacade>> selected_items = _tree.getSelectionModel().getSelectedItems();
		final TreeItem<StepConfigurationFacade> parent = selected_items.get(0).getParent();
		if (parent == null)
			return null;
		return parent.getValue().getModelNode();
		}

	private void showStep(Long step_id)
		{
		Platform.runLater(() -> _auto_expanded_items.addAll(_tree.expandToMakeVisible(_step.findByStepId(step_id))));
		}

	public TreeView getTree()
		{
		return _tree;
		}

	public void dispose()
		{
		_context.closeShuttables();
		}

	private final StepConfiguration _step;
	private final FancyTreeView<StepConfigurationFacade> _tree;
	private final UndoStack _undo_stack;
	private final InteractiveTestController _controller;

	private final EventListener _event_listener = new EventListener();

	private HashSet<TreeItem<StepConfigurationFacade>> _auto_expanded_items = new HashSet<>();

	class TestStateListener implements InteractiveTaskStateListener
		{
		@Override
		public void stateChanged(InteractiveTaskState state)
			{
			if (state.equals(InteractiveTaskState.STARTING))
				_controller.getTestRunner().getExecutionContext().addEventListener(_event_listener);
			else if (state.equals(InteractiveTaskState.STOPPING))
				{
				_controller.getTestRunner().getExecutionContext().removeEventListener(_event_listener);
				for (TreeItem<StepConfigurationFacade> item : _auto_expanded_items)
					item.setExpanded(false);
				_auto_expanded_items.clear();
				}
			}
		}

	class EventListener implements MuseEventListener
		{
		@Override
		public void eventRaised(MuseEvent event)
			{
			switch (event.getTypeId())
				{
				case StartStepEventType.TYPE_ID:
					_tree.getSelectionModel().clearSelection();
					final Long started_step_id = StepEventType.getStepId(event);
					StepConfiguration step = _step.findByStepId(started_step_id);
					if (step == null)
						step = findDynamicallyLoadedStep(started_step_id);
					if (step == null)
						LOG.error("Unable to locate step id = " + started_step_id);
					else
						_auto_expanded_items.addAll(_tree.expandAndScrollTo(step));
					break;
				case EndStepEventType.TYPE_ID:
					final Long ended_step_id = StepEventType.getStepId(event);
					StepConfiguration ended = _step.findByStepId(ended_step_id);
					if (ended == null)
						ended = findDynamicallyLoadedStep(ended_step_id);
					if (ended == null)
						LOG.error("Unable to locate step id = " + ended_step_id);
					else
						for (TreeItem<StepConfigurationFacade> auto_expanded_item : _auto_expanded_items)
							if (ended.getStepId().equals(auto_expanded_item.getValue().getModelNode().getStepId()))
								{
								_auto_expanded_items.remove(auto_expanded_item);
								auto_expanded_item.setExpanded(false);
								break;
								}
					if (event.hasTag(StepEventType.INCOMPLETE))
						{
						StepConfiguration loaded_step = findDynamicallyLoadedStep(ended_step_id);
						if (loaded_step != null && loaded_step.getChildren() != null)
                            _auto_expanded_items.addAll(_tree.expandAndScrollTo(loaded_step.getChildren().get(0)));
						else if (_loaded_steps.containsKey(ended_step_id))
							_auto_expanded_items.addAll(_tree.expandAndScrollTo(_loaded_steps.get(ended_step_id).get(0)));
						}
					else
						_loaded_steps.remove(ended_step_id);
					break;
				case PauseTaskEventType.TYPE_ID:
					showStep(StepEventType.getStepId(event));
					break;
				case DynamicStepLoadingEventType.TYPE_ID:
					_loaded_steps.put(StepEventType.getStepId(event), DynamicStepLoadingEventType.getLoadedSteps(event, _controller.getTestRunner().getExecutionContext()));
					break;
				}
			}

		private StepConfiguration findDynamicallyLoadedStep(Long step_id)
			{
			for (List<StepConfiguration> list : _loaded_steps.values())
				for (StepConfiguration step : list)
					{
					if (step_id.equals(step.getStepId()))
						return step;
					StepConfiguration found = step.findByStepId(step_id);
					if (found != null)
						return found;
					}
			return null;
			}

		private Map<Long, List<StepConfiguration>> _loaded_steps = new HashMap<>();
		}

	private final StepEditContext _context;

	private final static Logger LOG = LoggerFactory.getLogger(StepTree2.class);
	}