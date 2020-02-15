package org.museautomation.ui.editors.suite;

import org.museautomation.core.suite.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class SetTestIdAction extends UndoableAction
    {
    public SetTestIdAction(ParameterListTestSuite suite, String new_test_id)
        {
        _suite = suite;
        _new_test_id = new_test_id;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_test_id = _suite.getTestId();
        _suite.setTestId(_new_test_id);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _suite.setTestId(_old_test_id);
        return true;
        }

    private ParameterListTestSuite _suite;
    private String _new_test_id;
    private String _old_test_id;
    }


