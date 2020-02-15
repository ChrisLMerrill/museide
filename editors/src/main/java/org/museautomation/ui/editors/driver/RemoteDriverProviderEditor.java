package org.museautomation.ui.editors.driver;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.selenium.*;
import org.museautomation.selenium.providers.*;
import org.museautomation.ui.extend.actions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RemoteDriverProviderEditor implements WebdriverProviderEditor
    {
    public RemoteDriverProviderEditor()
        {
        _grid = new GridPane();
        _grid.setHgap(5);
        }

    @Override
    public void edit(WebDriverProvider provider, UndoStack undo)
        {
        if (!(provider instanceof RemoteDriverProvider))
            throw new IllegalArgumentException("can only edit a RemoteDriverProvider");
        _provider = (RemoteDriverProvider) provider;
        _undo = undo;

        createFields();
        }

    @Override
    public Node getNode()
        {
        return _grid;
        }

    public void dispose()
        {
        if (_provider != null)
            _provider.removeChangeListener(_listener);
        }

    @Override
    public WebDriverProvider getProvider()
        {
        return _provider;
        }

    private void createFields()
        {
        Platform.runLater(() ->
            {
            _grid.getChildren().removeAll();

            Label label = new Label("URL:");
            _grid.add(label, 0, 0);
            GridPane.setHgrow(label, Priority.NEVER);

            _url_field = new TextField();
            GridPane.setHgrow(_url_field, Priority.ALWAYS);
            _url_field.setId(URL_FIELD_ID);
            _url_field.setText(_provider.getUrl());
            _grid.add(_url_field, 1, 0);

            _url_field.focusedProperty().addListener((observable, was_focused, is_focused) ->
                {
                if (was_focused && !Objects.equals(_url_field.getText(), _provider.getUrl()))
                    new ChangeRemoteDriverProviderUrl(_provider, _url_field.getText()).execute(_undo);
                });
            _provider.addChangeListener(_listener);
            });
        }

    private RemoteDriverProvider _provider;
    private UndoStack _undo;
    private TextField _url_field;

    private GridPane _grid;

    RemoteDriverProvider.ChangeListener _listener = (old_url, new_url) ->
        {
        if (!Objects.equals(new_url, _url_field.getText()))
            _url_field.setText(new_url);
        };

    public final static String URL_FIELD_ID = "rdpe-url";
    }


