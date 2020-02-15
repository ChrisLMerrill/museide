package org.museautomation.ui.extend.edit.stack;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class EditorStack implements Validatable
    {
    public EditorStack(EditInProgress edit, UndoStack undo)
        {
        _edit = edit;
        _undo_stack = undo;
        _restore_point = undo.getRestorePoint();
        _root = new BorderPane();

        _bar = new BreadCrumbBar();
        _bar.setAutoNavigationEnabled(false);
        _bar.setId(BCBAR_ID);
        _bar.setOnCrumbAction(event ->
            {
            StackableEditor editor = event.getSelectedCrumb().getValue()._editor;
            // note that we should never empty this stack, as the user can't remove the root editor. So there is NO need to check stack.empty(). If that occurs, there is a bug elsewhere.
            StackableEditor new_editor = null;
            while (!(editor == _editor_stack.peek().getValue()._editor) && _editor_stack.peek().getValue()._editor.isValid())
                {
                _editor_stack.pop();
                new_editor = _editor_stack.peek().getValue()._editor;
                }
            if (new_editor != null)
                {
                _root.setCenter(new_editor.getNode());
                Platform.runLater(editor::requestFocus);
                }
            _bar.setSelectedCrumb(_editor_stack.peek());
            event.consume();
            });

        GridPane top_row = new GridPane();
        top_row.add(_bar, 0, 0);
        GridPane.setHgrow(_bar, Priority.ALWAYS);
        GridPane.setMargin(_bar, new Insets(2));

        _save_button = Buttons.createSave();
        _save_button.setOnAction(event -> saveEdit());
        GridPane.setMargin(_save_button, new Insets(5));
        top_row.add(_save_button, 1, 0);

        _cancel_button = Buttons.createCancel();
        _cancel_button.setOnAction(event -> cancelEdit());
        GridPane.setMargin(_cancel_button, new Insets(5));
        top_row.add(_cancel_button, 2, 0);

        _root.setTop(top_row);
        }

    @SuppressWarnings("WeakerAccess")  // public API
    public void saveEdit()
        {
        // pop editors until we get to one that is not valid
        while (!_editor_stack.empty())
            {
            StackableEditor editor = _editor_stack.peek().getValue()._editor;
            if (editor.isValid())
                _editor_stack.pop();
            else
                {
                _root.setCenter(editor.getNode());
                _bar.setSelectedCrumb(_editor_stack.peek());
                return;
                }
            }
        notifyEditCommit();
        }

    /**
     * Implement this to notify the InprogressEdit of the commit.
     */
    @SuppressWarnings("WeakerAccess")  // available to subclasses
    protected abstract void notifyEditCommit();

    @SuppressWarnings("WeakerAccess")  // public API
    public void cancelEdit()
        {
        _restore_point.revertTo();
        _edit.cancel();
        }

    public Parent getNode()
        {
        return _root;
        }

    @SuppressWarnings("unused")  // public API
    public void requestFocus()
        {
        _root.getCenter().requestFocus();
        }

    @SuppressWarnings("unused")  // public API
    public void push(StackableEditor editor, String name)
        {
        editor.setStack(this);
        TreeItem<EditorOnStack> item = new TreeItem<>(new EditorOnStack(name, editor));
        if (!_editor_stack.isEmpty())
            _editor_stack.peek().getChildren().add(item);
        _editor_stack.push(item);
        _bar.setSelectedCrumb(item);
        activateEditor(editor);
        }

    private void activateEditor(StackableEditor editor)
        {
        _root.setCenter(editor.getNode());
        editor.activate();
        Platform.runLater(editor::requestFocus);
        }

    @SuppressWarnings("unused")  // public API
    public UndoStack getUndoStack()
        {
        return _undo_stack;
        }

    public boolean isValid()
        {
        for (TreeItem<EditorOnStack> item : _editor_stack)
            if (!item.getValue()._editor.isValid())
                return false;
        return true;
        }

    @SuppressWarnings("unused")  // public API
    public void showSaveCancelButtons(boolean visible)
	    {
	    _save_button.setVisible(visible);
	    _cancel_button.setVisible(visible);
	    }

    private final Button _save_button;
    private final Button _cancel_button;

    @SuppressWarnings({"WeakerAccess", "SpellCheckingInspection"})  // public API to aid unit testing
    public final static String BCBAR_ID = "bcbar";

    @SuppressWarnings("WeakerAccess")  // available to subclasses
    protected final EditInProgress _edit;
    private final UndoStack _undo_stack;
    private final UndoStack.UndoPoint _restore_point;

    private BorderPane _root;
    private BreadCrumbBar<EditorOnStack> _bar;
    private Stack<TreeItem<EditorOnStack>> _editor_stack = new Stack<>();

    class EditorOnStack
        {
        String _name;
        StackableEditor _editor;

        EditorOnStack(String name, StackableEditor editor)
            {
            _name = name;
            _editor = editor;
            }

        @Override
        public String toString()
            {
            return _name;
            }
        }
    }
