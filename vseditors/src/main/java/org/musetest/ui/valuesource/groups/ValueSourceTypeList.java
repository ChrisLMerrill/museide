package org.musetest.ui.valuesource.groups;

import org.musetest.core.values.descriptor.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceTypeList implements ValueSourceTypeGroup
    {
    public ValueSourceTypeList(String name)
        {
        _name = name;
        }

    @Override
    public String getName()
        {
        return _name;
        }

    @Override
    public List<ValueSourceDescriptor> getValueSourceTypes()
        {
        return _types;
        }

    @Override
    public void add(ValueSourceDescriptor type, String group_name)
        {
        if (MuseValueSourceTypeGroup.DONT_SHOW.equals(group_name))
            return;

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

            ValueSourceTypeGroup subgroup = getSubgroup(sub_group_name);
            subgroup.add(type, remaining_name);
            }
        }

    @Override
    public List<ValueSourceTypeGroup> getSubGroups()
        {
        return _sub_groups;
        }

    /**
     * Gets the subgroup with the provided name. Creates it if it does not exist.
     */
    private ValueSourceTypeGroup getSubgroup(String name)
        {
        for (ValueSourceTypeGroup group : _sub_groups)
            if (name.equals(group.getName()))
                return group;

        ValueSourceTypeGroup group = new ValueSourceTypeList(name);
        _sub_groups.add(group);
        return group;
        }

    @Override
    public void add(ValueSourceTypeGroup group)
        {
        _sub_groups.add(group);
        }

    private String _name;
    private List<ValueSourceDescriptor> _types = new ArrayList<>();
    private List<ValueSourceTypeGroup> _sub_groups = new ArrayList<>();
    }


