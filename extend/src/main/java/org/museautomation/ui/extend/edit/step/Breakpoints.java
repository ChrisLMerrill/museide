package org.museautomation.ui.extend.edit.step;

import org.museautomation.core.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface Breakpoints
    {
    void setBreakpoint(StepConfiguration step);
    void clearBreakpoint(StepConfiguration step);
    boolean isBreakpoint(StepConfiguration step);
    }