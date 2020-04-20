package org.museautomation.ui.extend.edit.tags;

import javafx.scene.*;
import javafx.scene.input.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TagsEditorTests extends ComponentTest
	{
	@Test
    void displayEmpty()
		{
		MockTaggable tags = new MockTaggable();
		_editor.setTags(tags);
		waitForUiEvents();

		Assertions.assertTrue(exists(byClass(TagsLabel.TAGS_STYLE)));
		Assertions.assertFalse(exists(TagsLabel.TAG_STYLE));
		}

	@Test
    void displayOne()
		{
		MockTaggable tags = new MockTaggable();
		tags.tags().addTag("abc");
		_editor.setTags(tags);
		waitForUiEvents();

		Assertions.assertTrue(exists(byClass(TagsLabel.TAGS_STYLE)));
		Assertions.assertTrue(exists("abc"));
		Assertions.assertEquals(1, lookup(byClass(TagsLabel.TAG_STYLE)).queryAll().size());
		}

	@Test
    void displayTwo()
		{
		MockTaggable tags = new MockTaggable();
		tags.tags().addTag("abc");
		tags.tags().addTag("def");
		_editor.setTags(tags);
		waitForUiEvents();

		Assertions.assertTrue(exists(byClass(TagsLabel.TAGS_STYLE)));
		Assertions.assertTrue(exists("abc"));
		Assertions.assertTrue(exists("def"));
		Assertions.assertEquals(2, lookup(byClass(TagsLabel.TAG_STYLE)).queryAll().size());
		}

	@Test
    void addTag()
		{
		MockTaggable tags = new MockTaggable();
		AtomicReference<String> add_listener_called = new AtomicReference<>(null);
		_editor.setAddListener(add_listener_called::set);
		_editor.setTags(tags);
		waitForUiEvents();

		clickOn(id(TagsEditor.ADD_BUTTON_ID));
		Assertions.assertTrue(exists(id(TagsEditor.ADD_FIELD_ID)), "tag add field not displayed");
		fillFieldAndPressEnter(id(TagsEditor.ADD_FIELD_ID), "newtag");
		Assertions.assertEquals("newtag", add_listener_called.get(), "tag add listener not called");

		tags.tags().addTag("newtag");
		_editor.refresh();
		waitForUiEvents();
		Set<Node> tag_nodes = lookup(byClass(TagsLabel.TAG_STYLE)).queryAll();
		Assertions.assertEquals(1, tag_nodes.size(), "tag not shown");
		Assertions.assertTrue(exists("newtag"));
		}

	@Test
    void failZeroLengthTag()
	    {
	    MockTaggable tags = new MockTaggable();
        AtomicReference<Boolean> add_listener_called = new AtomicReference<>(false);
        _editor.setAddListener(tag -> add_listener_called.set(true));
        _editor.setTags(tags);
        waitForUiEvents();

        clickOn(id(TagsEditor.ADD_BUTTON_ID));
        clickOn(id(TagsEditor.ADD_FIELD_ID)).push(KeyCode.ENTER);
        Assertions.assertFalse(add_listener_called.get(), "tag add listener was called");
	    Assertions.assertFalse(exists(id(TagsEditor.ADD_FIELD_ID)), "tag add field still showing");
	    }

	@Test
    void cancelAddTag()
	    {
	    MockTaggable tags = new MockTaggable();
        AtomicReference<String> add_listener_called = new AtomicReference<>(null);
        _editor.setAddListener(add_listener_called::set);
        _editor.setTags(tags);
        waitForUiEvents();

        clickOn(id(TagsEditor.ADD_BUTTON_ID));
        pressEscape(id(TagsEditor.ADD_FIELD_ID));
        Assertions.assertNull(add_listener_called.get(), "tag add listener was called");
        Assertions.assertFalse(exists(id(TagsEditor.ADD_FIELD_ID)), "editor is still showing");
	    }

	@Test
    void removeTag()
		{
		MockTaggable tags = new MockTaggable();
		tags.tags().addTag("abc");
		tags.tags().addTag("def");
		AtomicReference<String> delete_listener_called = new AtomicReference<>(null);
		_editor.setDeleteListener(delete_listener_called::set);
		_editor.setTags(tags);
		waitForUiEvents();

		Iterator<Node> tag_node_iterator = lookup(byClass(TagsLabel.TAG_STYLE)).queryAll().iterator();

		Node abc_tag_node = tag_node_iterator.next();
		Assertions.assertNotNull(abc_tag_node);
		final Node abc_delete_button = from(abc_tag_node).lookup(id(TagsEditor.DELETE_ID)).query();
		Assertions.assertNotNull(abc_delete_button, "delete button is missing");

		clickOn(abc_delete_button);
		Assertions.assertNotNull(delete_listener_called.get(), "delete listener was not called");
		Assertions.assertEquals("abc", delete_listener_called.get(), "delete listener called with wrong tag");

		Node def_tag_node = tag_node_iterator.next();
		Node def_delete_button = from(def_tag_node).lookup(id(TagsEditor.DELETE_ID)).query();
		clickOn(def_delete_button);
		Assertions.assertEquals("def", delete_listener_called.get(), "delete listener called with wrong tag");

		tags.tags().removeTag("abc");
		_editor.refresh();
		waitForUiEvents();
		Assertions.assertFalse(exists("abc"), "abc tag is still showing");
		Assertions.assertTrue(exists("def"), "def tag is missing");
		tags.tags().removeTag("def");
		_editor.refresh();
		waitForUiEvents();
		Assertions.assertFalse(exists("def"), "def tag is still showing");
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