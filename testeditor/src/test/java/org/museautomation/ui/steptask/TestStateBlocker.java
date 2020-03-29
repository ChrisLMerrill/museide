package org.museautomation.ui.steptask;

import org.junit.jupiter.api.*;
import org.museautomation.core.execution.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * Blocks until the test achieves the desired state
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TestStateBlocker implements InteractiveTaskStateListener
    {
    TestStateBlocker(InteractiveTestController controller)
        {
        _controller = controller;
        _controller.addListener(this);
        }

    @SuppressWarnings("SameParameterValue")
    synchronized void blockUntil(InteractiveTaskState state)
        {
        if (_controller.getState().equals(state))
            return;

        _state = state;
        try
            {
            wait(2000);
            }
        catch (InterruptedException e)
            {
            Assertions.fail("gave up waiting after 2 sec...it probably failed if this was used (as intended) for a should-finish-more-or-less-instantly operation in a unit test.");
            }
        }

    @Override
    public synchronized void stateChanged(InteractiveTaskState state)
        {
        if (state.equals(_state))
            notify();
        }

    private InteractiveTestController _controller;
    private InteractiveTaskState _state;
    }