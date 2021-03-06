package org.museautomation.ui.steptree;

import org.museautomation.core.*;
import org.museautomation.core.step.*;
import org.museautomation.ui.extend.edit.step.*;

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
        String desc = _context.getProject().getStepDescriptors().get(_step).getShortDescription(_step);
        if (desc.length() > 120)
            desc = desc.substring(0, 120) + "...";
        return desc;
		}

	private StepEditContext _context;
	private StepConfiguration _step;
	}
