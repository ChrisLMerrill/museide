package org.musetest.ui.valuesource;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.*;
import org.musetest.core.values.*;
import org.musetest.core.values.descriptor.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.edit.stack.*;
import org.musetest.ui.extend.glyphs.*;
import org.musetest.ui.valuesource.actions.*;
import org.musetest.ui.valuesource.list.*;
import org.musetest.ui.valuesource.map.*;

/**
 * An editor for a value source. Can be added to an editor stack.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExpertValueSourceEditor extends BaseValueSourceEditor implements StackableEditor, ValueSourceEditor
    {
    public ExpertValueSourceEditor(MuseProject project, UndoStack undo)
        {
        super(project, undo);

        _grid.setPadding(new Insets(10));
        _grid.setHgap(5);
        _grid.setVgap(5);

        //
        // create the type selector
        //
        _type_selector = new ValueSourceTypeSelector(getProject())
            {
            @Override
            protected void typeSelected(ValueSourceDescriptor descriptor)
                {
                super.typeSelected(descriptor);
                new SourceTypeChangeAction(getSource(), descriptor.getType()).execute(getUndoStack());
                }
            };
        _type_selector.getButton().setId(TYPE_FIELD_ID);

        //
        // create the primitive value editor
        //
        _value_editor = new PrimitiveValueOptionalField(true);
        GridPane.setHgrow(_value_editor.getNode(), Priority.ALWAYS);
        _aggregator = new ValidationStateAggregator();
        _aggregator.addSubSource(_value_editor);
        _value_editor.addValidationStateListener((source, valid) -> changeValid(valid));
        _value_editor.setChangeListener(new_value -> new SourceValueChangeAction(getSource(), _value_editor.getValue()).execute(getUndoStack()));

        //
        // single sub-source editor
        // (starts with an add button, in case there isn't a sub-source
        //
        _subsource_pane = new StackPane();
        _subsource_pane.setAlignment(Pos.CENTER_LEFT);
        GridPane.setHgrow(_subsource_pane, Priority.ALWAYS);
        Button add_subsource_button = new Button("Add", Glyphs.create("FA:PLUS"));
        add_subsource_button.setId(ADD_SUBSOURCE_ID);
        add_subsource_button.setOnAction(event ->
            {
            ValueSourceConfiguration new_source = ValueSourceConfiguration.forValue("string value");
            getSource().setSource(new_source);
            createSubsourceEditor(project, undo, _aggregator);
            _subsource_editor.setSource(new_source);
            });
        _subsource_pane.getChildren().add(add_subsource_button);

        //
        // editor for map of named sub-sources
        //
        _map_editor = new ValueSourceMapEditor(project, undo);
        _map_editor.setStack(_editor_stack);
        GridPane.setHgrow(_map_editor.getNode(), Priority.ALWAYS);

        //
        // editor for list of indexed sub-sources
        //
        _list_editor = new ValueSourceListEditor(project, undo);
        GridPane.setHgrow(_list_editor.getNode(), Priority.ALWAYS);
        }

    private void layoutControls()
        {
        _grid.getChildren().clear();

        if (getSource() == null || getSource().getMetadataField(BaseValueSourceEditor.DISABLE_TYPE_CHANGES) == null)
            {
            _grid.add(new Label("Source type"), 0, 0);
            _grid.add(_type_selector.getButton(), 1, 0);
            }

        _grid.add(new Label("Value:"), 0, 1);
        _grid.add(_value_editor.getNode(), 1, 1);

        _grid.add(new Label("Sub-source:"), 0, 2);
        _grid.add(_subsource_pane, 1, 2);

        Label named_label = new Label("Named sources:");
        GridPane.setValignment(named_label, VPos.TOP);
        GridPane.setMargin(named_label, new Insets(8, 0, 0, 0));
        _grid.add(named_label, 0, 3);
        _grid.add(_map_editor.getNode(), 1, 3);

        Label indexed_label = new Label("Indexed sources:");
        GridPane.setValignment(indexed_label, VPos.TOP);
        GridPane.setMargin(indexed_label, new Insets(8, 0, 0, 0));
        _grid.add(indexed_label, 0, 4);
        _grid.add(_list_editor.getNode(), 1, 4);

        if (_mode_switch_widget != null)
            _grid.add(_mode_switch_widget, 1, 5);
        }

    private void createSubsourceEditor(MuseProject project, UndoStack undo, ValidationStateAggregator aggregator)
        {
        if (_subsource_editor != null)
            return;

        _subsource_pane.getChildren().clear();

        _subsource_editor = new DefaultInlineVSE(project, undo);
        aggregator.addSubSource(_subsource_editor);
        _subsource_editor.getNode().setId(SUBSOURCE_EDITOR_ID);

        Button remove_button = Buttons.createRemove();
        remove_button.setId(REMOVE_SUBSOURCE_ID);
        remove_button.setOnAction(event ->
            new SubSourceChangeAction(getSource(), null).execute(getUndoStack()));

        Hyperlink link = Buttons.createLinkWithIcon("more", "FA:ANGLE_DOUBLE_RIGHT", ExpertValueSourceEditor.ADVANCED_SUBSOURCE_ID, "edit this source", ContentDisplay.RIGHT);
        link.setOnAction(event ->
            {
            MultimodeValueSourceEditor sub_editor = new MultimodeValueSourceEditor(getSource().getSource(), getProject(), getUndoStack());
            _editor_stack.push(sub_editor, ExpertValueSourceEditor.SUBSOURCE_BREADCRUMB_LABEL);
            });

        GridPane grid = new GridPane();
        grid.add(_subsource_editor.getNode(), 0, 0);
        GridPane.setHgrow(_subsource_editor.getNode(), Priority.ALWAYS);
        grid.add(remove_button, 1, 0);
        grid.add(link, 2, 0);

        _subsource_pane.getChildren().add(grid);
        }

    @Override
    public Node getNode()
        {
        return _grid;
        }

    @Override
    public void setStack(EditorStack stack)
        {
        _editor_stack = stack;
        _map_editor.setStack(_editor_stack);
        _list_editor.setStack(_editor_stack);
        }

    @Override
    public void requestFocus()
        {
        _type_selector.getButton().requestFocus();
        }

    @Override
    public void activate()
        {
        Platform.runLater(() ->
            {
            ValueSourceConfiguration source = getSource();
            if (source != null)
                {
                _type_selector.setType(getProject().getValueSourceDescriptors().get(source.getType()));
                _value_editor.setValue(source.getValue());

                if (source.getSource() != null)
                    {
                    createSubsourceEditor(getProject(), getUndoStack(), _aggregator);
                    _subsource_editor.setSource(source.getSource());
                    }

                _map_editor.setSource(source);
                _list_editor.setSource(source);
                }
            layoutControls();
            });
        }

    @Override
    public boolean isValid()
        {
        return _aggregator.isValid();
        }

    public void setSource(ValueSourceConfiguration source)
        {
        super.setSource(source);
        activate();
        }

    public void addModeSwitchLink(Node link)
        {
        _mode_switch_widget = link;
        GridPane.setHalignment(_mode_switch_widget, HPos.RIGHT);
        layoutControls();
        }

    private EditorStack _editor_stack;
    private GridPane _grid = new GridPane();
    private ValueSourceTypeSelector _type_selector;
    private PrimitiveValueEditor _value_editor;
    private ValidationStateAggregator _aggregator;
    private StackPane _subsource_pane;
    private DefaultInlineVSE _subsource_editor;
    private ValueSourceMapEditor _map_editor;
    private ValueSourceListEditor _list_editor;
    private Node _mode_switch_widget = null;

    public final static String TYPE_FIELD_ID = "evse-type_chooser";
    public final static String ADD_SUBSOURCE_ID = "evse-add_subsource";
    public final static String REMOVE_SUBSOURCE_ID = "evse-remove_subsource";
    public final static String SUBSOURCE_EDITOR_ID = "evse-subsource";
    public final static String ADVANCED_SUBSOURCE_ID = "evse-advanced_subsource";
    public final static String SUBSOURCE_BREADCRUMB_LABEL = "(sub)";
    }


