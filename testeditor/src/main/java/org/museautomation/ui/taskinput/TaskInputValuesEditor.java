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
                TaskInputValueEditorRow row = new TaskInputValueEditorRow(_context);
                row.setInput(inputs.get(i));
                row.addToGrid(_grid, i);
                _rows.add(row);
                }
            });
        }

    public boolean isSatisfied()
        {
        for (TaskInputValueEditorRow row : _rows)
            if (!row.isSatisfied())
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

    private final GridPane _grid = new GridPane();
    private final MuseExecutionContext _context;
    private final List<TaskInputValueEditorRow> _rows = new ArrayList<>();
    }