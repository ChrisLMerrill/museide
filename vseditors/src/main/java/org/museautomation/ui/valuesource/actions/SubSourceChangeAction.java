package org.museautomation.ui.valuesource.actions;

import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class SubSourceChangeAction extends UndoableAction
    {
    public SubSourceChangeAction(ValueSourceConfiguration parent_source, ValueSourceConfiguration new_subsource)
        {
        _parent_source = parent_source;
        _new_subsource = new_subsource;
        }

    @Override
    protected boolean undoImplementation()
        {
        _parent_source.setSource(_old_subsource);
        return true;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_subsource = _parent_source.getSource();
        _parent_source.setSource(_new_subsource);
        return true;
        }

    private final ValueSourceConfiguration _parent_source;
    private final ValueSourceConfiguration _new_subsource;
    private ValueSourceConfiguration _old_subsource;
    }


