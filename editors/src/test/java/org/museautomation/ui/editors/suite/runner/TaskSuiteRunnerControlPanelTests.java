package org.museautomation.ui.editors.suite.runner;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.builtins.plugins.suite.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.*;
import org.museautomation.core.context.*;
import org.museautomation.core.events.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.suite.*;
import org.museautomation.core.task.*;
import org.museautomation.core.values.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TaskSuiteRunnerControlPanelTests extends ComponentTest
    {
    @Test
    void startSuite()
        {
        clickOn(id(TaskSuiteRunnerControlPanel.RUN_BUTTON_ID));
        waitForUiEvents();
        Assertions.assertTrue(_runner.isStartRequested());
        }

    @Test
    void displaySuccessfulTaskResults()
	    {
	    MockStepTask task = new MockStepTask();
	    task.setId("MockTask");

	    // test completes successfully
	    final BasicTaskConfiguration config = new BasicTaskConfiguration(task);
	    config.addPlugin(new TaskSuiteResultCounterConfiguration().createPlugin());
	    _runner.startTest(config);
	    _runner.finishTest(TaskResult.create(task.getId(), task.getDescription(), "success"), 1, 3, null);
	    waitForUiEvents();
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.COMPLETE_LABEL_ID)).contains("1"));
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.COMPLETE_LABEL_ID)).contains("3"));
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.FAILED_LABEL_ID)).contains("0"));
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.ERROR_LABEL_ID)).contains("0"));
	    }

    @Test
    void displayFailTaskResults()
	    {
	    MockStepTask task = new MockStepTask();
	    task.setId("MockTask");

	    // test fails
	    final BasicTaskConfiguration config = new BasicTaskConfiguration(task);
	    config.addPlugin(new TaskSuiteResultCounterConfiguration().createPlugin());
	    _runner.startTest(config);
	    _runner.finishTest(TaskResult.create(task.getId(), task.getDescription(), "failed", TaskResult.FailureType.Failure, "failed"), 2, 3, null);
	    waitForUiEvents();
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.COMPLETE_LABEL_ID)).contains("2"));
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.COMPLETE_LABEL_ID)).contains("3"));
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.FAILED_LABEL_ID)).contains("1"));
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.ERROR_LABEL_ID)).contains("0"));
	    }

    @Test
    void displayErrorTaskResults()
	    {
	    MockStepTask task = new MockStepTask();
	    task.setId("MockTask");

	    // test encounters error
	    final BasicTaskConfiguration config = new BasicTaskConfiguration(task);
	    config.addPlugin(new TaskSuiteResultCounterConfiguration().createPlugin());
	    _runner.startTest(config);
	    _runner.finishTest(TaskResult.create(task.getId(), task.getDescription(), "error", TaskResult.FailureType.Error, "error"), 3, 3, null);
	    waitForUiEvents();
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.COMPLETE_LABEL_ID)).contains("3"));
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.FAILED_LABEL_ID)).contains("0"));
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.ERROR_LABEL_ID)).contains("1"));
	    }

    @Test
    void displayTaskResultsInProgress()
	    {
	    MockStepTask task = new MockStepTask();
	    task.setId("MockTest");
	    StepConfiguration message_step = new StepConfiguration(LogMessage.TYPE_ID);
	    message_step.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("message"));
	    message_step.setStepId(123L);
	    task.setStep(message_step);

	    // test and step started
	    TaskRunner test_runner = _runner.startTest(new BasicTaskConfiguration(task));
	    test_runner.getExecutionContext().raiseEvent(StartStepEventType.create(message_step, new SingleStepExecutionContext(new DefaultSteppedTaskExecutionContext(new SimpleProject(), task), message_step, true)));
	    waitForUiEvents();

	    Assertions.assertEquals(task.getDescription(), textOf(id(TaskSuiteRunnerControlPanel.TASK_LABEL_ID)));
	    Assertions.assertTrue(textOf(id(TaskSuiteRunnerControlPanel.STEP_LABEL_ID)).toLowerCase().contains("log"));
	    }


    @Override
    public Node createComponentNode()
        {
        SimpleTaskSuite suite = new SimpleTaskSuite();
        MockStepTask task = new MockStepTask();
        suite.add(task);
        _runner = new MockInteractiveTaskSuiteRunner();

        TaskSuiteRunnerControlPanel control_panel = new TaskSuiteRunnerControlPanel(suite, new SimpleProject());
        control_panel.injectRunner(_runner);
        return control_panel.getNode();
        }

    private MockInteractiveTaskSuiteRunner _runner;
    }