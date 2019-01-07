package org.musetest.ui.extend.edit;

import javafx.scene.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class NodeEnabler
    {
    public void setEnabled(boolean enabled)
        {
        if (_component == null)
            _inital_state = enabled;
        else
            _component.setDisable(!enabled);
        }

    public void setNode(Node component)
        {
        _component = component;
        if (_inital_state != null)
            _component.setDisable(!_inital_state);
        }

    public abstract void shutdown();

    Boolean _inital_state = null;
    Node _component;
    }


