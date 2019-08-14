package org.musetest.ui.ide.navigation;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.resource.storage.*;
import org.musetest.ui.extend.glyphs.*;
import org.musetest.ui.ide.*;
import org.musetest.ui.ide.navigation.resources.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class NavigatorView
    {
    public NavigatorView(ResourceEditors editors, Stage stage)
        {
        _editors = editors;
        _stage = stage;
        activateInitialUI();
        }

    private void activateInitialUI()
        {
        VBox rows = new VBox();
        rows.setAlignment(Pos.CENTER);
        rows.setSpacing(20);

        Button open_project = new Button("Open Project...", Glyphs.create("FA:FOLDER_OPEN_ALT"));
        rows.getChildren().add(open_project);
        open_project.setOnAction(event ->
            {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose project folder");

            List<RecentProject> projects = RecentProjectSettings.get().getProjects();
            if (projects.size() > 0)
                chooser.setInitialDirectory(new File(projects.get(0).getLocation()).getParentFile());

            File folder = chooser.showDialog(_stage.getOwner());
            if (folder != null)
                openProject(folder);
            });

        RecentProjectsPanel recent = new RecentProjectsPanel(this);
        rows.getChildren().add(recent.getNode());

        _root.setCenter(rows);
        }

    void openProject(File folder)
        {
        _project = new SimpleProject(new FolderIntoMemoryResourceStorage(folder, "com.webperformance.muse.measurements"), folder.getName());
        _project.open();
        activateNavigationUI();
        RecentProjectSettings.get().addProject(folder.getPath());
        }

    private void activateNavigationUI()
        {
        ProjectNavigator navigator = new ProjectNavigator(_project, _editors);
        _root.setCenter(navigator.getNode());
        }

    public Node getNode()
        {
        return _root;
        }

    private Stage _stage;
    private final ResourceEditors _editors;
    private final BorderPane _root = new BorderPane();
    private MuseProject _project = null;
    }