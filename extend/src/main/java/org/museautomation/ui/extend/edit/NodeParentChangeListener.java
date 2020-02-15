package org.museautomation.ui.extend.edit;

import javafx.scene.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class NodeParentChangeListener
    {
    protected NodeParentChangeListener(final Node component)
        {
        component.parentProperty().addListener((observable, old_value, new_value) ->
            {
            if (old_value != null && new_value == null)
                onRemove();
            else if (old_value == null && new_value != null)
                onAdd();
            });
        }

    public void onRemove() {}
    public void onAdd() {}
    }


