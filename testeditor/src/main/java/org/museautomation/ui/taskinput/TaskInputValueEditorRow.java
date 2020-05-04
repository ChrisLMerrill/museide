package org.museautomation.ui.taskinput;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import org.museautomation.core.*;
import org.museautomation.core.task.*;
import org.museautomation.core.task.input.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.glyphs.*;
import org.museautomation.ui.valuesource.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TaskInputValueEditorRow
    {
    public TaskInputValueEditorRow(MuseExecutionContext context)
        {
        _context = context;
        _editor = new DefaultInlineVSE(context.getProject(), new UndoStack());
        _editor.getNode().setId(VALUE_FIELD_ID);
        ValueSourceConfiguration source = new ValueSourceConfiguration();
        _editor.setSource(source);

        _use_default.setId(USE_DEFAULT_ID);
        _use_default.setGraphic(Glyphs.create("FA:ARROW_LEFT"));
        _use_default.setTooltip(new Tooltip("Use default value"));

        source.addChangeListener(e -> setSatisfactionIcon(isSatisfied()));
        _use_default.setOnAction(event ->
            {
            _editor.setSource(_default_source);
            setSatisfactionIcon(isSatisfied());
            });
        }

    public void addToGrid(GridPane grid, int row_index)
        {
        grid.add(_name, 0, row_index);
        grid.add(_editor.getNode(), 1, row_index);
        GridPane.setHgrow(_editor.getNode(), Priority.ALWAYS);
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
        _default_source = input.getDefault();
        if (_default_source == null)
            _use_default.setDisable(true);
        _name.setText(input.getName());
        _type.setText(input.getType().getName());
        }

    public TaskInput getInput()
        {
        return _input;
        }

    public boolean isSatisfied()
        {
        ValueSourceConfiguration source = _editor.getSource();
        if (source == null)
            return false;
        if (source.equals(new ValueSourceConfiguration()) && !_input.isRequired())
            return true;
        try
            {
            Object value = source.createSource(_context.getProject()).resolveValue(_context);
            return _input.getType().isInstance(value);
            }
        catch (MuseExecutionError e)
            {
            return false;
            }
        }

    public boolean isDefault()
        {
        if (_default_source == null)
            return false;
        return _default_source.equals(_editor.getSource());
        }

    public ResolvedTaskInput getResolvedInput()
        {
        try
            {
            Object value = _editor.getSource().createSource(_context.getProject()).resolveValue(_context);
            ResolvedInputSource source = new ResolvedInputSource.UserInputSource();
            if (isDefault())
                source = new ResolvedInputSource.DefaultValueSource();
            return new ResolvedTaskInput(_input.getName(), value, source);
            }
        catch (MuseExecutionError e)
            {
            return null;
            }
        }

    public void addSatisfactionChangeListener(SatisfactionListener listener)
        {
        _listeners.add(listener);
        }

    private TaskInput _input;
    private ValueSourceConfiguration _default_source;
    private final MuseExecutionContext _context;

    private final Label _name = new Label();
    private final DefaultInlineVSE _editor;
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