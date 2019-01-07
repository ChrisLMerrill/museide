package org.musetest.ui.extensions;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.extensions.*;
import org.musetest.ui.ide.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExtensionInfoPanel
    {
    public ExtensionInfoPanel()
        {
        _main = new BorderPane();
        _main.getStyleClass().add("editing-container");

        VBox left_area = new VBox();
        _main.setLeft(left_area);
        BorderPane.setMargin(left_area, new Insets(0, 20, 0, 0));

        _name_and_version = new Label();
        _name_and_version.getStyleClass().add("larger");
        _name_and_version.setId(NAME_LABEL_ID);
        _name_and_version.setMinWidth(Control.USE_PREF_SIZE);
        left_area.getChildren().add(_name_and_version);

        _author_description = new Hyperlink();
        _author_description.getStyleClass().add("littlelarger");
        _author_description.setId(EXTENSION_AUTHOR_LABEL_ID);
        _author_description.setMinWidth(Control.USE_PREF_SIZE);
        left_area.getChildren().add(_author_description);

        _version_description = new Label();
        _version_description.setId(VERSION_DESCRIPTION_LABEL_ID);
        _version_description.setMinWidth(Control.USE_PREF_SIZE);
        left_area.getChildren().add(_version_description);

        _extension_description = new Label();
        _extension_description.setId(EXTENSION_DESCRIPTION_LABEL_ID);
        _extension_description.setWrapText(true);
        _main.setCenter(_extension_description);
        }

    public Node getNode()
        {
        return _main;
        }

    public void setInfo(ExtensionInfo info)
        {
        Platform.runLater(() ->
            {
            _name_and_version.setText(info.getExtensionName() + " " + info.getVersionName());
            _author_description.setText(info.getExtensionAuthor());
            _author_description.setOnAction(event -> IdeApplication.getInstance().getHostServices().showDocument(info.getAuthorUrl()));
            _version_description.setText(info.getVersionDescription());
            _extension_description.setText(info.getExtensionDesc());
            });
        }

    public void setButtons(Node... buttons)
        {
        Platform.runLater(() ->
            {
            if (_button_area == null)
                {
                _button_area = new HBox();
                _button_area.setAlignment(Pos.CENTER);
                _main.setRight(_button_area);
                GridPane.setHgrow(_button_area, Priority.NEVER);
                }
            for (Node button : buttons)
                _button_area.getChildren().add(button);
            });
        }

    private BorderPane _main;
    private HBox _button_area;
    private Label _name_and_version;
    private Hyperlink _author_description;
    private Label _version_description;
    private Label _extension_description;

    public static final String NAME_LABEL_ID = "omue-eip-name";
    public static final String VERSION_DESCRIPTION_LABEL_ID = "omue-eip-verdesc";
    public static final String EXTENSION_DESCRIPTION_LABEL_ID = "omue-eip-extdesc";
    public static final String EXTENSION_AUTHOR_LABEL_ID = "omue-eip-extauth";
    }


