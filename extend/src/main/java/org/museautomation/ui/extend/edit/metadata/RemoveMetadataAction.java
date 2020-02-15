package org.museautomation.ui.extend.edit.metadata;

import org.museautomation.ui.extend.actions.*;
import org.museautomation.core.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RemoveMetadataAction extends UndoableAction
	{
	public RemoveMetadataAction(ContainsMetadata container, String name)
		{
		_container = container;
		_name = name;
		}

	@Override
	protected boolean executeImplementation()
		{
		_old_value = _container.getMetadataField(_name);
		_container.removeMetadataField(_name);
		return true;
		}

	@Override
	protected boolean undoImplementation()
		{
		if (_old_value != null)
			_container.setMetadataField(_name, _old_value);
		return true;
		}

	private ContainsMetadata _container;
	private String _name;
	private Object _old_value;
	}