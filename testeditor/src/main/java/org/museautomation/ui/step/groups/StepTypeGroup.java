package org.museautomation.ui.step.groups;

import org.museautomation.core.step.descriptor.*;

import java.util.*;

/**
 * A group of step types - for organizing the presentation in the UI.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface StepTypeGroup
    {
    String getName();
    List<StepDescriptor> getStepTypes();

    void add(StepDescriptor type, String group_name);

    List<StepTypeGroup> getSubGroups();

    void add(StepTypeGroup group);
    }

