package org.musetest.ui.extend.edit.tags;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TagsLabelTests extends ComponentTest
	{
	@Test
	public void displayEmpty()
	    {
	    MockTaggable tags = new MockTaggable();
	    _label.setTags(tags);
	    waitForUiEvents();

	    Assert.assertTrue(exists(byClass(TagsLabel.TAGS_STYLE)));
	    Assert.assertFalse(exists(TagsLabel.TAG_STYLE));
	    }

	@Test
	public void displayOne()
	    {
	    MockTaggable tags = new MockTaggable();
	    tags.addTag("abc");
	    _label.setTags(tags);
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
	    _label.setTags(tags);
	    waitForUiEvents();

	    Assert.assertTrue(exists(byClass(TagsLabel.TAGS_STYLE)));
	    Assert.assertTrue(exists("abc"));
	    Assert.assertTrue(exists("def"));
	    Assert.assertEquals(2, lookup(byClass(TagsLabel.TAG_STYLE)).queryAll().size());
	    }

	@Override
	protected Node createComponentNode()
		{
		_label = new TagsLabel();
		return _label.getNode();
		}

	private TagsLabel _label;
	}


