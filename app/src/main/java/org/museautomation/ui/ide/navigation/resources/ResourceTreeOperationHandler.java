package org.museautomation.ui.ide.navigation.resources;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.*;
import net.christophermerrill.FancyFxTree.*;
import org.museautomation.ui.extend.icons.*;
import org.museautomation.ui.ide.*;
import org.museautomation.ui.ide.navigation.resources.actions.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.ui.extend.actions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceTreeOperationHandler extends FancyTreeOperationHandler<ResourceTreeNodeFacade>
    {
    public ResourceTreeOperationHandler(MuseProject project, ResourceEditors editors, UndoStack undo, Node node)
        {
        _project = project;
        _editors = editors;
        _undo = undo;
        _node = node;
        }

    @Override
    public void handleDoubleClick(TreeCell<ResourceTreeNodeFacade> item, boolean control_down, boolean shift_down, boolean alt_down)
	    {
        if (_selections.size() == 1)
            {
            ResourceTreeNode node = _selections.get(0).getModelNode();
            if (node instanceof ResourceNode)
                {
                ResourceNode resource_node = (ResourceNode) node;
                boolean handled = _editors.editResource(resource_node.getResourceToken(), resource_node.getProject());
                if (!handled)
                    {
                    Alert dialog = new Alert(Alert.AlertType.WARNING, "Sorry...I can't find an editor for this resource.");
                    dialog.setTitle("Unsupported Operation");
                    dialog.setHeaderText(null);
                    dialog.initStyle(StageStyle.UTILITY);
                    dialog.initOwner(_node.getScene().getWindow());
                    dialog.show();
                    }
                }
            }
        }

    @Override
    public void selectionChanged(ObservableList<TreeItem<ResourceTreeNodeFacade>> selected_items)
        {
        _selections = new ArrayList<>();
        for (TreeItem<ResourceTreeNodeFacade> item : selected_items)
            _selections.add(item.getValue());
        }

    @Override
    public boolean handleCopy(ObservableList<TreeItem<ResourceTreeNodeFacade>> selected_items)
        {
        List<ResourceToken<MuseResource>> to_copy = new ArrayList<>();
        for (TreeItem<ResourceTreeNodeFacade> item : selected_items)
            if (item.getValue().getModelNode() instanceof ResourceNode)
                to_copy.add(((ResourceNode)item.getValue().getModelNode()).getResourceToken());

        _paste_action = CopyResourceAction.create(to_copy, _project);
        return true;
        }

    @Override
    public boolean handlePaste(ObservableList<TreeItem<ResourceTreeNodeFacade>> selected_items)
        {
        if (_paste_action == null)
            return false;
        return _paste_action.execute(_undo);
        }

    @Override
    public boolean handleDelete(ObservableList<TreeItem<ResourceTreeNodeFacade>> selected_items)
        {
        if (selected_items.size() == 0)
            return true;

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, String.format("Do you want to delete %d resource(s)?", selected_items.size()));
        dialog.setTitle("Confirm delete");
        dialog.setHeaderText(null);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.initOwner(_node.getScene().getWindow());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
            {
            List<ResourceToken<MuseResource>> to_delete = new ArrayList<>();
            for (TreeItem<ResourceTreeNodeFacade> item : selected_items)
                if (item.getValue().getModelNode() instanceof ResourceNode)
                    to_delete.add(((ResourceNode)item.getValue().getModelNode()).getResourceToken());

            return DeleteResourceAction.create(to_delete, _project).execute(_undo);
            }

        return false;
        }

    @Override
    public boolean handleCut(ObservableList<TreeItem<ResourceTreeNodeFacade>> selected_items)
        {
        return false;  // So far only supporting one project at a time, so can only paste back into the same project.
        }

    @Override
    public boolean handleUndo()
        {
        _undo.undoLastAction();
        return true;
        }

    @Override
    public DragOverInfo dragOver(Dragboard dragboard, ResourceTreeNodeFacade onto_node)
	    {
	    return new DragOverInfo();
	    }

    @Override
    public ContextMenu getContextMenu(ObservableList<TreeItem<ResourceTreeNodeFacade>> selected_items)
        {
        if (selected_items.size() == 1 && selected_items.get(0).getValue().getModelNode() instanceof ResourceGroupNode)
            {
            MenuItem item = new MenuItem("New...");
            item.setOnAction(event ->
                {
                ResourceGroupNode group = (ResourceGroupNode) selected_items.get(0).getValue().getModelNode();
                CreateResourcePanel dialog = new CreateResourcePanel(_project, _undo);
                Icons.setIcons(dialog.getDialog().getDialogPane());
                if (group instanceof ResourceTypeGroupNode)
                    dialog.setType(((ResourceTypeGroupNode) group).getType());
                dialog.getDialog().initOwner(_node.getScene().getWindow());
                dialog.getDialog().show();
                });

            ContextMenu menu = new ContextMenu();
            menu.getItems().add(item);
            return menu;
            }
        return null;
        }

    private final MuseProject _project;
    private final ResourceEditors _editors;
    private final UndoStack _undo;
    private final Node _node;

    private List<ResourceTreeNodeFacade> _selections = Collections.emptyList();
    private UndoableAction _paste_action = null;
    }


