package org.museautomation.ui.valuesource.groups;

import org.museautomation.core.values.descriptor.*;

import java.util.*;

/**
 * A group of step types - for organizing the presentation in the UI.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ValueSourceTypeGroup
    {
    String getName();
    List<ValueSourceDescriptor> getValueSourceTypes();

    void add(ValueSourceDescriptor type, String group_name);

    List<ValueSourceTypeGroup> getSubGroups();

    void add(ValueSourceTypeGroup group);
    }

