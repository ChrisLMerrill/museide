package org.museautomation.ui.step;

import org.junit.jupiter.api.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.steptask.*;
import org.museautomation.ui.steptask.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class UniqueIdTests
	{
	@Test
    void addUniqueIdsToSteps()
		{
		// create a StepConfiguration that doesn't have an ID
		StepConfiguration step1 = new StepConfiguration("mock-step");
		StepConfiguration step2 = new StepConfiguration("mock-step-2");
		StepConfiguration all = new StepConfiguration("compound");
		all.addChild(step1);
		all.addChild(step2);
		SteppedTask test = new SteppedTask(all);

		UniqueIds.addToStepsIfNeeded(test, new SimpleProject());

		Assertions.assertNotNull(step1.getStepId());
		Assertions.assertNotNull(step2.getStepId());
		Assertions.assertNotEquals(0, step1.getStepId().longValue());
		Assertions.assertNotEquals(0, step2.getStepId().longValue());
		Assertions.assertNotEquals(step1.getStepId().longValue(), step2.getStepId().longValue());
		}

	@Test
    void fixDuplicateIds()
		{
		StepConfiguration step1 = new StepConfiguration("mock-step");
		step1.setStepId(7L);
		StepConfiguration step2 = new StepConfiguration("mock-step-2");
		step2.setStepId(7L);
		StepConfiguration all = new StepConfiguration("compound");
		all.addChild(step1);
		all.addChild(step2);
		SteppedTask test = new SteppedTask(all);

		UniqueIds.addToStepsIfNeeded(test, new SimpleProject());

		Assertions.assertNotNull(step1.getStepId());
		Assertions.assertNotNull(step2.getStepId());
		Assertions.assertNotEquals(0, step1.getStepId().longValue());
		Assertions.assertNotEquals(0, step2.getStepId().longValue());
		Assertions.assertNotEquals(step1.getStepId().longValue(), step2.getStepId().longValue());
		}
	}