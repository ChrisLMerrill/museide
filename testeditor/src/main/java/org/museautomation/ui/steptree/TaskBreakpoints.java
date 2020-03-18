package org.museautomation.ui.steptree;

import org.museautomation.core.step.*;
import org.museautomation.ui.extend.edit.step.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TaskBreakpoints implements Breakpoints
    {
    @Override
    public void setBreakpoint(StepConfiguration step)
        {
        System.out.println("set breakpoint on step " + step.getStepId());
        _breakpoints.add(step);
        }

    @Override
    public boolean isBreakpoint(StepConfiguration step)
        {
        return _breakpoints.contains(step);
        }

    private Set<StepConfiguration> _breakpoints = new HashSet<>();
    }