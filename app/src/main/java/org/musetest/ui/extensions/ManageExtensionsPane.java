package org.musetest.ui.extensions;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import org.musetest.core.*;
import org.musetest.core.resource.storage.*;
import org.musetest.extensions.*;
import org.musetest.extensions.install.*;
import org.musetest.extensions.registry.*;
import org.musetest.ui.extend.components.GraphicNodeBuilder;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.i4s.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ManageExtensionsPane
    {
    public ManageExtensionsPane(MuseProject project)
        {
        _project = project;
        _grid = new GridPane();
        _grid.setPrefWidth(800);
        _grid.setPrefHeight(600);

        Label title = new Label("Manage Project Extensions");
        title.getStyleClass().add("heading");
        GridPane.setHgrow(title, Priority.ALWAYS);
        _grid.add(title, 0, 0);

        Button close = Buttons.createClose(20);
        _grid.add(close, 1, 0);
        GridPane.setValignment(close, VPos.CENTER);
        GridPane.setMargin(close, new Insets(5));
        close.setOnAction(event ->
            {
            if (_listener != null)
                _listener.cancelButtonPressed();
            });

        _grid.setOnKeyPressed(event ->
            {
            if (event.getCode().equals(KeyCode.ESCAPE) && _listener != null)
                _listener.cancelButtonPressed();
            });

        _installed_panel = new ExtensionInfoListPanel();
        Tab installed_tab = new Tab("Installed");
        ScrollPane installed_scroller = new ScrollPane();
        installed_tab.setContent(installed_scroller);
        installed_scroller.setFitToWidth(true);
        installed_scroller.setFitToHeight(true);
        installed_scroller.setContent(_installed_panel.getNode());
        installed_tab.closableProperty().setValue(false);
        _installed_panel.customizePanels((panel, info) ->
            {
            final Button uninstall_button = Buttons.createRemove(24);
            uninstall_button.setOnAction(event ->
                {
                final ExtensionRegistry registry = new ExtensionRegistry(project);
                final List<ExtensionRegistryEntry> entries;
                try
                    {
                    entries = registry.listExtensions();
                    }
                catch (ExtensionRegistryException e)
                    {
                    LOG.error("Unable to list extensions due to: " + e.getMessage());
                    // TODO: indicate an error in the UI
                    return;
                    }
                for (ExtensionRegistryEntry entry : entries)
                    if (entry.getInfo().getVersionId() == info.getVersionId())
                        {
                        File installed_location = ((FolderIntoMemoryResourceStorage) _project.getResourceStorage()).getBaseLocation();
                        ExtensionUninstaller uninstaller = ExtensionUninstallers.findUninstaller(entry, installed_location);

                        // TODO show status
                        uninstaller.uninstall(entry, installed_location, registry);

                        // TODO check the uninstall result for errors and show to user
                        }
                showInstalledExtensions();
                showAvailableExtensions();
                });
            panel.setButtons(uninstall_button);
            });


        _available_panel = new ExtensionInfoListPanel();
        SplitPane divider = new SplitPane();
        divider.setOrientation(Orientation.VERTICAL);
        divider.setDividerPositions(0.5);

        // create loading panel
        BorderPane spinner_pane = new BorderPane();
        Label loading_message = new Label("Looking for extensions...");
        loading_message.setGraphic(GraphicNodeBuilder.getInstance().getImageResourceView("progress-spinner.gif"));
        loading_message.setAlignment(Pos.CENTER);
        spinner_pane.setCenter(loading_message);
        divider.getItems().add(spinner_pane);

        Tab available_tab = new Tab("Available");
        available_tab.setContent(divider);
        available_tab.closableProperty().setValue(false);
        _available_panel.customizePanels((panel, info) ->
            {
            final Button install_button = Buttons.createAdd(24);
            install_button.setOnAction(event ->
                {
                final File location = ((FolderIntoMemoryResourceStorage) _project.getResourceStorage()).getBaseLocation();
                final ExtensionInstallLog log = new ExtensionInstallLog(location);

                if (_install_log_panel != null)
                    divider.getItems().remove(_install_log_panel.getNode());
                _install_log_panel = new ExtensionInstallLogDisplayPanel(log);
                divider.getItems().add(_install_log_panel.getNode());

                new Thread(() ->
                    {
                    ExtensionInstallers.find(info).install(info, location, new ExtensionRegistry(project), log);
                    Platform.runLater(() ->
                        {
                        showInstalledExtensions();
                        showAvailableExtensions();
                        });
                    }).start();
                });
            panel.setButtons(install_button);
            });

        TabPane tabs = new TabPane(installed_tab, available_tab);

        _grid.add(tabs, 0, 1, 2, 1);
        GridPane.setHgrow(tabs, Priority.ALWAYS);
        GridPane.setVgrow(tabs, Priority.ALWAYS);

        showInstalledExtensions();
        if (getInstalledExtensions().size() == 0)
            tabs.getSelectionModel().select(1);

        new Thread(() ->
            {
            _available = I4sClient.get().getAvailableExtensions();
            Platform.runLater(() ->
                {
                divider.getItems().clear();
                divider.getItems().add(_available_panel.getNode());
                });
            showAvailableExtensions();
            }).start();
        }

    private void showAvailableExtensions()
        {
        final List<ExtensionInfo> installed = getInstalledExtensions();
        List<ExtensionInfo> showable = new ArrayList<>();
        for (ExtensionInfo extension : _available)
            {
            if (!installed.contains(extension))
                showable.add(extension);
            }
        _available_panel.setInfo(showable);
        }

    private void showInstalledExtensions()
        {
        _installed_panel.setInfo(getInstalledExtensions());
        }

    private List<ExtensionInfo> getInstalledExtensions()
        {
        final ExtensionRegistry registry = new ExtensionRegistry(_project);
        List<ExtensionInfo> installed = new ArrayList<>();
        if (registry != null)
            {
            try
                {
                for (ExtensionRegistryEntry entry : registry.listExtensions())
                    installed.add(entry.getInfo());
                }
            catch (ExtensionRegistryException e)
                {
                LOG.error("Unable to list extensions", e);
                }
            }
        return installed;
        }

    public Node getNode()
        {
        return _grid;
        }

    public void setButtonListener(ManageExtensionsPaneButtonListener listener)
        {
        _listener = listener;
        }


    public interface ManageExtensionsPaneButtonListener
        {
        void cancelButtonPressed();
        }

    private final MuseProject _project;
    private List<ExtensionInfo> _available;

    private GridPane _grid;
    private ExtensionInfoListPanel _installed_panel;

    private ExtensionInfoListPanel _available_panel;
    private ExtensionInstallLogDisplayPanel _install_log_panel;

    private ManageExtensionsPaneButtonListener _listener;

    private final static Logger LOG = LoggerFactory.getLogger(ManageExtensionsPane.class);
    }


