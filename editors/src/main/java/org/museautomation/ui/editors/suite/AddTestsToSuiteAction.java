package org.museautomation.ui.editors.suite;

import org.museautomation.core.suite.*;
import org.museautomation.ui.extend.actions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class AddTestsToSuiteAction extends UndoableAction
    {
    public AddTestsToSuiteAction(IdListTaskSuite suite, List<String> test_ids)
        {
        _suite = suite;
        _test_ids = test_ids;
        }

    @Override
    protected boolean executeImplementation()
        {
        for (String id : _test_ids)
            _suite.addTaskId(id);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        for (String id : _test_ids)
            _suite.removeTaskId(id);
        return true;
        }

    final private IdListTaskSuite _suite;
    final private List<String> _test_ids;
    }


