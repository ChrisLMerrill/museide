package org.musetest.ui.ide.navigation.resources;

import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.*;
import org.musetest.core.resource.types.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.components.*;
import org.musetest.ui.extend.components.validation.*;
import org.musetest.ui.ide.navigation.resources.actions.*;

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
        _grid.getStylesheets().add(getClass().getResource("/ide.css").toExternalForm());
        _grid.setVgap(5);
        _grid.setHgap(5);

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
        _id_field.textProperty().addListener((observable, oldValue, newValue) ->
            {
            validate();
            });

        _error_field = new Label();
        _grid.add(_error_field, 0, 2, 2, 1);
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
            return new CreateResourceAction(_type_selector.getSelection(), _id_field.getText(), _project);
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

        //TODO, is a valid ID for the ResourceStorage...e.g. filesystem limitations)

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

    private GridPane _grid = new GridPane();
    private TextField _id_field;
    private ResourceTypeAndSubtypeSelector _type_selector;
    private Dialog _dialog;
    private ButtonType _create_button_type;
    private Label _error_field = null;

    public final static String ID_FIELD = "crd.id";
    }


