package org.museautomation.ui.ide.navigation.resources.nodes;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProjectNodeFactories
    {
    public static ProjectNodeFactory getCurrentFactory()
        {
        return new ResourceTypeProjectNodeFactory();
        }
    }