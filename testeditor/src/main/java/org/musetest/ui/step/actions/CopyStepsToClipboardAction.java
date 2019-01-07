package org.musetest.ui.step.actions;

import javafx.scene.input.*;
import org.musetest.core.step.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.steptree.*;
import org.slf4j.*;

import java.util.*;

/**
 * This action must be run on the event thread.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class CopyStepsToClipboardAction extends BaseEditAction
	{
	/**
	 * Pass in the copied step (they should already be duplicated if needed)
	 */
	public CopyStepsToClipboardAction(StepConfiguration copy_step)
		{
		_copied_steps = new ArrayList<>();
		_copied_steps.add(copy_step);
		}

	/**
	 * Pass in the copied list steps (they should already be duplicated if needed)
	 */
	public CopyStepsToClipboardAction(List<StepConfiguration> copy_steps)
		{
		_copied_steps = copy_steps;
		}

	@Override
	protected boolean executeImplementation()
		{
		// put them on the clipboard
        ClipboardContent content = new ClipboardContent();
		content.put(DataFormat.PLAIN_TEXT, ClipboardSerializer.listOfStepsToString(_copied_steps));
        boolean copied = Clipboard.getSystemClipboard().setContent(content);
LOG.info("copied to clipboard: " + copied);
        return copied;
		}

	private final List<StepConfiguration> _copied_steps;

	private final static Logger LOG = LoggerFactory.getLogger(CopyStepsToClipboardAction.class);
	}


