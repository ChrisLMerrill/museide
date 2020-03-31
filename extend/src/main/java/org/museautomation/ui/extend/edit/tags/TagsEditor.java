package org.museautomation.ui.extend.edit.tags;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import org.museautomation.core.metadata.*;
import org.museautomation.ui.extend.edit.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TagsEditor
	{
	public TagsEditor()
		{
		_body.getStyleClass().add(TagsLabel.TAGS_STYLE);
		}

	public Node getNode()
		{
		return _body;
		}

	public void setTags(Taggable tags)
		{
		if (_tags == tags)
			return;
		_tags = tags;

		if (tags == null)
			Platform.runLater(() -> _body.getChildren().clear());
		else
			showTags();
		}

	public void refresh()
		{
		showTags();
		}

	private void showTags()
		{
		final List<Node> tag_nodes = new ArrayList<>();
		for (String tag : _tags.tags().getTags())
			{
			Label label = new Label(tag);
			if (_delete_listener == null)
				{
				label.getStyleClass().add(TagsLabel.TAG_STYLE);
				tag_nodes.add(label);
				}
			else
				{
				HBox tag_box = new HBox();
				tag_box.setSpacing(5);
				tag_box.getStyleClass().add(TagsLabel.TAG_STYLE);
				tag_box.getChildren().add(label);
				Button delete_button = Buttons.createRemove(DELETE_ID);
				delete_button.setOnAction(event -> _delete_listener.deleteTag(tag));
				tag_box.getChildren().add(delete_button);
				tag_nodes.add(tag_box);
				}
			}
		if (_add_listener != null)
			{
			Hyperlink add_link = Buttons.createLinkWithIcon("add", "FA:PLUS", ADD_BUTTON_ID, "add a tag", ContentDisplay.LEFT);
			add_link.setPadding(new Insets(0, 2, 0, 10));
			add_link.setOnAction(event ->
				{
				_body.getChildren().remove(add_link);

				TextField entry_field = new TextField();
				entry_field.setPrefColumnCount(15);
				entry_field.setId(ADD_FIELD_ID);
				_body.getChildren().add(entry_field);
				entry_field.requestFocus();
				entry_field.setOnAction(action ->
					{
					if (entry_field.getText().length() > 0)
						_add_listener.addTag(entry_field.getText());
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
			tag_nodes.add(add_link);
			}
		Platform.runLater(() ->
			{
			_body.getChildren().clear();
			_body.getChildren().addAll(tag_nodes);
			});
		}

	public void setDeleteListener(DeleteListener listener)
		{
		_delete_listener = listener;
		}

	public void setAddListener(AddListener listener)
		{
		_add_listener = listener;
		}

	private HBox _body = new HBox();
	private Taggable _tags;
	private DeleteListener _delete_listener = null;
	private AddListener _add_listener = null;

	public final static String ADD_BUTTON_ID = "omuieet-add-id";
	public final static String ADD_FIELD_ID = "omuieet-add-field-id";
	public final static String DELETE_ID = "omuieet-delete-id";

	public interface DeleteListener
		{
		void deleteTag(String tag);
		}

	public interface AddListener
		{
		void addTag(String tag);
		}
	}