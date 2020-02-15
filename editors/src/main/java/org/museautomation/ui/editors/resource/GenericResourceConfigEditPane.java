package org.museautomation.ui.editors.resource;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.ui.valuesource.map.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.generic.*;
import org.museautomation.core.values.descriptor.*;
import org.museautomation.ui.extend.actions.*;

public class GenericResourceConfigEditPane
	{
	public GenericResourceConfigEditPane(MuseProject project, UndoStack undo)
		{
		_project = project;
		_undo = undo;

		_box.setPadding(new Insets(5));
		_box.setSpacing(5);

		_editor = new ValueSourceMapEditor(_project, _undo);
		}

	public Parent getNode()
		{
		return _box;
		}

	public void setResource(GenericResourceConfiguration config)
		{
		_config = config;
		_editor.setSource(_config.parameters(), SubsourceDescriptor.getSubsourceDescriptors(_config.getClass()));

		buildUI();
		}

	@SuppressWarnings("WeakerAccess")  // users may override to add more UI sections.
	protected void buildUI()
		{
		final Label resource_type = new Label(_config.getType().getName());
		resource_type.getStyleClass().add("larger");
		_box.getChildren().add(resource_type);

		final Label resource_description = new Label(_config.getType().getDescriptor().getShortDescription());
		_box.getChildren().add(resource_description);

		Separator separator = new Separator();
		separator.setValignment(VPos.CENTER);
		_box.getChildren().add(separator);

		final Label section_label = new Label("Parameters");
		section_label.getStyleClass().add("littlelarger");
		_box.getChildren().add(section_label);

		_box.getChildren().add(_editor.getNode());
		}

	public void requestFocus()
		{
		_editor.requestFocus();
		}

	protected final MuseProject _project;
	protected final UndoStack _undo;
	private GenericResourceConfiguration _config;

	private ValueSourceMapEditor _editor;
	private VBox _box = new VBox();
	}
