package org.museautomation.ui.extend.edit.metadata;

import org.museautomation.ui.extend.actions.*;
import org.museautomation.core.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class AddMetadataAction extends UndoableAction
	{
	public AddMetadataAction(ContainsMetadata container, String name, Object value)
		{
		_container = container;
		_name = name;
		_value = value;
		}

	@Override
	protected boolean executeImplementation()
		{
		_old_value = _container.getMetadataField(_name);
		_container.setMetadataField(_name, _value);
		return true;
		}

	@Override
	protected boolean undoImplementation()
		{
		if (_old_value == null)
			_container.removeMetadataField(_name);
		else
			_container.setMetadataField(_name, _old_value);
		return false;
		}

	private ContainsMetadata _container;
	private String _name;
	private Object _value;
	private Object _old_value;
	}