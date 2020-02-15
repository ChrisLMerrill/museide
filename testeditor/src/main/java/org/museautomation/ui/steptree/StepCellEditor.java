package org.museautomation.ui.steptree;

import javafx.scene.*;
import net.christophermerrill.FancyFxTree.*;
import org.museautomation.ui.step.inline.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepCellEditor implements FancyTreeCellEditor
	{
	StepCellEditor(StepEditContext context, StepConfigurationFacade facade, boolean start_editor_in_full_mode)
		{
		EditInProgress edit = new EditInProgress()
			{
			@Override
			public void cancel()
				{
				_cell.cancelEdit();
				}

			@Override     
			public void commit(Object target)
				{
				_cell.commitEdit(facade);
				}
			};

		_cell_editor = new InlineStepEditorContainerImplementation(context, facade.getModelNode(), edit, start_editor_in_full_mode);
		_cell_editor.getNode().getStyleClass().add(STYLE_CLASS);
		}

	@Override
	public Node getNode()
		{
		return _cell_editor.getNode();
		}

	@Override
	public void setCell(FancyTreeCell cell)
		{
		_cell = cell;
		}

    @Override
    public void cancelEdit() { }

    public void requestFocus()
		{
		_cell_editor.requestFocus();
		}

	public void destroy()
		{
		_cell_editor.destroy();
		}

	private InlineStepEditorContainerImplementation _cell_editor;
	private FancyTreeCell _cell;

	public final static String STYLE_CLASS = "step-cell-editor";
	}


