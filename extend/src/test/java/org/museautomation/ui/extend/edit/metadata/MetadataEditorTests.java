package org.museautomation.ui.extend.edit.metadata;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MetadataEditorTests extends ComponentTest
	{
	@Test
    void showOneDatum()
	    {
	    MockMetadata data = new MockMetadata();
	    data.setMetadataField("field1", "value1");
	    _editor.setMetadata(data);
	    waitForUiEvents();

	    Assertions.assertTrue(exists("field1=value1"), "field1 not displayed");
	    Assertions.assertTrue(exists(byClass(MetadataLabel.LABEL_CLASS)), "field1 not styled");
	    }

	@Test
    void showTwoData()
	    {
	    MockMetadata data = new MockMetadata();
	    data.setMetadataField("field1", "value1");
	    data.setMetadataField("field2", "value2");
	    _editor.setMetadata(data);
	    waitForUiEvents();

	    Assertions.assertTrue(exists("field1=value1"), "field1 not displayed");
	    Assertions.assertTrue(exists("field2=value2"), "field2 not displayed");
	    Assertions.assertEquals(2, numberOf(byClass(MetadataLabel.LABEL_CLASS)), "fields not styled");
	    }
	
	@Test
    void showAndHideReservedNames()
	    {
	    MockMetadata data = new MockMetadata();
	    data.setMetadataField("_id", "id");
	    data.setMetadataField("_description", "description");
	    data.setMetadataField("_tags", "tags");
	    _editor.setMetadata(data);
	    waitForUiEvents();

	    Assertions.assertFalse(exists("_id=id"), "system data not hidden");
	    Assertions.assertFalse(exists("_description=description"), "system data not hidden");
	    Assertions.assertFalse(exists("_tags=tags"), "system data not hidden");

	    _editor.setFilterReservedNames(false);
	    waitForUiEvents();

	    Assertions.assertTrue(exists("_id=id"), "system data not shown");
	    Assertions.assertTrue(exists("_description=description"), "system data not shown");
	    Assertions.assertTrue(exists("_tags=tags"), "system data not shown");

	    _editor.setFilterReservedNames(true);
	    waitForUiEvents();

	    Assertions.assertFalse(exists("_id=id"), "system data not hidden");
	    Assertions.assertFalse(exists("_description=description"), "system data not hidden");
	    Assertions.assertFalse(exists("_tags=tags"), "system data not hidden");
	    }

	@Test
    void addData()
	    {
	    AtomicReference<String> name_added = new AtomicReference<>();
	    AtomicReference<Object> value_added = new AtomicReference<>();
	    _editor.setAddListener((name, value) ->
		    {
		    name_added.set(name);
		    value_added.set(value);
		    });
	    MockMetadata data = new MockMetadata();
	    _editor.setMetadata(data);
	    waitForUiEvents();

	    clickOn(byClass(MetadataEditor.ADD_BUTTON_STYLE));
	    Assertions.assertTrue(exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)), "add field not displayed");

	    fillFieldAndPressEnter(byClass(MetadataEditor.ADD_FIELD_SYTLE), "n1=v1");
	    Assertions.assertNotNull(name_added.get(), "add listener not called");
	    Assertions.assertEquals("n1", name_added.get(), "add not called with the correct name");
	    Assertions.assertEquals("v1", value_added.get(), "add not called with the correct value");
	    }

	@Test
    void cancelAdd()
	    {
	    AtomicReference<Boolean> added = new AtomicReference<>(false);
        _editor.setAddListener((name, value) -> added.set(true));
        MockMetadata data = new MockMetadata();
        _editor.setMetadata(data);
        waitForUiEvents();

	    clickOn(byClass(MetadataEditor.ADD_BUTTON_STYLE));
	    pressEscape(byClass(MetadataEditor.ADD_FIELD_SYTLE));
	    Assertions.assertFalse(added.get(), "listener was called");
	    Assertions.assertFalse(exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)), "adder is showing");
	    }

	@Test
    void failWithoutEquals()
	    {
        Assertions.assertNull(getValueForInput("abc"));
	    Assertions.assertTrue(exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)), "adder not showing");
	    }

	@Test
    void failNullValue()
	    {
        Assertions.assertNull(getValueForInput("n1="));
	    Assertions.assertTrue(exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)), "adder not showing");
	    }

	@Test
    void failNullName()
	    {
        Assertions.assertNull(getValueForInput("=123"));
	    Assertions.assertTrue(exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)), "adder not showing");
	    }

	@Test
    void addInteger()
	    {
	    Assertions.assertEquals(123L, getValueForInput("n1=123"));
	    Assertions.assertFalse(exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)), "adder not hidden");
	    }

	@Test
    void addBooleanTrue()
	    {
	    Assertions.assertEquals(true, getValueForInput("n1=true"));
	    Assertions.assertFalse(exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)), "adder not hidden");
	    }

	@Test
    void addBooleanFalse()
	    {
	    Assertions.assertEquals(false, getValueForInput("n1=false"));
	    Assertions.assertFalse(exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)), "adder not hidden");
	    }

	private Object getValueForInput(String input)
		{
		AtomicReference<Object> value_added = new AtomicReference<>();
		_editor.setAddListener((name, value) -> value_added.set(value));
		MockMetadata data = new MockMetadata();
		_editor.setMetadata(data);
		waitForUiEvents();

		clickOn(byClass(MetadataEditor.ADD_BUTTON_STYLE));
		fillFieldAndPressEnter(byClass(MetadataEditor.ADD_FIELD_SYTLE), input);
		return value_added.get();
		}

	@Test
    void removeData()
	    {
	    // setup with a data
	    MockMetadata data = new MockMetadata();
	    data.setMetadataField("field1", "value1");
	    data.setMetadataField("field2", "value2");
	    List<String> removed_names = new ArrayList<>();
	    List<Object> removed_values = new ArrayList<>();
	    _editor.setRemoveListener((name, value) ->
		    {
		    removed_names.add(name);
		    removed_values.add(value);
		    });
	    _editor.setMetadata(data);
	    waitForUiEvents();

	    for (Node node : lookup(byClass(MetadataLabel.REMOVE_BUTTON_CLASS)).queryAll())
	        clickOn(node);
	    Assertions.assertEquals(2, removed_names.size(), "remove listener not called twice");

	    Assertions.assertTrue(removed_names.contains("field1"), "field1 not deleted");
	    Assertions.assertTrue(removed_names.contains("field2"), "field2 not deleted");
	    Assertions.assertTrue(removed_values.contains("value1"), "value1 not deleted");
	    Assertions.assertTrue(removed_values.contains("value2"), "value2 not deleted");
	    }

	@Test
    void showRemovedData()
	    {
	    MockMetadata data = new MockMetadata();
	    data.setMetadataField("field1", "value1");
	    data.setMetadataField("field2", "value2");
	    _editor.setMetadata(data);
	    waitForUiEvents();

	    data.removeMetadataField("field1");
	    _editor.refresh();
	    waitForUiEvents();
	    Assertions.assertTrue(exists("field2=value2"), "field2 was removed by mistake");
	    Assertions.assertFalse(exists("field1=value1"), "field1 not removed");

	    data.removeMetadataField("field2");
	    _editor.refresh();
	    waitForUiEvents();
	    Assertions.assertFalse(exists("field2=value2"), "field2 not removed");
	    }

	@Test
    void showAddedData()
	    {
	    MockMetadata data = new MockMetadata();
	    _editor.setMetadata(data);
	    waitForUiEvents();

	    data.setMetadataField("field1", "value1");
	    _editor.refresh();
	    waitForUiEvents();

	    data.setMetadataField("field2", "value2");
	    _editor.refresh();
	    waitForUiEvents();
	    Assertions.assertTrue(exists("field2=value2"), "field2 not added");
	    Assertions.assertTrue(exists("field1=value1"), "field1 disappeared");
	    }

	@Override
    public Node createComponentNode()
		{
		return _editor.getNode();
		}

	private MetadataEditor _editor = new MetadataEditor();
	}