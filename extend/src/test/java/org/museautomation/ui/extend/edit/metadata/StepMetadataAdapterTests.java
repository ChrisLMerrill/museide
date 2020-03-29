package org.museautomation.ui.extend.edit.metadata;

import org.junit.jupiter.api.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.step.*;
import org.museautomation.core.step.events.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class StepMetadataAdapterTests
	{
	@Test
    void addField()
	    {
	    _adapter.setMetadataField("field1", "value1");
	    Assertions.assertEquals("value1", _step.getMetadataField("field1"), "field not added");
	    }

	@Test
    void removeField()
	    {
	    _adapter.removeMetadataField("initial");
	    Assertions.assertNull(_step.getMetadataField("initial"), "field not removed");
	    }

	@Test
    void getFieldNames()
	    {
	    Set<String> names = _adapter.getMetadataFieldNames();
	    Assertions.assertEquals(1, names.size());
	    Assertions.assertEquals("initial", names.iterator().next());
	    }

	@Test
    void getValue()
	    {
	    Assertions.assertEquals("initvalue", _adapter.getMetadataField("initial"), "got wrong value");
	    }

	@Test
    void getStepMetadataChangeEvent()
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
	    Assertions.assertEquals(_adapter, target_received.get());
	    Assertions.assertEquals("initial", name_received.get());
	    Assertions.assertEquals("initvalue", old_value_received.get());
	    Assertions.assertEquals("newval", new_value_received.get());
	    }

	@Test
    void destroy() throws NoSuchFieldException, IllegalAccessException
		{
	    _adapter.destroy();
	    Assertions.assertEquals(0, getNumberOfListenersRegistered(_step), "change listener not removed from step");
	    }

	@Test
    void stepHasNoMetadata()
	    {
	    StepConfiguration step = new StepConfiguration();
	    StepMetadataAdapter adapter = new StepMetadataAdapter(step);
	    Assertions.assertEquals(0, adapter.getMetadataFieldNames().size(), "should return empty set");
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

	@BeforeEach
	void setup()
		{
		_step.setMetadataField("initial", "initvalue");
		}

	private StepConfiguration _step = new StepConfiguration(LogMessage.TYPE_ID);
	private StepMetadataAdapter _adapter = new StepMetadataAdapter(_step);
	}