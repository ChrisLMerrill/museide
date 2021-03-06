package org.museautomation.ui.ide.navigation.resources.nodes;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ResourceTreeNodeListener
    {
    void childAdded(int index, ResourceTreeNode child);
    void childRemoved(int index, ResourceTreeNode child);
    }


