package org.musetest.ui.editors.suite.runner;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.builtins.step.*;
import org.musetest.core.*;
import org.musetest.core.context.*;
import org.musetest.core.events.*;
import org.musetest.core.execution.*;
import org.musetest.core.project.*;
import org.musetest.core.step.*;
import org.musetest.core.suite.*;
import org.musetest.core.test.*;
import org.musetest.core.test.plugins.*;
import org.musetest.core.values.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TestSuiteRunnerControlPanelTests extends ComponentTest
    {
    @Test
    public void startSuite()
        {
        clickOn(id(TestSuiteRunnerControlPanel.RUN_BUTTON_ID));
        waitForUiEvents();
        Assert.assertTrue(_runner.isStartRequested());
        }

    @Test
    public void displaySuccessfulTestResults()
	    {
	    MockStepTest test = new MockStepTest();
	    test.setId("MockTest");

	    // test completes successfully
	    final BasicTestConfiguration config = new BasicTestConfiguration(test);
	    config.addPlugin(new TestResultCollectorConfiguration().createPlugin());
	    _runner.startTest(config);
	    _runner.finishTest(TestResult.create(test.getId(), test.getDescription(), "success"), 1, 3, null);
	    waitForUiEvents();
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.COMPLETE_LABEL_ID)).contains("1"));
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.COMPLETE_LABEL_ID)).contains("3"));
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.FAILED_LABEL_ID)).contains("0"));
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.ERROR_LABEL_ID)).contains("0"));
	    }

    @Test
    public void displayFailTestResults()
	    {
	    MockStepTest test = new MockStepTest();
	    test.setId("MockTest");

	    // test fails
	    final BasicTestConfiguration config = new BasicTestConfiguration(test);
	    config.addPlugin(new TestResultCollectorConfiguration().createPlugin());
	    _runner.startTest(config);
	    _runner.finishTest(TestResult.create(test.getId(), test.getDescription(), "failed", TestResult.FailureType.Failure, "failed"), 2, 3, null);
	    waitForUiEvents();
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.COMPLETE_LABEL_ID)).contains("2"));
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.COMPLETE_LABEL_ID)).contains("3"));
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.FAILED_LABEL_ID)).contains("1"));
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.ERROR_LABEL_ID)).contains("0"));
	    }

    @Test
    public void displayErrorTestResults()
	    {
	    MockStepTest test = new MockStepTest();
	    test.setId("MockTest");

	    // test encounters error
	    final BasicTestConfiguration config = new BasicTestConfiguration(test);
	    config.addPlugin(new TestResultCollectorConfiguration().createPlugin());
	    _runner.startTest(config);
	    _runner.finishTest(TestResult.create(test.getId(), test.getDescription(), "error", TestResult.FailureType.Error, "error"), 3, 3, null);
	    waitForUiEvents();
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.COMPLETE_LABEL_ID)).contains("3"));
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.FAILED_LABEL_ID)).contains("0"));
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.ERROR_LABEL_ID)).contains("1"));
	    }

    @Test
    public void displayTestResultsInProgress()
	    {
	    MockStepTest test = new MockStepTest();
	    test.setId("MockTest");
	    StepConfiguration message_step = new StepConfiguration(LogMessage.TYPE_ID);
	    message_step.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("message"));
	    message_step.setStepId(123L);
	    test.setStep(message_step);

	    // test and step started
	    TestRunner test_runner = _runner.startTest(new BasicTestConfiguration(test));
	    test_runner.getExecutionContext().raiseEvent(StartStepEventType.create(message_step, new SingleStepExecutionContext(new DefaultSteppedTestExecutionContext(new SimpleProject(), test), message_step, true)));
	    waitForUiEvents();

	    Assert.assertEquals(test.getDescription(), textOf(id(TestSuiteRunnerControlPanel.TEST_LABEL_ID)));
	    Assert.assertTrue(textOf(id(TestSuiteRunnerControlPanel.STEP_LABEL_ID)).toLowerCase().contains("log"));
	    }


    @Override
    protected Node createComponentNode()
        {
        SimpleTestSuite suite = new SimpleTestSuite();
        MockStepTest test = new MockStepTest();
        suite.add(test);
        _runner = new MockInteractiveTestSuiteRunner();

        TestSuiteRunnerControlPanel control_panel = new TestSuiteRunnerControlPanel(suite, new SimpleProject());
        control_panel.injectRunner(_runner);
        return control_panel.getNode();
        }

    private MockInteractiveTestSuiteRunner _runner;
    }