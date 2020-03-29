package org.museautomation.ui.step.groups;

import org.museautomation.core.*;
import org.museautomation.core.step.descriptor.*;

import java.util.*;

/**
 * Finds/builds the available step type groups for the project.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepTypeGroups
    {
    public static StepTypeGroup get(MuseProject project)
        {
        StepTypeGroup group = all_groups.get(project);
        if (group == null)
            {
            // lookup all types
            group = new StepTypeList("all");
            for (StepDescriptor descriptor : project.getStepDescriptors().findAll())
                group.add(descriptor, descriptor.getGroupName());

            group.sortAll();
            all_groups.put(project, group);
            }
        return group;
        }

    private static Map<MuseProject, StepTypeGroup> all_groups = new HashMap<>();
    }


