package org.museautomation.ui.steptest.execution;

import org.museautomation.core.*;
import org.museautomation.core.events.*;
import org.museautomation.core.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("WeakerAccess")  // used by GUI
public class PauseAfterStep implements MuseEventListener
    {
    public PauseAfterStep(InteractiveTestRunner runner)
        {
        _runner = runner;
        _step = null;
        }

    public PauseAfterStep(InteractiveTestRunner runner, StepConfiguration step)
        {
        _runner = runner;
        _step = step;
        }

    @Override
    public void eventRaised(MuseEvent event)
        {
        if (event.getTypeId().equals(EndStepEventType.TYPE_ID))
            {
            if (_step == null || _step.getStepId().equals(event.getAttribute(StepEventType.STEP_ID)))
                {
                _runner.requestPause();
                _runner.getExecutionContext().removeEventListener(this);
                }
            }
        }

    private final InteractiveTestRunner _runner;
    private final StepConfiguration _step;
    }