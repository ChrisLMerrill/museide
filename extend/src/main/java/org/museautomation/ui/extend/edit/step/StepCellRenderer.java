package org.museautomation.ui.extend.edit.step;

import org.museautomation.core.*;
import org.museautomation.core.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused")
public interface StepCellRenderer
	{
	void configure(StepEditContext context, StepConfiguration step);
	ExtensionSelectionPriority getPriority();
	String getStepLabel();
	}

