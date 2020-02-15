package org.museautomation.ui.valuesource;

import org.museautomation.core.*;
import org.museautomation.core.step.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * An editor for value sources within a step editor.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ValueSourceInStepInlineEditor extends ValueSourceEditor
	{
	ExtensionSelectionPriority getPriority(StepEditContext context, StepConfiguration parent, String source_name);
	}
