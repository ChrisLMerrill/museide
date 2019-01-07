package org.musetest.ui.extend.edit.tags;

import org.musetest.core.util.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MockTaggable implements Taggable
	{
	@Override
	public Set<String> getTags()
		{
		return _tags;
		}

	@Override
	public void setTags(Set<String> tags)
		{
		_tags = tags;
		}

	@Override
	public boolean addTag(String tag)
		{
		return _tags.add(tag);
		}

	@Override
	public boolean removeTag(String tag)
		{
		return _tags.remove(tag);
		}

	@Override
	public boolean hasTag(String tag)
		{
		return _tags.contains(tag);
		}

	private Set<String> _tags = new HashSet<>();
	}