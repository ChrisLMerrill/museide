package org.musetest.ui.editors.browser;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.*;
import org.musetest.selenium.*;
import org.musetest.ui.extend.edit.*;
import org.openqa.selenium.remote.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class BrowserCapabilitiesEditor extends BaseResourceEditor
    {
    public BrowserCapabilitiesEditor()
        {
        _grid = new GridPane();
        _grid.setPadding(new Insets(5));
        _grid.setVgap(5);
        _grid.setPrefWidth(400);
        _grid.setPrefHeight(200);
        createFields();
        }

    private void createFields()
        {
        _grid.add(new Label("Name"), 0, 0);

        _name_field = new ComboBox<>();
        _grid.add(_name_field, 1, 0);
        _name_field.setEditable(true);
        _name_field.setId(NAME_FIELD_ID);
        _name_field.getSelectionModel().selectedItemProperty().addListener((observable, old_value, new_value) ->
            {
            if (!Objects.equals(_name_field.getValue(), _browser.getName()))
                new ChangeBrowserNameAction(_browser, new_value).execute(getUndoStack());
            });

        _grid.add(new Label("Version"), 0, 1);
        _version_field = new ComboBox<>();
        _version_field.setEditable(true);
        _version_field.setId(VERSION_FIELD_ID);
        _version_field.getSelectionModel().selectedItemProperty().addListener((observable, old_value, new_value) ->
            {
            if (!Objects.equals(_version_field.getValue(), _browser.getVersion()))
                new ChangeBrowserVersionAction(_browser, new_value).execute(getUndoStack());
            });
        _grid.add(_version_field, 1, 1);

        _grid.add(new Label("Platform"), 0, 2);
        _platform_field = new ComboBox<>();
        _platform_field.setEditable(true);
        _platform_field.setId(PLATFORM_FIELD_ID);
        _platform_field.getSelectionModel().selectedItemProperty().addListener((observable, old_value, new_value) ->
            {
            if (!Objects.equals(_platform_field.getValue(), _browser.getPlatform()))
                new ChangeBrowserPlatformAction(_browser, new_value).execute(getUndoStack());
            });
        _grid.add(_platform_field, 1, 2);
        }

    @Override
    public void dispose()
        {
        _grid.getChildren().removeAll();
        if (_browser != null)
            _browser.removeChangeListener(_listener);
        }

    @Override
    protected Parent getEditorArea()
        {
        return _grid;
        }

    @Override
    public boolean canEdit(MuseResource resource)
        {
        return resource instanceof SeleniumBrowserCapabilities;
        }

    @Override
    public void editResource(MuseProject project, MuseResource resource)
        {
        super.editResource(project, resource);
        if (_browser != null)
            _browser.removeChangeListener(_listener);
        _browser = (SeleniumBrowserCapabilities) resource;
        fillFields();
        }

    private void fillFields()
        {
        Platform.runLater(() ->
            {
            _name_field.getItems().addAll("firefox", "chrome", "internet explorer", "safari", "opera", "MicrosoftEdge", "iPad", "iPhone", "android", "phantomjs");
            _name_field.getSelectionModel().select(_browser.getName());

            _version_field.getItems().addAll("ANY");
            _version_field.getSelectionModel().select(_browser.getVersion());

            _platform_field.getItems().addAll("ANY", "WINDOWS", "XP", "VISTA", "MAC", "LINUX", "UNIX", "ANDROID");
            _platform_field.getSelectionModel().select(_browser.getPlatform());
            _browser.addChangeListener(_listener);
            });
        }

    @Override
    public ValidationStateSource getValidationStateSource()
        {
        return null;
        }

    @Override
    public void requestFocus()
        {
        _name_field.requestFocus();
        }

    private SeleniumBrowserCapabilities _browser;

    private GridPane _grid;
    private ComboBox<String> _name_field;
    private ComboBox<String> _platform_field;
    private ComboBox<String> _version_field;
    private SBCChangeListener _listener = new SBCChangeListener();

    private class SBCChangeListener implements SeleniumBrowserCapabilities.ChangeListener
        {
        @Override
        public void capabilityChanged(String name, Object old_value, Object new_value)
            {
            if (CapabilityType.BROWSER_NAME.equals(name))
                {
                if (!Objects.equals(new_value, _name_field.getValue()))
                    _name_field.valueProperty().set(new_value.toString());
                }
            else if (CapabilityType.VERSION.equals(name))
                {
                if (!Objects.equals(new_value, _version_field.getValue()))
                    _version_field.valueProperty().set(new_value.toString());
                }
            else if (CapabilityType.PLATFORM.equals(name))  // TODO research if PLATFORM_NAME should be used
                {
                if (!Objects.equals(new_value, _platform_field.getValue()))
                    _platform_field.valueProperty().set(new_value.toString());
                }
            }
        }

    public final static String NAME_FIELD_ID = "bce-name";
    public final static String PLATFORM_FIELD_ID = "bce-platform";
    public final static String VERSION_FIELD_ID = "bce-version";
    }


