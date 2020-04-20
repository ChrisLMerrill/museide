package org.museautomation.ui.extend.edit.tags;

import org.museautomation.core.metadata.*;

public class MockTaggable implements Taggable
	{
    @Override
    public TagContainer tags()
        {
        return _tags;
        }

	private TagContainer _tags = new HashSetTagContainer();
	}