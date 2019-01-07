package org.musetest.ui.step;

import org.musetest.core.*;
import org.musetest.core.context.*;
import org.musetest.core.step.*;
import org.musetest.core.step.descriptor.*;
import org.musetest.core.values.descriptor.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@MuseTypeId("mock-step-with-list")
@MuseStepName("List Step")
@MuseStepShortDescription("Step with list")
@MuseStepLongDescription("A mock step with a list param")
@MuseSubsourceDescriptor(displayName = "List", description = "List of things", type = SubsourceDescriptor.Type.List, name = MockStepWithListParam.LIST_PARAM, optional = true)
public class MockStepWithListParam extends BaseStep
    {
    public MockStepWithListParam(StepConfiguration configuration)
        {
        super(configuration);
        }

    @Override
    protected StepExecutionResult executeImplementation(StepExecutionContext stepExecutionContext) throws MuseExecutionError
        {
        return null;
        }

    final static String LIST_PARAM = "list";
    public final static String TYPE_ID = MockStepWithListParam.class.getAnnotation(MuseTypeId.class).value();
    }


