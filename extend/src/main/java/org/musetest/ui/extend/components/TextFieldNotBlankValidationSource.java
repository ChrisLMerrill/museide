package org.musetest.ui.extend.components;

import javafx.beans.value.*;
import javafx.scene.control.*;
import org.musetest.ui.extend.edit.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TextFieldNotBlankValidationSource implements ValidationStateSource
    {
    public TextFieldNotBlankValidationSource(TextField text)
        {
        _text = text;
        _text.textProperty().addListener(new ChangeListener<String>()
            {
            @Override
            public void changed(ObservableValue<? extends String> observable, String old_value, String new_value)
                {
                boolean was_valid = old_value.trim().length() > 0;
                boolean is_valid = new_value.trim().length() > 0;
                if (!Objects.equals(was_valid, is_valid))
                    for (ValidationStateListener listener : _listeners)
                        listener.validationStateChanged(TextFieldNotBlankValidationSource.this, is_valid);
                }
            });
        }

    @Override
    public boolean isValid()
        {
        return _text.getText().trim().length() > 0;
        }

    @Override
    public void addValidationStateListener(ValidationStateListener listener)
        {
        _listeners.add(listener);
        }

    @Override
    public void removeValidationStateListener(ValidationStateListener listener)
        {
        _listeners.remove(listener);
        }

    private TextField _text;
    private List<ValidationStateListener> _listeners = new ArrayList<>();
    }


