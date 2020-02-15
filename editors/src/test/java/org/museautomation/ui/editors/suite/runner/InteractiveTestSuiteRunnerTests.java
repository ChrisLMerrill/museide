package org.museautomation.ui.editors.suite.runner;

import org.junit.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.*;
import org.museautomation.core.events.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.suite.*;
import org.museautomation.core.test.*;
import org.museautomation.core.values.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class InteractiveTestSuiteRunnerTests
    {
    @Test
    public void receiveEvents() throws IOException
	    {
        SimpleTestSuite suite = new SimpleTestSuite();
        StepConfiguration step = new StepConfiguration(LogMessage.TYPE_ID);
        step.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("test message"));
        MockStepTest test = new MockStepTest(step);
        test.setId("receive-events-test");
        suite.add(test);

        final AtomicReference<MuseTest> started_test = new AtomicReference<>();
        final AtomicReference<MuseTestSuite> started_suite = new AtomicReference<>();

        final AtomicReference<MuseTest> finished_test = new AtomicReference<>();
        final AtomicReference<MuseTestSuite> finished_suite = new AtomicReference<>();
        final AtomicReference<TestResult> test_result = new AtomicReference<>();
        final AtomicReference<EventLog> test_log = new AtomicReference<>();

        final AtomicReference<Boolean> step_started_event = new AtomicReference<>(false);
        final AtomicReference<Boolean> step_finished_event = new AtomicReference<>(false);

        final SimpleProject project = new SimpleProject();
        project.getResourceStorage().addResource(test);
        InteractiveTestSuiteRunner runner = new ThreadedInteractiveTestSuiteRunner();
        runner.addListener(new InteractiveTestSuiteRunner.Listener()
            {
            @Override
            public void testStarted(TestConfiguration test_config, TestRunner test_runner)
                {
                _test_config = test_config;
                started_test.set(test_config.test());

                test_runner.getExecutionContext().addEventListener(event ->
                    {
                    if (event.getTypeId().equals(StartStepEventType.TYPE_ID))
                        step_started_event.set(true);
                    if (event.getTypeId().equals(EndStepEventType.TYPE_ID))
                        step_finished_event.set(true);
                    });
                }

            @Override
            public void testCompleted(TestResult result, int completed, Integer total, EventLog log)
                {
                finished_test.set(_test_config.test());
                test_result.set(result);
                test_log.set(log);
                }

            @Override
            public void testSuiteStarted(MuseTestSuite suite)
                {
                started_suite.set(suite);
                }

            @Override
            public void testSuiteCompleted(MuseTestSuite suite)
	            {
                finished_suite.set(suite);
                }

            private TestConfiguration _test_config;
            });

        // execute the suite
        runner.execute(project, suite, Collections.emptyList());
        runner.waitForCompletion();

        Assert.assertEquals(suite, started_suite.get());
        Assert.assertEquals(suite, finished_suite.get());

        Assert.assertEquals(test, started_test.get());
        Assert.assertEquals(test, finished_test.get());

        Assert.assertTrue(step_started_event.get());
        Assert.assertTrue(step_finished_event.get());
        }
    }
