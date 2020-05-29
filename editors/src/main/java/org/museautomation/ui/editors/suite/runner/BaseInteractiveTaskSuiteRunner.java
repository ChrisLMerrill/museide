package org.museautomation.ui.editors.suite.runner;

import org.museautomation.core.*;
import org.museautomation.core.events.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.suite.*;
import org.museautomation.core.task.*;

import java.util.*;

/**
 * Provides basic support for implementers of InteractiveTestSuiteRunner.
 * Provides event listener functions.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class BaseInteractiveTaskSuiteRunner extends SimpleTaskSuiteRunner implements InteractiveTaskSuiteRunner
    {
    public void addListener(Listener listener)
        {
        _listeners.add(listener);
        }

    public void removeListener(Listener listener)
        {
        _listeners.remove(listener);
        }

    @Override
    public boolean isComplete()
        {
        return _complete;
        }

    protected Listener _listener = new Listener()
        {
        @Override
        public void taskSuiteStarted(MuseTaskSuite suite)
            {
            for (Listener listener : _listeners)
                listener.taskSuiteStarted(suite);
            }

        @Override
        public void taskSuiteCompleted(MuseTaskSuite suite)
            {
            for (Listener listener : _listeners)
                listener.taskSuiteCompleted(suite);
            _complete = true;
            }

        @Override
        public void taskStarted(TaskConfiguration test_config, TaskRunner test_runner)
            {
            for (Listener listener : _listeners)
                listener.taskStarted(test_config, test_runner);
            }

        @Override
        public void taskCompleted(TaskResult result, int completed, Integer total, EventLog log)
	        {
            for (Listener listener : _listeners)
                listener.taskCompleted(result, completed, total, log);
            }
        };

    private Set<Listener> _listeners = new HashSet<>();
    private boolean _complete = false;
    }
