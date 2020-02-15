package org.museautomation.ui.extend.edit.metadata;

import org.museautomation.core.util.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MockMetadata implements ContainsMetadata
	{
	@Override
	public void setMetadataField(String name, Object value)
		{
		_map.put(name, value);
		}

	@Override
	public void removeMetadataField(String name)
		{
		_map.remove(name);
		}

	@Override
	public Object getMetadataField(String name)
		{
		return _map.get(name);
		}

	@Override
	public Set<String> getMetadataFieldNames()
		{
		return _map.keySet();
		}

	@Override
	public void addChangeListener(ChangeEventListener listener)
		{
		}

	@Override
	public boolean removeChangeListener(ChangeEventListener listener)
		{
		return false;
		}

	private HashMap<String, Object> _map = new HashMap<>();
	}


