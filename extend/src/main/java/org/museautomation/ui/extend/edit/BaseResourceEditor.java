package org.museautomation.ui.extend.edit;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import org.controlsfx.control.*;
import org.controlsfx.tools.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.actions.ui.*;
import org.museautomation.ui.extend.glyphs.*;
import org.museautomation.core.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class BaseResourceEditor implements MuseResourceEditor
    {
    /**
     * Override this method to accept the resource to be edited.
     * Note that this method can be called before or after #getEditorArea(), so it should
     * not make any assumptions about the GUI being active.
     */
    @Override
    public void editResource(MuseProject project, MuseResource resource)
        {
        _project = project;
        _resource = resource;
        }

    @Override
    public Scene getScene()
        {
        if (_scene == null)
            _scene = createScene();
        return _scene;
        }

    @Override
    public Parent getNode()
        {
        if (_notifier == null)
            {
            _border_pane = new BorderPane();
            _notifier = new NotificationPane(_border_pane);

            Node button_bar = createButtonBar();
            _border_pane.setTop(button_bar);

            Parent main_area = getEditorArea();
//            _scene.getStylesheets().add(Styles.find("ide"));
            _border_pane.setCenter(main_area);
            }
        return _notifier;
        }

    private Scene createScene()
        {
        _scene = new Scene(getNode());
//        _scene.getStylesheets().add(Styles.find("ide"));

        _scene.setOnKeyPressed(event ->
            {
            if (event.isControlDown() && event.getCode().equals(KeyCode.Z))
                {
                if (!_undo_stack.isEmpty())
                    _undo_stack.undoLastAction();
                event.consume();
                }
            });

        return _scene;
        }

    /**
     * Call this to release resources and remove change listeners when the editor will no longer be used.
     *
     * Subclasses should call this and then cleanup their own resources
     */
    @Override
    public void dispose()
        {
        _scene = null;
        _notifier = null;
        _undo_stack = null;
        _project = null;
        _resource = null;
        }

    /**
     * Override this method to return the main component for the UI.
     * Note that editResource() can be called before or after this method...so it should not
     * assume the editor has already been provided a resource. It should just return the main node.
     */
    protected abstract Parent getEditorArea();

    private Node createButtonBar()
        {
        GridPane button_bar = new GridPane();
        button_bar.setPadding(new Insets(0, 0, 0, 5));

        Button undo = new Button("Undo", Glyphs.create("FA:UNDO"));
        button_bar.add(undo, button_bar.getChildren().size(), 0);
        undo.setOnAction(event -> _undo_stack.undoLastAction());
        new UndoEnabler(getUndoStack()).setNode(undo);

        Button redo = new Button("Redo", Glyphs.create("FA:REPEAT"));
        button_bar.add(redo, button_bar.getChildren().size(), 0);
        redo.setOnAction(event -> _undo_stack.redoNextAction());
        new RedoEnabler(getUndoStack()).setNode(redo);


        addButtons(button_bar);

        HBox file_buttons = new HBox();
        file_buttons.setPadding(new Insets(4, 4, 4, 4));
        file_buttons.setAlignment(Pos.TOP_RIGHT);
        file_buttons.setSpacing(4);
        button_bar.add(file_buttons, button_bar.getChildren().size(), 0);
        GridPane.setHgrow(file_buttons, Priority.ALWAYS);

        Button save = new Button("Save", Glyphs.create("FA:SAVE"));
        save.setTooltip(new Tooltip("Save changes to the test"));
        file_buttons.getChildren().add(save);
        new SaveEnabler(getUndoStack(), getValidationStateSource()).setNode(save);
        save.setOnAction(event ->
            {
            String error = saveChanges();
            if (error == null)
                {
                showSuccessMessage("Save successful.");
                getUndoStack().clear();
                }
            else
                showFailureMessage("Save failed: " + error);
            });

        return Borders.wrap(button_bar).lineBorder().color(Color.GRAY).thickness(0, 0, 1, 0).innerPadding(0).outerPadding(0).buildAll();
        }

    protected void addButtons(GridPane button_bar)
        {

        }

    public void showInLowerSplitPane(Node node)
	    {
	    if (_splitter == null)
		    {
		    // create the splitter
		    _splitter = new SplitPane();
		    _splitter.setOrientation(Orientation.VERTICAL);
		    _splitter.getItems().add(_border_pane.getCenter());
		    _border_pane.setCenter(_splitter);
		    }

	    _splitter.getItems().add(node);
	    }

    public void hideLowerSplitPane()
	    {
	    final ObservableList<Node> items = _splitter.getItems();
	    if (items.size() > 0)
		    items.remove(1);
	    }

    @Override
    public boolean isChanged()
        {
        return !_undo_stack.isEmpty();
        }

    @Override
    public void revertChanges()
        {
        _undo_stack.undoAll();
        }

    @Override
    public String saveChanges()
        {
        return _project.getResourceStorage().saveResource(_resource);
        }

    public MuseProject getProject()
        {
        return _project;
        }

    protected MuseResource getResource()
        {
        return _resource;
        }

    public void showSuccessMessage(String message)
        {
        _notifier.getActions().clear();
        _notifier.setText(message);
        _notifier.setGraphic(Glyphs.create("FA:CHECK", Color.GREEN));
        _notifier.show();
        new TimedNotifierHider(_notifier, 3000);
        }

    public void showFailureMessage(String message)
        {
        _notifier.getActions().clear();
        _notifier.setText(message);
        _notifier.setGraphic(Glyphs.create("FA:EXCLAMATION_CIRCLE", Color.RED));
        _notifier.show();
        new TimedNotifierHider(_notifier, 3000);
        }

    public UndoStack getUndoStack()
        {
        return _undo_stack;
        }

    public NotificationPane getNotifier()
        {
        return _notifier;
        }

    private MuseProject _project;
    private MuseResource _resource;

    private UndoStack _undo_stack = new UndoStack();

    private NotificationPane _notifier;
    private Scene _scene;
    private BorderPane _border_pane;

    private SplitPane _splitter;
    }
