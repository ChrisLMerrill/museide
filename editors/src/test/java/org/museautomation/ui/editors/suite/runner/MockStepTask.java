package org.museautomation.ui.editors.suite.runner;

import org.museautomation.builtins.step.*;
import org.museautomation.core.context.*;
import org.museautomation.core.events.*;
import org.museautomation.core.step.*;
import org.museautomation.core.steptask.*;
import org.museautomation.core.values.*;

import java.util.*;

/**
 * Simulates running a stepped task. Doesn't actually execute the step.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("WeakerAccess")
public class MockStepTask extends SteppedTask
    {
    public MockStepTask(StepConfiguration step_config)
        {
        setStep(step_config);
        }

    public MockStepTask()
        {
        final StepConfiguration step = new StepConfiguration(LogMessage.TYPE_ID);
        step.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("a message about the mock step test"));
        setStep(step);
        }

    @Override
    protected boolean executeImplementation(TaskExecutionContext context)
        {
        if (!(context instanceof SteppedTaskExecutionContext))
            throw new IllegalArgumentException("MockStepTask.executeImplementation() must be provided a SteppedTestExecutionContext. Instead, received at " + context.getClass().getSimpleName());

        EventLogger logger = new EventLogger();
        context.addEventListener(logger);

        context.raiseEvent(StartTaskEventType.create(getId(), "Mock Step Test"));

        // simulate running a step
        if (getStep() != null)
            {
            SingleStepExecutionContext step_context = new SingleStepExecutionContext((StepsExecutionContext) context, getStep(), true);
            context.raiseEvent(StartStepEventType.create(getStep(), step_context));
            context.raiseEvent(EndStepEventType.create(getStep(), step_context, new BasicStepExecutionResult(StepExecutionStatus.COMPLETE)));
            }

        context.raiseEvent(EndTaskEventType.create());
        return true;
        }

    @Override
    public Map<String, ValueSourceConfiguration> getDefaultVariables()
        {
        return null;
        }

    @Override
    public void setDefaultVariables(Map<String, ValueSourceConfiguration> default_variables)
        {

        }

    @Override
    public void setDefaultVariable(String name, ValueSourceConfiguration source)
        {

        }
    }
