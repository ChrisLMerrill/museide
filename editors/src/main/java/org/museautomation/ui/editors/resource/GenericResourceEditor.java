package org.museautomation.ui.editors.resource;

import javafx.scene.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.generic.*;
import org.museautomation.ui.extend.edit.*;

@SuppressWarnings("unused")  // instantiated via reflection
public class GenericResourceEditor extends BaseResourceEditor
	{
	@Override
	public void editResource(MuseProject project, MuseResource resource)
		{
		super.editResource(project, resource);
		_config_editor = new GenericResourceConfigEditPane(project, getUndoStack());
		_config_editor.setResource((GenericResourceConfiguration) resource);
		}

	@Override
	protected Parent getEditorArea()
		{
		return _config_editor.getNode();
		}

	@Override
	public boolean canEdit(MuseResource resource)
		{
		return resource instanceof GenericResourceConfiguration;
		}

	@Override
	public ValidationStateSource getValidationStateSource()
		{
		return null;
		}

	@Override
	public void requestFocus()
		{
		_config_editor.requestFocus();
		}

	private GenericResourceConfigEditPane _config_editor;
	}