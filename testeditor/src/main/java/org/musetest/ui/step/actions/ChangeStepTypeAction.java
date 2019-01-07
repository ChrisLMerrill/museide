package org.musetest.ui.step.actions;

import org.musetest.core.step.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ChangeStepTypeAction extends UndoableAction
    {
    public ChangeStepTypeAction(StepConfiguration step, String new_type)
        {
        _step = step;
        _new_type = new_type;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_type = _step.getType();
        _step.setType(_new_type);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _step.setType(_old_type);
        _old_type = null;
        return true;
        }

    private final StepConfiguration _step;
    private final String _new_type;
    private String _old_type;
    }


