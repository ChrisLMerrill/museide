package org.museautomation.ui.extend.edit;

import java.util.*;

/**
 * A ValidationStateSource that listens to the ValidationState of a collection of other ValidationStateSources. It
 * broadcasts its own state as valid IF and ONLY IF all of those sources are also valid.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValidationStateAggregator implements ValidationStateSource, ValidationStateListener
    {
    public void addSubSource(ValidationStateSource source)
        {
        source.addValidationStateListener(this);

        if (!_sub_states.contains(source))
            _sub_states.add(source);

        setNewState(source.isValid());
        }

    private void setNewState(boolean valid)
        {
        if (Objects.equals(valid, _is_valid))
            return;  // no change in state

        boolean new_state = determineCurrentState();
        if (Objects.equals(new_state, _is_valid))
            return;
        changeState(new_state);
        }

    private void reevaluateState()
        {
        boolean new_state = determineCurrentState();
        if (Objects.equals(new_state, _is_valid))
            return;
        changeState(new_state);
        }

    private void changeState(boolean new_state)
        {
        // broadcast a state change
        _is_valid = new_state;
        for (ValidationStateListener listener : _listeners)
            listener.validationStateChanged(this, _is_valid);
        }

    private boolean determineCurrentState()
        {
        boolean new_state = true;
        for (ValidationStateSource state : _sub_states)
            if (!state.isValid())
                {
                new_state = false;
                break;
                }
        return new_state;
        }

    @Override
    public boolean isValid()
        {
        return _is_valid;
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

    @Override
    public void validationStateChanged(ValidationStateSource source, boolean valid)
        {
        setNewState(valid);
        }

    private boolean _is_valid = true;
    private List<ValidationStateSource> _sub_states = new ArrayList<>();
    private List<ValidationStateListener> _listeners = new ArrayList<>();
    }


