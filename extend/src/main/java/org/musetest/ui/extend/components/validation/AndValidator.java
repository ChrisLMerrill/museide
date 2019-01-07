package org.musetest.ui.extend.components.validation;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class AndValidator extends TextFieldValidator
    {
    @Override
    protected boolean isValid(String new_value)
        {
        for (TextFieldValidator child : _children)
            if (!child.isValid(new_value))
                return false;
        return true;
        }

    public void add(TextFieldValidator child)
        {
        _children.add(child);
        }

    private List<TextFieldValidator> _children = new ArrayList<>();
    }


