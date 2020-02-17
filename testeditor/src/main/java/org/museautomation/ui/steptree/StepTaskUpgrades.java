package org.museautomation.ui.steptree;

import org.museautomation.ui.steptask.*;
import org.museautomation.core.*;
import org.museautomation.core.step.*;
import org.museautomation.core.steptask.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepTaskUpgrades
	{
	public StepTaskUpgrades(SteppedTask task, MuseProject project)
		{
		_task = task;
		_project = project;
		}

	public void apply()
		{
		UniqueIds.addToStepsIfNeeded(_task, _project);
		upgradeDescriptions(_task.getStep());
		}

	// TODO remove at some point (this was needed for 0.11 update)
	private void upgradeDescriptions(StepConfiguration step)
		{
		if (step.getMetadata() != null)
			{
			final Map<String, Object> metadata = step.getMetadata();
			Object description = metadata.remove(StepConfiguration.META_DESCRIPTION_OLD);
			if (description != null)
				metadata.put(StepConfiguration.META_DESCRIPTION, description);
			}
		if (step.getChildren() != null)
			for (StepConfiguration child : step.getChildren())
				upgradeDescriptions(child);
		}

	private SteppedTask _task;
	private MuseProject _project;
	}


