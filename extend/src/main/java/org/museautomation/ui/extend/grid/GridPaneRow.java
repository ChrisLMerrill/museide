package org.museautomation.ui.extend.grid;

import javafx.scene.*;
import javafx.scene.layout.*;

import java.util.*;

/**
 * Represents a row in a GridPane. Allows you to manipulate rows of components (i.e. insert and delete)
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class GridPaneRow
    {
    public GridPaneRow(GridPaneRows rows, int row_index)
        {
        _rows = rows;
        _row = row_index;
        _rows.add(this);
        }

    public void setNode(Node node, int column)
        {
        _rows.getGridPane().add(node, column, _row);
        _nodes.add(node);
        }

    public void removeNodes()
        {
        for (Node node : _nodes)
            _rows.getGridPane().getChildren().remove(node);
        }

    public int getIndex()
        {
        return _row;
        }

    void shiftRow(int by)
        {
        int new_row = _row + by;
        for (Node node : _nodes)
            GridPane.setRowIndex(node, new_row);
        _row = new_row;
        }

    protected GridPaneRows _rows;
    private int _row;
    private Set<Node> _nodes = new HashSet<>();
    }


