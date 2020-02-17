package org.museautomation.ui.editors.suite;

import org.museautomation.core.suite.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class SetDataIdAction extends UndoableAction
    {
    public SetDataIdAction(ParameterListTaskSuite suite, String new_data_id)
        {
        _suite = suite;
        _new_data_id = new_data_id;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_data_id = _suite.getDataTableId();
        _suite.setDataTableId(_new_data_id);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _suite.setDataTableId(_old_data_id);
        return true;
        }

    private ParameterListTaskSuite _suite;
    private String _new_data_id;
    private String _old_data_id;
    }


