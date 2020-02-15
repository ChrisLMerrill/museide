package org.museautomation.ui.step.actions;

import org.museautomation.core.step.*;
import org.museautomation.ui.extend.actions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class InsertStepsAction extends UndoableAction
	{
	public InsertStepsAction(StepConfiguration parent_step, StepConfiguration new_step, int index)
		{
		_parent_step = parent_step;
		if (new_step == null)
			_new_steps = Collections.emptyList();
		else
			_new_steps = Collections.singletonList(new_step);
		_index = index;
		}

	public InsertStepsAction(StepConfiguration parent_step, List<StepConfiguration> steps, int index)
		{
		_parent_step = parent_step;
		_new_steps = steps;
		_index = index;
		}

	@Override
	protected boolean executeImplementation()
		{
		for (int i = 0; i < _new_steps.size(); i++)
			_parent_step.addChild(_index + i, _new_steps.get(i));
		return true;
		}

	@Override
	protected boolean undoImplementation()
		{
		// remove in the reverse order they were added. Just for consistency
		List<StepConfiguration> children = new ArrayList<>();
		children.addAll(_new_steps);
		Collections.reverse(children);
		for (StepConfiguration child : children)
			_parent_step.removeChild(child);
		return true;
		}

	final StepConfiguration _parent_step;
	List<StepConfiguration> _new_steps;
	int _index;
	}