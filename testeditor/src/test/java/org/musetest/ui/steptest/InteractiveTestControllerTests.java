package org.musetest.ui.steptest;

import org.junit.*;
import org.musetest.builtins.step.*;
import org.musetest.core.events.*;
import org.musetest.core.events.matching.*;
import org.musetest.core.execution.*;
import org.musetest.core.project.*;
import org.musetest.core.step.*;
import org.musetest.core.steptest.*;
import org.musetest.core.values.*;
import org.musetest.ui.extend.edit.step.*;
import org.musetest.ui.steptest.execution.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class InteractiveTestControllerTests
    {
    /**
     * The InteractiveTestController should pause in this case, rather than stopping.
     */
    @Test
    public void testPausedOnFatalVerifyFailure()
        {
        StepConfiguration step = new StepConfiguration(Verify.TYPE_ID);
        step.addSource(Verify.CONDITION_PARAM, ValueSourceConfiguration.forValue(false)); // will cause a failure
        step.addSource(Verify.TERMINATE_PARAM, ValueSourceConfiguration.forValue(true)); // should cause test to pause
        SteppedTest test = setupLogTest(step);

        SimpleProject project = new SimpleProject();
        InteractiveTestController controller = new InteractiveTestControllerImpl();
        controller.run(new SteppedTestProviderImpl(project, test));
        TestStateBlocker blocker = new TestStateBlocker(controller);
        blocker.blockUntil(InteractiveTestState.PAUSED);

        Assert.assertEquals(InteractiveTestState.PAUSED, controller.getState());
        Assert.assertNull(controller.getResult());  // test not complete

        // should pause after verify step
        Assert.assertTrue(controller.getTestRunner().getExecutionContext().getEventLog().findEvents(new EventTypeMatcher(StartStepEventType.TYPE_ID)).size() == 2);
        }

    /**
     * The InteractiveTestController should pause in this case, rather than stopping.
     */
    @Test
    public void testStoppedAfterError()
        {
        StepConfiguration step = new StepConfiguration(Verify.TYPE_ID);
        SteppedTest test = setupLogTest(step);

        SimpleProject project = new SimpleProject();
        InteractiveTestController controller = new InteractiveTestControllerImpl();
        controller.run(new SteppedTestProviderImpl(project, test));
        TestStateBlocker blocker = new TestStateBlocker(controller);
        blocker.blockUntil(InteractiveTestState.PAUSED);

        Assert.assertEquals(InteractiveTestState.PAUSED, controller.getState());
        Assert.assertNull(controller.getResult());  // test not considered done

        // should be stopped after verify step
        Assert.assertTrue(controller.getTestRunner().getExecutionContext().getEventLog().findEvents(new EventTypeMatcher(StartStepEventType.TYPE_ID)).size() == 2);
        }

    private static SteppedTest setupLogTest(StepConfiguration first_step)
        {
        StepConfiguration log_step = new StepConfiguration(LogMessage.TYPE_ID);
        log_step.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("abc"));

        StepConfiguration main_step;
        if (first_step == null)
            main_step = log_step;
        else
            {
            main_step = new StepConfiguration(BasicCompoundStep.TYPE_ID);
            main_step.addChild(first_step);
            main_step.addChild(log_step);
            }

        return new SteppedTest(main_step);
        }
    }