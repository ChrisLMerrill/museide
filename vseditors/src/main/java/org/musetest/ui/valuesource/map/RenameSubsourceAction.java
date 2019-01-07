package org.musetest.ui.valuesource.map;

import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;

/**
 * Change one of the named sub-sources of a ValueSourceConfiguration.  E.g. as a result of addSource(name, source);
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class RenameSubsourceAction extends UndoableAction
    {
    RenameSubsourceAction(ContainsNamedSources target_source, String old_name, String new_name)
        {
        _target_source = target_source;
        _old_name = old_name;
        _new_name = new_name;
        }

    @Override
    protected boolean executeImplementation()
        {
        if (_target_source.getSource(_new_name) != null)
            return false;

        _target_source.renameSource(_old_name, _new_name);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _target_source.renameSource(_new_name, _old_name);
        return true;
        }

    private ContainsNamedSources _target_source;
    private String _new_name;
    private String _old_name;
    }


