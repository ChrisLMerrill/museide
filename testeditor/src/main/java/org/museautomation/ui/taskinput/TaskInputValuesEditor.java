package org.museautomation.ui.taskinput;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import org.museautomation.core.*;
import org.museautomation.core.task.*;
import org.museautomation.core.task.input.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TaskInputValuesEditor
    {
    public TaskInputValuesEditor(MuseExecutionContext context)
        {
        _context = context;

        _grid.setVgap(5.0);
        _grid.setHgap(5.0);
        }

    public Node getNode()
        {
        return _grid;
        }

    public void setInputs(List<TaskInput> inputs)
        {
        Platform.runLater(() ->
            {
            for (int i = 0; i < inputs.size(); i++)
                {
                final TaskInputValueEditorRow row = new TaskInputValueEditorRow(_context);
                row.addSatisfactionChangeListener((old_value, new_value) ->
                    {
                    boolean was_satisfied = isSatisfied();
                    _inputs_satisfied.put(row.getInput().getName(), new_value);
                    boolean is_satisifed = isSatisfied();
                    if (was_satisfied != is_satisifed)
                        for (InputsSatisfiedListener listener : _listeners)
                            listener.satisfactionChanged(was_satisfied, is_satisifed);
                    });
                row.setInput(inputs.get(i));
                row.addToGrid(_grid, i);
                _rows.add(row);
                }
            });
        }

    public boolean isSatisfied()
        {
        for (boolean satisfied : _inputs_satisfied.values())
            if (!satisfied)
                return false;
        return true;
        }

    public List<ResolvedTaskInput> getResolvedInputs()
        {
        List<ResolvedTaskInput> resolved_inputs = new ArrayList<>();
        for (TaskInputValueEditorRow row : _rows)
            {
            ResolvedTaskInput resolved = row.getResolvedInput();
            if (resolved != null)
                resolved_inputs.add(resolved);
            }
        return resolved_inputs;
        }

    public void addSatisfactionChangeListener(InputsSatisfiedListener listener)
        {
        _listeners.add(listener);
        }

    private final GridPane _grid = new GridPane();
    private final MuseExecutionContext _context;
    private final List<TaskInputValueEditorRow> _rows = new ArrayList<>();
    private final List<InputsSatisfiedListener> _listeners = new ArrayList<>();
    private final Map<String,Boolean> _inputs_satisfied = new HashMap<>();

    public interface InputsSatisfiedListener
        {
        void satisfactionChanged(boolean old_value, boolean new_value);
        }
    }