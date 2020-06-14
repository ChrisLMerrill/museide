package org.museautomation.ui.ide.navigation.resources.nodes;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceNodeFactories
    {
    public static ResourceTreeNodeFactory getCurrentFactory()
        {
        // TODO save the last-used for a project?
        return new ResourceTypeProjectNodeFactory();
//        return new ResourcePathProjectNodeFactory();
        }

    public static List<ResourceTreeNodeFactory> getFactories()
        {
        List<ResourceTreeNodeFactory> factories = new ArrayList<>();
        factories.add(new ResourceTypeProjectNodeFactory());
        factories.add(new ResourcePathProjectNodeFactory());
        return factories;
        }
    }