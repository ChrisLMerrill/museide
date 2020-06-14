package org.museautomation.ui.ide.navigation.resources;

import org.museautomation.core.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProjectNode extends ResourceTreeNode
    {
    ProjectNode(MuseProject project, List<ResourceTreeNode> nodes)
        {
        super(project);
        _children.addAll(nodes);
        }

    @Override
    public String getTreeLabel()
        {
        return _project.getName();
        }

    @Override
    public String getTreeIconGlyphName()
        {
        return "FA:FOLDER_ALT";
        }

    @Override
    public List<ResourceTreeNode> getChildren()
        {
        return _children;
        }

    private final List<ResourceTreeNode> _children = new ArrayList<>();
    }


