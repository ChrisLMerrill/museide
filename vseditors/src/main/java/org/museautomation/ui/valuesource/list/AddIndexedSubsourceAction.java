package org.museautomation.ui.valuesource.list;

import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class AddIndexedSubsourceAction extends UndoableAction
    {
    public AddIndexedSubsourceAction(ContainsIndexedSources target_source, int index, ValueSourceConfiguration added_source)
        {
        _target_source = target_source;
        _index = index;
        _added_source = added_source;
        }

    @Override
    protected boolean executeImplementation()
        {
        _target_source.addSource(_index, _added_source);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _target_source.removeSource(_index);
        return true;
        }

    private ContainsIndexedSources _target_source;
    private int _index;
    private ValueSourceConfiguration _added_source;
    }


