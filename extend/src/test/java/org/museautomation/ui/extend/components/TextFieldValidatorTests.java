package org.museautomation.ui.extend.components;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.extend.components.validation.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TextFieldValidatorTests extends ComponentTest
    {
    @Test
    void testInitialValue()
        {
        NotBlankTextValidator validator = new NotBlankTextValidator();
        validator.attachTo(_text_field);
        waitForUiEvents();

        Assertions.assertTrue(InputValidation.isShowingError(_text_field));
        }

    @Test
    void testChangedToValid()
        {
        NotBlankTextValidator validator = new NotBlankTextValidator();
        validator.attachTo(_text_field);
        waitForUiEvents();

        clickOn("#text").write("abc");

        Assertions.assertFalse(InputValidation.isShowingError(_text_field));
        }

    @Test
    void testAndValidators()
        {
        AndValidator and = new AndValidator();
        and.add(new IntegerFieldValidator());
        and.add(new NotBlankTextValidator());
        and.attachTo(_text_field);

        waitForUiEvents();

        // starts out invalid
        Assertions.assertTrue(InputValidation.isShowingError(_text_field));
        clickOn("#text").write("abc");
        // still invalid, despite being non-blank
        Assertions.assertTrue(InputValidation.isShowingError(_text_field));
        }

    @Override
    public Node createComponentNode()
        {
        BorderPane root = new BorderPane();

        _text_field = new TextField();
        _text_field.setId("text");
        root.setTop(_text_field);

        return root;
        }

    private TextField _text_field;
    }