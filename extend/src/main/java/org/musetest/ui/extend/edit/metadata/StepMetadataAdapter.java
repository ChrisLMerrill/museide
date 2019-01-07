package org.musetest.ui.extend.edit.metadata;

import org.musetest.core.step.*;
import org.musetest.core.step.events.*;
import org.musetest.core.util.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepMetadataAdapter implements ContainsMetadata, ChangeEventListener
	{
	public StepMetadataAdapter(StepConfiguration step)
		{
		_step = step;
		_step.addChangeListener(this);
		}

	@Override
	public void setMetadataField(String name, Object value)
		{
		_step.setMetadataField(name, value);
		}

	@Override
	public void removeMetadataField(String name)
		{
		_step.removeMetadataField(name);
		}

	@Override
	public Object getMetadataField(String name)
		{
		return _step.getMetadataField(name);
		}

	@Override
	public Set<String> getMetadataFieldNames()
		{
		if (_step.getMetadata() == null)
			return Collections.emptySet();
		return _step.getMetadata().keySet();
		}

	@Override
	public void addChangeListener(ChangeEventListener listener)
		{
		_listeners.add(listener);
		}

	@Override
	public boolean removeChangeListener(ChangeEventListener listener)
		{
		return _listeners.remove(listener);
		}

	public void destroy()
		{
		_step.removeChangeListener(this);
		}

	@Override
	public void changeEventRaised(ChangeEvent e)
		{
		if (e instanceof MetadataChangeEvent)
			{
			MetadataChangeEvent event = (MetadataChangeEvent) e;
			for (ChangeEventListener listener : _listeners)
				listener.changeEventRaised(new MetadataChangeEvent(this, event.getName(), event.getOldValue(), event.getNewValue()));
			}
		}

	private StepConfiguration _step;
	private List<ChangeEventListener> _listeners = new ArrayList<>();
	}


