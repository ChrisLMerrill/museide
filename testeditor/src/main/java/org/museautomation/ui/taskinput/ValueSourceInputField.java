package org.museautomation.ui.taskinput;

import javafx.scene.*;
import org.museautomation.core.*;
import org.museautomation.core.task.*;
import org.museautomation.core.task.input.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.valuesource.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceInputField implements TaskInputField
    {
    public ValueSourceInputField(MuseExecutionContext context)
        {
        _context = context;
        _editor = new DefaultInlineVSE(context.getProject(), new UndoStack());
        ValueSourceConfiguration source = new ValueSourceConfiguration();
        _editor.setSource(source);
        source.addChangeListener(e -> updateSatisfactionState());

        _editor.addValidationStateListener((source1, valid) -> System.out.println("valid = " + valid));
        }

    @Override
    public void setTaskInput(TaskInput input)
        {
        _input = input;
        }

    @Override
    public void useDefault()
        {
        _editor.setSource(_input.getDefault());
        updateSatisfactionState();
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

    @Override
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

    @Override
    public Node getNode()
        {
        return _editor.getNode();
        }

    @Override
    public void addListener(TaskInputFieldListener listener)
        {
        _listeners.add(listener);
        }

    @Override
    public void removeListener(TaskInputFieldListener listener)
        {
        _listeners.remove(listener);
        }

    private void updateSatisfactionState()
        {
        boolean now_satisified = isSatisfied();
        if (_is_satisfied == null || now_satisified != _is_satisfied)
            {
            _is_satisfied = now_satisified;
            for (TaskInputFieldListener listener : _listeners)
                listener.satisfactionChanged(_is_satisfied);
            }
        }

    private boolean isDefault()
        {
        if (_input.getDefault() == null)
            return false;
        return _input.getDefault().equals(_editor.getSource());
        }

    private final MuseExecutionContext _context;
    private TaskInput _input;
    private final DefaultInlineVSE _editor;
    private Boolean _is_satisfied;
    private final Set<TaskInputFieldListener> _listeners = new HashSet<>();
    }