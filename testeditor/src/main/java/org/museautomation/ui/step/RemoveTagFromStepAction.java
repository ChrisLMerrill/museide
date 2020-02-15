package org.museautomation.ui.step;

import org.museautomation.core.step.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RemoveTagFromStepAction extends UndoableAction
	{
	public RemoveTagFromStepAction(StepConfiguration step, String tag)
		{
		_step = step;
		_tag = tag;
		}

	@Override
	protected boolean executeImplementation()
		{
		_removed = _step.removeTag(_tag);
		return true;
		}

	@Override
	protected boolean undoImplementation()
		{
		if (_removed)
			_step.addTag(_tag);
		return true;
		}

	private StepConfiguration _step;
	private String _tag;
	private boolean _removed;
	}


