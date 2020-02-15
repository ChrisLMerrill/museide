package org.museautomation.ui.valuesource;

import com.google.common.base.Objects;
import javafx.scene.*;
import javafx.scene.control.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.ui.extend.edit.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class PrimitiveValueEditorField implements ValidationStateSource, PrimitiveValueEditor
    {
    public PrimitiveValueEditorField()
        {
        _text.setId(INPUT_ID);
        _text.textProperty().addListener((observable, old_value, new_value) ->
            {
            checkInput(new_value);
            });

        _text.focusedProperty().addListener((observable, oldValue, newValue) ->
            {
            if (newValue == false)
                {
                checkInput(_text.getText());
                if (_listener != null && isValid() && !Objects.equal(_value, _original_value))
                    {
                    _listener.valueChanged(_value);
                    _original_value = _value;
                    }
                }
            });
        InputValidation.setValid(_text, false);
        }

    private void checkInput(String new_value)
        {
        String value = null;
        if (new_value != null)
            value = new_value.trim();
        if (value == null || value.length() == 0)
            {
            setValid(false);
            return;
            }

        try
            {
            internalSetValue(Long.parseLong(value));
            return;
            }
        catch (Exception e)
            { /* ok, not an int */ }

        try
            {
            if (value.equals("true") || value.equals("false"))
                {
                internalSetValue(Boolean.parseBoolean(new_value));
                return;
                }
            }
        catch (Exception e)
            { /* ok, not a boolean */ }

        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2)
            {
            internalSetValue(value.substring(1, value.length() - 1));
            return;
            }

        setValid(false);
        }

    public Node getNode()
        {
        return _text;
        }

    private void internalSetValue(Object value)
        {
        setValid(true);
        if (Objects.equal(_value, value))
            return;
        _value = value;
        }

    @Override
    public Object getValue()
        {
        return _value;
        }

    @Override
    public void setValue(Object value)
        {
        _value = value;
        _original_value = value;
        boolean is_valid = true;
        if (_value instanceof Long || _value instanceof Boolean)
            _text.setText(_value.toString());
        else if (_value instanceof String)
            _text.setText("\"" + _value.toString() + "\"");
        else if (_value == null)
            {
            _text.setText(null);
            is_valid = false;
            }
        else
            {
            _text.setText("???");
            is_valid = false;
            }
            setValid(is_valid);
        }

    private void setValid(boolean valid)
        {
        if (notifyValidationState(valid))
            InputValidation.setValid(_text, valid);
        }

    @Override
    public boolean isValid()
        {
        return _valid;
        }

    private boolean notifyValidationState(boolean valid)
        {
        if (Objects.equal(_valid, valid))
            return false;

        _valid = valid;
        for (ValidationStateListener listener : _validation_listeners)
            listener.validationStateChanged(this, valid);
        return true;
        }


    @Override
    public void addValidationStateListener(ValidationStateListener listener)
        {
        _validation_listeners.add(listener);
        }

    @Override
    public void removeValidationStateListener(ValidationStateListener listener)
        {
        _validation_listeners.remove(listener);
        }

    @Override
    public void setChangeListener(ChangeListener listener)
        {
        _listener = listener;
        }

    private TextField _text = new TextField();
    private Object _value;
    private Object _original_value;
    private boolean _valid = false;
    private List<ValidationStateListener> _validation_listeners = new ArrayList<>();
    private ChangeListener _listener = null;

    public interface ChangeListener
        {
        void valueChanged(Object new_value);
        }

    public final static String INPUT_ID = "pve-input";
    }


