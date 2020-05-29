package org.museautomation.ui.editors.suite.runner;

import org.museautomation.core.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.task.*;
import org.museautomation.core.events.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface InteractiveTaskSuiteRunner extends MuseTaskSuiteRunner
    {
    void addListener(Listener listener);
    void removeListener(Listener listener);
    void start();
    void stop();
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isComplete();
    void waitForCompletion();

    interface Listener
        {
        void taskSuiteStarted(MuseTaskSuite suite);
        void taskSuiteCompleted(MuseTaskSuite suite);

        void taskStarted(TaskConfiguration test_config, TaskRunner test_runner);
        void taskCompleted(TaskResult result, int completed, Integer total, EventLog log);
        }
    }
