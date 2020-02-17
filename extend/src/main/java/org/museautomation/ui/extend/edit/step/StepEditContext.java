package org.museautomation.ui.extend.edit.step;

import org.museautomation.ui.extend.actions.*;
import org.museautomation.core.*;
import org.museautomation.core.step.*;
import org.museautomation.core.task.*;

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


