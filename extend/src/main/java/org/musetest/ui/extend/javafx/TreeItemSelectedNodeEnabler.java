package org.musetest.ui.extend.javafx;

import javafx.collections.*;
import javafx.scene.control.*;
import org.musetest.ui.extend.edit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TreeItemSelectedNodeEnabler extends NodeEnabler implements ListChangeListener<TreeItem>
    {
    public TreeItemSelectedNodeEnabler(TreeView tree)
        {
        _tree = tree;
        tree.getSelectionModel().getSelectedItems().addListener(this);
        }

    @Override
    public void onChanged(Change<? extends TreeItem> change)
        {
        setEnabled(change.getList().size() == 1);
        }

    @Override
    public void shutdown()
        {
        _tree.getSelectionModel().getSelectedItems().removeListener(this);
        }

    private TreeView _tree;
    }


