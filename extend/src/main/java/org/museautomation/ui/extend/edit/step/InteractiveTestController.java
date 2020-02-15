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
    boolean run(SteppedTestProvider test_provider);
    void runOneStep(SteppedTestProvider provider);
    void stop();
    void pause();
    void resume();
    void step();

    InteractiveTestState getState();
    TestResult getResult();

    /**
     * Get the current TestRunner, or null if there is no TestRunner active.
     */
    TestRunner getTestRunner();

    void addListener(InteractiveTestStateListener listener);
    void removeListener(InteractiveTestStateListener listener);
    }
