package org.musetest.ui.valuesource;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.glyphs.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class PrimitiveValueOptionalField implements PrimitiveValueEditor
    {
    public PrimitiveValueOptionalField(boolean optional)
        {
        _optional = optional;
        _grid.setHgap(5);

        _add_button = new Button("Add", Glyphs.create("FA:PLUS"));
        _add_button.setId(ADD_BUTTON_ID);
        _add_button.setOnAction(event ->
            {
            _show_field = true;
            if (_change_listener != null)
                _change_listener.valueChanged(null);
            layoutControls();
            });

        _value_field = new PrimitiveValueEditorField();
        GridPane.setHgrow(_value_field.getNode(), Priority.ALWAYS);

        if (_optional)
            {
            _delete_button = Buttons.createRemove();
            _delete_button.setId(DELETE_BUTTON_ID);
            _delete_button.setOnAction(event ->
                {
                _show_field = false;
                _value_field.setValue(null);
                if (_change_listener != null)
                    _change_listener.valueChanged(null);
                layoutControls();
                });
            }

        _show_field = !optional;
        layoutControls();
        }

    private void layoutControls()
        {
        Platform.runLater(() ->
            {
            _grid.getChildren().clear();
            if (_show_field)
                {
                _grid.add(_value_field.getNode(), 0, 0);
                if (_delete_button != null)
                    _grid.add(_delete_button, 1, 0);
                }
            else
                _grid.add(_add_button, 0, 0);
            });
        }

    public void setOptional(boolean optional)
        {
        _optional = optional;
        _show_field = (_value_field.getValue() != null || !_optional);
        layoutControls();
        }

    public Node getNode()
        {
        return _grid;
        }

    @Override
    public Object getValue()
        {
        return _value_field.getValue();
        }

    @Override
    public void setValue(Object value)
        {
        _value_field.setValue(value);
        _show_field = (value != null || !_optional);
        Platform.runLater(this::layoutControls);
        }

    @Override
    public boolean isValid()
        {
        return (_optional && !_show_field) || _value_field.isValid();
        }

    @Override
    public void addValidationStateListener(ValidationStateListener listener)
        {
        _value_field.addValidationStateListener(listener);
        }

    @Override
    public void removeValidationStateListener(ValidationStateListener listener)
        {
        _value_field.removeValidationStateListener(listener);
        }

    @Override
    public void setChangeListener(PrimitiveValueEditorField.ChangeListener listener)
        {
        _change_listener = listener;
        _value_field.setChangeListener(listener);
        }

    private boolean _optional;

    private GridPane _grid = new GridPane();
    private final Button _add_button;
    private Button _delete_button = null;
    private final PrimitiveValueEditorField _value_field;
    private PrimitiveValueEditorField.ChangeListener _change_listener;
    private boolean _show_field = false;

    public final static String ADD_BUTTON_ID = "pvsof-add-button";
    public final static String DELETE_BUTTON_ID = "pvsof-delete-button";
    }


