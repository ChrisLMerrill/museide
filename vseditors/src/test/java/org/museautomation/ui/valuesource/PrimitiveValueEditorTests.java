package org.museautomation.ui.valuesource;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.extend.components.*;

import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class PrimitiveValueEditorTests extends ComponentTest
    {
    @Test
    void acceptString()
        {
        final String text = "abc";
        checkEntry(quoted(text), text, true);
        }

    @Test
    void acceptInteger()
        {
        checkEntry("789", 789L, true);
        }

    @Test
    void acceptTrue()
        {
        checkEntry(Boolean.TRUE.toString(), Boolean.TRUE, true);
        }

    @Test
    void acceptFalse()
        {
        checkEntry(Boolean.FALSE.toString(), Boolean.FALSE, true);
        }

    @Test
    void displayString()
        {
        checkDisplay("abc", quoted("abc"));
        }

    @Test
    void displayInteger()
        {
        checkDisplay(123L, "123");
        }

    @Test
    void displayNull()
        {
        checkDisplay(null, null);
        }

    @Test
    void displayTrue()
        {
        checkDisplay(Boolean.TRUE, Boolean.TRUE.toString());
        }

    @Test
    void displayFalse()
        {
        checkDisplay(Boolean.FALSE, Boolean.FALSE.toString());
        }

    @Test
    void changeListener()
        {
        _editor.setValue(123L);
        AtomicLong counter = new AtomicLong();
        AtomicReference value = new AtomicReference(null);
        _editor.setChangeListener(new_value ->
            {
            counter.set(counter.get() + 1);
            value.set(new_value);
            });

        final String new_value = "abc";
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), quoted(new_value));

        Assertions.assertEquals(1, counter.get(), "listener should be called once");
        Assertions.assertEquals(new_value, value.get(), "listener not passed the right value");
        }

    @Test
    void noChangeNotificationForInvalidInput()
        {
        AtomicLong counter = new AtomicLong();
        _editor.setChangeListener(new_value -> counter.set(counter.get() + 1));

        final String new_value = "abc123";
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), new_value);  // not a valid entry because string is not quoted

        Assertions.assertEquals(0, counter.get(), "listener should not be called");
        }

    @Test
    void changeValidationState()
        {
        _editor.setValue("abc");
        TextField field = lookup("#" + PrimitiveValueEditorField.INPUT_ID).query();
        Assertions.assertFalse(InputValidation.isShowingError(field), "should indicate valid input");

        clickOn(field);
        type(KeyCode.HOME);
        type(KeyCode.A);
        Assertions.assertTrue(InputValidation.isShowingError(field), "should indicate invalid input");

        type(KeyCode.BACK_SPACE);
        Assertions.assertFalse(InputValidation.isShowingError(field), "should indicate valid input");
        }

    private void checkDisplay(Object initial_value, String initial_display)
        {
        _editor.setValue(initial_value);
        waitForUiEvents();
        Assertions.assertEquals(initial_display, textOf(id(PrimitiveValueEditorField.INPUT_ID)), "initial value not displayed");
        }

    @SuppressWarnings("SameParameterValue")
    private void checkEntry(String entry, Object result, boolean is_valid)
        {
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), entry);
        Assertions.assertEquals(result, _editor.getValue(), "expected value is not returned from editor");
        Assertions.assertEquals(is_valid, _editor.isValid(), "valid state not as expected");
        }

    @Override
    protected Node createComponentNode()
        {
        _editor = new PrimitiveValueEditorField();
        BorderPane node = new BorderPane();
        node.setTop(_editor.getNode());
        node.setBottom(new TextField("other field to accept focus"));
        return node;
        }

    private PrimitiveValueEditor _editor;
    }