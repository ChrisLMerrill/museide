package org.musetest.ui.valuesource;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.*;
import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.components.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.valuesource.map.*;

import java.util.*;

/**
 * A 1-line editor for a named value source
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class InlineNamedVSE extends BaseValueSourceEditor implements NamedValueSourceEditor
    {
    public InlineNamedVSE(MuseProject project, UndoStack undo)
        {
        super(project, undo);

        // create the name editor
        _name_editor = new TextField();
        _name_editor.setId("name");
        _name_editor.getStyleClass().add(NAME_CLASS);
        GridPane.setHgrow(_name_editor, Priority.SOMETIMES);
        _grid.add(_name_editor, 0, 0);
        _valid_state.addSubSource(new TextFieldNotBlankValidationSource(_name_editor));
        _name_editor.focusedProperty().addListener((observable, oldValue, newValue) ->
            {
            if (!Objects.equals(_name, _name_editor.getText().trim()))
                {
                // if not valid, don't send the change event
                if (InputValidation.isShowingError(_name_editor))
                    return;

                String old_name = _name;
                _name = _name_editor.getText().trim();
                for (NameChangeListener listener : _listeners)
                    listener.nameChanged(InlineNamedVSE.this, old_name, _name);
                }
            });
        _name_editor.textProperty().addListener((observable, old_value, new_value) ->
            {
            boolean valid = new_value.trim().length() > 0;
            if (valid && _name_validator != null)
                valid = _name_validator.isValid(new_value) || new_value.equals(_name);
            InputValidation.setValid(_name_editor, valid && GeneralIdentifierTextFieldValidator.isValid(new_value));
            });


        Label separator = new Label(" = ");
        _grid.add(separator, 1, 0);

        // create the value source editor
        _source_editor = new DefaultInlineVSE(project, undo);
        _source_editor.getNode().setId("value");
        _source_editor.getNode().getStyleClass().add(VALUE_CLASS);
        GridPane.setHgrow(_source_editor.getNode(), Priority.ALWAYS);
        _grid.add(_source_editor.getNode(), 2, 0);
        _valid_state.addSubSource(_source_editor);

        _valid_state.addValidationStateListener((source, valid) ->
            changeValid(valid));
        }

    @Override
    public void setSource(ValueSourceConfiguration source)
        {
        super.setSource(source);
        _source_editor.setSource(source);
        _source_editor.getNode().setId(getValueFieldId(_name));
        }

    public void setName(String name)
        {
        _name = name;
        _name_editor.setText(name);
        _name_editor.setId(getNameFieldId(name));
        }

    public String getName()
        {
        return _name;
        }

    @Override
    public Node getNode()
        {
        return _grid;
        }

    @Override
    public void requestFocus()
        {
        _name_editor.requestFocus();
        _name_editor.selectAll();
        }

    @Override
    public void addNameChangeListener(NameChangeListener listener)
        {
        _listeners.add(listener);
        }

    @Override
    public void removeNameChangeListener(NameChangeListener listener)
        {
        _listeners.remove(listener);
        }

    @Override
    public void setNameValidator(SubsourceNameValidator validator)
        {
        _name_validator = validator;
        }

    public Node getNameNode()
	    {
	    return _name_editor;
	    }

    public Node getValueNode()
	    {
	    return _source_editor.getNode();
	    }

    @Override
    public boolean isValid()
        {
        if (InputValidation.isShowingError(_name_editor))
            return false;
        return _valid_state.isValid();
        }

    /**
     * For unit tests.
     */
    public void setFieldId(String id)
        {
        _source_editor.setFieldId(id);
        }

    private GridPane _grid = new GridPane();
    private String _name;
    private TextField _name_editor;
    private DefaultInlineVSE _source_editor;
    private ValidationStateAggregator _valid_state = new ValidationStateAggregator();
    private List<NameChangeListener> _listeners = new ArrayList<>();
    private SubsourceNameValidator _name_validator;

    // for testability
    public static String getNameFieldId(String name)
        {
        return "name[" + name + "]";
        }
    public static String getValueFieldId(String name)
        {
        return "value[" + name + "]";
        }

    @SuppressWarnings("WeakerAccess")  // can be used for testing
    public static String NAME_CLASS = "omuev-invse-name";
    @SuppressWarnings("WeakerAccess")  // can be used for testing
    public static String VALUE_CLASS = "omuev-invse-value";
    }