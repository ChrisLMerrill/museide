package org.musetest.ui.valuesource;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.builtins.step.*;
import org.musetest.builtins.value.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.step.*;
import org.musetest.core.step.descriptor.*;
import org.musetest.core.values.*;
import org.musetest.core.values.descriptor.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.edit.stack.*;
import org.musetest.ui.valuesource.mocks.*;

import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FixedNameValueSourceEditorTests extends ComponentTest
    {
    @Test
    public void initialDisplay()
        {
        final String param_value = "a string value";
        SubsourceDescriptor value_descriptor = setupStep(param_value, null);

        // verify the name is displayed
        Assert.assertEquals(value_descriptor.getDisplayName(), ((Labeled) lookup(id(FixedNameValueSourceEditor.getNameFieldId(value_descriptor.getName()))).query()).getText());

        // verify the value is displayed
        TextInputControl text_field = lookup(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))).query();
        Assert.assertEquals(quoted(param_value), text_field.getText());

        // verify the tooltip shows the description
        Assert.assertEquals(value_descriptor.getDescription(), getTooltipText(text_field));
        }

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
    public void requiredStepParameter()
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

        Assert.assertNotNull("the edit field should be visible", lookup(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))).query());
        Assert.assertNull("there should be no add button", lookup(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))).query());
        Assert.assertNull("there should be no delete button", lookup(id(FixedNameValueSourceEditor.getDeleteButtonId(value_descriptor.getName()))).query());
        }

    @Test
    public void nullRequiredStepParameter()
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

        Assert.assertNotNull("the edit field should be visible", lookup(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))).query());
        Assert.assertNull("there should be no add button", lookup(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))).query());
        Assert.assertNull("there should be no delete button", lookup(id(FixedNameValueSourceEditor.getDeleteButtonId(value_descriptor.getName()))).query());
        }

    @Test
    public void optionalStepParameter()
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
        Assert.assertNull(lookup(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))).query());

        // verify the Add button is visible
        Node add_button = lookup(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))).query();
        Assert.assertNotNull(add_button);

        // press the add button
        clickOn(add_button);

        // verify source added, editor visible, add button hidden
        Assert.assertNotNull(step.getSource(value_descriptor.getName()));
        Assert.assertNotNull(lookup(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))).query());
        Assert.assertNull(lookup(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))).query());

        // verify the Delete button is visible
        Node delete_button = lookup(id(FixedNameValueSourceEditor.getDeleteButtonId(value_descriptor.getName()))).query();
        Assert.assertNotNull(delete_button);

        // press the delete button
        clickOn(delete_button);
        waitForUiEvents();

        // verify source removed, editor removed, add button visible
        Assert.assertNull(step.getSource(value_descriptor.getName()));
        Assert.assertNull(lookup(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))).query());
        Assert.assertNotNull(lookup(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))).query());
        }

    @Test
    public void optionalValueSourceParameter()
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
        Assert.assertNull(lookup(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))).query());

        // verify the Add button is visible
        Node add_button = lookup(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))).query();
        Assert.assertNotNull(add_button);

        // press the add button
        clickOn(add_button);

        // verify source added, editor visible, add button hidden
        Assert.assertNotNull(parent_source.getSource(value_descriptor.getName()));
        Assert.assertNotNull(lookup(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))).query());
        Assert.assertNull(lookup(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))).query());

        Node delete_button = lookup(id(FixedNameValueSourceEditor.getDeleteButtonId(value_descriptor.getName()))).query();
        Assert.assertNotNull("delete button is missing", delete_button);

        // press the delete button
        clickOn(delete_button);
        waitForUiEvents();

        // verify source removed, editor hidden, add button visible
        Assert.assertNull(parent_source.getSource(value_descriptor.getName()));
        Assert.assertNull(lookup(id(FixedNameValueSourceEditor.getValueFieldId(value_descriptor.getName()))).query());
        Assert.assertNotNull(lookup(id(FixedNameValueSourceEditor.getAddButtonId(value_descriptor.getName()))).query());
        }

    @Test
    public void moreLink()
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

        // verify the "more" link exists
        Node link = lookup(id(FixedNameValueSourceEditor.getAdvancedLinkId(value_descriptor.getName()))).query();
        Assert.assertNotNull(link);

        // click it
        clickOn(link);

        // verify the editor stack was called
        Assert.assertTrue(pushed.get());
        }

    @Override
    protected Node createComponentNode()
        {
        return _grid;
        }

    private GridPane _grid = new GridPane();
    private MuseProject _project = new SimpleProject();
    }


