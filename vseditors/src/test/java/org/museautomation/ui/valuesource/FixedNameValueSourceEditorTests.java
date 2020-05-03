package org.museautomation.ui.valuesource;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.valuesource.mocks.*;
import org.museautomation.builtins.step.*;
import org.museautomation.builtins.value.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.step.descriptor.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.descriptor.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.stack.*;

import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FixedNameValueSourceEditorTests extends ComponentTest
    {
    @Test
    void initialDisplay()
        {
        final String param_value = "a string value";
        SubsourceDescriptor value_descriptor = setupStep(param_value, null);

        // verify the name is displayed
        Assertions.assertEquals(value_descriptor.getDisplayName(), ((Labeled) lookup(id(FixedNameValueSourceEditor.getNameFieldId(value_descriptor.getName()))).query()).getText());

        // verify the value is displayed
        TextInputControl text_field = lookup(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))).query();
        Assertions.assertEquals(quoted(param_value), text_field.getText());

        // verify the tooltip shows the description
        Assertions.assertEquals(value_descriptor.getDescription(), getTooltipText(text_field));
        }

    @SuppressWarnings("SameParameterValue")
    private SubsourceDescriptor setupStep(String param_value, EditorStack stack)
        {
        StepDescriptor step_descriptor = _project.getStepDescriptors().get(LogMessage.class);
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue(param_value);
        SubsourceDescriptor value_descriptor = step_descriptor.getSubsourceDescriptors()[0];
        final FixedNameValueSourceEditor editor = new FixedNameValueSourceEditor(_project, new UndoStack(), value_descriptor, new StepConfiguration(LogMessage.TYPE_ID));
        editor.setSource(source);
        if (stack != null)
            editor.setStack(stack);
        Platform.runLater(() ->
            {
            _grid.add(editor.getNameLabel(), 0, 0);
            _grid.add(editor.getNode(), 1, 0);
            });
        waitForUiEvents();
        return value_descriptor;
        }

    @Test
    void requiredStepParameter()
        {
        StepDescriptor step_descriptor = _project.getStepDescriptors().get(StoreVariable.class);
        SubsourceDescriptor value_descriptor = step_descriptor.getSubsourceDescriptors()[0];
        StepConfiguration step = new StepConfiguration(StoreVariable.TYPE_ID);
        ValueSourceConfiguration subsource = ValueSourceConfiguration.forValue("a value");
        step.addSource(value_descriptor.getName(), subsource);
        final FixedNameValueSourceEditor editor = new FixedNameValueSourceEditor(_project, new UndoStack(), value_descriptor, step);
        editor.setSource(subsource);
        Platform.runLater(() ->
            {
            _grid.add(editor.getNameLabel(), 0, 0);
            _grid.add(editor.getNode(), 1, 0);
            });
        waitForUiEvents();

        Assertions.assertTrue(exists(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))), "the edit field should be visible");
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))), "there should be no add button");
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getDeleteButtonId(value_descriptor.getName()))), "there should be no delete button");
        }

    @Test
    void nullRequiredStepParameter()
        {
        StepDescriptor step_descriptor = _project.getStepDescriptors().get(StoreVariable.class);
        SubsourceDescriptor value_descriptor = step_descriptor.getSubsourceDescriptors()[0];
        StepConfiguration step = new StepConfiguration(StoreVariable.TYPE_ID);
        ValueSourceConfiguration subsource = ValueSourceConfiguration.forType(StringValueSource.TYPE_ID);
        step.addSource(value_descriptor.getName(), subsource);
        final FixedNameValueSourceEditor editor = new FixedNameValueSourceEditor(_project, new UndoStack(), value_descriptor, step);
        editor.setSource(subsource);
        Platform.runLater(() ->
            {
            _grid.add(editor.getNameLabel(), 0, 0);
            _grid.add(editor.getNode(), 1, 0);
            });
        waitForUiEvents();

        Assertions.assertTrue(exists(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))), "the edit field should be visible");
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))), "there should be no add button");
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getDeleteButtonId(value_descriptor.getName()))), "there should be no delete button");
        }

    @Test
    void optionalStepParameter()
        {
        StepDescriptor step_descriptor = _project.getStepDescriptors().get(IncrementVariable.class);
        SubsourceDescriptor value_descriptor = step_descriptor.getSubsourceDescriptors()[1];
        StepConfiguration step = new StepConfiguration(IncrementVariable.TYPE_ID);
        final FixedNameValueSourceEditor editor = new FixedNameValueSourceEditor(_project, new UndoStack(), value_descriptor, step);
        Platform.runLater(() ->
            {
            _grid.add(editor.getNameLabel(), 0, 0);
            _grid.add(editor.getNode(), 1, 0);
            });
        waitForUiEvents();

        // verify the editor is not visible
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))));

        // press the add button
        clickOn(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName())));

        // verify source added, editor visible, add button hidden
        Assertions.assertNotNull(step.getSource(value_descriptor.getName()));
        Assertions.assertTrue(exists(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))));
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))));

        // press the delete button
        clickOn(id(FixedNameValueSourceEditor.getDeleteButtonId(value_descriptor.getName())));
        waitForUiEvents();

        // verify source removed, editor removed, add button visible
        Assertions.assertNull(step.getSource(value_descriptor.getName()));
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))));
        Assertions.assertTrue(exists(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))));
        }

    @Test
    void optionalValueSourceParameter()
        {
        ValueSourceDescriptor source_descriptor = _project.getValueSourceDescriptors().get(SourceWithOptionalNamedSubsource.class);
        SubsourceDescriptor value_descriptor = source_descriptor.getSubsourceDescriptors()[0];
        ValueSourceConfiguration parent_source = ValueSourceConfiguration.forValue("abc123");
        final FixedNameValueSourceEditor editor = new FixedNameValueSourceEditor(_project, new UndoStack(), value_descriptor, parent_source);
        Platform.runLater(() ->
            {
            _grid.add(editor.getNameLabel(), 0, 0);
            _grid.add(editor.getNode(), 1, 0);
            });
        waitForUiEvents();

        // verify the editor is not visible
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))));

        // press the add button
        clickOn(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName())));

        // verify source added, editor visible, add button hidden
        Assertions.assertNotNull(parent_source.getSource(value_descriptor.getName()));
        Assertions.assertTrue(exists(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))));
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))));

        // press the delete button
        clickOn(id(FixedNameValueSourceEditor.getDeleteButtonId(value_descriptor.getName())));
        waitForUiEvents();

        // verify source removed, editor hidden, add button visible
        Assertions.assertNull(parent_source.getSource(value_descriptor.getName()));
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))));
        Assertions.assertTrue(exists(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))));
        }

    @Test
    void moreLink()
        {
        final String param_value = "a string value";
        AtomicBoolean pushed = new AtomicBoolean(false);
        SubsourceDescriptor value_descriptor = setupStep(param_value, new EditorStack(null, new UndoStack())
            {
            @Override
            public void push(StackableEditor editor, String name)
                {
                pushed.set(true);
                }

            @Override
            protected void notifyEditCommit()
                {

                }
            });

        // click it
        clickOn(id(FixedNameValueSourceEditor.getAdvancedLinkId(value_descriptor.getName())));

        // verify the editor stack was called
        Assertions.assertTrue(pushed.get());
        }

    @Override
    public Node createComponentNode()
        {
        return _grid;
        }

    private GridPane _grid = new GridPane();
    private MuseProject _project = new SimpleProject();
    }