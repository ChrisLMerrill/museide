package org.museautomation.ui.valuesource;

import org.museautomation.core.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;

import java.util.*;

/**
 * This base class for ValueSourceEditors implements much of the needed state and listener management.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class BaseValueSourceEditor implements ValueSourceEditor
    {
    BaseValueSourceEditor(MuseProject project, UndoStack undo)
        {
        _project = project;
        _undo = undo;
        }

    @Override
    public ValueSourceConfiguration getSource()
        {
        return _source;
        }

    /**
     * Set the source for this editor. Should only be called externally. Subclasses should call changeSource(), which
     * will also fire change events as needed.  Subclasses who override this method, should call it first and then
     * update their UI elements.
     */
    @Override
    public void setSource(ValueSourceConfiguration source)
        {
        _source = source;
        }

    /**
     * Subclasses should call this when state of the editor changes to/from valid/invalid. Callers are free
     * to be chatty, this method will filter out duplicate/redundant calls before forwarding to listeners.
     */
    void changeValid(boolean valid)
        {
        if (valid != _valid)
            {
            _valid = valid;
            for (ValidationStateListener listener : _valid_listeners)
                listener.validationStateChanged(this, valid);
            }
        }

    protected MuseProject getProject()
        {
        return _project;
        }

    UndoStack getUndoStack()
        {
        return _undo;
        }

    @Override
    public void addValidationStateListener(ValidationStateListener listener)
        {
        _valid_listeners.add(listener);
        }

    @Override
    public void removeValidationStateListener(ValidationStateListener listener)
        {
        _valid_listeners.remove(listener);
        }

    @Override
    public boolean isValid()
        {
        return _valid;
        }

    private ValueSourceConfiguration _source;
    private MuseProject _project;
    private UndoStack _undo;
    private boolean _valid = true;
    private List<ValidationStateListener> _valid_listeners = new ArrayList<>();

    public final static String DISABLE_TYPE_CHANGES = "bvse.disable_type_changes";
    }
