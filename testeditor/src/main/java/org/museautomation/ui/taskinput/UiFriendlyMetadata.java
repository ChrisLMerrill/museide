package org.museautomation.ui.taskinput;

import org.museautomation.core.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UiFriendlyMetadata
    {
    public UiFriendlyMetadata(ContainsMetadata metadata)
        {
        _metadata = metadata;
        }

    public String getString(String name, String default_value)
        {
        Object value = _metadata.getMetadataField(name);
        if (value == null)
            return default_value;
        else
            return value.toString();
        }

    final ContainsMetadata _metadata;
    }