package org.museautomation.ui.valuesource.actions;

import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ChangeSourceValueAction extends UndoableAction
    {
    public ChangeSourceValueAction(ValueSourceConfiguration source, Object new_value)
        {
        _source = source;
        _new_value = new_value;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_value = _source.getValue();
        _source.setValue(_new_value);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _source.setValue(_old_value);
        return true;
        }

    private ValueSourceConfiguration _source;
    private Object _old_value;
    private Object _new_value;
    }


