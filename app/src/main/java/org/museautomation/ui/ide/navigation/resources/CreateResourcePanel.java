package org.museautomation.ui.ide.navigation.resources;

import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.core.resource.storage.*;
import org.museautomation.ui.extend.*;
import org.museautomation.ui.ide.navigation.resources.actions.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.resource.types.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.ui.extend.components.validation.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class CreateResourcePanel
    {
    public CreateResourcePanel(MuseProject project, UndoStack undo)
        {
        _project = project;
        _undo = undo;
        createContent();
        }

    private void createContent()
        {
        _grid.getStylesheets().add(Styles.get("ide"));
        _grid.setVgap(5);
        _grid.setHgap(5);
        _grid.setPadding(new Insets(10));
        _grid.setMinWidth(250);
        _grid.setMinHeight(150);

        Label type_label = new Label("Type:");
        _grid.add(type_label, 0, 0);

        _type_selector = new ResourceTypeAndSubtypeSelector(_project);
        _grid.add(_type_selector.getNode(), 1, 0);

        Label id_label = new Label("Id: ");
        _grid.add(id_label, 0, 1);

        _id_field = new TextField();
        _id_field.setId(ID_FIELD);
        _grid.add(_id_field, 1, 1);
        new NotBlankTextValidator().attachTo(_id_field);
        _id_field.textProperty().addListener((observable, oldValue, newValue) -> validate());
        GridPane.setHgrow(_id_field, Priority.ALWAYS);

        Label path_label = new Label("Path: ");
        _grid.add(path_label, 0, 2);

        _path_chooser = new ComboBox<>();
        _path_chooser.editableProperty().setValue(true);
        for (String path : ProjectStructureSettings.get(_project).getSubfolders())
            _path_chooser.getItems().add(path);
        _grid.add(_path_chooser, 1, 2);

        _error_field = new Label();
        _grid.add(_error_field, 0, 3, 2, 1);
        GridPane.setHgrow(_error_field, Priority.ALWAYS);
        }

    public Dialog getDialog()
        {
        if (_dialog != null)
            return _dialog;

        _dialog = new Dialog();
        _dialog.setTitle("Create resource");
        _dialog.setHeaderText("Create a new resource");

        _create_button_type = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        _dialog.getDialogPane().getButtonTypes().addAll(_create_button_type, ButtonType.CANCEL);

        _dialog.getDialogPane().setContent(_grid);

        Platform.runLater(() -> _id_field.requestFocus());

        Button ok_button = (Button) _dialog.getDialogPane().lookupButton(_create_button_type);
        ok_button.addEventFilter(ActionEvent.ACTION, event ->
            {
            CreateResourceAction action = getAction();
            if (action != null)
                {
                boolean success = action.execute(_undo);
                if (!success)
                    {
                    setError(action.getErrorMessage());
                    event.consume();
                    }
                }
            });

        validate();
        return _dialog;
        }

    public Node getNode()
        {
        return _grid;
        }

    public void setType(ResourceType type)
        {
        _type_selector.select(type);
        }

    public void setId(String id)
        {
        _id_field.setText(id);
        validate();
        }

    public CreateResourceAction getAction()
        {
        if (validate())
            return new CreateResourceAction(_type_selector.getSelection(), _id_field.getText(), _project, _path_chooser.getValue());
        else
            return null;
        }

    private boolean validate()
        {
        boolean valid = true;
        String id = _id_field.getText();
        if (id.length() == 0)
            valid = false;

        if (_project.getResourceStorage().findResource(id) != null)
            {
            valid = false;
            setError("Id already exists in project");
            }

        // it does not already exist, but is it valid for the storage method?  (currently only files...)
        if (valid)
            valid = new FilenameValidator().isValid(id);

        InputValidation.setValid(_id_field, valid);
        if (_dialog != null)
            _dialog.getDialogPane().lookupButton(_create_button_type).setDisable(!valid);

        if (valid)
            clearError();
        return valid;
        }

    private void clearError()
        {
        Platform.runLater(() -> _error_field.setText(null));
        }

    private void setError(String error)
        {
        _error_field.setText(error);
        }

    private final MuseProject _project;
    private final UndoStack _undo;

    private final GridPane _grid = new GridPane();
    private ResourceTypeAndSubtypeSelector _type_selector;
    private TextField _id_field;
    private ComboBox<String> _path_chooser;
    private Dialog _dialog;
    private ButtonType _create_button_type;
    private Label _error_field = null;

    public final static String ID_FIELD = "crd.id";
    }


