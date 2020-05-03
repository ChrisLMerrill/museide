package org.museautomation.ui.step;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.valuesource.*;
import org.museautomation.ui.valuesource.list.*;
import org.museautomation.ui.valuesource.map.*;
import org.museautomation.builtins.step.*;
import org.museautomation.builtins.value.collection.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.step.descriptor.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class DefaultStepEditorTests extends ComponentTest
    {
    @Test
    void displayStep()
        {
        StepConfiguration step = new StepConfiguration(LogMessage.TYPE_ID);
        step.setMetadataField(StepConfiguration.META_DESCRIPTION, "description of the step");
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue("the message contents");
        step.addSource(LogMessage.MESSAGE_PARAM, source);
        _project = new SimpleProject();
        _editor.setStep(step);
        waitForUiEvents();

        // description should be in the description field
        Assertions.assertEquals(step.getMetadataField(StepConfiguration.META_DESCRIPTION), ((TextInputControl) lookup("#" + DefaultStepEditor.DESCRIPTION_FIELD_ID).query()).getText());

        // step type should be selected
        StepDescriptor descriptor = _project.getStepDescriptors().get(step);
        Assertions.assertEquals(descriptor.getName(), ((Labeled) lookup("#" + DefaultStepEditor.TYPE_FIELD_ID).query()).getText().trim());

        // step help short and long descriptions
        Assertions.assertEquals(descriptor.getShortDescription(), ((Text) lookup("#" + DefaultStepEditor.HELP_TYPE_SHORT_DESCRIPTION).query()).getText());

        // first (and only) parameter should contain the message
        Assertions.assertEquals(quoted(source.getValue()), ((TextInputControl) lookup("#" + FixedNameValueSourceEditor.getValueFieldId(descriptor.getSubsourceDescriptors()[0].getName())).query()).getText());
        }

    @Test
    void changeStepType()
        {
        StepConfiguration step = setupLogStep();

        StepDescriptor verify_descriptor = _project.getStepDescriptors().get(Verify.TYPE_ID);

        // change the step type
        clickOn("#" + DefaultStepEditor.TYPE_FIELD_ID).clickOn(verify_descriptor.getName());
        waitForUiEvents();

        // verify step, chooser selection and description is changed
        Assertions.assertEquals(verify_descriptor.getType(), step.getType());
        Assertions.assertEquals(verify_descriptor.getName(), ((Labeled) lookup("#" + DefaultStepEditor.TYPE_FIELD_ID).query()).getText().trim());
        Assertions.assertEquals(verify_descriptor.getShortDescription(), ((Text) lookup("#" + DefaultStepEditor.HELP_TYPE_SHORT_DESCRIPTION).query()).getText().trim());

        // verify the parameters changed
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(LogMessage.MESSAGE_PARAM))), "the message param wasn't removed");
        Assertions.assertTrue(exists(id(FixedNameValueSourceEditor.getAddButtonId(Verify.CONDITION_PARAM))), "the id param wasn't added");
        }

    private StepConfiguration setupLogStep()
        {
        return setupLogStep(null);
        }

    private StepConfiguration setupLogStep(String description)
        {
        StepConfiguration step = new StepConfiguration(LogMessage.TYPE_ID);
        if (description != null)
            step.setMetadataField(StepConfiguration.META_DESCRIPTION, description);
        _project = new SimpleProject();
        _editor.setStep(step);
        waitForUiEvents();
        return step;
        }

    @Test
    void changeDescription()
        {
        StepConfiguration step = setupLogStep();

        String new_description = "new_description";
        fillFieldAndTabAway(id(DefaultStepEditor.DESCRIPTION_FIELD_ID), new_description);

        Assertions.assertEquals(new_description, step.getMetadataField(StepConfiguration.META_DESCRIPTION), "the description is missing");
        }

    @Test
    void nullifyEmptyDescription()
        {
        StepConfiguration step = setupLogStep("non-empty description");
        waitForUiEvents();

        clearFieldAndTabAway(id(DefaultStepEditor.DESCRIPTION_FIELD_ID));

        Assertions.assertNull(step.getMetadataField(StepConfiguration.META_DESCRIPTION), "the description should be null");
        }

    @Test
    void nullifyWhitespaceDescription()
        {
        StepConfiguration step = setupLogStep("non-empty description");
        waitForUiEvents();

        fillFieldAndTabAway(id(DefaultStepEditor.DESCRIPTION_FIELD_ID), " ");

        Assertions.assertNull(step.getMetadataField(StepConfiguration.META_DESCRIPTION), "the description should be null");
        }

    /**
     * This tests for a specific bug where the removal works fine if you tab away from
     * the field before pressing the delete button, but if you press delete immediately
     * after a *valid* edit, the add button appears, but the edit fields linger.
     */
    @Test
    void removeOptionalFieldImmediatelyAfterEdit()
        {
        StepConfiguration step = new StepConfiguration(IncrementVariable.TYPE_ID);
        step.addSource(IncrementVariable.NAME_PARAM, ValueSourceConfiguration.forValue("var_name"));
        final String subsource_name = IncrementVariable.AMOUNT_PARAM;
        step.addSource(subsource_name, ValueSourceConfiguration.forValue(123L));
        _editor.setStep(step);
        waitForUiEvents();

        // change the value and immediately (without tabbing away) press the delete button
        fillField(id(FixedNameValueSourceEditor.getValueFieldId(subsource_name)), quoted("xyz"));
        clickOn(id(FixedNameValueSourceEditor.getDeleteButtonId(subsource_name)));

        // verify edit field removed
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(subsource_name))));
        }

    @Test
    void displayMapSubsource()
        {
        StepConfiguration step = new StepConfiguration(CallFunction.TYPE_ID);
        _editor.setStep(step);
        waitForUiEvents();

        Assertions.assertTrue(exists(id(ValueSourceMapEditor.ADD_BUTTON_ID)), "the add button for the param map is missing");

        step.addSource("param1", ValueSourceConfiguration.forValue("value1"));
        _editor.setStep(step);
        waitForUiEvents();

        Assertions.assertTrue(exists("param1"));
        Assertions.assertTrue(exists(quoted("value1")));
        }

    @Test
    void displayListSubsource()
        {
        StepConfiguration step = new StepConfiguration(MockStepWithListParam.TYPE_ID);
        _editor.setStep(step);
        waitForUiEvents();

        Assertions.assertTrue(exists(id(ValueSourceListEditor.ADD_BUTTON_ID)), "the add button for the param list is missing");

        ValueSourceConfiguration list = ValueSourceConfiguration.forType(ListSource.TYPE_ID);
        ValueSourceConfiguration item = ValueSourceConfiguration.forValue("item1");
        list.addSource(0, item);
        step.replaceSource(MockStepWithListParam.LIST_PARAM, list);
        _editor.setStep(step);
        waitForUiEvents();

        Assertions.assertTrue(exists(quoted("item1")));
        }

    @Test
    void displayMapSubsourceNoSources()
        {
        StepConfiguration step = new StepConfiguration(CallFunction.TYPE_ID);
        _editor.setStep(step);
        waitForUiEvents();

        Assertions.assertTrue(exists(id(ValueSourceMapEditor.ADD_BUTTON_ID)), "the add button for the param map is missing");
        Assertions.assertFalse(exists(FixedNameValueSourceEditor.getValueFieldId(CallFunction.ID_PARAM)), "the map should not include the function name field");
        }

    @Test
    void displayMapSubsourceWithSource()
        {
        StepConfiguration step = new StepConfiguration(CallFunction.TYPE_ID);
        final String param1 = "param1";
        final String value1 = "value1";
        step.addSource(param1, ValueSourceConfiguration.forValue(value1));
        step.addSource(CallFunction.ID_PARAM, ValueSourceConfiguration.forValue("function-name"));
        _editor.setStep(step);
        waitForUiEvents();

        Assertions.assertTrue(exists(id(ValueSourceMapEditor.ADD_BUTTON_ID)), "the add button for the param map is missing");
        Assertions.assertTrue(exists(id(InlineNamedVSE.getValueFieldId(param1))), "the map is missing the param field");
        Assertions.assertEquals(0, numberOf(id(InlineNamedVSE.getValueFieldId(CallFunction.ID_PARAM))), "the map is showing the id (function name) field");
        }

    @Override
    public Node createComponentNode()
        {
        _editor = new DefaultStepEditor(new RootStepEditContext(_project, new UndoStack(), null));
        return _editor.getNode();
        }

    @Override
    public double getDefaultHeight()
        {
        return super.getDefaultHeight() * 2;
        }

    private DefaultStepEditor _editor;
    private SimpleProject _project = new SimpleProject();
    }