package org.musetest.ui.steptree;

import javafx.application.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import net.christophermerrill.FancyFxTree.*;
import org.jetbrains.annotations.*;
import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.javafx.*;
import org.musetest.ui.step.actions.*;
import org.slf4j.*;

import java.util.*;
import java.util.stream.*;

import static net.christophermerrill.FancyFxTree.FancyTreeOperationHandler.EditType.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepTreeOperations extends FancyTreeOperationHandler<StepConfigurationFacade>
	{
	StepTreeOperations(MuseProject project, UndoStack undo, StepConfiguration root)
		{
		_project = project;
		_undo = undo;
		_root = root;
		}

	@Override
	public boolean handleDelete(ObservableList<TreeItem<StepConfigurationFacade>> selected_items)
		{
		StepConfiguration root = getRoot(selected_items);
		List<StepConfiguration> to_delete = getSelectedConfigurations(selected_items);
		DeleteStepsAction action = new DeleteStepsAction(root, to_delete);
		return action.execute(_undo);
		}

	@Override
	public boolean handleCut(ObservableList<TreeItem<StepConfigurationFacade>> selected_items)
		{
		StepConfiguration root = getRoot(selected_items);
		List<StepConfiguration> to_cut = getSelectedConfigurations(selected_items);
		CutStepsToClipboardAction action = new CutStepsToClipboardAction(root, to_cut);
		return action.execute(_undo);
		}

	@Override
	public boolean handleCopy(ObservableList<TreeItem<StepConfigurationFacade>> selected_items)
		{
LOG.info("COPY stuff");
		List<StepConfiguration> to_copy = getSelectedConfigurations(selected_items);
		List<StepConfiguration> copies = new ArrayList<>();
		for (StepConfiguration target : to_copy)
			copies.add(StepConfiguration.copy(target, _project));
		CopyStepsToClipboardAction action = new CopyStepsToClipboardAction(copies);
		return action.execute(_undo);
		}

	@Override
	public boolean handlePaste(ObservableList<TreeItem<StepConfigurationFacade>> selected_items)
		{
		TreeItem<StepConfigurationFacade> target_item = selected_items.get(0);
		final StepConfiguration target_step = target_item.getValue().getModelNode();
		int index = 0;
		StepConfiguration parent_step = target_step;
		if (target_item.getParent() != null)
			{
			parent_step = target_item.getParent().getValue().getModelNode();
			index = parent_step.getChildren().indexOf(target_step) + 1;
			}
		return new PasteStepsFromClipboardAction(_project, parent_step, index).execute(_undo);
		}

	@NotNull
	private List<StepConfiguration> getSelectedConfigurations(ObservableList<TreeItem<StepConfigurationFacade>> selected_items)
		{
		List<StepConfiguration> to_delete = new ArrayList<>();
		for (TreeItem<StepConfigurationFacade> facade : selected_items)
			to_delete.add(facade.getValue().getModelNode());
		return to_delete;
		}

	private StepConfiguration getRoot(ObservableList<TreeItem<StepConfigurationFacade>> selected_items)
		{
		return TreeItems.getRoot(selected_items.get(0)).getValue().getModelNode();
		}

	@Override
	public boolean handleUndo()
		{
		return _undo.undoLastAction();
		}

	@Override
	public void handleDoubleClick(TreeCell<StepConfigurationFacade> cell, boolean control_down, boolean shift_down, boolean alt_down)
		{
		if (isContainedInExternalResource(cell))
			{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Not supported");
			alert.setHeaderText("In-place editing not allowed");
			alert.setContentText("Macros and Functions cannot be edited here. Open it in a separate editor to edit. Changes will be reflected the next time it is involked from the test.");
			alert.show();
			return;
			}
		if (cell.getItem().isInProgress())
			{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Not supported");
			alert.setHeaderText("Step is in-progress");
	        alert.setContentText("This step cannot be edited because it is in progress (currently running or incomplete).");
			alert.show();
			return;
			}
		StepTreeDoubleClickModifiers.CURRENT = new StepTreeDoubleClickModifiers(control_down, shift_down, alt_down);
		cell.startEdit();
		}

	private boolean isContainedInExternalResource(TreeCell<StepConfigurationFacade> cell)
		{
		TreeItem<StepConfigurationFacade> item = cell.getTreeItem().getParent();
		while (item != null)
			{
			if (item.getValue().referencesExternalResource())
				return true;
			item = item.getParent();
			}
		return false;
		}

	@Override
	public StartDragInfo startDrag(List<List<Integer>> selection_paths, ObservableList<TreeItem<StepConfigurationFacade>> selected_items)
		{
		StartDragInfo info = new StartDragInfo();
		info._transfer_modes = TransferMode.ANY;
		List<Long> ids = selected_items.stream().map(item -> item.getValue().getModelNode().getStepId()).collect(Collectors.toList());
LOG.info(String.format("startDrag %d steps", ids.size()));
		info.addContent(DataFormat.PLAIN_TEXT, ClipboardSerializer.listOfLongsToString(ids));
		return info;
		}

	@Override
	public DragOverInfo dragOver(Dragboard dragboard, StepConfigurationFacade onto_node)
		{
		DragOverInfo info = new DragOverInfo();
		if (dragboard.getContent(DataFormat.PLAIN_TEXT) != null)
			{
LOG.info("dragOver - about to get the nodes");
            Object content = dragboard.getContent(DataFormat.PLAIN_TEXT);
LOG.info("dragboard content is a " + content.getClass().getSimpleName());
			List<Long> dropped_nodes = ClipboardSerializer.listOfLongsfromString((String) dragboard.getContent(DataFormat.PLAIN_TEXT));
LOG.info(String.format("dragOver %d steps", dropped_nodes.size()));
//			if (dropped_nodes.contains(onto_node.getModelNode().getStepId()))
//				return info;

			info = super.dragOver(dragboard, onto_node);
			if (!_project.getStepDescriptors().get(onto_node.getModelNode()).isCompound())
                info.removeDropLocation(DropLocation.ON);
            return info;
			}
        else
            LOG.info("StepList is null :(");
		return info;
		}

	@Override
	public boolean finishDrag(TransferMode transfer_mode, Dragboard dragboard, StepConfigurationFacade item, DropLocation location)
		{
LOG.info("finishDrag()");
		if (dragboard.getContent(DataFormat.PLAIN_TEXT) != null)
			{
            List<Long> dropped_step_ids = ClipboardSerializer.listOfLongsfromString((String) dragboard.getContent(DataFormat.PLAIN_TEXT));
LOG.info(String.format("finishDrag %d steps",dropped_step_ids.size()));
			List<StepConfiguration> dropped_steps = new ArrayList<>();
			for (Long id : dropped_step_ids)
				{
				final StepConfiguration step = _root.findByStepId(id);
				if (step == null)
					{
					LOG.error(String.format("Unable to find step %d in the root", id));
					return false;
					}
				dropped_steps.add(step);
				}
			if (dropped_steps.size() == 0)
				return false;
			StepConfiguration parent;
			int add_at_index;
			switch (location)
				{
				case BEFORE:
					parent = _root.findParentOf(item.getModelNode());
					add_at_index = parent.getChildren().indexOf(item.getModelNode());
					break;
				case ON:
					parent = item.getModelNode();
					add_at_index = 0;
					if ((item.getModelNode()).getChildren() != null)
						add_at_index = (item.getModelNode()).getChildren().size();
					break;
				case AFTER:
					parent = _root.findParentOf(item.getModelNode());
					add_at_index = parent.getChildren().indexOf(item.getModelNode()) + 1;
					break;
				default:
					return false;
				}

			UndoableAction action;
			if (transfer_mode.equals(TransferMode.COPY))
				{
				// must copy the steps
				List<StepConfiguration> steps_to_drop = new ArrayList<>();
				for (StepConfiguration step : dropped_steps)
					steps_to_drop.add(StepConfiguration.copy(step, _project));
				action = new InsertStepsAction(parent, steps_to_drop, add_at_index);
				}
			else
				{
				// move is a delete and insert
				CompoundAction compound = new CompoundAction();
				DeleteStepsAction delete = new DeleteStepsAction(_root, dropped_steps);
				compound.addAction(delete);
				int index_offset = add_at_index;
				if (parent.getChildren() != null)
					index_offset -= parent.getChildren().indexOf(item.getModelNode());
				InsertStepsAction insert = new InsertStepsRelativeAction(parent, dropped_steps, item.getModelNode(), index_offset);
				compound.addAction(insert);
				action = compound;
				}

			Platform.runLater(() -> action.execute(_undo));
			}
		return false;
		}

	@Override
	public ContextMenu getContextMenu(ObservableList<TreeItem<StepConfigurationFacade>> selected_items)
		{
		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(createEditMenuItems(selected_items, Cut, Copy, Paste, Delete));
		return menu;
		}

	private final MuseProject _project;
	private final UndoStack _undo;
	private final StepConfiguration _root;

	private final static DataFormat LIST_OF_STEP_IDS = new DataFormat("application/x-list-of-step-ids");

	private final static Logger LOG = LoggerFactory.getLogger(StepTreeOperations.class);
	}


