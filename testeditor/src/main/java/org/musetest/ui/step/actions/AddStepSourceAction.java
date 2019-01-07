package org.musetest.ui.step.actions;

import org.musetest.core.step.*;
import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class AddStepSourceAction extends UndoableAction
    {
    public AddStepSourceAction(StepConfiguration step, String source_name, ValueSourceConfiguration new_source)
        {
        _step = step;
        _source_name = source_name;
        _added_source = new_source;
        }

    @Override
    protected boolean executeImplementation()
        {
        _step.addSource(_source_name, _added_source);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _step.removeSource(_source_name);
        return true;
        }

    private final StepConfiguration _step;
    private final String _source_name;
    private ValueSourceConfiguration _added_source;
    }


