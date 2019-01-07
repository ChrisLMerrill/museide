package org.musetest.ui.extend.edit.step;

import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.core.test.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused")  // public API
public interface StepEditContext
	{
	MuseProject getProject();
	StepConfiguration getParentStep();
	StepEditContext getParentContext();
	InteractiveTestController getController();
	UndoStack getUndo();
	void addShuttable(Shuttable shuttable);
	void closeShuttables();
	}


