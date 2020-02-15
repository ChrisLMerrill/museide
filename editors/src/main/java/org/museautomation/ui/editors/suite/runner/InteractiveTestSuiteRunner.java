package org.museautomation.ui.editors.suite.runner;

import org.museautomation.core.*;
import org.museautomation.core.events.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.test.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface InteractiveTestSuiteRunner extends MuseTestSuiteRunner
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
        void testSuiteStarted(MuseTestSuite suite);
        void testSuiteCompleted(MuseTestSuite suite);

        void testStarted(TestConfiguration test_config, TestRunner test_runner);
        void testCompleted(TestResult result, int completed, Integer total, EventLog log);
        }
    }
