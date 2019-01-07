package org.musetest.ui.editors.suite.runner;

import org.musetest.builtins.step.*;
import org.musetest.core.context.*;
import org.musetest.core.events.*;
import org.musetest.core.step.*;
import org.musetest.core.steptest.*;
import org.musetest.core.values.*;

import java.util.*;

/**
 * Simulates running a stepped test. Doesn't actually execute the step.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("WeakerAccess")
public class MockStepTest extends SteppedTest
    {
    public MockStepTest(StepConfiguration step_config)
        {
        setStep(step_config);
        }

    public MockStepTest()
        {
        final StepConfiguration step = new StepConfiguration(LogMessage.TYPE_ID);
        step.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("a message about the mock step test"));
        setStep(step);
        }

    @Override
    protected boolean executeImplementation(TestExecutionContext context)
        {
        if (!(context instanceof SteppedTestExecutionContext))
            throw new IllegalArgumentException("Must be provided a SteppedTestExuectionContext");

        EventLogger logger = new EventLogger();
        context.addEventListener(logger);

        context.raiseEvent(StartTestEventType.create(getId(), "Mock Step Test"));

        // simulate running a step
        if (getStep() != null)
            {
            SingleStepExecutionContext step_context = new SingleStepExecutionContext((StepsExecutionContext) context, getStep(), true);
            context.raiseEvent(StartStepEventType.create(getStep(), step_context));
            context.raiseEvent(EndStepEventType.create(getStep(), step_context, new BasicStepExecutionResult(StepExecutionStatus.COMPLETE)));
            }

        context.raiseEvent(EndTestEventType.create());
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
