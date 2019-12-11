package org.musetest.ui.ide.navigation.resources;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import net.christophermerrill.ShadowboxFx.*;
import org.controlsfx.control.*;
import org.jetbrains.annotations.*;
import org.musetest.core.*;
import org.musetest.core.resource.*;
import org.musetest.core.resource.storage.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.components.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.glyphs.*;
import org.musetest.ui.extend.javafx.*;
import org.musetest.ui.ide.*;
import org.musetest.ui.ide.navigation.*;
import org.musetest.ui.ide.navigation.resources.actions.*;
import org.musetest.ui.seideimport.*;
import org.musetest.ui.settings.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProjectNavigator
    {
    public ProjectNavigator(MuseProject project, ResourceEditors editors)
        {
        _project = project;
        _editors = editors;
        }

    public Scene getScene()
        {
        if (_scene == null)
            _scene = createScene();
        return _scene;
        }

    private Scene createScene()
        {
        Scene scene = new Scene(getNode());
        scene.getStylesheets().add(getClass().getResource("/Trees.css").toExternalForm());
        return scene;
        }

    public Parent getNode()
        {
        if (_node == null)
            {
            BorderPane border_pane = new BorderPane();
            _node = new NotificationPane(border_pane);

            ResourceTreeOperationHandler ops_handler = new ResourceTreeOperationHandler(_project, _editors, _undo);
            _tree = new ProjectResourceTree(_project, ops_handler);
            border_pane.setCenter(_tree.getNode());

            Label project_label = new Label(" " + _project.getName());
            project_label.setTooltip(new Tooltip("Project location: " + ((FolderIntoMemoryResourceStorage)_project.getResourceStorage()).getBaseLocation().getAbsolutePath()));
            Styles.addStyle(project_label, "heading");

            GridPane label_box = new GridPane();
            label_box.getChildren().add(project_label);
            GridPane.setHgrow(project_label, Priority.ALWAYS);
            GridPane.setHalignment(project_label, HPos.LEFT);

            if (_project_closer != null)
                {
                Button close_button = Buttons.createCancel(20);
                close_button.setTooltip(new Tooltip("Close Project"));
                label_box.add(close_button, label_box.getChildren().size(), 0);
                GridPane.setHgrow(close_button, Priority.NEVER);
                GridPane.setHalignment(close_button, HPos.RIGHT);
                GridPane.setMargin(close_button, new Insets(5));
                close_button.setOnAction((event) ->
                    {
                    LOG.info("Close the project!");
                    _project_closer.close();
                    });
                }

            GridPane button_bar = new GridPane();
            createButtons(button_bar);

            VBox header = new VBox();
            header.getChildren().add(label_box);
            header.getChildren().add(button_bar);

            border_pane.setTop(header);
            }
        return _node;
        }

    private void createButtons(GridPane button_bar)
        {
        HBox edit_buttons = new HBox();
        edit_buttons.setPadding(new Insets(4, 4, 4, 4));
        edit_buttons.setAlignment(Pos.TOP_LEFT);
        edit_buttons.setSpacing(4);
        button_bar.add(edit_buttons, button_bar.getChildren().size(), 0);
        GridPane.setHgrow(edit_buttons, Priority.ALWAYS);

        Button add_button = createAddResourceButton();
        edit_buttons.getChildren().add(add_button);

        Button import_button = createImportButton();
        edit_buttons.getChildren().add(import_button);

        for (ProjectNavigatorAdditionalButtonProvider provider : BUTTON_PROVIDERS)
            for (Button button : provider.getButtons(_project, getNode()))
                edit_buttons.getChildren().add(button);
        }

    @NotNull
    private Button createAddResourceButton()
        {
        Button add_button = new Button("Add", Glyphs.create("FA:PLUS"));
        add_button.setTooltip(new Tooltip("Add new resource to project"));
        add_button.setOnAction(event ->
            {
            PopupDialog popper = new PopupDialog("Create", "Create resource")
                {
                CreateResourcePanel _panel;

                @Override
                protected Node createContent()
                    {
                    _panel = new CreateResourcePanel(_project, _undo);
                    return _panel.getNode();
                    }

                @Override
                protected boolean okPressed()
                    {
                    CreateResourceAction action = _panel.getAction();
                    if (action != null)
                        {
                        boolean created = action.execute(_undo);
                        if (created)
                            showAndEditResource(action.getToken());
                        else
                            {
                            LOG.error("Cannot create resource: " + action.getErrorMessage());
                            return false;
                            }
                        }
                    return true;
                    }
                };
            popper.show(add_button);
            });
        return add_button;
        }

    @NotNull
    private Button createImportButton()
        {
        Button import_button = new Button("Import", Glyphs.create("FA:SIGN_IN"));
        import_button.setTooltip(new Tooltip("Import a SeleniumIDE test"));
        import_button.setOnAction(event ->
            {
            FileChooser chooser = new FileChooser();
            File initial_directory = RecentFileSettings.get().suggestRecentFolder(_project);
            if (initial_directory.exists())
                chooser.setInitialDirectory(initial_directory);
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SeleniumIDE Test", "*.html", "*.side"));
            List<File> selections = chooser.showOpenMultipleDialog(getNode().getScene().getWindow());
            if (selections == null || selections.size() == 0)
                return;

            // remember this folder
            RecentFileSettings.get().setRecentPath(selections.get(0).getParent());

            ImportCandidates candidates = ImportCandidates.build(_project, (File[]) selections.toArray(new File[selections.size()]));
            if (candidates.size() == 0)
                return;

            ImportCandidatesPane import_pane = new ImportCandidatesPane(_project);
            import_pane.setCandidates(candidates);
            ShadowboxPane shadowbox = ShadowboxPane.findFromNode(getNode());
            shadowbox.showOverlayOnShadowbox(import_pane.getNode());
            import_pane.setButtonListener(new ImportCandidatesPane.ImportPaneButtonListener()
                {
                @Override
                public void importButtonPressed()
                    {
                    shadowbox.removeOverlay();
                    ImportSeleniumIdeTestsAction action = import_pane.getAction();
                    action.execute(_undo);
                    }

                @Override
                public void cancelButtonPressed()
                    {
                    shadowbox.removeOverlay();
                    }
                });
            });
        return import_button;
        }
    
    private void showAndEditResource(ResourceToken token)
        {
        ProjectNode root = _tree.getRootNode();
        ResourceTreeNode node = root.findResourceNode(token);
        _tree.getTreeView().getSelectionModel().clearSelection();
        _tree.getTreeView().expandScrollToAndSelect(node);
        _editors.editResource(token, _project);
        }

    public void setProjectCloser(NavigatorView.ProjectCloser project_closer)
        {
        _project_closer = project_closer;
        }

    public void requestFocus()
        {
        _tree.requestFocus();
        }

    private final MuseProject _project;
    private final ResourceEditors _editors;
    private final UndoStack _undo = new UndoStack();

    private Scene _scene;
    private NotificationPane _node;
    private ProjectResourceTree _tree;
    private NavigatorView.ProjectCloser _project_closer = null;

    private final static Logger LOG = LoggerFactory.getLogger(ProjectNavigator.class);

    private final static List<ProjectNavigatorAdditionalButtonProvider> BUTTON_PROVIDERS = new ArrayList<>();

    public static void addButtonProvider(ProjectNavigatorAdditionalButtonProvider provider)
        {
        BUTTON_PROVIDERS.add(provider);
        }
    }