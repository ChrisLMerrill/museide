package org.museautomation.ui.step.actions;

import org.museautomation.core.step.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class DeleteStepSourceAction extends UndoableAction
    {
    public DeleteStepSourceAction(StepConfiguration step, String source_name)
        {
        _step = step;
        _source_name = source_name;
        }

    @Override
    protected boolean executeImplementation()
        {
        _deleted_source = _step.removeSource(_source_name);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _step.addSource(_source_name, _deleted_source);
        return true;
        }

    private final StepConfiguration _step;
    private final String _source_name;
    private ValueSourceConfiguration _deleted_source;
    }


