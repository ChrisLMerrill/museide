package org.museautomation.ui.extend.grid;

import javafx.scene.*;
import javafx.scene.layout.*;
import org.museautomation.ui.extend.actions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class GridPaneRowWithAdvancedEditingMode extends GridPaneRow
    {
    @SuppressWarnings("unused")  // public API
    public GridPaneRowWithAdvancedEditingMode(GridPaneRows rows, int row_index, UndoStack undo_stack)
        {
        super(rows, row_index);
        _undo_stack = undo_stack;
        }

    @Override
    public void setNode(Node node, int column)
        {
        super.setNode(node, column);
        _nodes.put(column, node);
        }

    @SuppressWarnings("unused")  // used by implementors
    public void enterAdvancedMode()
        {
        GridPane grid = _rows.getGridPane();
        _restore_point = _undo_stack.getRestorePoint();

        // remove the basic controls from the grid
        for (Node node : _nodes.values())
            grid.getChildren().remove(node);

        _advanced_node = createAdvancedNode();
        _advanced_node.getStyleClass().add("editing-container");
        GridPane.setColumnSpan(_advanced_node, 3);

        // add the stack to the grid
        grid.add(_advanced_node, 0, getIndex());
        }

    @SuppressWarnings("WeakerAccess")  // used by implementors
    protected abstract Node createAdvancedNode();

    @SuppressWarnings("unused")  // used by implementors
    protected void returnToBasicMode(boolean save)
        {
        GridPane grid = _rows.getGridPane();

        // remove the stack from the grid
        grid.getChildren().remove(_advanced_node);
        _advanced_node = null;

        // return the basic controls to the grid
        for (Integer column : _nodes.keySet())
            grid.add(_nodes.get(column), column, getIndex());

        if (!save)
            _restore_point.revertTo();
        }

    // the nodes, mapped by their column
    private Map<Integer, Node> _nodes = new HashMap<>();

    // advanced mode
    private Node _advanced_node;
    private UndoStack.UndoPoint _restore_point;
    private UndoStack _undo_stack;
    }


