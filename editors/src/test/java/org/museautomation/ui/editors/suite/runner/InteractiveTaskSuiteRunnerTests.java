package org.museautomation.ui.editors.suite.runner;

import org.junit.jupiter.api.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.*;
import org.museautomation.core.events.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.suite.*;
import org.museautomation.core.task.*;
import org.museautomation.core.values.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class InteractiveTaskSuiteRunnerTests
    {
    @Test
    void receiveEvents() throws IOException
	    {
        SimpleTaskSuite suite = new SimpleTaskSuite();
        StepConfiguration step = new StepConfiguration(LogMessage.TYPE_ID);
        step.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("test message"));
        MockStepTask task = new MockStepTask(step);
        task.setId("receive-events-task");
        suite.add(task);

        final AtomicReference<MuseTask> started_task = new AtomicReference<>();
        final AtomicReference<MuseTaskSuite> started_suite = new AtomicReference<>();

        final AtomicReference<MuseTask> finished_task = new AtomicReference<>();
        final AtomicReference<MuseTaskSuite> finished_suite = new AtomicReference<>();
        final AtomicReference<TaskResult> test_result = new AtomicReference<>();
        final AtomicReference<EventLog> test_log = new AtomicReference<>();

        final AtomicReference<Boolean> step_started_event = new AtomicReference<>(false);
        final AtomicReference<Boolean> step_finished_event = new AtomicReference<>(false);

        final SimpleProject project = new SimpleProject();
        project.getResourceStorage().addResource(task);
        InteractiveTaskSuiteRunner runner = new ThreadedInteractiveTaskSuiteRunner();
        runner.addListener(new InteractiveTaskSuiteRunner.Listener()
            {
            @Override
            public void testStarted(TaskConfiguration task_config, TaskRunner task_runner)
                {
                _task_config = task_config;
                started_task.set(task_config.task());

                task_runner.getExecutionContext().addEventListener(event ->
                    {
                    if (event.getTypeId().equals(StartStepEventType.TYPE_ID))
                        step_started_event.set(true);
                    if (event.getTypeId().equals(EndStepEventType.TYPE_ID))
                        step_finished_event.set(true);
                    });
                }

            @Override
            public void testCompleted(TaskResult result, int completed, Integer total, EventLog log)
                {
                finished_task.set(_task_config.task());
                test_result.set(result);
                test_log.set(log);
                }

            @Override
            public void testSuiteStarted(MuseTaskSuite suite)
                {
                started_suite.set(suite);
                }

            @Override
            public void testSuiteCompleted(MuseTaskSuite suite)
	            {
                finished_suite.set(suite);
                }

            private TaskConfiguration _task_config;
            });

        // execute the suite
        runner.execute(project, suite, Collections.emptyList());
        runner.waitForCompletion();

        Assertions.assertEquals(suite, started_suite.get());
        Assertions.assertEquals(suite, finished_suite.get());

        Assertions.assertEquals(task, started_task.get());
        Assertions.assertEquals(task, finished_task.get());

        Assertions.assertTrue(step_started_event.get());
        Assertions.assertTrue(step_finished_event.get());
        }
    }