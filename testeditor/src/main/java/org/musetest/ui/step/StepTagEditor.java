package org.musetest.ui.step;

import javafx.scene.*;
import org.musetest.core.step.*;
import org.musetest.core.step.events.*;
import org.musetest.core.util.*;
import org.musetest.ui.extend.edit.tags.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepTagEditor implements ChangeEventListener
	{
	public StepTagEditor(StepEditContext context)
		{
		_context = context;
		_editor.setDeleteListener(tag -> new RemoveTagFromStepAction(_step, tag).execute(_context.getUndo()));
		_editor.setAddListener(tag -> new AddTagToStepAction(_step, tag).execute(_context.getUndo()));

		new NodeParentChangeListener(_editor.getNode())
			{
			@Override
			public void onRemove()
				{
				if (_step != null)
					_step.removeChangeListener(StepTagEditor.this);
				}

			@Override
			public void onAdd()
				{
				if (_step != null)
					_step.addChangeListener(StepTagEditor.this);
				}
			};
		}

	public void setStep(StepConfiguration step)
		{
		if (_step != null)
			_step.removeChangeListener(this);
		_step = step;
		if (_step != null)
			_step.addChangeListener(this);
		_editor.setTags(step);
		}

	public Node getNode()
		{
		return _editor.getNode();
		}

	@Override
	public void changeEventRaised(ChangeEvent e)
		{
		if (e instanceof MetadataChangeEvent && ((MetadataChangeEvent)e).getName().equals(StepConfiguration.META_TAGS))
			_editor.refresh();
		}

	private TagsEditor _editor = new TagsEditor();
	private StepConfiguration _step;
	private final StepEditContext _context;
	}


