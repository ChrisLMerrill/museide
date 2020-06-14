package org.museautomation.ui.ide.navigation.resources;

import org.museautomation.core.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ProjectNodeFactory
    {
    ProjectNode createProjectNode(MuseProject project);
    }