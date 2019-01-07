package org.musetest.ui.step;

import org.musetest.core.step.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class AddTagToStepAction extends UndoableAction
	{
	public AddTagToStepAction(StepConfiguration step, String tag)
		{
		_step = step;
		_tag = tag;
		}

	@Override
	protected boolean executeImplementation()
		{
		_added = _step.addTag(_tag);
		return true;
		}

	@Override
	protected boolean undoImplementation()
		{
		if (_added)
			_step.removeTag(_tag);
		return true;
		}

	private StepConfiguration _step;
	private String _tag;
	private boolean _added;
	}


