package org.museautomation.ui.extend.grid;

import javafx.scene.layout.*;

import java.util.*;

/**
 * In conjunction with GridPaneRow, provides simpler interface for manipulating a GridPane as
 * a group of rows.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class GridPaneRows
    {
    public GridPaneRows(GridPane pane)
        {
        _grid = pane;
        }

    public void add(GridPaneRow row)
        {
        if (_rows.contains(row))
            return; // already in the grid

        try
            {
            if (_rows.get(row.getIndex()) != null)
                shiftRowsDown(row.getIndex());
            }
        catch (IndexOutOfBoundsException e)
            {
            // ok...adding past the end, so don't need to worry about shifting them
            }
        _rows.add(row.getIndex(), row);
        }

    private void shiftRowsDown(int starting_row)
        {
        for (int i = _rows.size() - 1; i >= starting_row ; i--)
            {
            GridPaneRow row = _rows.get(i);
            if (row != null)
                row.shiftRow(1);
            }
        }

    GridPane getGridPane()
        {
        return _grid;
        }

    public GridPaneRow remove(int row_index)
        {
        GridPaneRow row = _rows.get(row_index);
        remove(row);
        return row;
        }

    public void remove(GridPaneRow second_row)
        {
        second_row.removeNodes();
        shiftRowsUp(second_row.getIndex() + 1);
        _rows.remove(second_row);
        }

    private void shiftRowsUp(int starting_row)
        {
        for (int i = _rows.size() - 1; i >= starting_row ; i--)
            {
            GridPaneRow row = _rows.get(i);
            if (row != null)
                row.shiftRow(-1);
            }
        }

    public List<GridPaneRow> getRows()
        {
        return Collections.unmodifiableList(_rows);
        }

    public void removeAll()
        {
        if (_rows.size() == 0)
            return;
        _grid.getChildren().clear();
        _rows.clear();
        }

    public int size()
        {
        return _rows.size();
        }

    public GridPaneRow getRow(int index)
        {
        return _rows.get(index);
        }

    private GridPane _grid;
    private List<GridPaneRow> _rows = new ArrayList<>();
    }
