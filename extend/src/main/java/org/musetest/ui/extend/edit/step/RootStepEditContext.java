package org.musetest.ui.extend.edit.step;

import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.core.test.*;
import org.musetest.ui.extend.actions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused")  // used by StepTree implementation
public class RootStepEditContext implements StepEditContext
	{
	public RootStepEditContext(MuseProject project, UndoStack undo, InteractiveTestController controller)
		{
		_project = project;
		_undo = undo;
		_controller = controller;
		}

	public void closeShuttables()
		{
		for (Shuttable shuttable : _shuttables)
			shuttable.shutdown();
		_shuttables.clear();
		}

	public void addShuttable(Shuttable shuttable)
		{
		_shuttables.add(shuttable);
		}

	public InteractiveTestController getController()
		{
		return _controller;
		}

	@Override
	public MuseProject getProject()
		{
		return _project;
		}

	@Override
	public StepConfiguration getParentStep()
		{
		return null;
		}

	@Override
	public StepEditContext getParentContext()
		{
		return null;
		}

	@Override
	public UndoStack getUndo()
		{
		return _undo;
		}

	private MuseProject _project;
	private UndoStack _undo;
	private InteractiveTestController _controller;
	private Set<Shuttable> _shuttables = new HashSet<>();
	}