package org.musetest.ui.extend.components.validation;

import javafx.beans.value.*;
import javafx.scene.control.*;
import org.musetest.ui.extend.components.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class TextFieldValidator
    {
    public void attachTo(TextInputControl field)
        {
        _field = field;

        field.textProperty().addListener(this::handleChangeEvent);
        evaluate(_field.getText());
        }

    private void handleChangeEvent(ObservableValue<? extends String> observable, String old_value, String new_value)
        {
        evaluate(new_value);
        }

    private void evaluate(String new_value)
        {
        InputValidation.setValid(_field, isValid(new_value));
        }

    protected abstract boolean isValid(String new_value);

    private TextInputControl _field;
    }


