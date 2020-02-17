package org.museautomation.ui.editors.suite;

import org.museautomation.core.suite.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class SetTaskIdAction extends UndoableAction
    {
    public SetTaskIdAction(ParameterListTaskSuite suite, String new_test_id)
        {
        _suite = suite;
        _new_test_id = new_test_id;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_test_id = _suite.getTaskId();
        _suite.setTaskId(_new_test_id);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _suite.setTaskId(_old_test_id);
        return true;
        }

    private ParameterListTaskSuite _suite;
    private String _new_test_id;
    private String _old_test_id;
    }


