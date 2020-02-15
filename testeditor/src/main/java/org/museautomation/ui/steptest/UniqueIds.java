package org.museautomation.ui.steptest;

import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.steptest.*;

import java.util.*;

/**
 * Ensures that each step in a SteppedTest has a unique ID (within the test).
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UniqueIds
	{
	public static void addToStepsIfNeeded(SteppedTest test, MuseProject project)
		{
		StepConfiguration step = test.getStep();
		addToStepIfNeeded(step, project, new IdTracker());
		}

	private static void addToStepIfNeeded(StepConfiguration step, MuseProject project, IdTracker tracker)
		{
		checkAndRepairId(step, project, tracker);
		if (step.getStepId() == null)
			step.setStepId(IdGenerator.get(project).generateLongId());
		if (step.getChildren() != null)
			for (StepConfiguration child : step.getChildren())
				addToStepIfNeeded(child, project, tracker);
		}

	private static void checkAndRepairId(StepConfiguration step, MuseProject project, IdTracker tracker)
		{
		// upgrade tests that used the original id name.  // TODO remove at some point (this was needed for 0.11 update)
		if (step.getMetadata() != null)
			{
			Object old_id = step.getMetadata().remove(StepConfiguration.META_ID_OLD);
			if (old_id != null)
				step.getMetadata().put(StepConfiguration.META_ID, old_id);
			}

		Long id = step.getStepId();
		if (id == null)
			return;  // nothing to repair
		if (tracker._existing_ids.contains(id))
			{
			Long new_id = IdGenerator.get(project).generateLongId();
			while (tracker._existing_ids.contains(new_id))
				{
				IdGenerator.get(project).conflict();
				new_id = IdGenerator.get(project).generateLongId();
				}
			step.setStepId(new_id);
			tracker._existing_ids.add(new_id);
			}
		else
			tracker._existing_ids.add(id);
		}

	private static class IdTracker
		{
		Set<Long> _existing_ids = new HashSet<>();
		}
	}


