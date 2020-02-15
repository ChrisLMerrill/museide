package org.museautomation.ui.step;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
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
    public void displayStep()
        {
        StepConfiguration step = new StepConfiguration(LogMessage.TYPE_ID);
        step.setMetadataField(StepConfiguration.META_DESCRIPTION, "description of the step");
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue("the message contents");
        step.addSource(LogMessage.MESSAGE_PARAM, source);
        _project = new SimpleProject();
        _editor.setStep(step);
        waitForUiEvents();

        // description should be in the description field
        Assert.assertEquals(step.getMetadataField(StepConfiguration.META_DESCRIPTION), ((TextInputControl) lookup("#" + DefaultStepEditor.DESCRIPTION_FIELD_ID).query()).getText());

        // step type should be selected
        StepDescriptor descriptor = _project.getStepDescriptors().get(step);
        Assert.assertEquals(descriptor.getName(), ((Labeled) lookup("#" + DefaultStepEditor.TYPE_FIELD_ID).query()).getText().trim());

        // step help short and long descriptions
        Assert.assertEquals(descriptor.getShortDescription(), ((Text) lookup("#" + DefaultStepEditor.HELP_TYPE_SHORT_DESCRIPTION).query()).getText());

        // first (and only) parameter should contain the message
        Assert.assertEquals(quoted(source.getValue()), ((TextInputControl) lookup("#" + FixedNameValueSourceEditor.getValueFieldId(descriptor.getSubsourceDescriptors()[0].getName())).query()).getText());
        }

    @Test
    public void changeStepType()
        {
        StepConfiguration step = setupLogStep();

        StepDescriptor verify_descriptor = _project.getStepDescriptors().get(Verify.TYPE_ID);

        // change the step type
        clickOn("#" + DefaultStepEditor.TYPE_FIELD_ID).clickOn(verify_descriptor.getName());
        waitForUiEvents();

        // verify step, chooser selection and description is changed
        Assert.assertEquals(verify_descriptor.getType(), step.getType());
        Assert.assertEquals(verify_descriptor.getName(), ((Labeled) lookup("#" + DefaultStepEditor.TYPE_FIELD_ID).query()).getText().trim());
        Assert.assertEquals(verify_descriptor.getShortDescription(), ((Text) lookup("#" + DefaultStepEditor.HELP_TYPE_SHORT_DESCRIPTION).query()).getText().trim());

        // verify the parameters changed
        Assert.assertFalse("the message param wasn't removed", exists(id(FixedNameValueSourceEditor.getValueFieldId(LogMessage.MESSAGE_PARAM))));
        Assert.assertTrue("the id param wasn't added", exists(id(FixedNameValueSourceEditor.getAddButtonId(Verify.CONDITION_PARAM))));
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
    public void changeDescription()
        {
        StepConfiguration step = setupLogStep();

        String new_description = "new_description";
        fillFieldAndTabAway(id(DefaultStepEditor.DESCRIPTION_FIELD_ID), new_description);

        Assert.assertEquals("the description is missing", new_description, step.getMetadataField(StepConfiguration.META_DESCRIPTION));
        }

    @Test
    public void nullifyEmptyDescription()
        {
        StepConfiguration step = setupLogStep("non-empty description");
        waitForUiEvents();

        clearFieldAndTabAway(id(DefaultStepEditor.DESCRIPTION_FIELD_ID));

        Assert.assertNull("the description should be null", step.getMetadataField(StepConfiguration.META_DESCRIPTION));
        }

    @Test
    public void nullifyWhitespaceDescription()
        {
        StepConfiguration step = setupLogStep("non-empty description");
        waitForUiEvents();

        fillFieldAndTabAway(id(DefaultStepEditor.DESCRIPTION_FIELD_ID), " ");

        Assert.assertNull("the description should be null", step.getMetadataField(StepConfiguration.META_DESCRIPTION));
        }

    /**
     * This tests for a specific bug where the removal works fine if you tab away from
     * the field before pressing the delete button, but if you press delete immediately
     * after a *valid* edit, the add button appears, but the edit fields linger.
     */
    @Test
    public void removeOptionalFieldImmediatelyAfterEdit()
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
        Assert.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(subsource_name))));
        }

    @Test
    public void displayMapSubsource()
        {
        StepConfiguration step = new StepConfiguration(CallFunction.TYPE_ID);
        _editor.setStep(step);
        waitForUiEvents();

        Assert.assertTrue("the add button for the param map is missing", exists(id(ValueSourceMapEditor.ADD_BUTTON_ID)));

        step.addSource("param1", ValueSourceConfiguration.forValue("value1"));
        _editor.setStep(step);
        waitForUiEvents();

        Assert.assertTrue(exists("param1"));
        Assert.assertTrue(exists(quoted("value1")));
        }

    @Test
    public void displayListSubsource()
        {
        StepConfiguration step = new StepConfiguration(MockStepWithListParam.TYPE_ID);
        _editor.setStep(step);
        waitForUiEvents();

        Assert.assertTrue("the add button for the param list is missing", exists(id(ValueSourceListEditor.ADD_BUTTON_ID)));

        ValueSourceConfiguration list = ValueSourceConfiguration.forType(ListSource.TYPE_ID);
        ValueSourceConfiguration item = ValueSourceConfiguration.forValue("item1");
        list.addSource(0, item);
        step.replaceSource(MockStepWithListParam.LIST_PARAM, list);
        _editor.setStep(step);
        waitForUiEvents();

        Assert.assertTrue(exists(quoted("item1")));
        }

    @Test
    public void displayMapSubsourceNoSources()
        {
        StepConfiguration step = new StepConfiguration(CallFunction.TYPE_ID);
        _editor.setStep(step);
        waitForUiEvents();

        Assert.assertTrue("the add button for the param map is missing", exists(id(ValueSourceMapEditor.ADD_BUTTON_ID)));
        Assert.assertFalse("the map should not include the function name field", exists(FixedNameValueSourceEditor.getValueFieldId(CallFunction.ID_PARAM)));
        }

    @Test
    public void displayMapSubsourceWithSource()
        {
        StepConfiguration step = new StepConfiguration(CallFunction.TYPE_ID);
        final String param1 = "param1";
        final String value1 = "value1";
        step.addSource(param1, ValueSourceConfiguration.forValue(value1));
        step.addSource(CallFunction.ID_PARAM, ValueSourceConfiguration.forValue("function-name"));
        _editor.setStep(step);
        waitForUiEvents();

        Assert.assertTrue("the add button for the param map is missing", exists(id(ValueSourceMapEditor.ADD_BUTTON_ID)));
        Assert.assertTrue("the map is missing the param field", exists(id(InlineNamedVSE.getValueFieldId(param1))));
        Assert.assertEquals("the map is showing the id (function name) field", 0, numberOf(id(InlineNamedVSE.getValueFieldId(CallFunction.ID_PARAM))));
        }

    @Override
    protected Node createComponentNode()
        {
        _editor = new DefaultStepEditor(new RootStepEditContext(_project, new UndoStack(), null));
        return _editor.getNode();
        }

    @Override
    protected double getDefaultHeight()
        {
        return super.getDefaultHeight() * 2;
        }

    private DefaultStepEditor _editor;
    private SimpleProject _project = new SimpleProject();
    }


