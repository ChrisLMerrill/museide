package org.musetest.ui.editors.suite.runner;

import org.musetest.core.*;
import org.musetest.core.context.*;
import org.musetest.core.events.*;
import org.musetest.core.execution.*;
import org.musetest.core.plugins.*;
import org.musetest.core.project.*;
import org.musetest.core.test.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class MockInteractiveTestSuiteRunner extends BaseInteractiveTestSuiteRunner
    {
    @Override
    public void start()
        {
        _is_start_requested = true;
        }

    boolean isStartRequested()
        {
        return _is_start_requested;
        }

    @Override
    public boolean execute(MuseProject project, MuseTestSuite suite, List<MusePlugin> plugins)
	    {
	    _is_start_requested = true;
        return true;
        }

    @Override
    public void setOutputPath(String path) { }

    @SuppressWarnings("SameParameterValue")
    void finishTest(TestResult result, int completed, int total, EventLog log)
        {
        _listener.testCompleted(result, completed, total, log);
        }

    TestRunner startTest(TestConfiguration config)
        {
        SimpleTestRunner test_runner = new SimpleTestRunner(new DefaultTestExecutionContext(_project, config.test()));
        _listener.testStarted(config, test_runner);
        return test_runner;
        }

    @Override
    public void stop()
        {
        }

    public void waitForCompletion()
        {
        while (!isComplete())
            try { Thread.sleep(1); } catch (InterruptedException e) { /* don't care */ }
        }

    private boolean _is_start_requested = false;
    private SimpleProject _project = new SimpleProject();
    }


