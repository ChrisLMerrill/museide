package org.museautomation.ui.step.groups;

import org.museautomation.core.step.descriptor.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepTypeList implements StepTypeGroup
    {
    public StepTypeList(String name)
        {
        _name = name;
        }

    @Override
    public String getName()
        {
        return _name;
        }

    @Override
    public List<StepDescriptor> getStepTypes()
        {
        return _types;
        }

    @Override
    public void add(StepDescriptor type, String group_name)
        {
        if (group_name == null || group_name.trim().length() == 0)
            _types.add(type);
        else
            {
            String sub_group_name = group_name;
            String remaining_name = null;
            int dot = group_name.indexOf('.');
            if (dot >= 0)
                {
                sub_group_name = group_name.substring(0, dot);
                remaining_name = group_name.substring(dot + 1);
                }

            StepTypeGroup subgroup = getSubgroup(sub_group_name);
            subgroup.add(type, remaining_name);
            }
        }

    @Override
    public List<StepTypeGroup> getSubGroups()
        {
        return _sub_groups;
        }

    /**
     * Gets the subgroup with the provided name. Creates it if it does not exist.
     */
    private StepTypeGroup getSubgroup(String name)
        {
        for (StepTypeGroup group : _sub_groups)
            if (name.equals(group.getName()))
                return group;

        StepTypeGroup group = new StepTypeList(name);
        _sub_groups.add(group);
        return group;
        }

    @Override
    public void add(StepTypeGroup group)
        {
        _sub_groups.add(group);
        }

    private String _name;
    private List<StepDescriptor> _types = new ArrayList<>();
    private List<StepTypeGroup> _sub_groups = new ArrayList<>();
    }


