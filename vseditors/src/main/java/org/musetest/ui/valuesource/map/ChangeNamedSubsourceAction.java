package org.musetest.ui.valuesource.map;

import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;

/**
 * Change one of the named sub-sources of a ValueSourceConfiguration.  E.g. as a result of addSource(name, source);
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ChangeNamedSubsourceAction extends UndoableAction
    {
    public ChangeNamedSubsourceAction(ContainsNamedSources target_source, String name, ValueSourceConfiguration new_source)
        {
        _target_source = target_source;
        _name = name;
        _new_source = new_source;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_source = _target_source.replaceSource(_name, _new_source);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _target_source.replaceSource(_name, _old_source);
        return true;
        }

    private ContainsNamedSources _target_source;
    private String _name;
    private ValueSourceConfiguration _new_source;
    private ValueSourceConfiguration _old_source;
    }


