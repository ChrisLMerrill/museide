package org.museautomation.ui.extend.edit;

import org.museautomation.core.resource.*;
import org.museautomation.core.resource.types.*;

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
