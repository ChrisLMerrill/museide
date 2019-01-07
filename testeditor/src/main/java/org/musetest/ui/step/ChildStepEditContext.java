package org.musetest.ui.step;

import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.core.test.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ChildStepEditContext implements StepEditContext
	{
	public ChildStepEditContext(StepEditContext parent_context, StepConfiguration parent_step)
		{
		_parent_context = parent_context;
		_parent_step = parent_step;
		}

	@Override
	public void closeShuttables()
		{
		_parent_context.closeShuttables();
		}

	@Override
	public void addShuttable(Shuttable shuttable)
		{
		_parent_context.addShuttable(shuttable);
		}

	@Override
	public InteractiveTestController getController()
		{
		return _parent_context.getController();
		}

	@Override
	public StepConfiguration getParentStep()
		{
		return _parent_step;
		}

	@Override
	public MuseProject getProject()
		{
		return _parent_context.getProject();
		}

	@Override
	public StepEditContext getParentContext()
		{
		return _parent_context;
		}

	@Override
	public UndoStack getUndo()
		{
		return _parent_context.getUndo();
		}

	private final StepEditContext _parent_context;
	private final StepConfiguration _parent_step;
	}
