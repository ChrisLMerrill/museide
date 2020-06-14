package org.museautomation.ui.ide.navigation.resources.nodes;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceNodeFactories
    {
    public static ResourceTreeNodeFactory getDefault()
        {
        return new ResourceTypeProjectNodeFactory();
        }

    public static List<ResourceTreeNodeFactory> getFactories()
        {
        List<ResourceTreeNodeFactory> factories = new ArrayList<>();
        factories.add(new ResourceTypeProjectNodeFactory());
        factories.add(new ResourcePathProjectNodeFactory());
        return factories;
        }

    public static ResourceTreeNodeFactory getByName(String name)
        {
        for (ResourceTreeNodeFactory factory : getFactories())
            if (name.equals(factory.getName()))
                return factory;
        return getDefault();
        }
    }