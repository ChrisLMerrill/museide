package org.museautomation.ui.valuesource.actions;

import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class SourceTypeChangeAction extends UndoableAction
    {
    public SourceTypeChangeAction(ValueSourceConfiguration source, String type)
        {
        _source = source;
        _new_type = type;
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


