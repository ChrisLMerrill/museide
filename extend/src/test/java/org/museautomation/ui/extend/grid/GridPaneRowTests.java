package org.museautomation.ui.extend.grid;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class GridPaneRowTests extends ComponentTest
    {
    @Test
    void addRemoveRow()
        {
        GridPaneRow row = createRow(0);
        Assertions.assertTrue(exists(getRowLabel(0)));

        Platform.runLater(row::removeNodes);
        waitForUiEvents();
        Assertions.assertFalse(exists("row0"));
        }

    @Test
    void addSecondRow()
        {
        createRow(0);
        Assertions.assertTrue(exists(getRowLabel(0)));
        createRow(1);
        Assertions.assertTrue(exists(getRowLabel(1)));

        // ensure the first is visually above the bottom
        Node label0 = lookup(getRowLabel(0)).query();
        Node label1 = lookup(getRowLabel(1)).query();
        Assertions.assertTrue(label0.getLayoutY() < label1.getLayoutY());
        }

    @Test
    void removeFirstRow()
        {
        GridPaneRow first_row = createRow(0);
        createRow(1);
        Platform.runLater(first_row::removeNodes);
        waitForUiEvents();

        Assertions.assertFalse(exists(getRowLabel(0)));
        Assertions.assertTrue(exists(getRowLabel(1)));
        }

    @Test
    void removeLastRow()
        {
        createRow(0);
        GridPaneRow second_row = createRow(1);
        Platform.runLater(() -> _rows.remove(second_row));
        waitForUiEvents();

        Assertions.assertTrue(exists(getRowLabel(0)));
        Assertions.assertFalse(exists(getRowLabel(1)));
        }

    @Test
    void insertRow()
        {
        createRow(0);
        Assertions.assertTrue(exists(getRowLabel(0)));

        GridPaneRow row2 = new GridPaneRow(_rows, 1);
        _rows.add(row2);
        Platform.runLater(() -> row2.setNode(new Label(getRowLabel(2)), 0));
        waitForUiEvents();
        Assertions.assertTrue(exists(getRowLabel(2)));

        // insert the row
        GridPaneRow row1 = new GridPaneRow(_rows, 1);
        Platform.runLater(() -> row1.setNode(new Label(getRowLabel(1)), 0));
        waitForUiEvents();
        Assertions.assertTrue(exists(getRowLabel(1)));

        Node label0 = lookup(getRowLabel(0)).query();
        Node label1 = lookup(getRowLabel(1)).query();
        Node label2 = lookup(getRowLabel(2)).query();
        Assertions.assertTrue(label0.getLayoutY() < label1.getLayoutY() && label1.getLayoutY() < label2.getLayoutY());
        }

    private GridPaneRow createRow(int index)
        {
        GridPaneRow row = new GridPaneRow(_rows, index);
        Platform.runLater(() -> row.setNode(new Label(getRowLabel(index)), 0));
        waitForUiEvents();
        return row;
        }

    private String getRowLabel(int index)
        {
        return "row" + index;
        }

    @Override
    public Node createComponentNode()
        {
        GridPane pane = new GridPane();
        _rows = new GridPaneRows(pane);
        return pane;
        }

    private GridPaneRows _rows;
    }