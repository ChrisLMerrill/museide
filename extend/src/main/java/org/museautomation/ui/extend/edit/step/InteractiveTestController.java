package org.museautomation.ui.extend.edit.step;

import org.museautomation.core.*;
import org.museautomation.core.execution.*;

/**
 * Beginnings of work to pull out a useful interface from the base implementation
 *
 * Manages the state of an interactive execution of a stepped test and summarizes test events
 * into InteractiveTestState change events.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface InteractiveTestController
    {
    boolean run(SteppedTaskProvider test_provider);
    void runOneStep(SteppedTaskProvider provider);
    void stop();
    void pause();
    void resume();
    void step();

    InteractiveTaskState getState();
    TaskResult getResult();
    Breakpoints getBreakpoints();

    /**
     * Get the current TestRunner, or null if there is no TestRunner active.
     */
    TaskRunner getTestRunner();

    void addListener(InteractiveTaskStateListener listener);
    void removeListener(InteractiveTaskStateListener listener);
    }
