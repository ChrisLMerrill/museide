package org.museautomation.ui.step.actions;

import org.museautomation.core.step.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ChangeStepMetadataAction extends UndoableAction
    {
    public ChangeStepMetadataAction(StepConfiguration step, String new_description)
        {
        _step = step;
        _new_description = new_description;
        }

    @Override
    protected boolean executeImplementation()
        {
        StepConfiguration step = _step;
        _old_description = (String) step.getMetadataField(StepConfiguration.META_DESCRIPTION);
        step.setMetadataField(StepConfiguration.META_DESCRIPTION, _new_description);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _step.setMetadataField(StepConfiguration.META_DESCRIPTION, _old_description);
        _old_description = null;
        return true;
        }

    private final String _new_description;
    private String _old_description;
    private final StepConfiguration _step;
    }


