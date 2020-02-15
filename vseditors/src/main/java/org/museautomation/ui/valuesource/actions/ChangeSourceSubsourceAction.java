package org.museautomation.ui.valuesource.actions;

import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ChangeSourceSubsourceAction extends UndoableAction
    {
    public ChangeSourceSubsourceAction(ValueSourceConfiguration source, ValueSourceConfiguration new_subsource)
        {
        _source = source;
        _new_subsource = new_subsource;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_subsource = _source.getSource();
        _source.setSource(_new_subsource);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _source.setSource(_old_subsource);
        return true;
        }

    private ValueSourceConfiguration _source;
    private ValueSourceConfiguration _new_subsource;
    private ValueSourceConfiguration _old_subsource;
    }


