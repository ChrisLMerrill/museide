package org.museautomation.ui.extend.actions;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class CompoundAction extends UndoableAction
    {
    public CompoundAction(List<BaseEditAction> actions)
        {
        _actions.addAll(actions);
        }

    public CompoundAction()
        {
        }

    public void addAction(BaseEditAction action)
        {
        _actions.add(action);
        }

    @Override
    protected boolean executeImplementation()
        {
        boolean all_successful = true;
        for (BaseEditAction action : _actions)
            {
            boolean result = action.execute(_child_undo_stack);
            if (!result)
                all_successful = false;
            }
        return all_successful;
        }

    @Override
    protected boolean undoImplementation()
        {
        return _child_undo_stack.undoAll();
        }

    public int getSize()
        {
        return _actions.size();
        }

    private List<BaseEditAction> _actions = new ArrayList<>();
    private UndoStack _child_undo_stack = new UndoStack();
    }


