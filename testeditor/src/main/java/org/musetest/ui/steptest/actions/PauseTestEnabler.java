package org.musetest.ui.steptest.actions;

import org.musetest.core.execution.*;
import org.musetest.ui.extend.edit.step.*;

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
    public void stateChanged(InteractiveTestState state)
        {
        setEnabled(state.equals(InteractiveTestState.RUNNING));
        }
    }


