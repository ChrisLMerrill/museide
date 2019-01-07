package org.musetest.ui.valuesource;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.ui.extend.components.*;

import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class PrimitiveValueSourceOptionalFieldTests extends ComponentTest
    {
    @Test
    public void displayInitiallyNull()
        {
        checkNoValueDisplayed();
        }

    @Test
    public void displayInitiallyNullWhenRequired()
        {
        _editor.setOptional(false);
        waitForUiEvents();

        checkValueDisplayed("");
        checkErrorMode(true);
        }

    @Test
    public void displayInitialValue()
        {
        final String value = "abc";
        _editor.setValue(value);
        waitForUiEvents();
        checkValueDisplayed(quoted(value));
        checkErrorMode(false);
        }

    @Test
    public void addRequiredValue()
        {
        _editor.setOptional(false);
        waitForUiEvents();

        final String value = "abc123";
        AtomicReference<String> changed_value = new AtomicReference<>(null);
        _editor.setChangeListener(new_value -> changed_value.set((String)new_value));

        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), quoted(value));

        checkValueDisplayed(quoted(value));
        Assert.assertEquals("the expected value is not set in the editor", value, _editor.getValue());
        Assert.assertEquals("the change event was not received or does not have the correct value", value, changed_value.get());
        }

    @Test
    public void addValue()
        {
        clickOn(id(PrimitiveValueOptionalField.ADD_BUTTON_ID));
        checkValueDisplayed("");

        final String value = "abc123";
        AtomicReference<String> changed_value = new AtomicReference<>(null);
        _editor.setChangeListener(new_value -> changed_value.set((String)new_value));

        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), quoted(value));

        checkValueDisplayed(quoted(value));
        Assert.assertEquals("the expected value is not set in the editor", value, _editor.getValue());
        Assert.assertEquals("the change event was not received or does not have the correct value", value, changed_value.get());
        }

    @Test
    public void removeValue()
        {
        _editor.setValue("starter value");
        waitForUiEvents();
        AtomicBoolean value_nullified = new AtomicBoolean(false);
        _editor.setChangeListener(new_value ->
            {
            if (new_value == null)
                value_nullified.set(true);
            });

        clickOn(id(PrimitiveValueOptionalField.DELETE_BUTTON_ID));

        checkNoValueDisplayed();
        Assert.assertEquals("the expected value is not set in the editor", null, _editor.getValue());
        Assert.assertTrue("the change event to null was not received", value_nullified.get());
        }

    private void checkNoValueDisplayed()
        {
        Assert.assertTrue("the add button should be displayed", exists(id(PrimitiveValueOptionalField.ADD_BUTTON_ID)));
        Assert.assertFalse("the value field should NOT be displayed", exists(id(PrimitiveValueEditorField.INPUT_ID)));
        Assert.assertFalse("the delete button should NOT be displayed", exists(id(PrimitiveValueOptionalField.DELETE_BUTTON_ID)));
        }

    private void checkValueDisplayed(String value)
        {
        Assert.assertFalse("the add button should NOT be displayed", exists(id(PrimitiveValueOptionalField.ADD_BUTTON_ID)));
        Assert.assertTrue("the delete button should be displayed", exists(id(PrimitiveValueOptionalField.DELETE_BUTTON_ID)));
        Assert.assertTrue("the value field should be displayed", exists(id(PrimitiveValueEditorField.INPUT_ID)));
        Assert.assertEquals("the value field not displaying the expected value", value, textOf(id(PrimitiveValueEditorField.INPUT_ID)));
        }

    private void checkErrorMode(boolean error_shown)
        {
        Node node = lookup(id(PrimitiveValueEditorField.INPUT_ID)).query();
        Assert.assertEquals("field shows wrong error mode ", error_shown, InputValidation.isShowingError(node));
        }

    @Override
    protected Node createComponentNode()
        {
        _editor = new PrimitiveValueOptionalField(true);
        return _editor.getNode();
        }

    private PrimitiveValueOptionalField _editor;
    }
