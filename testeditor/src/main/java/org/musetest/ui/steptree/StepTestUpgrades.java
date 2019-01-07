package org.musetest.ui.steptree;

import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.core.steptest.*;
import org.musetest.ui.steptest.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepTestUpgrades
	{
	public StepTestUpgrades(SteppedTest test, MuseProject project)
		{
		_test = test;
		_project = project;
		}

	public void apply()
		{
		UniqueIds.addToStepsIfNeeded(_test, _project);
		upgradeDescriptions(_test.getStep());
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

	private SteppedTest _test;
	private MuseProject _project;
	}


