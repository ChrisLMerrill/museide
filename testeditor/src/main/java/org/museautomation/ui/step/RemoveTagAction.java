package org.museautomation.ui.step;

import org.museautomation.core.metadata.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RemoveTagAction extends UndoableAction
	{
	public RemoveTagAction(Taggable step, String tag)
		{
		_taggable = step;
		_tag = tag;
		}

	@Override
	protected boolean executeImplementation()
		{
		_removed = _taggable.tags().removeTag(_tag);
		return true;
		}

	@Override
	protected boolean undoImplementation()
		{
		if (_removed)
			_taggable.tags().addTag(_tag);
		return true;
		}

	private Taggable _taggable;
	private String _tag;
	private boolean _removed;
	}


