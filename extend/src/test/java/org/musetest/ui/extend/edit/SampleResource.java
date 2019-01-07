package org.musetest.ui.extend.edit;

import org.musetest.core.resource.*;
import org.musetest.core.resource.types.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class SampleResource extends BaseMuseResource
	{
	@Override
	public ResourceType getType()
		{
		return new SampleResourceType();
		}

	public String getEditText()
		{
		return getType().getName() + ": " + getId();
		}

	public static class SampleResourceType extends ResourceType
		{
		public SampleResourceType()
			{
			super("sample-resource", "Sample Resource", SampleResource.class);
			}
		}
	}
