package org.museautomation.ui.ide.navigation.resources;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProjectNodeFactories
    {
    static ProjectNodeFactory getCurrentFactory()
        {
        return new ResourceTypeProjectNodeFactory();
        }
    }