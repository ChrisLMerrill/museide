package org.museautomation.ui.step;

import org.museautomation.core.metadata.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class AddTagAction extends UndoableAction
	{
	public AddTagAction(Taggable taggable, String tag)
		{
		_taggable = taggable;
		_tag = tag;
		}

	@Override
	protected boolean executeImplementation()
		{
		_added = _taggable.tags().addTag(_tag);
		return _added;
		}

	@Override
	protected boolean undoImplementation()
		{
		if (_added)
			return _taggable.tags().removeTag(_tag);
		return false;
		}

	private Taggable _taggable;
	private String _tag;
	private boolean _added;
	}


