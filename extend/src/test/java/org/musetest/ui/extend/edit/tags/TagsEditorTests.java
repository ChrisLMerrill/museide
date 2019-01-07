package org.musetest.ui.extend.edit.tags;

import javafx.scene.*;
import javafx.scene.input.*;
import net.christophermerrill.testfx.*;
import org.junit.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TagsEditorTests extends ComponentTest
	{
	@Test
	public void displayEmpty()
		{
		MockTaggable tags = new MockTaggable();
		_editor.setTags(tags);
		waitForUiEvents();

		Assert.assertTrue(exists(byClass(TagsLabel.TAGS_STYLE)));
		Assert.assertFalse(exists(TagsLabel.TAG_STYLE));
		}

	@Test
	public void displayOne()
		{
		MockTaggable tags = new MockTaggable();
		tags.addTag("abc");
		_editor.setTags(tags);
		waitForUiEvents();

		Assert.assertTrue(exists(byClass(TagsLabel.TAGS_STYLE)));
		Assert.assertTrue(exists("abc"));
		Assert.assertEquals(1, lookup(byClass(TagsLabel.TAG_STYLE)).queryAll().size());
		}

	@Test
	public void displayTwo()
		{
		MockTaggable tags = new MockTaggable();
		tags.addTag("abc");
		tags.addTag("def");
		_editor.setTags(tags);
		waitForUiEvents();

		Assert.assertTrue(exists(byClass(TagsLabel.TAGS_STYLE)));
		Assert.assertTrue(exists("abc"));
		Assert.assertTrue(exists("def"));
		Assert.assertEquals(2, lookup(byClass(TagsLabel.TAG_STYLE)).queryAll().size());
		}

	@Test
	public void addTag()
		{
		MockTaggable tags = new MockTaggable();
		AtomicReference<String> add_listener_called = new AtomicReference<>(null);
		_editor.setAddListener(add_listener_called::set);
		_editor.setTags(tags);
		waitForUiEvents();

		clickOn(id(TagsEditor.ADD_BUTTON_ID));
		Assert.assertTrue("tag add field not displayed", exists(id(TagsEditor.ADD_FIELD_ID)));
		fillFieldAndPressEnter(id(TagsEditor.ADD_FIELD_ID), "newtag");
		Assert.assertEquals("tag add listener not called", "newtag", add_listener_called.get());

		tags.addTag("newtag");
		_editor.refresh();
		waitForUiEvents();
		Set<Node> tag_nodes = lookup(byClass(TagsLabel.TAG_STYLE)).queryAll();
		Assert.assertEquals("tag not shown", 1, tag_nodes.size());
		Assert.assertTrue(exists("newtag"));
		}

	@Test
	public void failZeroLengthTag()
	    {
	    MockTaggable tags = new MockTaggable();
        AtomicReference<Boolean> add_listener_called = new AtomicReference<>(false);
        _editor.setAddListener(tag -> add_listener_called.set(true));
        _editor.setTags(tags);
        waitForUiEvents();

        clickOn(id(TagsEditor.ADD_BUTTON_ID));
        clickOn(id(TagsEditor.ADD_FIELD_ID)).push(KeyCode.ENTER);
        Assert.assertFalse("tag add listener was called", add_listener_called.get());
	    Assert.assertFalse("tag add field still showing", exists(id(TagsEditor.ADD_FIELD_ID)));
	    }

	@Test
	public void cancelAddTag()
	    {
	    MockTaggable tags = new MockTaggable();
        AtomicReference<String> add_listener_called = new AtomicReference<>(null);
        _editor.setAddListener(add_listener_called::set);
        _editor.setTags(tags);
        waitForUiEvents();

        clickOn(id(TagsEditor.ADD_BUTTON_ID));
        pressEscape(id(TagsEditor.ADD_FIELD_ID));
        Assert.assertEquals("tag add listener was called", null, add_listener_called.get());
        Assert.assertFalse("editor is still showing", exists(id(TagsEditor.ADD_FIELD_ID)));
	    }

	@Test
	public void removeTag()
		{
		MockTaggable tags = new MockTaggable();
		tags.addTag("abc");
		tags.addTag("def");
		AtomicReference<String> delete_listener_called = new AtomicReference<>(null);
		_editor.setDeleteListener(delete_listener_called::set);
		_editor.setTags(tags);
		waitForUiEvents();

		Iterator<Node> tag_node_iterator = lookup(byClass(TagsLabel.TAG_STYLE)).queryAll().iterator();

		Node abc_tag_node = tag_node_iterator.next();
		Assert.assertNotNull(abc_tag_node);
		final Node abc_delete_button = from(abc_tag_node).lookup(id(TagsEditor.DELETE_ID)).query();
		Assert.assertNotNull("delete button is missing", abc_delete_button);

		clickOn(abc_delete_button);
		Assert.assertNotNull("delete listener was not called", delete_listener_called.get());
		Assert.assertEquals("delete listener called with wrong tag", "abc", delete_listener_called.get());

		Node def_tag_node = tag_node_iterator.next();
		Node def_delete_button = from(def_tag_node).lookup(id(TagsEditor.DELETE_ID)).query();
		clickOn(def_delete_button);
		Assert.assertEquals("delete listener called with wrong tag", "def", delete_listener_called.get());

		tags.removeTag("abc");
		_editor.refresh();
		waitForUiEvents();
		Assert.assertFalse("abc tag is still showing", exists("abc"));
		Assert.assertTrue("def tag is missing", exists("def"));
		tags.removeTag("def");
		_editor.refresh();
		waitForUiEvents();
		Assert.assertFalse("def tag is still showing", exists("def"));
		}

	@Override
	protected boolean fillToWidthAndHeight()
		{
		return false;
		}

	@Override
	protected Node createComponentNode()
		{
		_editor = new TagsEditor();
		return _editor.getNode();
		}

	private TagsEditor _editor;
	}