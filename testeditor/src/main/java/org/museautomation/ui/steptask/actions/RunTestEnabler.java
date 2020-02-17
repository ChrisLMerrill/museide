package org.museautomation.ui.steptask.actions;

import org.museautomation.core.execution.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RunTestEnabler extends TestControlEnabler
    {
    public RunTestEnabler(InteractiveTestController controller)
        {
        super(controller);
        }

    @Override
    public void stateChanged(InteractiveTaskState state)
        {
        setEnabled(state.equals(InteractiveTaskState.IDLE) || state.equals(InteractiveTaskState.PAUSED));
        }
    }


