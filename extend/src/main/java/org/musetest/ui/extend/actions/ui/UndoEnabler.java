package org.musetest.ui.extend.actions.ui;

import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.edit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UndoEnabler extends NodeEnabler implements UndoStackListener
    {
    public UndoEnabler(UndoStack stack)
        {
        _stack = stack;
        _stack.addListener(this);
        stackSizeChanged(stack);
        }

    @Override
    public void stackSizeChanged(UndoStack stack)
        {
        setEnabled(!_stack.isEmpty());
        }

    @Override
    public void shutdown()
        {
        _stack.removeListener(this);
        }

    private UndoStack _stack;
    }


