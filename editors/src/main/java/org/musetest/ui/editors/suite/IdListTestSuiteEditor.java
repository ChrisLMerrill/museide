package org.musetest.ui.editors.suite;

import javafx.application.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import org.controlsfx.control.*;
import org.musetest.core.*;
import org.musetest.core.suite.*;
import org.musetest.ui.extend.components.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.glyphs.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class IdListTestSuiteEditor extends BaseTestSuiteEditor
    {
    @Override
    protected Parent getEditorArea()
        {
        return _grid;
        }

    @Override
    public void editResource(MuseProject project, MuseResource resource)
        {
        super.editResource(project, resource);
        _grid.setVgap(5);
        _grid.setHgap(5);
        _grid.setPadding(new Insets(5));
        _suite = (IdListTestSuite) resource;
        _suite.addChangeListener(_listener);

        Button add_button = new Button("Add", Glyphs.create("FA:PLUS"));
        add_button.setTooltip(new Tooltip("Add test or test suite to this test suite"));
        add_button.setId(ADD_BUTTON_ID);
        add_button.setOnAction(event ->
            {
            PopupDialog popper = new PopupDialog("Add", "Choose resources")
                {
                private CheckListView<String> _list;

                @Override
                protected Node createContent()
                    {
                    _list = new CheckListView();
                    new UnusedTests(project, _suite).addUnusedTestIds(_list.getItems());

                    _list.getCheckModel().getCheckedItems().addListener(
                        (ListChangeListener<String>) c -> setOkButtonEnabled(_list.getCheckModel().getCheckedIndices().size() > 0));

                    return _list;
                    }

                @Override
                protected boolean okPressed()
                    {
                    addTests(_list.getCheckModel().getCheckedItems());
                    return true;
                    }
                };
            popper.setOkButtonEnabled(false);
            popper.show(add_button);
            });

        Button remove_button = new Button("Remove", Glyphs.create("FA:MINUS"));
        remove_button.setTooltip(new Tooltip("Remove selections from suite"));
        remove_button.setId(REMOVE_BUTTON_ID);
        remove_button.setOnAction(event -> removeSelectedTests());
        remove_button.setDisable(true);

        HBox button_box = new HBox();
        button_box.setSpacing(5);
        _grid.add(button_box, 0, 0);
        button_box.getChildren().add(add_button);
        button_box.getChildren().add(remove_button);

        // create list of test ids
        _list_of_ids = FXCollections.observableArrayList();
        _list_of_ids.addAll(_suite.getTestIds());

        _list = new ListView<>(new SortedList<>(_list_of_ids, String::compareTo));  // pass in a sorted list
        _list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        _grid.add(_list, 0, 1);
        GridPane.setHgrow(_list, Priority.ALWAYS);

        // listen for delete key
        _list.setOnKeyPressed(event ->
            {
            if (event.getCode() == KeyCode.DELETE)
                removeSelectedTests();
            });

        // disable the delete button unless something is selected
        _list.getSelectionModel().getSelectedItems().addListener(
            (ListChangeListener<String>) c -> remove_button.setDisable(_list.getSelectionModel().getSelectedItems().size() == 0));
        }

    private void removeSelectedTests()
        {
        ObservableList<String> selections = _list.getSelectionModel().getSelectedItems();
        if (selections.size() > 0)
            {
            List to_delete = new ArrayList(selections);
            new RemoveTestsFromSuiteAction(_suite, to_delete).execute(getUndoStack());
            }
        }

    @Override
    public boolean canEdit(MuseResource resource)
        {
        return resource instanceof IdListTestSuite;
        }

    @Override
    public ValidationStateSource getValidationStateSource()
        {
        return null;
        }

    @Override
    public void requestFocus()
        {
        _list.requestFocus();
        }

    private void addTests(List<String> ids)
        {
        if (ids.size() > 0)
            new AddTestsToSuiteAction(_suite, ids).execute(getUndoStack());
        }

    private ListView<String> _list;
    private GridPane _grid = new GridPane();
    private IdListTestSuite _suite;
    private ObservableList<String> _list_of_ids;

    private IdListTestSuite.ChangeListener _listener = new IdListTestSuite.ChangeListener()
        {
        @Override
        public void testIdAdded(String id)
            {
            Platform.runLater(() -> _list_of_ids.add(id));
            }

        @Override
        public void testIdRemoved(String id)
            {
            Platform.runLater(() -> _list_of_ids.remove(id));
            }
        };

    final static String ADD_BUTTON_ID = "iltse-add-button";
    private final static String REMOVE_BUTTON_ID = "iltse-remove-button";
    }


