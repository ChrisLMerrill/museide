package org.musetest.ui.extend.actions;

import org.slf4j.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class UndoableAction extends BaseEditAction
    {
    // implement the undo here
    protected abstract boolean undoImplementation();

    public boolean execute(UndoStack undo_stack)
        {
        _undo_stack = undo_stack;
        boolean success;
        try
	        {
	        success = executeImplementation();
	        if (!success)
	            LOG.error("command failed: " + getClass().getSimpleName());
	        }
	    catch (Exception e)
		    {
		    LOG.error("command " + getClass().getSimpleName() + " failed with an exception", e);
		    success = false;
		    }

        if (success && _undo_stack != null)
            _undo_stack.push(this);

        return success;
        }

    public boolean undo()
        {
        boolean success = undoImplementation();
        if (!success)
            LOG.error("Unable to undo a command: " + getClass().getSimpleName());
        return success;
        }

    protected UndoStack _undo_stack;

    final static Logger LOG = LoggerFactory.getLogger(UndoableAction.class);
    }


