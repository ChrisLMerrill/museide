package org.museautomation.ui.extend.edit.tags;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.core.util.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TagsLabel
	{
	public TagsLabel()
		{
		_body.getStyleClass().add(TAGS_STYLE);
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
		showTags();
		}

	private void showTags()
		{
		final List<Label> labels = new ArrayList<>();
		for (String tag : _tags.getTags())
			{
			Label label = new Label(tag);
			label.getStyleClass().add(TAG_STYLE);
			labels.add(label);
			}
		Platform.runLater(() ->
			{
			_body.getChildren().clear();
			_body.getChildren().addAll(labels);
			});
		}

	private HBox _body = new HBox();
	private Taggable _tags;

	public final static String TAGS_STYLE = "tags-label";
	public final static String TAG_STYLE = "tag-label";
	}