package org.musetest.ui.step;

import org.junit.*;
import org.musetest.core.project.*;
import org.musetest.core.step.*;
import org.musetest.core.steptest.*;
import org.musetest.ui.steptest.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UniqueIdTests
	{
	@Test
	public void addUniqueIdsToSteps()
		{
		// create a StepConfiguration that doesn't have an ID
		StepConfiguration step1 = new StepConfiguration("mock-step");
		StepConfiguration step2 = new StepConfiguration("mock-step-2");
		StepConfiguration all = new StepConfiguration("compound");
		all.addChild(step1);
		all.addChild(step2);
		SteppedTest test = new SteppedTest(all);

		UniqueIds.addToStepsIfNeeded(test, new SimpleProject());

		Assert.assertNotNull(step1.getStepId());
		Assert.assertNotNull(step2.getStepId());
		Assert.assertNotEquals(0, step1.getStepId().longValue());
		Assert.assertNotEquals(0, step2.getStepId().longValue());
		Assert.assertNotEquals(step1.getStepId().longValue(), step2.getStepId().longValue());
		}

	@Test
	public void fixDuplicateIds()
		{
		StepConfiguration step1 = new StepConfiguration("mock-step");
		step1.setStepId(7L);
		StepConfiguration step2 = new StepConfiguration("mock-step-2");
		step2.setStepId(7L);
		StepConfiguration all = new StepConfiguration("compound");
		all.addChild(step1);
		all.addChild(step2);
		SteppedTest test = new SteppedTest(all);

		UniqueIds.addToStepsIfNeeded(test, new SimpleProject());

		Assert.assertNotNull(step1.getStepId());
		Assert.assertNotNull(step2.getStepId());
		Assert.assertNotEquals(0, step1.getStepId().longValue());
		Assert.assertNotEquals(0, step2.getStepId().longValue());
		Assert.assertNotEquals(step1.getStepId().longValue(), step2.getStepId().longValue());
		}
	}


