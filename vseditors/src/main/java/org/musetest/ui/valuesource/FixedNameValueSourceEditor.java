package org.musetest.ui.valuesource;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.builtins.value.*;
import org.musetest.core.*;
import org.musetest.core.values.*;
import org.musetest.core.values.descriptor.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.edit.stack.*;
import org.musetest.ui.extend.glyphs.*;
import org.musetest.ui.valuesource.map.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FixedNameValueSourceEditor implements Validatable, ValueSourceEditor
    {
    @SuppressWarnings("WeakerAccess") // public API
    public FixedNameValueSourceEditor(MuseProject project, UndoStack undo, SubsourceDescriptor descriptor, ContainsNamedSources container)
        {
        this(project, undo, descriptor);
        _container = container;
        }

    private FixedNameValueSourceEditor(MuseProject project, UndoStack undo, SubsourceDescriptor descriptor)
        {
        _project = project;
        _undo = undo;
        _descriptor = descriptor;
        createAddControls();
        }

    @Override
    public void setSource(ValueSourceConfiguration source)
        {
        _source = source;
        buildFields();
        }

    private void buildFields()
        {
        if (_source == null)
            createAddControls();
        else
            createEditControls();
        }

    @Override
    public ValueSourceConfiguration getSource()
        {
        return _source;
        }

    @Override
    public Node getNode()
        {
        return _body;
        }

    @Override
    public void requestFocus()
        {
        _focus_target.requestFocus();
        }

    @Override
    public void addValidationStateListener(ValidationStateListener listener)
        {
        _listeners.add(listener);
        }

    @Override
    public void removeValidationStateListener(ValidationStateListener listener)
        {
        _listeners.remove(listener);
        }

    public Node getNameLabel()
        {
        if (_name_label == null)
            {
            _name_label = new Label(_descriptor.getDisplayName());
            _name_label.setId(getNameFieldId(_descriptor.getName()));
            }
        return _name_label;
        }

    private void createEditControls()
        {
        if (_source == null && !_descriptor.isOptional())
            {
            _source = _descriptor.getDefault();
            if (_source == null)
                _source = ValueSourceConfiguration.forValue("value goes here");
            new AddNamedSubsourceAction(_container, _source, _descriptor.getName()).execute(_undo);
            }

        _editor = new DefaultInlineVSE(_project, _undo);
        TextInputControl editor_node = (TextInputControl) _editor.getNode();
        editor_node.setId(getValueFieldId(_descriptor.getName()));
        _editor.setSource(_source);
        _focus_target = editor_node;

        addTooltip(editor_node);

        Button delete_button = null;
        if (_descriptor.isOptional())
            {
            delete_button = Buttons.createRemove();
            delete_button.setCursor(Cursor.HAND);
            delete_button.setId(getDeleteButtonId(_descriptor.getName()));
            delete_button.getStyleClass().clear();
            delete_button.setTooltip(new Tooltip("Delete"));
            delete_button.setOnAction(event ->
                {
                _source = null;
                new RemoveNamedSubsourceAction(_container, _descriptor.getName()).execute(_undo);
                createAddControls();
                });
            }

        GridPane pane = new GridPane();
        GridPane.setHgrow(_editor.getNode(), Priority.ALWAYS);
        pane.add(_editor.getNode(), 0, 0);
        if (delete_button != null)
            pane.add(delete_button, 1, 0);
        if (_stack != null)
            {
            Hyperlink advanced = Buttons.createLinkWithIcon("more", "FA:ANGLE_DOUBLE_RIGHT", getAdvancedLinkId(_descriptor.getName()), "edit this source", ContentDisplay.RIGHT);
            advanced.setOnAction(event ->
                {
                MultimodeValueSourceEditor sub_editor = new MultimodeValueSourceEditor(_source, _project, _undo);
                _stack.push(sub_editor, _descriptor.getDisplayName());
                });
            pane.add(advanced, 2, 0);
            }

        replaceValueNode(pane);
        }

    private void addTooltip(Node node)
        {
        Tooltip tooltip = new Tooltip(_descriptor.getDescription());
        Tooltip.install(node, tooltip);
        if (node instanceof TextInputControl)
            ((TextInputControl) node).setPromptText(_descriptor.getDescription());
        }

    private void createAddControls()
        {
        Button add = new Button("Add");
        add.setGraphic(Glyphs.create("FA:PLUS"));
        add.setId(getAddButtonId(_descriptor.getName()));
        add.setOnAction(event -> createEditControls());
        _focus_target = add;
        addTooltip(add);

        GridPane grid = new GridPane();
        grid.add(add, 0, 0);
        GridPane.setHalignment(add, HPos.LEFT);
        replaceValueNode(grid);
        }

    private void replaceValueNode(Node new_node)
        {
        _body.setCenter(new_node);
        }

    public void setStack(EditorStack stack)
        {
        _stack = stack;
        buildFields();
        }

    public static String getNameFieldId(String name)
        {
        return NAME_FIELD_ID_BASE + name;
        }

    public static String getValueFieldId(String name)
        {
        return VALUE_FIELD_ID_BASE + name;
        }

    public static String getAddButtonId(String name)
        {
        return ADD_BUTTON_ID_BASE + name;
        }

    public static String getDeleteButtonId(String name)
        {
        return DELETE_BUTTON_ID_BASE + name;
        }

    public static String getAdvancedLinkId(String name)
        {
        return ADVANCED_LINK_ID_BASE + name;
        }

    public boolean isValid()
        {
        return _editor == null || _editor.isValid();
        }

    private MuseProject _project;
    private UndoStack _undo;
    private ValueSourceConfiguration _source;
    private SubsourceDescriptor _descriptor;
    private ContainsNamedSources _container;
    private EditorStack _stack;
    private List<ValidationStateListener> _listeners = new ArrayList<>();
    private Node _focus_target;

    private DefaultInlineVSE _editor;
    private final BorderPane _body = new BorderPane();
    private Label _name_label;

    private final static String NAME_FIELD_ID_BASE = "fnvse-name-";
    private final static String VALUE_FIELD_ID_BASE = "fnvse-value-";
    private final static String ADD_BUTTON_ID_BASE = "fnvse-add-";
    private final static String DELETE_BUTTON_ID_BASE = "fnvse-delete-";
    private final static String ADVANCED_LINK_ID_BASE = "fnvse-advanced-";
    }


