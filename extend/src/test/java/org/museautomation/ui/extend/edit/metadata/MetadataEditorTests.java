package org.museautomation.ui.extend.edit.metadata;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MetadataEditorTests extends ComponentTest
	{
	@Test
	public void showOneDatum()
	    {
	    MockMetadata data = new MockMetadata();
	    data.setMetadataField("field1", "value1");
	    _editor.setMetadata(data);
	    waitForUiEvents();

	    Assert.assertTrue("field1 not displayed", exists("field1=value1"));
	    Assert.assertTrue("field1 not styled", exists(byClass(MetadataLabel.LABEL_CLASS)));
	    }

	@Test
	public void showTwoData()
	    {
	    MockMetadata data = new MockMetadata();
	    data.setMetadataField("field1", "value1");
	    data.setMetadataField("field2", "value2");
	    _editor.setMetadata(data);
	    waitForUiEvents();

	    Assert.assertTrue("field1 not displayed", exists("field1=value1"));
	    Assert.assertTrue("field2 not displayed", exists("field2=value2"));
	    Assert.assertEquals("fields not styled", 2, numberOf(byClass(MetadataLabel.LABEL_CLASS)));
	    }
	
	@Test
	public void showAndHideReservedNames()
	    {
	    MockMetadata data = new MockMetadata();
	    data.setMetadataField("_id", "id");
	    data.setMetadataField("_description", "description");
	    data.setMetadataField("_tags", "tags");
	    _editor.setMetadata(data);
	    waitForUiEvents();

	    Assert.assertFalse("system data not hidden", exists("_id=id"));
	    Assert.assertFalse("system data not hidden", exists("_description=description"));
	    Assert.assertFalse("system data not hidden", exists("_tags=tags"));

	    _editor.setFilterReservedNames(false);
	    waitForUiEvents();

	    Assert.assertTrue("system data not shown", exists("_id=id"));
	    Assert.assertTrue("system data not shown", exists("_description=description"));
	    Assert.assertTrue("system data not shown", exists("_tags=tags"));

	    _editor.setFilterReservedNames(true);
	    waitForUiEvents();

	    Assert.assertFalse("system data not hidden", exists("_id=id"));
	    Assert.assertFalse("system data not hidden", exists("_description=description"));
	    Assert.assertFalse("system data not hidden", exists("_tags=tags"));
	    }

	@Test
	public void addData()
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
	    Assert.assertTrue("add field not displayed", exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)));

	    fillFieldAndPressEnter(byClass(MetadataEditor.ADD_FIELD_SYTLE), "n1=v1");
	    Assert.assertNotNull("add listener not called", name_added.get());
	    Assert.assertEquals("add not called with the correct name", "n1", name_added.get());
	    Assert.assertEquals("add not called with the correct value", "v1", value_added.get());
	    }

	@Test
	public void cancelAdd()
	    {
	    AtomicReference<Boolean> added = new AtomicReference<>(false);
        _editor.setAddListener((name, value) -> added.set(true));
        MockMetadata data = new MockMetadata();
        _editor.setMetadata(data);
        waitForUiEvents();

	    clickOn(byClass(MetadataEditor.ADD_BUTTON_STYLE));
	    pressEscape(byClass(MetadataEditor.ADD_FIELD_SYTLE));
	    Assert.assertFalse("listener was called", added.get());
	    Assert.assertFalse("adder is showing", exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)));
	    }

	@Test
	public void failWithoutEquals()
	    {
	    Assert.assertEquals(null, getValueForInput("abc"));
	    Assert.assertTrue("adder not showing", exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)));
	    }

	@Test
	public void failNullValue()
	    {
	    Assert.assertEquals(null, getValueForInput("n1="));
	    Assert.assertTrue("adder not showing", exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)));
	    }

	@Test
	public void failNullName()
	    {
	    Assert.assertEquals(null, getValueForInput("=123"));
	    Assert.assertTrue("adder not showing", exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)));
	    }

	@Test
	public void addInteger()
	    {
	    Assert.assertEquals(123L, getValueForInput("n1=123"));
	    Assert.assertFalse("adder not hidden", exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)));
	    }

	@Test
	public void addBooleanTrue()
	    {
	    Assert.assertEquals(true, getValueForInput("n1=true"));
	    Assert.assertFalse("adder not hidden", exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)));
	    }

	@Test
	public void addBooleanFalse()
	    {
	    Assert.assertEquals(false, getValueForInput("n1=false"));
	    Assert.assertFalse("adder not hidden", exists(byClass(MetadataEditor.ADD_FIELD_SYTLE)));
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
	public void removeData()
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
	    Assert.assertEquals("remove listener not called twice", 2, removed_names.size());

	    Assert.assertTrue("field1 not deleted", removed_names.contains("field1"));
	    Assert.assertTrue("field2 not deleted", removed_names.contains("field2"));
	    Assert.assertTrue("value1 not deleted", removed_values.contains("value1"));
	    Assert.assertTrue("value2 not deleted", removed_values.contains("value2"));
	    }

	@Test
	public void showRemovedData()
	    {
	    MockMetadata data = new MockMetadata();
	    data.setMetadataField("field1", "value1");
	    data.setMetadataField("field2", "value2");
	    _editor.setMetadata(data);
	    waitForUiEvents();

	    data.removeMetadataField("field1");
	    _editor.refresh();
	    waitForUiEvents();
	    Assert.assertTrue("field2 was removed by mistake", exists("field2=value2"));
	    Assert.assertFalse("field1 not removed", exists("field1=value1"));

	    data.removeMetadataField("field2");
	    _editor.refresh();
	    waitForUiEvents();
	    Assert.assertFalse("field2 not removed", exists("field2=value2"));
	    }

	@Test
	public void showAddedData()
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
	    Assert.assertTrue("field2 not added", exists("field2=value2"));
	    Assert.assertTrue("field1 disappeared", exists("field1=value1"));
	    }

	@Override
	protected Node createComponentNode()
		{
		return _editor.getNode();
		}

	private MetadataEditor _editor = new MetadataEditor();
	}


