package org.musetest.ui.step;

import javafx.application.*;
import javafx.stage.*;
import org.junit.*;
import org.musetest.builtins.step.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.step.*;
import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.step.actions.*;
import org.testfx.framework.junit.*;
import org.testfx.util.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepActionTests extends ApplicationTest // needed for testing clipboard access
    {
    @Test
    public void insertAtBeginning()
        {
        UndoableAction insert = new InsertStepsAction(_root_step, _new_step, 0);
        insert.execute(_undo);

        Assert.assertEquals(3, _root_step.getChildren().size());
        Assert.assertSame(_new_step, _root_step.getChildren().get(0));
        Assert.assertSame(_step1, _root_step.getChildren().get(1));
        Assert.assertSame(_step2, _root_step.getChildren().get(2));

        _undo.undoLastAction();

        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_step2, _root_step.getChildren().get(1));
        }

    @Test
    public void insertMultipleAtBeginning()
        {
        List<StepConfiguration> to_add = new ArrayList<>();
        to_add.add(_new_step);
        StepConfiguration other_new = new StepConfiguration("dummy");
        to_add.add(other_new);
        UndoableAction insert = new InsertStepsAction(_root_step, to_add, 0);
        insert.execute(_undo);

        Assert.assertEquals(4, _root_step.getChildren().size());
        Assert.assertSame(_new_step, _root_step.getChildren().get(0));
        Assert.assertSame(other_new, _root_step.getChildren().get(1));
        Assert.assertSame(_step1, _root_step.getChildren().get(2));
        Assert.assertSame(_step2, _root_step.getChildren().get(3));

        _undo.undoLastAction();

        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_step2, _root_step.getChildren().get(1));
        }

    @Test
    public void insertAtEnd()
        {
        UndoableAction insert = new InsertStepsAction(_root_step, _new_step, 2);
        insert.execute(_undo);

        Assert.assertEquals(3, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_step2, _root_step.getChildren().get(1));
        Assert.assertSame(_new_step, _root_step.getChildren().get(2));

        _undo.undoLastAction();

        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_step2, _root_step.getChildren().get(1));
        }

    @Test
    public void insertAtMiddle()
        {
        UndoableAction insert = new InsertStepsAction(_root_step, _new_step, 1);
        insert.execute(_undo);

        Assert.assertEquals(3, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_new_step, _root_step.getChildren().get(1));
        Assert.assertSame(_step2, _root_step.getChildren().get(2));

        _undo.undoLastAction();

        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_step2, _root_step.getChildren().get(1));
        }

    @Test
    public void insertInEmpty()
        {
        UndoableAction insert = new InsertStepsAction(_step1, _new_step, 0);
        insert.execute(_undo);

        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertEquals(1, _step1.getChildren().size());
        Assert.assertSame(_new_step, _step1.getChildren().get(0));

        _undo.undoLastAction();

        Assert.assertTrue(_step1.getChildren() == null || _step1.getChildren().size() == 0);
        }

    @Test
    public void deleteFromBeginning()
        {
        UndoableAction delete = new DeleteStepsAction(_root_step, _step1);
        delete.execute(_undo);

        Assert.assertEquals(1, _root_step.getChildren().size());
        Assert.assertSame(_step2, _root_step.getChildren().get(0));

        _undo.undoLastAction();

        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        }

    @Test
    public void deleteFromEnd()
        {
        UndoableAction delete = new DeleteStepsAction(_root_step, _step2);
        delete.execute(_undo);

        Assert.assertEquals(1, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));

        _undo.undoLastAction();
        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step2, _root_step.getChildren().get(1));
        }

    @Test
    public void deleteFromMiddle()
        {
        _root_step.addChild(1, _new_step);
        UndoableAction delete = new DeleteStepsAction(_root_step, _new_step);
        delete.execute(_undo);

        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_step2, _root_step.getChildren().get(1));

        _undo.undoLastAction();
        Assert.assertEquals(3, _root_step.getChildren().size());
        Assert.assertSame(_new_step, _root_step.getChildren().get(1));
        }

    @Test
    public void deleteMultiple()
        {
        List<StepConfiguration> delete_list = new ArrayList<>();
        delete_list.add(_step1);
        delete_list.add(_step2);
        UndoableAction delete = new DeleteStepsAction(_root_step, delete_list);
        delete.execute(_undo);

        Assert.assertTrue(_root_step.getChildren() == null || _root_step.getChildren().size() == 0);

        _undo.undoLastAction();
        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_step2, _root_step.getChildren().get(1));
        }

    @Test
    public void deleteDeeperChild()
        {
        StepConfiguration to_delete = new StepConfiguration("delete me");
        _step2.addChild(to_delete);
        UndoableAction delete = new DeleteStepsAction(_root_step, to_delete);
        delete.execute(_undo);

        Assert.assertTrue(_step2.getChildren() == null || _step2.getChildren().size() == 0);

        _undo.undoLastAction();
        Assert.assertEquals(1, _step2.getChildren().size());
        Assert.assertSame(to_delete, _step2.getChildren().get(0));
        }

    @Test
    public void cutAndPaste()
        {
        // cut extends delete, so no need to test variations
        UndoableAction cut = new CutStepsToClipboardAction(_root_step, _step1);
        Platform.runLater(() -> cut.execute(_undo));
        WaitForAsyncUtils.waitForFxEvents();

        Assert.assertEquals(1, _root_step.getChildren().size());
        Assert.assertSame(_step2, _root_step.getChildren().get(0));

        // undo cut
        _undo.undoLastAction();
        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));

        // cut again
        Platform.runLater(() -> cut.execute(_undo));
        WaitForAsyncUtils.waitForFxEvents();

        Assert.assertEquals(1, _root_step.getChildren().size());
        Assert.assertSame(_step2, _root_step.getChildren().get(0));

        // paste
        Platform.runLater(() ->
            {
            UndoableAction paste = new PasteStepsFromClipboardAction(_project, _root_step, 1);
            paste.execute(_undo);
            });
        WaitForAsyncUtils.waitForFxEvents();

        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertEquals(_step1.getType(), _root_step.getChildren().get(1).getType());  // will be the same type (should check everything equal but step-id, but that would be testing something else)
        Assert.assertNotEquals(_step1.getStepId(), _root_step.getChildren().get(1).getStepId());  // will have a new stepid

        // undo paste
        _undo.undoLastAction();
        Assert.assertEquals(1, _root_step.getChildren().size());
        Assert.assertSame(_step2, _root_step.getChildren().get(0));
        }

    @Test
    public void copyAndPaste()
        {
        CopyStepsToClipboardAction copy = new CopyStepsToClipboardAction(StepConfiguration.copy(_step1, _project));
        Platform.runLater(() -> copy.execute(_undo));
        WaitForAsyncUtils.waitForFxEvents();

        Assert.assertEquals(2, _root_step.getChildren().size());  // didn't cut

        // paste
        Platform.runLater(() ->
            {
            UndoableAction paste = new PasteStepsFromClipboardAction(_project, _root_step, 2);
            paste.execute(_undo);
            });

        WaitForAsyncUtils.waitForFxEvents();

        Assert.assertEquals(3, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_step2, _root_step.getChildren().get(1));
        Assert.assertEquals(_step1.getType(), _root_step.getChildren().get(2).getType());  // will be a copy...just checking the type to be sure the right one is in the right place
        Assert.assertNotEquals(_step1.getStepId(), _root_step.getChildren().get(2).getStepId());  // copy should have different id

        // undo paste
        _undo.undoLastAction();
        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_step2, _root_step.getChildren().get(1));
        }

    @Test
    public void copyAndPasteTwiceMakesUniqueStepIds()
        {
        CopyStepsToClipboardAction copy = new CopyStepsToClipboardAction(StepConfiguration.copy(_step1, _project));
        Platform.runLater(() -> copy.execute(_undo));
        WaitForAsyncUtils.waitForFxEvents();

        Assert.assertEquals(2, _root_step.getChildren().size());  // didn't cut

        // paste
        Platform.runLater(() ->
            {
            UndoableAction paste = new PasteStepsFromClipboardAction(_project, _root_step, 2);
            paste.execute(_undo);
            UndoableAction paste2 = new PasteStepsFromClipboardAction(_project, _root_step, 2);
            paste2.execute(_undo);
            });

        WaitForAsyncUtils.waitForFxEvents();

        Assert.assertEquals(4, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_step2, _root_step.getChildren().get(1));
        Assert.assertEquals(_step1.getType(), _root_step.getChildren().get(2).getType());  // will be a copy...just checking the type to be sure the right one is in the right place
        Assert.assertEquals(_step1.getType(), _root_step.getChildren().get(3).getType());  // will be a copy...just checking the type to be sure the right one is in the right place
        Assert.assertNotEquals(_step1.getStepId(), _root_step.getChildren().get(2).getStepId());
        Assert.assertNotEquals(_step1.getStepId(), _root_step.getChildren().get(3).getStepId());
        Assert.assertNotEquals(_root_step.getChildren().get(2).getStepId(), _root_step.getChildren().get(3).getStepId());

        // undo paste
        _undo.undoLastAction();
        _undo.undoLastAction();
        Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step1, _root_step.getChildren().get(0));
        Assert.assertSame(_step2, _root_step.getChildren().get(1));
        }

    @Test
    public void cutParentAndChildren()
        {
        setup();

        _root_step.getChildren().clear();
        StepConfiguration if_step = new StepConfiguration(IfStep.TYPE_ID);
        if_step.addSource(IfStep.CONDITION_PARAM, ValueSourceConfiguration.forValue(true));
        _root_step.addChild(if_step);

        if_step.addChild(_step1);
        if_step.addChild(_step2);

        List<StepConfiguration> steps_to_cut = new ArrayList<>();
        steps_to_cut.add(if_step);
        steps_to_cut.add(_step1);
        steps_to_cut.add(_step1);

        // cut the composite and child steps
        CutStepsToClipboardAction cut = new CutStepsToClipboardAction(_root_step, steps_to_cut);
        AtomicBoolean success = new AtomicBoolean(false);
        Platform.runLater(() ->
            {
            boolean result = cut.execute(_undo);
            success.set(result);
            });
        WaitForAsyncUtils.waitForFxEvents();
        Assert.assertTrue(success.get());
        Assert.assertNull(_root_step.getChildren());

        // restore them
        _undo.undoLastAction();
        Assert.assertEquals(1, _root_step.getChildren().size());
        Assert.assertEquals(if_step, _root_step.getChildren().get(0));
        Assert.assertEquals(2, if_step.getChildren().size());
        Assert.assertEquals(_step1, if_step.getChildren().get(0));
        Assert.assertEquals(_step2, if_step.getChildren().get(1));
        }

    @Before
    public void setup()
        {
        _root_step = new StepConfiguration(BasicCompoundStep.TYPE_ID);
        _root_step.setMetadataField(StepConfiguration.META_DESCRIPTION, "root step");

        _step1 = new StepConfiguration(LogMessage.TYPE_ID);
        _step1.setMetadataField(StepConfiguration.META_ID, IdGenerator.get(_project).generateLongId());
        _step1.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("mymessage"));
        _root_step.addChild(_step1);

        _step2 = new StepConfiguration(Verify.TYPE_ID);
        _step2.setMetadataField(StepConfiguration.META_ID, IdGenerator.get(_project).generateLongId());
        _step2.addSource(Verify.CONDITION_PARAM, ValueSourceConfiguration.forValue("untrue string"));
        _root_step.addChild(_step2);

        _new_step = new StepConfiguration(StoreVariable.TYPE_ID);
        _new_step.addSource(StoreVariable.NAME_PARAM, ValueSourceConfiguration.forValue("var1"));
        _new_step.addSource(StoreVariable.VALUE_PARAM, ValueSourceConfiguration.forValue("value1"));

        _undo = new UndoStack();
        }

    @Override
    public void start(Stage stage)
        {

        }

    private MuseProject _project = new SimpleProject();
    private StepConfiguration _root_step;
    private StepConfiguration _step1;
    private StepConfiguration _step2;
    private StepConfiguration _new_step;
    private UndoStack _undo;
    }