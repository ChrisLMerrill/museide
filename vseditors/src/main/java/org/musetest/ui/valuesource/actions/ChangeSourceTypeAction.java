package org.musetest.ui.valuesource.actions;

import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ChangeSourceTypeAction extends UndoableAction
    {
    public ChangeSourceTypeAction(ValueSourceConfiguration source, String new_type)
        {
        _source = source;
        _new_type = new_type;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_type = _source.getType();
        _source.setType(_new_type);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _source.setType(_old_type);
        return true;
        }

    private ValueSourceConfiguration _source;
    private String _new_type;
    private String _old_type;
    }


