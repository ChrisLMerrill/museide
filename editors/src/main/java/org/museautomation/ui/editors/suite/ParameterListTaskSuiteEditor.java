package org.museautomation.ui.editors.suite;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.suite.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.ui.extend.edit.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ParameterListTaskSuiteEditor extends BaseTaskSuiteEditor
    {
    @Override
    public void editResource(MuseProject project, MuseResource resource)
        {
        super.editResource(project, resource);
        _suite = (ParameterListTaskSuite) resource;
        _suite.addListener(_listener);

        Platform.runLater(this::createUI);

        // deregister changes when de-parented
        new NodeParentChangeListener(_grid)
            {
            @Override
            public void onRemove()
                {
                _suite.removeListener(_listener);
                }
            };
        }

    private void createUI()
        {
        _task_chooser = new ResourceIdChooser(getProject(), new MuseTask.TaskResourceType(), _suite.getTaskId());
        _task_chooser.setId(TEST_FIELD_ID);
        _grid.add(_task_chooser, 1, 0);
        _task_chooser.valueProperty().addListener((observable, oldValue, newValue) ->
            {
            if (!Objects.equals(_suite.getTaskId(), _task_chooser.getSelectedId()))
                new SetTaskIdAction(_suite, _task_chooser.getSelectedId()).execute(getUndoStack());
            });

        _data_chooser = new ResourceIdChooser(getProject(), new DataTable.DataTableResourceType(), _suite.getDataTableId());
        _data_chooser.setId(DATATABLE_FIELD_ID);
        _grid.add(_data_chooser, 1, 1);
        _data_chooser.valueProperty().addListener((observable, old_value, new_value) ->
            {
            if (!Objects.equals(_suite.getDataTableId(), _data_chooser.getSelectedId()))
                new SetDataIdAction(_suite, _data_chooser.getSelectedId()).execute(getUndoStack());
            });
        }

    @Override
    protected Parent getEditorArea()
        {
        _grid.setPadding(new Insets(5));
        _grid.setVgap(5);
        _grid.setHgap(5);

        Label label = new Label("Test:");
        _grid.add(label, 0, 0);

        label = new Label("Data table: ");
        _grid.add(label, 0, 1);

        return _grid;
        }

    @Override
    public boolean canEdit(MuseResource resource)
        {
        return resource instanceof ParameterListTaskSuite;
        }

    @Override
    public ValidationStateSource getValidationStateSource()
        {
        return null;
        }

    @Override
    public void requestFocus()
        {
        _task_chooser.requestFocus();
        }

    private ParameterListTaskSuite _suite;

    private GridPane _grid = new GridPane();
    private ResourceIdChooser _task_chooser;
    private ResourceIdChooser _data_chooser;

    private ParameterListTaskSuite.ChangeListener _listener = new ParameterListTaskSuite.ChangeListener()
        {
        @Override
        public void taskIdChanged(String old_id, String new_id)
            {
            _task_chooser.selectId(new_id);
            }

        @Override
        public void datatableIdChanged(String old_id, String new_id)
            {
            _data_chooser.selectId(new_id);
            }
        };

    public final static String TEST_FIELD_ID = "pltse-test";
    public final static String DATATABLE_FIELD_ID = "pltse-data";
    }


