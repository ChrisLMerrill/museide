package org.museautomation.ui.taskinput;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import org.museautomation.core.*;
import org.museautomation.core.task.*;
import org.museautomation.core.task.input.*;
import org.museautomation.ui.extend.glyphs.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TaskInputValueEditorRow
    {
    public TaskInputValueEditorRow(MuseExecutionContext context)
        {
        _input_field = new ValueSourceInputField(context);
        _input_field.getNode().setId(VALUE_FIELD_ID);
        _input_field.addListener(this::setSatisfactionIcon);

        _use_default.setId(USE_DEFAULT_ID);
        _use_default.setGraphic(Glyphs.create("FA:ARROW_LEFT"));
        _use_default.setTooltip(new Tooltip("Use default value"));
        _use_default.setOnAction(event -> _input_field.useDefault());
        }

    public void addToGrid(GridPane grid, int row_index)
        {
        grid.add(_name, 0, row_index);
        grid.add(_input_field.getNode(), 1, row_index);
        GridPane.setHgrow(_input_field.getNode(), Priority.ALWAYS);
        if (_input.getDefault() != null)
            grid.add(_use_default, 2, row_index);
        grid.add(_type, 3, row_index);
        grid.add(_satisfaction_label, 4, row_index);
        setSatisfactionIcon(!_input.isRequired());
        }

    private void setSatisfactionIcon(boolean satisifed)
        {
        if (satisifed)
            {
            if (!REQUIRED_SATISFIED_ICON_ID.equals(_satisfaction_label.getId()))
                {
                _satisfaction_label.setGraphic(_satisfied_icon);
                _satisfaction_label.setId(REQUIRED_SATISFIED_ICON_ID);
                for (SatisfactionListener listener : _listeners)
                    listener.satisfactionChanged(false, true);
                }
            }
        else
            {
            if (!REQUIRED_NOT_SATISFIED_ICON_ID.equals(_satisfaction_label.getId()))
                {
                _satisfaction_label.setGraphic(_not_satisfied_icon);
                _satisfaction_label.setId(REQUIRED_NOT_SATISFIED_ICON_ID);
                for (SatisfactionListener listener : _listeners)
                    listener.satisfactionChanged(true, false);
                }
            }
        }

    public void setInput(TaskInput input)
        {
        _input = input;
        _input_field.setTaskInput(input);
        if (input.getDefault()  == null)
            _use_default.setDisable(true);

        _name.setText(new UiFriendlyMetadata(input.metadata()).getString("label", input.getName()));
        _type.setText(input.getType().getName());
        }

    public TaskInput getInput()
        {
        return _input;
        }

    public boolean isSatisfied()
        {
        return _input_field.isSatisfied();
        }

    public ResolvedTaskInput getResolvedInput()
        {
        return _input_field.getResolvedInput();
        }

    public void addSatisfactionChangeListener(SatisfactionListener listener)
        {
        _listeners.add(listener);
        }

    private TaskInput _input;

    private final Label _name = new Label();
    private TaskInputField _input_field;
    private final Button _use_default = new Button();
    private final Label _type = new Label();
    private final Label _satisfaction_label = new Label();
    private final Node _satisfied_icon = Glyphs.create("FA:CHECK_CIRCLE", Color.GREEN, 20);
    private final Node _not_satisfied_icon = Glyphs.create("FA:EXCLAMATION_CIRCLE", Color.RED, 20);
    private final List<SatisfactionListener> _listeners = new ArrayList<>();

    public final static String VALUE_FIELD_ID = "omuit-tiver-value-field";
    public final static String USE_DEFAULT_ID = "omuit-tiver-use-default";
    public final static String REQUIRED_NOT_SATISFIED_ICON_ID = "omuit-tiver-not-satified-icon";
    public final static String REQUIRED_SATISFIED_ICON_ID = "omuit-tiver-satified-icon";

    public interface SatisfactionListener
        {
        void satisfactionChanged(boolean old_value, boolean new_value);
        }
    }