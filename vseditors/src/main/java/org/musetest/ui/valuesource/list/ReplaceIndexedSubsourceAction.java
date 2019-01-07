package org.musetest.ui.valuesource.list;

import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ReplaceIndexedSubsourceAction extends UndoableAction
    {
    public ReplaceIndexedSubsourceAction(ValueSourceConfiguration target_source, int index, ValueSourceConfiguration new_source)
        {
        _target_source = target_source;
        _index = index;
        _new_source = new_source;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_source = _target_source.replaceSource(_index, _new_source);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _target_source.replaceSource(_index, _old_source);
        return true;
        }

    private ValueSourceConfiguration _target_source;
    private int _index;
    private ValueSourceConfiguration _new_source;
    private ValueSourceConfiguration _old_source;
    }


