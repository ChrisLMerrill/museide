package org.musetest.ui.steptest.actions;

import org.musetest.core.execution.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class TestControlEnabler extends NodeEnabler implements InteractiveTestStateListener
    {
    public TestControlEnabler(InteractiveTestController controller)
        {
        _controller = controller;
        stateChanged(_controller.getState());
        _controller.addListener(this);
        }

    @Override
    public void shutdown()
        {
        _controller.removeListener(this);
        }

    private final InteractiveTestController _controller;
    }


