package org.musetest.ui.step.actions;

import javafx.scene.input.*;
import org.musetest.core.step.*;
import org.musetest.ui.steptree.*;

import java.util.*;

/**
 * This action must be run on the event thread.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class CutStepsToClipboardAction extends DeleteStepsAction
	{
	public CutStepsToClipboardAction(StepConfiguration root_step, StepConfiguration delete_step)
		{
		super(root_step, delete_step);
		}

	public CutStepsToClipboardAction(StepConfiguration root_step, List<StepConfiguration> delete_steps)
		{
		super(root_step, delete_steps);
		}

	@Override
	protected boolean executeImplementation()
		{
		super.executeImplementation();

		// gather the steps
		final List<StepConfiguration> steps_to_clipboard = new ArrayList<>();
		for (StepDeleter deleter : _deleted_steps)
			steps_to_clipboard.add(deleter._deleted);

		// put them on the clipboard
		ClipboardContent content = new ClipboardContent();
		content.put(DataFormat.PLAIN_TEXT, ClipboardSerializer.listOfStepsToString(steps_to_clipboard));
		return Clipboard.getSystemClipboard().setContent(content);
		}
	}


