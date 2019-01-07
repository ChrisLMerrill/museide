package org.musetest.ui.editors.suite;

import org.musetest.core.suite.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class SetDataIdAction extends UndoableAction
    {
    public SetDataIdAction(ParameterListTestSuite suite, String new_data_id)
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

    private ParameterListTestSuite _suite;
    private String _new_data_id;
    private String _old_data_id;
    }


