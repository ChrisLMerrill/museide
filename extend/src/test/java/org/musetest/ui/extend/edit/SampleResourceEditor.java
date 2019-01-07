package org.musetest.ui.extend.edit;

import javafx.scene.*;
import javafx.scene.control.*;
import org.musetest.core.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class SampleResourceEditor extends BaseResourceEditor
	{
	@Override
	protected Parent getEditorArea()
		{
		return _edit_area;
		}

	@Override
	public void editResource(MuseProject project, MuseResource resource)
		{
		_edit_area.setText(((SampleResource) resource).getEditText());
		}

	@Override
	public boolean canEdit(MuseResource resource)
		{
		return true;
		}

	@Override
	public ValidationStateSource getValidationStateSource()
		{
		return null;
		}

	@Override
	public void requestFocus()
		{
		_edit_area.requestFocus();
		}

	private TextArea _edit_area = new TextArea();
	}
