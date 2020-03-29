package org.museautomation.ui.extend.components;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.stack.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class EditorStackTests extends ComponentTest
    {
    @Test
    void editorVisibility()
        {
        Assertions.assertTrue(exists("push1"));
        }

    @Test
    void titleVisibility()
        {
        Assertions.assertTrue(exists("title1"));
        }

    @Test
    void okCancelButtonsExist()
        {
        Assertions.assertTrue(exists("save"));
        Assertions.assertTrue(exists("cancel"));
        }

    @Test
    void editorFocus()
        {
        Node button = lookup("push1").query();
        Node focused = button.getScene().getFocusOwner();
        Assertions.assertEquals(focused, button);
        }

    @Test
    void editorsAreActivated()
        {
        Assertions.assertTrue(exists("children=0"));
        }

    @Test
    void pushNewEditor()
        {
        // press the button to push a new editor onto the stack
        Assertions.assertTrue(exists("title1"));
        Node button = lookup("push1").query();
        Assertions.assertNotNull(button);
        clickOn(button);

        // verify previous editor was removed
        Assertions.assertFalse(exists("push1"));

        // verify new editor is added...
        Node new_button = lookup("push1.1").query();
        Assertions.assertNotNull(new_button);
        Assertions.assertEquals(new_button, new_button.getScene().getFocusOwner());

        // ...and the new title, too
        Assertions.assertTrue(exists("title1"));
        Assertions.assertTrue(exists("title1.1"));
        }

    @Test
    void returnToPreviousEditor()
        {
        // press the button to push a new editor
        clickOn("push1");

        // the title of the previous editor to go back to it
        clickOn("title1");

        Assertions.assertFalse(exists("title1.1"), "the sub-title on the navbar wasn't removed");
        Assertions.assertTrue(exists("title1"), "the title on the navbar was removed");
        Node button1 = lookup("push1").query();
        Assertions.assertNotNull(button1);

        Assertions.assertEquals(button1, button1.getScene().getFocusOwner());
        }

    @Test
    void okButtonNotification()
        {
        Node button = lookup("save").query();
        clickOn(button);
        Assertions.assertTrue(_saved);
        Assertions.assertFalse(_cancelled);
        }

    @Test
    void cancelButtonNotification()
        {
        clickOn("cancel");
        Assertions.assertTrue(_cancelled);
        Assertions.assertFalse(_saved);
        }

    @Test
    void editsMergedToUndoStack()
        {
        clickOn("change1");     // make a change in an editor
        clickOn("push1");        // open sub-editor
        clickOn("change1.1");     // make a change in sub-editor
        clickOn("save");          // press OK

        // verify 2 action added to undo stack
        Assertions.assertEquals(2, _stack.getUndoStack().getNumberOfUndoableActions());
        }

    @Test
    void cancelRevertsChanges()
        {
        Assertions.assertTrue(_edits.isEmpty());

        clickOn("change1");     // make a change in an editor

        // verify a change was made
        Assertions.assertFalse(_edits.isEmpty());

        clickOn("cancel");          // press cancel

        // verify above changes were reverted
        Assertions.assertTrue(_edits.isEmpty());

        // verify 0 actions added to undo stack
        Assertions.assertEquals(0, _stack.getUndoStack().getNumberOfUndoableActions());
        }

    @Test
    void denyAcceptingWithInvalidEditor()
        {
        clickOn("change1");      // make a change
        clickOn("valid1");       // invalidate the editor
        clickOn("save");          // press OK

        // verify EditListener was not called
        Assertions.assertFalse(_saved);
        Assertions.assertFalse(_cancelled);

        // verify editor still there
        Assertions.assertNotNull(lookup("valid1").query());

        // verify change is in UndoStack and the change is still applied
        Assertions.assertFalse(_stack.getUndoStack().isEmpty());
        Assertions.assertFalse(_edits.isEmpty());
        }

    @Test
    void parentBecomesVisibleWhenReturningFromChild()
        {
        // verify initial value of field that depends on children
        Assertions.assertTrue(exists("valid1"), "this should be visible");

        clickOn("valid1");        // invalidate the editor

        // push sub editor
        clickOn("push1");

        Assertions.assertFalse(exists("valid1"), "this should no longer be visible");

        // return to parent editor
        clickOn("save");
        waitForUiEvents();

        // verify parent editor has been re-activated
        Assertions.assertTrue(exists("valid1"), "this should be visible again");
        }

    @Test
    void okReturnsToInvalidEditor()
        {
        clickOn((Node) lookup("change1").query());      // make a change
        clickOn((Node) lookup("valid1").query());       // invalidate the editor
        clickOn((Node) lookup("push1").query());        // open sub-editor
        clickOn((Node) lookup("change1.1").query());    // make a change
        clickOn((Node) lookup("valid1.1").query());     // invalidate the editor
        clickOn((Node) lookup("save").query());          // press OK

        // verify still on sub-editor
        Assertions.assertFalse(exists("change1"));
        Assertions.assertTrue(exists("change1.1"));
        // verify ok listener not called
        Assertions.assertFalse(_saved);
        Assertions.assertFalse(_cancelled);
        // verify changes from both editor are in place
        Assertions.assertEquals(2, _edits.size());

        clickOn("valid1.1");     // validate the editor
        clickOn("save");          // press OK

        // verify returned to parent editor
        Assertions.assertFalse(exists("change1.1"));
        Assertions.assertTrue(exists("change1"));
        // verify ok listener not called
        Assertions.assertFalse(_saved);
        Assertions.assertFalse(_cancelled);
        // verify changes from both editors are in place
        Assertions.assertEquals(2, _edits.size());
        }

    @Override
    protected Node createComponentNode()
        {
        _edits = new Stack<>();

        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_RIGHT);

        Button save_button = new Button("save");
        save_button.setOnAction(event -> _stack.saveEdit());
        row.getChildren().add(save_button);

        Button cancel_button =  new Button("cancel");
        cancel_button.setOnAction(event -> _stack.cancelEdit());
        row.getChildren().add(cancel_button);

        UndoStack undo = new UndoStack();
        final UndoStack.UndoPoint restore_point = undo.getRestorePoint();
        _stack = new EditorStack(new EditInProgress<>()
            {
            public void cancel()
                {
                _cancelled = true;
                restore_point.revertTo();
                }

            public void commit(Object target)
                {
                _saved = true;
                }
            }, undo)
            {
            @Override
            protected void notifyEditCommit()
                {
                _edit.commit(null);
                }
            };
        _stack.push(new StackableEditorImpl("1"), "title1");
        _cancelled = false;
        _saved = false;

        BorderPane borders = new BorderPane();
        borders.setTop(row);
        borders.setCenter(_stack.getNode());

        return borders;
        }

    class StackableEditorImpl implements StackableEditor
        {
        StackableEditorImpl(String id)
            {
            _id = id;
            int row = 0;
            _button = new Button("push" + _id);
            _root.add(_button, 0, row++);
            _button.setOnAction(event ->
                {
                String sub_id = _id + "." + _child_id++;
                StackableEditorImpl editor = new StackableEditorImpl(sub_id);
                _stack.push(editor, "title" + sub_id);
                });

            _valid = new CheckBox("valid" + _id);
            _valid.setSelected(true);
            _root.add(_valid, 0, row++);

            Button change = new Button("change" + _id);
            _root.add(change, 0, row++);
            change.setOnAction(event ->
                {
                UndoableAction action = new UndoableAction()
                    {
                    @Override
                    protected boolean undoImplementation()
                        {
                        Assertions.assertEquals("change" + _id, _edits.peek());
                        _edits.pop();
                        return true;
                        }

                    @Override
                    protected boolean executeImplementation()
                        {
                        _edits.push("change" + _id);
                        return true;
                        }
                    };
                action.execute(_stack.getUndoStack());
                });

            _label = new Label("children");
            _root.add(_label, 0, row);
            }

        @Override
        public Node getNode()
            {
            return _root;
            }

        @Override
        public void setStack(EditorStack stack)
            {
            _stack = stack;
            }

        @Override
        public void requestFocus()
            {
            _button.requestFocus();
            }

        @Override
        public void activate()
            {
            _label.setText("children=" + (_child_id - 1));
            }

        @Override
        public boolean isValid()
            {
            return _valid.isSelected();
            }

        private EditorStack _stack;
        private String _id;
        private int _child_id = 1;
        private GridPane _root = new GridPane();
        private final Button _button;
        private final Label _label;
        private final CheckBox _valid;
        }

    private EditorStack _stack;
    private boolean _cancelled;
    private boolean _saved;
    private Stack<String> _edits;
    }