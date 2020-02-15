package org.museautomation.ui.steptest.actions;

import org.museautomation.core.execution.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StopTestEnabler extends TestControlEnabler
    {
    public StopTestEnabler(InteractiveTestController controller)
        {
        super(controller);
        }

    @Override
    public void stateChanged(InteractiveTestState state)
        {
        setEnabled(!state.equals(InteractiveTestState.IDLE));
        }
    }


