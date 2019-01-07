package org.musetest.ui.extend.edit.step;

import org.musetest.core.*;
import org.musetest.core.step.*;

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

