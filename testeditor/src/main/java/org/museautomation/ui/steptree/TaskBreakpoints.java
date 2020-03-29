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
        _breakpoints.add(step);
        notifyListeners(step);
        }

    @Override
    public void clearBreakpoint(StepConfiguration step)
        {
        _breakpoints.remove(step);
        notifyListeners(step);
        }

    @Override
    public boolean isBreakpoint(StepConfiguration step)
        {
        return _breakpoints.contains(step);
        }

    private void notifyListeners(StepConfiguration step)
        {
        for (BreakpointsListener listener : _listeners)
            listener.breakpointChanged(step);
        }

    @Override
    public void addBreakpointsListener(BreakpointsListener listener)
        {
        _listeners.add(listener);
        }

    @Override
    public void removeBreakpointsListener(BreakpointsListener listener)
        {
        _listeners.remove(listener);
        }

    private Set<StepConfiguration> _breakpoints = new HashSet<>();
    private Set<BreakpointsListener> _listeners = new HashSet<>();
    }