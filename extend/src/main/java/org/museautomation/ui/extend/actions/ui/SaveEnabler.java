package org.museautomation.ui.extend.actions.ui;

import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class SaveEnabler extends NodeEnabler implements UndoStackListener, ValidationStateListener
    {
    public SaveEnabler(UndoStack stack, ValidationStateSource source)
        {
        _stack = stack;
        _stack.addListener(this);
        stackSizeChanged(stack);

        _valid = source;
        if (_valid == null)
            _valid = new ValidationStateSource()
                {
                @Override
                public boolean isValid()
                    {
                    return true;
                    }

                @Override
                public void addValidationStateListener(ValidationStateListener listener)
                    {

                    }

                @Override
                public void removeValidationStateListener(ValidationStateListener listener)
                    {

                    }
                };
        else
            {
            _valid.addValidationStateListener(this);
            validationStateChanged(_valid, _valid.isValid());
            }
        }

    @Override
    public void stackSizeChanged(UndoStack stack)
        {
        updateState();
        }

    @Override
    public void shutdown()
        {
        _stack.removeListener(this);
        _valid.removeValidationStateListener(this);
        }

    @Override
    public void validationStateChanged(ValidationStateSource source, boolean valid)
        {
        updateState();
        }

    private void updateState()
        {
        setEnabled(!_stack.isEmpty() && _valid.isValid());
        }

    private UndoStack _stack;
    private ValidationStateSource _valid;
    }


