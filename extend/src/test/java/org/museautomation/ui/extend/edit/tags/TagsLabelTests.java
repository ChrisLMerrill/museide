package org.museautomation.ui.extend.edit.tags;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TagsLabelTests extends ComponentTest
	{
	@Test
    void displayEmpty()
	    {
	    MockTaggable tags = new MockTaggable();
	    _label.setTags(tags);
	    waitForUiEvents();

	    Assertions.assertTrue(exists(byClass(TagsLabel.TAGS_STYLE)));
	    Assertions.assertFalse(exists(TagsLabel.TAG_STYLE));
	    }

	@Test
    void displayOne()
	    {
	    MockTaggable tags = new MockTaggable();
	    tags.tags().addTag("abc");
	    _label.setTags(tags);
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
	    _label.setTags(tags);
	    waitForUiEvents();

	    Assertions.assertTrue(exists(byClass(TagsLabel.TAGS_STYLE)));
	    Assertions.assertTrue(exists("abc"));
	    Assertions.assertTrue(exists("def"));
	    Assertions.assertEquals(2, lookup(byClass(TagsLabel.TAG_STYLE)).queryAll().size());
	    }

	@Override
	protected Node createComponentNode()
		{
		_label = new TagsLabel();
		return _label.getNode();
		}

	private TagsLabel _label;
	}