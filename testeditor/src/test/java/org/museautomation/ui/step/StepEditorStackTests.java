package org.museautomation.ui.step;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.valuesource.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.values.*;
import org.museautomation.selenium.steps.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepEditorStackTests extends ComponentTest
    {
    @Test
    void initialFocus()
        {
        setupStep(null, null);
        Node expected_focus = lookup("#" + ExpertStepEditor.DESCRIPTION_FIELD_ID).query();
        Node focused = expected_focus.getScene().getFocusOwner();
        Assertions.assertEquals(expected_focus, focused);
        }

    @Test
    void stepDisplayed()
        {
        StepConfiguration step = setupStep(null, null);

        // step name should be displayed in the BreadCrumbBar
        Assertions.assertNotNull(lookup(_project.getStepDescriptors().get(step).getName()).query());

        // description should be in the description field
        Assertions.assertEquals(STEP_DESCRIPTION, ((TextInputControl) lookup("#" + ExpertStepEditor.DESCRIPTION_FIELD_ID).query()).getText());

        // step type should be selected
        Assertions.assertEquals(_project.getStepDescriptors().get(step).getName(), ((Labeled) lookup("#" + ExpertStepEditor.TYPE_FIELD_ID).query()).getText().trim());
        }

    @Test
    void stepChangesSaved()
        {
        StepConfiguration step = setupStep(null, null);
        clickOn("#" + ExpertStepEditor.DESCRIPTION_FIELD_ID).push(KeyCode.CONTROL, KeyCode.A).write(NEW_DESCRIPTION);
        clickOn("#" + ExpertStepEditor.TYPE_FIELD_ID).clickOn(_project.getStepDescriptors().get(LogMessage.TYPE_ID).getName());
        clickOn("#" + Buttons.SAVE_ID);
        waitForUiEvents();

        Assertions.assertTrue(_committed);
        Assertions.assertFalse(_cancelled);
        Assertions.assertEquals(NEW_DESCRIPTION, step.getMetadataField(StepConfiguration.META_DESCRIPTION));
        Assertions.assertEquals(LogMessage.TYPE_ID, step.getType());
        }

    @Test
    void stepChangesCanceled()
        {
        final String name = "source1";
        final String orig_value = "orig_value";
        StepConfiguration step = setupStep(name, orig_value);
        final String orig_description = (String) step.getMetadataField(StepConfiguration.META_DESCRIPTION);
        final String orig_type = step.getType();
        clickOn("#" + ExpertStepEditor.DESCRIPTION_FIELD_ID).push(KeyCode.CONTROL, KeyCode.A).write(NEW_DESCRIPTION);
        clickOn("#" + ExpertStepEditor.TYPE_FIELD_ID).clickOn(_project.getStepDescriptors().get(LogMessage.TYPE_ID).getName());
        fillFieldAndTabAway("#" + InlineNamedVSE.getValueFieldId(name), "\"new_value\"");
        clickOn("#" + Buttons.CANCEL_ID);

        Assertions.assertTrue(_cancelled);
        Assertions.assertFalse(_committed);
        Assertions.assertEquals(orig_description, step.getMetadataField(StepConfiguration.META_DESCRIPTION));
        Assertions.assertEquals(orig_type, step.getType());
        Assertions.assertEquals(orig_value, step.getSource(name).getValue());
        }

    @Test
    void noChangeOnDescriptionFocusEvents()
        {
        setupStep(null, null);
        clickOn("#" + ExpertStepEditor.DESCRIPTION_FIELD_ID);
        clickOn("#" + ExpertStepEditor.TYPE_FIELD_ID);

        Assertions.assertTrue(_editor.getUndoStack().isEmpty());
        }

    private StepConfiguration setupStep(String param_name, String param_value)
        {
        StepConfiguration step = new StepConfiguration(CloseBrowser.TYPE_ID);
        step.setMetadataField(StepConfiguration.META_DESCRIPTION, STEP_DESCRIPTION);
        if (param_name != null)
            step.addSource(param_name, ValueSourceConfiguration.forValue(param_value));
        Platform.runLater(() -> _editor.setStep(step));
        waitForUiEvents();

        // switch to expert mode
        Node switch_link = lookup("#" + MultimodeStepEditor.SWITCH_TO_EXPERT_ID).query();
        Assertions.assertNotNull(switch_link);
        clickOn(switch_link);

        return step;
        }

    @Override
    protected Node createComponentNode()
        {
        _cancelled = false;
        _committed = false;

        UndoStack undo_stack = new UndoStack();
        final UndoStack.UndoPoint restore_point = undo_stack.getRestorePoint();
        _project = new SimpleProject();
        _editor = new StepEditorStack(new RootStepEditContext(_project, undo_stack, null), new EditInProgress<>()
            {
            public void cancel()
                {
                restore_point.revertTo();
                _cancelled = true;
                }
            public void commit(Object model)
                {
                _committed = true;
                }
            });

        return _editor.getNode();
        }

    private StepEditorStack _editor;
    private SimpleProject _project;

    private boolean _cancelled;
    private boolean _committed;

    private static String NEW_DESCRIPTION = "new_description";
    private static String STEP_DESCRIPTION = "step_description";
    }