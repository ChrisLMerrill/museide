package org.museautomation.ui.ide.navigation;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.museautomation.ui.ide.*;
import org.museautomation.ui.ide.navigation.resources.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.resource.storage.*;
import org.museautomation.ui.extend.glyphs.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

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
        for (ProjectOpenListener listener : OPEN_LISTENERS)
            listener.projectWillBeOpened(folder);
        _project = new SimpleProject(new FolderIntoMemoryResourceStorage(folder), folder.getName());
        _project.open();
        activateNavigationUI();
        RecentProjectSettings.get().addProject(folder.getPath());
        }

    private void activateNavigationUI()
        {
        ProjectNavigator navigator = new ProjectNavigator(_project, _editors);
        navigator.setProjectCloser(() ->
            {
            if (_editors.hasUnsavedChanges())
                {
                final AtomicReference<String> error = new AtomicReference<>();
                boolean close = SaveChangesDialog.createShowAndWait(
                    () -> error.set(_editors.saveAllChanges()),
                    _editors::revertAllChanges);

                if (!close || error.get() != null)
                    {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Unable to save a resource");
                    alert.setContentText(error.get());
                    alert.showAndWait();
                    return;
                    }
                }
            _editors.closeAll();
            _project = null;
            activateInitialUI();
            });
        _root.setCenter(navigator.getNode());

        detectAndWarnFileExtensionChange();
        }

    // TODO This can be removed in the future (a few months after Dec 2019?)
    // TODO Not before FromJsonFileResourceFactory is changed to stop reading .json files.
    private void detectAndWarnFileExtensionChange()
        {
        int count = 0;
        List<ResourceToken> tokens = _project.getResourceStorage().findResources(ResourceQueryParameters.forAllResources());
        for (ResourceToken token : tokens)
            {
            Object meta = token.metadata().getMetadataField("filename");
            if (meta != null && meta.toString().endsWith(".json"))
                count++;
            }
        if (count > 0)
            {
            final int info_count = count;
            Platform.runLater(() ->
                {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Notice");
                alert.setHeaderText(info_count + " resources found with .json extension in filename");
                alert.setContentText("Muse files will be moving from the .json filename extension to .muse.\nNew items created in MuseIDE will use the new extension.\nSoon, project resources will no longer be read from .json files.\nUse these commands to rename all the files in a folder:\n- Windows: ren *.json *.muse\n- Mac: for f in *.json; do mv $f `basename $f .json`.muse; done;");
                alert.show();
                });
            }
        }

    public Node getNode()
        {
        return _root;
        }

    private Stage _stage;
    private final ResourceEditors _editors;
    private final BorderPane _root = new BorderPane();
    private MuseProject _project = null;

    public interface ProjectCloser
        {
        void close();
        }

    public interface ProjectOpenListener
        {
        void projectWillBeOpened(File folder);
        }

    private final static List<ProjectOpenListener> OPEN_LISTENERS = new ArrayList<>();

    @SuppressWarnings("unused")  // public API
    public static void addProjectOpenListener(ProjectOpenListener listener)
        {
        OPEN_LISTENERS.add(listener);
        }
    }