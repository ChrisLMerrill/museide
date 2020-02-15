package org.museautomation.ui.editors.suite.runner;

import org.jetbrains.annotations.*;
import org.museautomation.core.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.plugins.*;
import org.museautomation.core.test.*;
import org.slf4j.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ThreadedInteractiveTestSuiteRunner extends BaseInteractiveTestSuiteRunner implements Runnable
    {
    @Override
    public boolean execute(MuseProject project, MuseTestSuite suite, List<MusePlugin> plugins)
	    {
	    _suite = suite;
        _project = project;
	    _manual_plugins = plugins;
        start();
        return true;
        }

    @Override
    public void setOutputPath(String path)
	    {
	    // no-op at this point.
	    }

    public void start()
        {
        if (_thread == null)
            _thread = new Thread(this);
        _thread.start();
        }

    public void stop()
        {
        _stop_requested = true;
        synchronized (_runner)
            {
            if (_runner != null)
                _runner.interrupt();
            }
        }

    @Override
    public void run()
        {
        _total = _suite.getTotalTestCount(_project);
        _number_completed = 0;

        _listener.testSuiteStarted(_suite);
        super.execute(_project, _suite, _manual_plugins);
        _listener.testSuiteCompleted(_suite);

        synchronized (this)
            {
            notifyAll();
            }
        }

    @Override
    protected boolean runTest(TestConfiguration configuration)
	    {
	    configuration.withinContext(_context);
	    boolean completed = super.runTest(configuration);
        _number_completed++;

        TestResult result = TestResult.find(configuration.context());
	    _listener.testCompleted(result, _number_completed, _total, configuration.context().getEventLog());

	    return completed;
	    }

    @NotNull
    @Override
    protected SimpleTestRunner createRunner(TestConfiguration configuration)
	    {
	    _runner = new BlockingThreadedTestRunner(_context, configuration);
	    _listener.testStarted(configuration, _runner);
	    return _runner;
	    }

    public void waitForCompletion()
        {
        while (!isComplete())
            {
            synchronized (this)
                {
                try
                    {
                    wait();
                    }
                catch (InterruptedException e)
                    {
                    // ok...
                    e.printStackTrace(System.err);
                    }
                }
            }
        }

    private MuseTestSuite _suite;
    private List<MusePlugin> _manual_plugins;
    private Thread _thread;
    private ThreadedTestRunner _runner;

    private boolean _stop_requested;
    private Integer _total;
    private int _number_completed;

    private final static Logger LOG = LoggerFactory.getLogger(ThreadedInteractiveTestSuiteRunner.class);
    }
