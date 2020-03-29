package org.museautomation.ui.steptask;

import org.junit.*;
import org.museautomation.ui.steptask.execution.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.events.*;
import org.museautomation.core.events.matching.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.steptask.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.edit.step.*;

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
        SteppedTask test = setupLogTest(step);

        SimpleProject project = new SimpleProject();
        InteractiveTestController controller = new InteractiveTestControllerImpl();
        controller.run(new SteppedTaskProviderImpl(project, test));
        TestStateBlocker blocker = new TestStateBlocker(controller);
        blocker.blockUntil(InteractiveTaskState.PAUSED);

        Assert.assertEquals(InteractiveTaskState.PAUSED, controller.getState());
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
        SteppedTask test = setupLogTest(step);

        SimpleProject project = new SimpleProject();
        InteractiveTestController controller = new InteractiveTestControllerImpl();
        controller.run(new SteppedTaskProviderImpl(project, test));
        TestStateBlocker blocker = new TestStateBlocker(controller);
        blocker.blockUntil(InteractiveTaskState.PAUSED);

        Assert.assertEquals(InteractiveTaskState.PAUSED, controller.getState());
        Assert.assertNull(controller.getResult());  // test not considered done

        // should be stopped after verify step
        Assert.assertTrue(controller.getTestRunner().getExecutionContext().getEventLog().findEvents(new EventTypeMatcher(StartStepEventType.TYPE_ID)).size() == 2);
        }

    private static SteppedTask setupLogTest(StepConfiguration first_step)
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

        return new SteppedTask(main_step);
        }
    }