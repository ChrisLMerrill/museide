package org.musetest.ui.extend.components.validation;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class NotBlankTextValidator extends TextFieldValidator
    {
    @Override
    protected boolean isValid(String new_value)
        {
        return new_value.trim().length() > 0;
        }
    }


