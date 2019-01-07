package org.musetest.ui.step;

import org.musetest.core.step.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.edit.stack.*;
import org.musetest.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepEditorStack extends EditorStack
    {
    public StepEditorStack(StepEditContext context, EditInProgress<Object> edit)
        {
        super(edit, context.getUndo());
        _context = context;
        }

    public void setStep(StepConfiguration step)
        {
        _editor = new MultimodeStepEditor(_context, step);
        push(_editor, _context.getProject().getStepDescriptors().get(step).getName());
        }

    @Override
    protected void notifyEditCommit()
        {
        // notify caller that an edit is complete.
        _edit.commit(null);
        }

    public void destroy()
	    {
	    _editor.destroy();
	    }

    private final StepEditContext _context;
    private MultimodeStepEditor _editor;
    }


