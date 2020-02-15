package org.museautomation.ui.extend.edit.metadata;

import org.junit.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.step.*;
import org.museautomation.core.step.events.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepMetadataAdapterTests
	{
	@Test
	public void addField()
	    {
	    _adapter.setMetadataField("field1", "value1");
	    Assert.assertEquals("field not added", "value1", _step.getMetadataField("field1"));
	    }

	@Test
	public void removeField()
	    {
	    _adapter.removeMetadataField("initial");
	    Assert.assertNull("field not removed", _step.getMetadataField("initial"));
	    }

	@Test
	public void getFieldNames()
	    {
	    Set<String> names = _adapter.getMetadataFieldNames();
	    Assert.assertEquals(1, names.size());
	    Assert.assertEquals("initial", names.iterator().next());
	    }

	@Test
	public void getValue()
	    {
	    Assert.assertEquals("got wrong value", "initvalue", _adapter.getMetadataField("initial"));
	    }

	@Test
	public void getStepMetadataChangeEvent()
	    {
	    AtomicReference<Object> target_received = new AtomicReference<>(null);
	    AtomicReference<Object> name_received = new AtomicReference<>(null);
	    AtomicReference<Object> old_value_received = new AtomicReference<>(null);
	    AtomicReference<Object> new_value_received = new AtomicReference<>(null);
	    _adapter.addChangeListener(((event) ->
	        {
	        if (event instanceof MetadataChangeEvent)
		        {
		        MetadataChangeEvent change = (MetadataChangeEvent) event;
		        target_received.set(event.getTarget());
		        name_received.set(change.getName());
		        old_value_received.set(change.getOldValue());
		        new_value_received.set(change.getNewValue());
		        }
	        }));

	    _step.setMetadataField("initial", "newval");
	    Assert.assertEquals(_adapter, target_received.get());
	    Assert.assertEquals("initial", name_received.get());
	    Assert.assertEquals("initvalue", old_value_received.get());
	    Assert.assertEquals("newval", new_value_received.get());
	    }

	@Test
	public void destroy() throws NoSuchFieldException, IllegalAccessException
		{
	    _adapter.destroy();
	    Assert.assertEquals("change listener not removed from step", 0, getNumberOfListenersRegistered(_step));
	    }

	@Test
	public void stepHasNoMetadata()
	    {
	    StepConfiguration step = new StepConfiguration();
	    StepMetadataAdapter adapter = new StepMetadataAdapter(step);
	    Assert.assertEquals("should return empty set", 0, adapter.getMetadataFieldNames().size());
	    }

	private int getNumberOfListenersRegistered(StepConfiguration step) throws NoSuchFieldException, IllegalAccessException
		{
		Field field = step.getClass().getDeclaredField("_listeners");
		field.setAccessible(true);
		Collection listeners = (Collection) field.get(step);
		if (listeners == null)
			return 0;
		return listeners.size();
		}

	@Before
	public void setup()
		{
		_step.setMetadataField("initial", "initvalue");
		}

	private StepConfiguration _step = new StepConfiguration(LogMessage.TYPE_ID);
	private StepMetadataAdapter _adapter = new StepMetadataAdapter(_step);
	}


