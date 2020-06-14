package org.museautomation.ui.ide.navigation.resources;

import javafx.scene.*;
import net.christophermerrill.FancyFxTree.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.ui.extend.javafx.*;
import org.museautomation.ui.ide.navigation.resources.nodes.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProjectResourceTree
    {
    public ProjectResourceTree(MuseProject project, ResourceTreeOperationHandler ops_handler)
        {
        _tree_view = new FancyTreeView<>(ops_handler, true);
        _tree_view.getStylesheets().add(Styles.getDefaultTreeStyles());
        _root_node = ProjectNodeFactories.getCurrentFactory().createProjectNode(project);
        project.addResourceListener(new ProjectResourceListener()
            {
            @Override
            public void resourceAdded(ResourceToken<MuseResource> added)
                {
                _root_node.notifyResourceAdded(added);
                }

            @Override
            public void resourceRemoved(ResourceToken<MuseResource> removed)
                {
                _root_node.notifyResourceRemoved(removed);
                }
            });
        _tree_view.setRoot(new ResourceTreeNodeFacade(_root_node));
        _tree_view.getRoot().setExpanded(true);
        _tree_view.setShowRoot(false);
        }

    public Node getNode()
        {
        return _tree_view;
        }

    public void requestFocus()
        {
        _tree_view.requestFocus();
        }

    public void expandAll()
        {
        _tree_view.expandAll();
        }

    public FancyTreeView<ResourceTreeNodeFacade> getTreeView()
        {
        return _tree_view;
        }

    public ProjectNode getRootNode()
        {
        return _root_node;
        }

    private final ProjectNode _root_node;
    private FancyTreeView<ResourceTreeNodeFacade> _tree_view;
    }