package org.musetest.ui.extend.edit.metadata;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import org.musetest.core.util.*;
import org.musetest.ui.extend.edit.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MetadataEditor implements ChangeEventListener
	{
	public void setMetadata(ContainsMetadata metadata)
		{
		if (_metadata != null)
			_metadata.removeChangeListener(this);
		_metadata = metadata;
		_metadata.addChangeListener(this);
		buildFields();
		}

	private void buildFields()
		{
		_nodes = new ArrayList<>();

		MetadataLabel.RemoveListener remove_adapter = null;
		if (_remove_listener != null)
			remove_adapter = (name, value) -> _remove_listener.dataRemoved(name, value);

		for (String name : _metadata.getMetadataFieldNames())
			{
			if (!_filter_system || isNotReservedName(name))
				_nodes.add(new MetadataLabel(name, _metadata.getMetadataField(name), remove_adapter).getNode());
			}

		if (_add_listener != null)
			{
			Hyperlink add_link = Buttons.createLinkWithIcon("add", "FA:PLUS", null, "add a tag", ContentDisplay.LEFT);
			add_link.setPadding(new Insets(0, 2, 0, 10));
			add_link.getStyleClass().add(ADD_BUTTON_STYLE);
			add_link.setOnAction(event ->
				{
				_body.getChildren().remove(add_link);

				TextField entry_field = new TextField();
				entry_field.setPrefColumnCount(15);
				entry_field.getStyleClass().add(ADD_FIELD_SYTLE);
				_body.getChildren().add(entry_field);
				entry_field.requestFocus();
				entry_field.setOnAction(action ->
					{
					if (entry_field.getText().length() > 0)
						{
						String[] parts = entry_field.getText().split("=");
						if (parts.length == 2
							&& parts[0].length() > 0
							&& isNotReservedName(parts[0])
							&& parts[1].length() > 0)
							{
							Object value;
							try
								{
								value = Long.valueOf(parts[1]);
								}
							catch (NumberFormatException e)
								{
								String str = parts[1];
								switch (str.toLowerCase())
									{
									case "true":
										value = true;
										break;
									case "false":
										value = false;
										break;
									default:
										value = str;
										break;
									}
								}
							_add_listener.dataAdded(parts[0], value);
							_body.getChildren().remove(entry_field);
							_body.getChildren().add(add_link);
							}
						}
					else
						{
						_body.getChildren().remove(entry_field);
						_body.getChildren().add(add_link);
						}
					});
				entry_field.setOnKeyPressed(key_event ->
					{
					if (key_event.getCode() == KeyCode.ESCAPE)
						{
						_body.getChildren().remove(entry_field);
						_body.getChildren().add(add_link);
						}
					});
				});
			_nodes.add(add_link);
			}

		Platform.runLater(() ->
			{
			_body.getChildren().clear();
			_body.getChildren().addAll(_nodes);
			});
		}

	private boolean isNotReservedName(String name)
		{
		char first = name.charAt(0);
		return Character.isAlphabetic(first);
		}

	public Node getNode()
		{
		return _body;
		}

	public void setFilterReservedNames(boolean filter)
		{
		if (filter != _filter_system)
			{
			_filter_system = filter;
			buildFields();
			}
		}

	public void setAddListener(AddListener listener)
		{
		_add_listener = listener;
		}

	public void setRemoveListener(RemoveListener listener)
		{
		_remove_listener = listener;
		}

	public void refresh()
		{
		buildFields();
		}

	@Override
	public void changeEventRaised(ChangeEvent event)
		{
		refresh();
		}

	private final HBox _body = new HBox();
	private ContainsMetadata _metadata;
	private boolean _filter_system = true;
	private AddListener _add_listener = null;
	private RemoveListener _remove_listener = null;
	private ArrayList<Node> _nodes;
	public static final String ADD_BUTTON_STYLE = "metadata-editor-add-button";
	public static final String ADD_FIELD_SYTLE = "metadata-editor-add-field";

	public interface AddListener
		{
		void dataAdded(String name, Object value);
		}

	public interface RemoveListener
		{
		void dataRemoved(String name, Object value);
		}
	}


