package org.museautomation.ui.extend.components;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
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
    public void editorVisibility()
        {
        Assert.assertTrue(exists("push1"));
        }

    @Test
    public void titleVisibility()
        {
        Assert.assertTrue(exists("title1"));
        }

    @Test
    public void okCancelButtonsExist()
        {
        Assert.assertTrue(exists("save"));
        Assert.assertTrue(exists("cancel"));
        }

    @Test
    public void editorFocus()
        {
        Node button = lookup("push1").query();
        Node focused = button.getScene().getFocusOwner();
        Assert.assertEquals(focused, button);
        }

    @Test
    public void editorsAreActivated()
        {
        Assert.assertTrue(exists("children=0"));
        }

    @Test
    public void pushNewEditor()
        {
        // press the button to push a new editor onto the stack
        Assert.assertTrue(exists("title1"));
        Node button = lookup("push1").query();
        Assert.assertNotNull(button);
        clickOn(button);

        // verify previous editor was removed
        Assert.assertFalse(exists("push1"));

        // verify new editor is added...
        Node new_button = lookup("push1.1").query();
        Assert.assertNotNull(new_button);
        Assert.assertEquals(new_button, new_button.getScene().getFocusOwner());

        // ...and the new title, too
        Assert.assertTrue(exists("title1"));
        Assert.assertTrue(exists("title1.1"));
        }

    @Test
    public void returnToPreviousEditor()
        {
        // press the button to push a new editor
        clickOn("push1");

        // the title of the previous editor to go back to it
        clickOn("title1");

        Assert.assertFalse("the sub-title on the navbar wasn't removed", exists("title1.1"));
        Assert.assertTrue("the title on the navbar was removed", exists("title1"));
        Node button1 = lookup("push1").query();
        Assert.assertNotNull(button1);

        Assert.assertEquals(button1, button1.getScene().getFocusOwner());
        }

    @Test
    public void okButtonNotification()
        {
        Node button = lookup("save").query();
        clickOn(button);
        Assert.assertTrue(_saved);
        Assert.assertFalse(_cancelled);
        }

    @Test
    public void cancelButtonNotification()
        {
        clickOn("cancel");
        Assert.assertTrue(_cancelled);
        Assert.assertFalse(_saved);
        }

    @Test
    public void editsMergedToUndoStack()
        {
        clickOn("change1");     // make a change in an editor
        clickOn("push1");        // open sub-editor
        clickOn("change1.1");     // make a change in sub-editor
        clickOn("save");          // press OK

        // verify 2 action added to undo stack
        Assert.assertEquals(2, _stack.getUndoStack().getNumberOfUndoableActions());
        }

    @Test
    public void cancelRevertsChanges()
        {
        Assert.assertTrue(_edits.isEmpty());

        clickOn("change1");     // make a change in an editor

        // verify a change was made
        Assert.assertFalse(_edits.isEmpty());

        clickOn("cancel");          // press cancel

        // verify above changes were reverted
        Assert.assertTrue(_edits.isEmpty());

        // verify 0 actions added to undo stack
        Assert.assertEquals(0, _stack.getUndoStack().getNumberOfUndoableActions());
        }

    @Test
    public void denyAcceptingWithInvalidEditor()
        {
        clickOn("change1");      // make a change
        clickOn("valid1");       // invalidate the editor
        clickOn("save");          // press OK

        // verify EditListener was not called
        Assert.assertFalse(_saved);
        Assert.assertFalse(_cancelled);

        // verify editor still there
        Assert.assertNotNull(lookup("valid1").query());

        // verify change is in UndoStack and the change is still applied
        Assert.assertFalse(_stack.getUndoStack().isEmpty());
        Assert.assertFalse(_edits.isEmpty());
        }

    @Test
    public void parentBecomesVisibleWhenReturningFromChild()
        {
        // verify initial value of field that depends on children
        Assert.assertTrue("this should be visible", exists("valid1"));

        clickOn("valid1");        // invalidate the editor

        // push sub editor
        clickOn("push1");

        Assert.assertFalse("this should no longer be visible", exists("valid1"));

        // return to parent editor
        clickOn("save");
        waitForUiEvents();

        // verify parent editor has been re-activated
        Assert.assertTrue("this should be visible again", exists("valid1"));
        }

    @Test
    public void okReturnsToInvalidEditor()
        {
        clickOn((Node) lookup("change1").query());      // make a change
        clickOn((Node) lookup("valid1").query());       // invalidate the editor
        clickOn((Node) lookup("push1").query());        // open sub-editor
        clickOn((Node) lookup("change1.1").query());    // make a change
        clickOn((Node) lookup("valid1.1").query());     // invalidate the editor
        clickOn((Node) lookup("save").query());          // press OK

        // verify still on sub-editor
        Assert.assertFalse(exists("change1"));
        Assert.assertTrue(exists("change1.1"));
        // verify ok listener not called
        Assert.assertFalse(_saved);
        Assert.assertFalse(_cancelled);
        // verify changes from both editor are in place
        Assert.assertEquals(2, _edits.size());

        clickOn("valid1.1");     // validate the editor
        clickOn("save");          // press OK

        // verify returned to parent editor
        Assert.assertFalse(exists("change1.1"));
        Assert.assertTrue(exists("change1"));
        // verify ok listener not called
        Assert.assertFalse(_saved);
        Assert.assertFalse(_cancelled);
        // verify changes from both editors are in place
        Assert.assertEquals(2, _edits.size());
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
        _stack = new EditorStack(new EditInProgress<Object>()
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
        public StackableEditorImpl(String id)
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
                        Assert.assertEquals("change" + _id, _edits.peek());
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


