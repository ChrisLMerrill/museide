package org.musetest.ui.extend.actions;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class BaseEditAction
    {
    // implement the action here
    protected abstract boolean executeImplementation();

    public boolean execute(UndoStack undo_stack)
        {
        return executeImplementation();
        }
    }


