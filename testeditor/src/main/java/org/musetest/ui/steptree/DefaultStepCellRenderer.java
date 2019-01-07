package org.musetest.ui.steptree;

import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.ui.extend.edit.step.*;

@SuppressWarnings("unused")  // instantiated by reflection
public class DefaultStepCellRenderer implements StepCellRenderer
	{
	@Override
	public void configure(StepEditContext context, StepConfiguration step)
		{
		_context = context;
		_step = step;
		}

	@Override
	public ExtensionSelectionPriority getPriority()
		{
		return ExtensionSelectionPriority.BUILTIN;
		}

	@Override
	public String getStepLabel()
		{
		return _context.getProject().getStepDescriptors().get(_step).getShortDescription(_step);
		}

	private StepEditContext _context;
	private StepConfiguration _step;
	}
