package org.musetest.ui.valuesource.list;

import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RemoveIndexedSubsourceAction extends UndoableAction
    {
    public RemoveIndexedSubsourceAction(ContainsIndexedSources target_source, int index)
        {
        _target_source = target_source;
        _index = index;
        }

    @Override
    protected boolean executeImplementation()
        {
        _removed_source = _target_source.removeSource(_index);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _target_source.addSource(_index, _removed_source);
        return true;
        }

    private ContainsIndexedSources _target_source;
    private int _index;
    private ValueSourceConfiguration _removed_source;
    }


