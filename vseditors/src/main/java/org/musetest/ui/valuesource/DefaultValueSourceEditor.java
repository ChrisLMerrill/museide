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
import org.musetest.ui.valuesource.actions.*;
import org.musetest.ui.valuesource.list.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class DefaultValueSourceEditor extends BaseValueSourceEditor implements StackableEditor
    {
    public DefaultValueSourceEditor(MuseProject project, UndoStack undo)
        {
        super(project, undo);

        _grid = new GridPane();
        _grid.setPadding(new Insets(10, 10, 10, 20));
        _grid.setHgap(5);
        _grid.setVgap(5);

        _type_chooser = new ValueSourceTypeSelector(project)
            {
            @Override
            protected void typeSelected(ValueSourceDescriptor descriptor)
                {
                super.typeSelected(descriptor);
                new UpgradeValueSourceAction(getProject(), getSource(), descriptor.getType()).execute(getUndoStack());
                _source_type_short_description.setText(descriptor.getShortDescription());
                layoutControls();
                }
            };
        _type_chooser.getButton().setId(TYPE_FIELD_ID);
        _source_type_short_description = new Label();
        _source_type_short_description.setId(TYPE_DESCRIPTION_ID);

        layoutControls();
        }

    public Node getNode()
        {
        return _grid;
        }

    public void setSource(ValueSourceConfiguration source)
        {
        super.setSource(source);
        Platform.runLater(() ->
            {
            ValueSourceDescriptor descriptor = getProject().getValueSourceDescriptors().get(source);
            _type_chooser.setType(descriptor);
            _source_type_short_description.setText(descriptor.getShortDescription());

            layoutControls();
            });
        }

    private synchronized void layoutControls()
        {
        _grid.getChildren().clear();
        _validatable_editors.clear();

        int row = 0;

        if (getSource() == null || getSource().getMetadataField(BaseValueSourceEditor.DISABLE_TYPE_CHANGES) == null)
            {
            _grid.add(new Label("Source Type"), 0, row);
            HBox line2 = new HBox();
            line2.setSpacing(5);
            GridPane.setHgrow(line2, Priority.ALWAYS);
            _grid.add(line2, 1, row++);
            line2.getChildren().add(_type_chooser.getButton());
            line2.getChildren().add(_source_type_short_description);
            }

        if (getSource() != null)
            {
            ValueSourceConfiguration source = getSource();
            ValueSourceDescriptor descriptor = getProject().getValueSourceDescriptors().get(source);
            for (SubsourceDescriptor sub_descriptor : descriptor.getSubsourceDescriptors())
                {
                switch (sub_descriptor.getType())
                    {
                    case Value:
                        PrimitiveValueOptionalField value_editor = new PrimitiveValueOptionalField(sub_descriptor.isOptional());
                        value_editor.setValue(source.getValue());
                        value_editor.setChangeListener(new_value ->
                            new ChangeSourceValueAction(source, new_value).execute(getUndoStack()));
                        _grid.add(new Label(sub_descriptor.getDisplayName()), 0, row);
                        _grid.add(value_editor.getNode(), 1, row);
                        _validatable_editors.add(value_editor);
                        break;
                    case Named:
                        FixedNameValueSourceEditor named_editor = new FixedNameValueSourceEditor(getProject(), getUndoStack(), sub_descriptor, source);
                        named_editor.setSource(source.getSource(sub_descriptor.getName()));
                        if (_stack != null)
                            named_editor.setStack(_stack);
                        _grid.add(named_editor.getNameLabel(), 0, row);
                        _grid.add(named_editor.getNode(), 1, row);
                        _validatable_editors.add(named_editor);
                        break;
                    case List:
                        ValueSourceListEditor list_editor = new ValueSourceListEditor(getProject(), getUndoStack());
                        list_editor.setSource(source);
                        if (_stack != null)
                            list_editor.setStack(_stack);

                        Label label = new Label(sub_descriptor.getDisplayName());
                        GridPane.setValignment(label, VPos.TOP);
                        _grid.add(label, 0, row);
                        _grid.add(list_editor.getNode(), 1, row);
                        _validatable_editors.add(list_editor);
                        break;
                    case Single:
                        DefaultInlineVSE subsource_editor = new DefaultInlineVSE(getProject(), getUndoStack());
                        subsource_editor.setSource(source.getSource());
                        subsource_editor.getNode().setId(SINGLE_SUBSOURCE_FIELD_ID);
                        ((TextInputControl) subsource_editor.getNode()).setPromptText(sub_descriptor.getDescription());
                        _grid.add(new Label(sub_descriptor.getDisplayName()), 0, row);
                        _grid.add(subsource_editor.getNode(), 1, row);
                        _validatable_editors.add(subsource_editor);
                        if (_stack != null)
                            {
                            Hyperlink advanced = Buttons.createLinkWithIcon("more", "FA:ANGLE_DOUBLE_RIGHT", SINGLE_SUBSOURCE_ADVANCED_LINK_ID, "edit this source", ContentDisplay.RIGHT);
                            advanced.setOnAction(event ->
                                {
                                MultimodeValueSourceEditor sub_editor = new MultimodeValueSourceEditor(source.getSource(), getProject(), getUndoStack());
                                _stack.push(sub_editor, sub_descriptor.getDisplayName());
                                });
                            _grid.add(advanced, 2, row);
                            }
                        break;
                    default:
                        _grid.add(new Label(sub_descriptor.getDisplayName()), 0, row);
                        _grid.add(new Label(String.format("Subsource type %s is not yet supported in this editor", sub_descriptor.getType())), 1, row);
                    }
                row++;
                }
            }
        if (_mode_switch_widget != null)
            _grid.add(_mode_switch_widget, 1, row);
        }

    public void setStack(EditorStack stack)
        {
        _stack = stack;
        }

    @Override
    public void requestFocus()
        {
        _grid.requestFocus();
        }

    public void activate()
        {
        Platform.runLater(this::layoutControls);
        }

    @Override
    public boolean isValid()
        {
        for (Validatable editor : _validatable_editors)
            if (!editor.isValid())
                return false;
        return true;
        }

    public void addModeSwitchLink(Node link)
        {
        _mode_switch_widget = link;
        GridPane.setHalignment(_mode_switch_widget, HPos.RIGHT);
        layoutControls();
        }

    private EditorStack _stack;

    private final GridPane _grid;
    private final List<Validatable> _validatable_editors = new ArrayList<>();
    private ValueSourceTypeSelector _type_chooser;
    private Label _source_type_short_description;
    private Node _mode_switch_widget = null;

    public final static String TYPE_FIELD_ID = "dvse-type-field";
    public final static String TYPE_DESCRIPTION_ID = "dvse-type-description";
    public final static String SINGLE_SUBSOURCE_FIELD_ID = "dvse-single-subsource-field";
    public final static String SINGLE_SUBSOURCE_ADVANCED_LINK_ID = "dvse-single-subsource-adv-link";
    }


