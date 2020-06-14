package org.museautomation.ui.ide.navigation.resources.nodes;

import org.museautomation.core.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ResourceTreeNodeFactory
    {
    String getName();
    ResourceTreeNode createNode(MuseProject project);
    }