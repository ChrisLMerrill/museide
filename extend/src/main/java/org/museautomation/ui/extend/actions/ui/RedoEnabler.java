package org.museautomation.ui.extend.actions.ui;

import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RedoEnabler extends NodeEnabler implements UndoStackListener
    {
    public RedoEnabler(UndoStack stack)
        {
        _stack = stack;
        _stack.addListener(this);
        stackSizeChanged(stack);
        }

    @Override
    public void stackSizeChanged(UndoStack stack)
        {
        setEnabled(!_stack.isRedoEmpty());
        }

    @Override
    public void shutdown()
        {
        _stack.removeListener(this);
        }

    private UndoStack _stack;
    }


