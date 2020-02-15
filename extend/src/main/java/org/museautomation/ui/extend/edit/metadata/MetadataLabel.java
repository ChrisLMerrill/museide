package org.museautomation.ui.extend.edit.metadata;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.ui.extend.edit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MetadataLabel
	{
	public MetadataLabel(String name, Object value, RemoveListener listener)
		{
		_body.getStyleClass().add(LABEL_CLASS);
		_body.setSpacing(5);
		Label label = new Label();
		label.setText(String.format("%s=%s", name, value.toString()));
		_body.getChildren().add(label);
		if (listener != null)
			{
			Button delete_button = Buttons.createRemove();
			delete_button.getStyleClass().add(REMOVE_BUTTON_CLASS);
			delete_button.setOnAction(event -> listener.removeButtonPressed(name, value));
			_body.getChildren().add(delete_button);
			}
		}

	public Node getNode()
		{
		return _body;
		}

	private final HBox _body = new HBox();

	public final static String LABEL_CLASS = "metadata-label";
	public static final String REMOVE_BUTTON_CLASS = "metadata-remove-button";

	public interface RemoveListener
		{
		void removeButtonPressed(String name, Object value);
		}
	}


