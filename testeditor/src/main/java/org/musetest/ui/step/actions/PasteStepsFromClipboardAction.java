package org.musetest.ui.step.actions;

import javafx.scene.input.*;
import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.ui.steptree.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class PasteStepsFromClipboardAction extends InsertStepsAction
	{
	public PasteStepsFromClipboardAction(MuseProject project, StepConfiguration parent_step, int index)
		{
		super(parent_step, new ArrayList<>(), index);

		String serialized = (String) Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT);
		List<StepConfiguration> steps = ClipboardSerializer.listOfStepsfromString(serialized);
		if (steps == null)
		    return;
		_new_steps = new ArrayList<>();
		for (StepConfiguration step : steps)
			_new_steps.add(StepConfiguration.copy(step, project)); // paste a copy with a new ID
		}
	}


