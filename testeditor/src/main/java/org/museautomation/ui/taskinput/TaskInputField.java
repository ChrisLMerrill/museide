package org.museautomation.ui.taskinput;

import javafx.scene.*;
import org.museautomation.core.task.*;
import org.museautomation.core.task.input.*;

/**
 * Interface for field UI widgets that can provide input to a task, based on the TaskInput
 * that is passed to them.
 *
 * They must provide feedback regarding the state of satisfaction of the TaskInput based
 * on input from the user.  For example, a non-required input is satisfied when no input
 * is provided, but would not be satisfied if the input does not match the specified input
 * type (such as non-numeric entry for a numeric input).
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface TaskInputField
    {
    void setTaskInput(TaskInput input);
    void useDefault();
    ResolvedTaskInput getResolvedInput();

    boolean isSatisfied();
    Node getNode();

    interface TaskInputFieldListener
        {
        void satisfactionChanged(boolean satisified);
        }

    void addListener(TaskInputFieldListener listener);
    void removeListener(TaskInputFieldListener listener);
    }