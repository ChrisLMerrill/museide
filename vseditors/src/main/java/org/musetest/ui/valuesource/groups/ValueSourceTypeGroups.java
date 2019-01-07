package org.musetest.ui.valuesource.groups;

import org.musetest.core.*;
import org.musetest.core.values.descriptor.*;

import java.util.*;

/**
 * Finds/builds the available value source type groups for the project.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceTypeGroups
    {
    public static ValueSourceTypeGroup get(MuseProject project)
        {
        ValueSourceTypeGroup group = all_groups.get(project);
        if (group == null)
            {
            // lookup all types
            group = new ValueSourceTypeList("all");
            for (ValueSourceDescriptor descriptor : project.getValueSourceDescriptors().findAll())
                group.add(descriptor, descriptor.getGroupName());

            all_groups.put(project, group);
            }
        return group;
        }

    private static Map<MuseProject, ValueSourceTypeGroup> all_groups = new HashMap<>();
    }


