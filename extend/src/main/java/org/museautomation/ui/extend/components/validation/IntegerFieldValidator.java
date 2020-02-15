package org.museautomation.ui.extend.components.validation;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class IntegerFieldValidator extends TextFieldValidator
    {
    @Override
    protected boolean isValid(String new_value)
        {
        try
            {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(new_value);
            return true;
            }
        catch (NumberFormatException e)
            {
            return false;
            }
        }
    }


