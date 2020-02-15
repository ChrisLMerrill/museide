package org.museautomation.ui.valuesource;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.museautomation.ui.extend.components.*;

import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class PrimitiveValueEditorTests extends ComponentTest
    {
    @Test
    public void acceptString()
        {
        final String text = "abc";
        checkEntry(quoted(text), text, true);
        }

    @Test
    public void acceptInteger()
        {
        checkEntry("789", 789L, true);
        }

    @Test
    public void acceptTrue()
        {
        checkEntry(Boolean.TRUE.toString(), Boolean.TRUE, true);
        }

    @Test
    public void acceptFalse()
        {
        checkEntry(Boolean.FALSE.toString(), Boolean.FALSE, true);
        }

    @Test
    public void displayString()
        {
        checkDisplay("abc", quoted("abc"));
        }

    @Test
    public void displayInteger()
        {
        checkDisplay(123L, "123");
        }

    @Test
    public void displayNull()
        {
        checkDisplay(null, null);
        }

    @Test
    public void displayTrue()
        {
        checkDisplay(Boolean.TRUE, Boolean.TRUE.toString());
        }

    @Test
    public void displayFalse()
        {
        checkDisplay(Boolean.FALSE, Boolean.FALSE.toString());
        }

    @Test
    public void changeListener()
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

        Assert.assertEquals("listener should be called once", 1, counter.get());
        Assert.assertEquals("listener not passed the right value", new_value, value.get());
        }

    @Test
    public void noChangeNotificationForInvalidInput()
        {
        AtomicLong counter = new AtomicLong();
        _editor.setChangeListener(new_value -> counter.set(counter.get() + 1));

        final String new_value = "abc123";
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), new_value);  // not a valid entry because string is not quoted

        Assert.assertEquals("listener should not be called", 0, counter.get());
        }

    @Test
    public void changeValidationState()
        {
        _editor.setValue("abc");
        TextField field = lookup("#" + PrimitiveValueEditorField.INPUT_ID).query();
        Assert.assertFalse("should indicate valid input", InputValidation.isShowingError(field));

        clickOn(field);
        type(KeyCode.HOME);
        type(KeyCode.A);
        Assert.assertTrue("should indicate invalid input", InputValidation.isShowingError(field));

        type(KeyCode.BACK_SPACE);
        Assert.assertFalse("should indicate valid input", InputValidation.isShowingError(field));
        }

    private void checkDisplay(Object initial_value, String initial_display)
        {
        _editor.setValue(initial_value);
        waitForUiEvents();
        Assert.assertEquals("initial value not displayed", initial_display, textOf(id(PrimitiveValueEditorField.INPUT_ID)));
        }

    private void checkEntry(String entry, Object result, boolean is_valid)
        {
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), entry);
        Assert.assertEquals("expected value is not returned from editor", result, _editor.getValue());
        Assert.assertEquals("valid state not as expected", is_valid, _editor.isValid());
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
