package org.museautomation.ui.extend.actions;

import org.slf4j.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UndoStack
    {
    public UndoStack()
        {
        }

    public void push(UndoableAction action)
        {
        _action_stack.push(action);
        if (!_redo_in_progress)
            _redo_stack.clear();
        notifyListeners();
        }

    public boolean isEmpty()
        {
        return _action_stack.isEmpty();
        }

    private void notifyListeners()
        {
        for (UndoStackListener listener : _listeners)
            listener.stackSizeChanged(this);
        }

    public void addListener(UndoStackListener listener)
        {
        _listeners.add(listener);
        }

    public void removeListener(UndoStackListener listener)
        {
        _listeners.remove(listener);
        }

    public void clear()
        {
        _action_stack.clear();
        notifyListeners();
        }

    public boolean undoLastAction()
        {
        if (!_action_stack.isEmpty())
            {
            UndoableAction action = _action_stack.pop();
            boolean result = action.undo();
            _redo_stack.push(action);
            notifyListeners();
            return result;
            }
        return false;
        }

    public boolean redoNextAction()
        {
        if (!_redo_stack.isEmpty())
            {
            UndoableAction action = _redo_stack.pop();
            _redo_in_progress = true;
            boolean result = action.execute(this);
            _redo_in_progress = false;
            notifyListeners();   //<-- this is not needed because the execute will notify listeners
            return result;
            }
        return false;
        }

    public int getNumberOfUndoableActions()
        {
        return _action_stack.size();
        }

    protected UndoableAction peekNextUndoableAction()
        {
        return _action_stack.peek();
        }

    public UndoPoint getRestorePoint()
        {
        UndoableAction action = null;
        if (!_action_stack.isEmpty())
            action = _action_stack.peek();
        return new UndoPoint(this, action);
        }

    public boolean undoAll()
        {
        while (!_action_stack.empty())
            if (!undoLastAction())
                return false;
        return true;
        }

    public boolean isRedoEmpty()
        {
        return _redo_stack.isEmpty();
        }

    public Iterator<UndoableAction> iterator()
        {
        return _action_stack.iterator();
        }

    private Stack<UndoableAction> _action_stack = new Stack<>();
    private Stack<UndoableAction> _redo_stack = new Stack<>();
    private boolean _redo_in_progress = false;

    private List<UndoStackListener> _listeners = new ArrayList<>();

    public class UndoPoint
        {
        protected UndoPoint(UndoStack stack, UndoableAction action_to_undo_to)
            {
            _stack = stack;
            _action_to_undo_to = action_to_undo_to;
            }

        public boolean revertTo()
            {
            if (_action_to_undo_to == null)
                return _stack.undoAll();

            // first, ensure the stack is in a state that we can revert to
            boolean valid_state = false;
            Iterator<UndoableAction> iterator = _stack.iterator();
            while (iterator.hasNext())
                if (iterator.next() == _action_to_undo_to)
                    valid_state = true;

            if (!valid_state)
                {
                LOG.error("Unable to revert - restore point not found in stack");
                return false;
                }

            while (_stack.peekNextUndoableAction() != _action_to_undo_to)
                {
                boolean success = _stack.undoLastAction();
                if (!success)
                    return false;
                }

            return true;
            }

        private UndoStack _stack;
        private UndoableAction _action_to_undo_to;
        }

    final static Logger LOG = LoggerFactory.getLogger(UndoStack.class);
    }


