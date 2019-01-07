package org.musetest.ui.step.actions;

import org.musetest.core.step.*;
import org.musetest.ui.extend.actions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class DeleteStepsAction extends UndoableAction
	{
	public DeleteStepsAction(StepConfiguration root_step, StepConfiguration delete_step)
		{
		_root_step = root_step;
		_steps_to_delete = Collections.singletonList(delete_step);
		}

	public DeleteStepsAction(StepConfiguration root_step, List<StepConfiguration> delete_steps)
		{
		_root_step = root_step;
		_steps_to_delete = pruneDescendents(delete_steps);
		}

    /**
     * Ensure that no item in the list is a descendent of another in the list
     */
    private List<StepConfiguration> pruneDescendents(List<StepConfiguration> steps)
        {
        List<StepConfiguration> result = new ArrayList<>();
        for (StepConfiguration candidate : steps)
            {
            // if the step is a descendent of any other step in the list, don't add to the result
            boolean skip = false;
            for (StepConfiguration step : steps)
                if (step.contains(candidate))
                    {
                    skip = true;
                    break;
                    }
            if (!skip)
                result.add(candidate);
            }
        return result;
        }

	@Override
	protected boolean executeImplementation()
		{
		_deleted_steps = new ArrayList<>();
		for (int i = 0; i < _steps_to_delete.size(); i++)
			{
			final StepConfiguration to_delete = _steps_to_delete.get(i);
			StepConfiguration parent = _root_step.findParentOf(to_delete);
			StepDeleter deleted = new StepDeleter(parent, to_delete);
			_deleted_steps.add(deleted);
			deleted.remove();
			}
		return true;
		}

	@Override
	protected boolean undoImplementation()
		{
		while (!_deleted_steps.isEmpty())
			_deleted_steps.remove(_deleted_steps.size() - 1).undo();
		return true;
		}

	protected final StepConfiguration _root_step;
	protected final List<StepConfiguration> _steps_to_delete;
	protected List<StepDeleter> _deleted_steps;

	protected class StepDeleter
		{
		public StepDeleter(StepConfiguration parent, StepConfiguration deleted)
			{
			_parent = parent;
			_deleted = deleted;
			}

		public void remove()
			{
			_index = _parent.removeChild(_deleted);
			}

		public void undo()
			{
			_parent.addChild(_index, _deleted);
			}

		StepConfiguration _parent;
		StepConfiguration _deleted;
		int _index;
		}
	}