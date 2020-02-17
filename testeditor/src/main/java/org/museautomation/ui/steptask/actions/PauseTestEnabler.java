package org.museautomation.ui.steptask.actions;

import org.museautomation.core.execution.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class PauseTestEnabler extends TestControlEnabler
    {
    public PauseTestEnabler(InteractiveTestController controller)
        {
        super(controller);
        }

    @Override
    public void stateChanged(InteractiveTaskState state)
        {
        setEnabled(state.equals(InteractiveTaskState.RUNNING));
        }
    }


