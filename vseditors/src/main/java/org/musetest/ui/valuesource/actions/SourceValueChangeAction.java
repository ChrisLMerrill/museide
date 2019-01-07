package org.musetest.ui.valuesource.actions;

import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class SourceValueChangeAction extends UndoableAction
    {
    public SourceValueChangeAction(ValueSourceConfiguration source, Object value)
        {
        _source = source;
        _new_value = value;
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
    private Object _new_value;
    private Object _old_value;
    }


