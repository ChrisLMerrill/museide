package org.museautomation.ui.valuesource.map;

import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RemoveNamedSubsourceAction extends UndoableAction
    {
    public RemoveNamedSubsourceAction(ContainsNamedSources target_source, String name)
        {
        _target_source = target_source;
        _name = name;
        }

    @Override
    protected boolean executeImplementation()
        {
        _removed_source = _target_source.removeSource(_name);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _target_source.addSource(_name, _removed_source);
        return true;
        }

    private ContainsNamedSources _target_source;
    private ValueSourceConfiguration _removed_source;
    private String _name;
    }


