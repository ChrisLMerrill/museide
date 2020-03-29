package org.museautomation.ui.valuesource;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.extend.components.*;

import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class PrimitiveValueSourceOptionalFieldTests extends ComponentTest
    {
    @Test
    void displayInitiallyNull()
        {
        checkNoValueDisplayed();
        }

    @Test
    void displayInitiallyNullWhenRequired()
        {
        _editor.setOptional(false);
        waitForUiEvents();

        checkValueDisplayed("");
        checkErrorMode(true);
        }

    @Test
    void displayInitialValue()
        {
        final String value = "abc";
        _editor.setValue(value);
        waitForUiEvents();
        checkValueDisplayed(quoted(value));
        checkErrorMode(false);
        }

    @Test
    void addRequiredValue()
        {
        _editor.setOptional(false);
        waitForUiEvents();

        final String value = "abc123";
        AtomicReference<String> changed_value = new AtomicReference<>(null);
        _editor.setChangeListener(new_value -> changed_value.set((String)new_value));

        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), quoted(value));

        checkValueDisplayed(quoted(value));
        Assertions.assertEquals(value, _editor.getValue(), "the expected value is not set in the editor");
        Assertions.assertEquals(value, changed_value.get(), "the change event was not received or does not have the correct value");
        }

    @Test
    void addValue()
        {
        clickOn(id(PrimitiveValueOptionalField.ADD_BUTTON_ID));
        checkValueDisplayed("");

        final String value = "abc123";
        AtomicReference<String> changed_value = new AtomicReference<>(null);
        _editor.setChangeListener(new_value -> changed_value.set((String)new_value));

        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), quoted(value));

        checkValueDisplayed(quoted(value));
        Assertions.assertEquals(value, _editor.getValue(), "the expected value is not set in the editor");
        Assertions.assertEquals(value, changed_value.get(), "the change event was not received or does not have the correct value");
        }

    @Test
    void removeValue()
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
        Assertions.assertNull(_editor.getValue(), "the expected value is not set in the editor");
        Assertions.assertTrue(value_nullified.get(), "the change event to null was not received");
        }

    private void checkNoValueDisplayed()
        {
        Assertions.assertTrue(exists(id(PrimitiveValueOptionalField.ADD_BUTTON_ID)), "the add button should be displayed");
        Assertions.assertFalse(exists(id(PrimitiveValueEditorField.INPUT_ID)), "the value field should NOT be displayed");
        Assertions.assertFalse(exists(id(PrimitiveValueOptionalField.DELETE_BUTTON_ID)), "the delete button should NOT be displayed");
        }

    private void checkValueDisplayed(String value)
        {
        Assertions.assertFalse(exists(id(PrimitiveValueOptionalField.ADD_BUTTON_ID)), "the add button should NOT be displayed");
        Assertions.assertTrue(exists(id(PrimitiveValueOptionalField.DELETE_BUTTON_ID)), "the delete button should be displayed");
        Assertions.assertTrue(exists(id(PrimitiveValueEditorField.INPUT_ID)), "the value field should be displayed");
        Assertions.assertEquals(value, textOf(id(PrimitiveValueEditorField.INPUT_ID)), "the value field not displaying the expected value");
        }

    private void checkErrorMode(boolean error_shown)
        {
        Node node = lookup(id(PrimitiveValueEditorField.INPUT_ID)).query();
        Assertions.assertEquals(error_shown, InputValidation.isShowingError(node), "field shows wrong error mode ");
        }

    @Override
    protected Node createComponentNode()
        {
        _editor = new PrimitiveValueOptionalField(true);
        return _editor.getNode();
        }

    private PrimitiveValueOptionalField _editor;
    }