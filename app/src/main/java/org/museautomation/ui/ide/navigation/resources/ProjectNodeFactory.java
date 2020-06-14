package org.museautomation.ui.ide.navigation.resources;

import org.museautomation.core.*;
import org.museautomation.ui.ide.navigation.resources.nodes.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ProjectNodeFactory
    {
    ProjectNode createProjectNode(MuseProject project);
    }