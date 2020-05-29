package org.museautomation.ui.editors.suite.runner;

import org.jetbrains.annotations.*;
import org.museautomation.core.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.plugins.*;
import org.museautomation.core.task.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ThreadedInteractiveTaskSuiteRunner extends BaseInteractiveTaskSuiteRunner implements Runnable
    {
    @Override
    public boolean execute(MuseProject project, MuseTaskSuite suite, List<MusePlugin> plugins)
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
        synchronized (_runner)
            {
            if (_runner != null)
                _runner.interrupt();
            }
        }

    @Override
    public void run()
        {
        _total = _suite.getTotalTaskCount(_project);
        _number_completed = 0;

        _listener.taskSuiteStarted(_suite);
        super.execute(_project, _suite, _manual_plugins);
        _listener.taskSuiteCompleted(_suite);

        synchronized (this)
            {
            notifyAll();
            }
        }

    @Override
    protected boolean runTask(TaskConfiguration configuration)
	    {
	    configuration.withinContext(_context);
	    boolean completed = super.runTask(configuration);
        _number_completed++;

        TaskResult result = TaskResult.find(configuration.context());
	    _listener.taskCompleted(result, _number_completed, _total, configuration.context().getEventLog());

	    return completed;
	    }

    @NotNull
    @Override
    protected SimpleTaskRunner createRunner(TaskConfiguration configuration)
	    {
	    _runner = new BlockingThreadedTaskRunner(_context, configuration);
	    _listener.taskStarted(configuration, _runner);
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

    private MuseTaskSuite _suite;
    private List<MusePlugin> _manual_plugins;
    private Thread _thread;
    private ThreadedTaskRunner _runner;

    private Integer _total;
    private int _number_completed;
    }
