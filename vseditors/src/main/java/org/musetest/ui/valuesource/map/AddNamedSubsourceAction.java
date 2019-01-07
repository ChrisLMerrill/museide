package org.musetest.ui.valuesource.map;

import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class AddNamedSubsourceAction extends UndoableAction
    {
    public AddNamedSubsourceAction(ContainsNamedSources target_source, ValueSourceConfiguration added_source, String name)
        {
        _target_source = target_source;
        _added_source = added_source;
        _name = name;
        }

    @Override
    protected boolean executeImplementation()
        {
        _target_source.addSource(_name, _added_source);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _target_source.removeSource(_name);
        return true;
        }

    private ContainsNamedSources _target_source;
    private ValueSourceConfiguration _added_source;
    private String _name;
    }


