package org.musetest.ui.step.actions;

import org.musetest.core.step.*;

import java.util.*;

/**
 * Inserts StepConfiguration(s) into another StepConfiguration (as children).
 * It supplies a reference StepConfiguration for adjusting the index.
 * But rather than placing at the specified index, it adjust the index
 * based on an offset from the index of the supplied StepConfiguration.
 *
 * This is solely useful for the drag-move operation, where the target index could possibly
 * change as a result of the first part of the move operation.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class InsertStepsRelativeAction extends InsertStepsAction
	{
	public InsertStepsRelativeAction(StepConfiguration root_step, List<StepConfiguration> steps, StepConfiguration target_step, int index_offset)
		{
		super(root_step, steps, 0);
		_target_step = target_step;
		_index_offset = index_offset;
		}

	@Override
	protected boolean executeImplementation()
		{
		if (_parent_step.getChildren() != null)
			_index = _parent_step.getChildren().indexOf(_target_step) + _index_offset;
		return super.executeImplementation();
		}

	private StepConfiguration _target_step;
	private int _index_offset;
	}


