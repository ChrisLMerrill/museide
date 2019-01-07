package org.musetest.ui.editors.variables;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.*;
import org.musetest.core.variables.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.valuesource.map.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused")  // instantiated via reflection
public class VariableListEditor extends BaseResourceEditor
    {
    @Override
    public boolean canEdit(MuseResource resource)
        {
        return resource instanceof VariableList;
        }

    @Override
    public void editResource(MuseProject project, MuseResource resource)
        {
        super.editResource(project, resource);
        _list = (VariableList) resource;
        createFields();
        fillFields();
        }

    @Override
    public void requestFocus()
        {
        if (_editor != null)
            _editor.requestFocus();
        }

    @Override
    public Parent getEditorArea()
        {
        return _grid;
        }

    private void createFields()
        {
        if (!_created)
            Platform.runLater(() ->
                {
                _grid.setPadding(new Insets(5));
                _grid.setPadding(new Insets(5));
                _grid.setVgap(5);
                _grid.setPrefWidth(400);
                _grid.setPrefHeight(200);

                _title = new Label();
                _grid.add(_title, 0, 0);

                _editor = new ValueSourceMapEditor(getProject(), getUndoStack());
                _grid.add(_editor.getNode(), 0, 1);
                GridPane.setHgrow(_editor.getNode(), Priority.ALWAYS);
                });
        _created = true;
        }

    private void fillFields()
        {
        Platform.runLater(() ->
            {
            _editor.setSource(_list.namedElementLocators());
            _title.setText(String.format("Variable List %s:", getResource().getId()));
            });
        }

    @Override
    public ValidationStateSource getValidationStateSource()
        {
        return null;
//        return _editor;
        }

    private VariableList _list;
    private ValueSourceMapEditor _editor;
    private GridPane _grid = new GridPane();
    private Label _title;
    private boolean _created = false;
    }


