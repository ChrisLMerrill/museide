package org.musetest.ui.valuesource;

import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.ui.extend.edit.step.*;

/**
 * An editor for value sources within a step editor.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ValueSourceInStepInlineEditor extends ValueSourceEditor
	{
	ExtensionSelectionPriority getPriority(StepEditContext context, StepConfiguration parent, String source_name);
	}
