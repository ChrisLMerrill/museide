package org.museautomation.ui.extend.components;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class PopupDialogTests extends ComponentTest
    {
    @Test
    void testOk()
        {
        Assertions.assertFalse(exists(OK_LABEL));           // popup contents not visible
        Assertions.assertFalse(exists(CHECKER_LABEL));

        clickOn(PUSHME_LABEL);
        Assertions.assertTrue(exists(OK_LABEL));            // popup contents visible
        Assertions.assertTrue(exists(CHECKER_LABEL));

        clickOn(OK_LABEL);
        Assertions.assertFalse(exists(OK_LABEL));
        Assertions.assertFalse(exists(CHECKER_LABEL));
        Assertions.assertTrue(_ok_pressed);
        }

    @Test
    void rejectOkPush()
        {
        _close_on_ok = false;

        clickOn(PUSHME_LABEL);
        clickOn(OK_LABEL);

        Assertions.assertTrue(exists(OK_LABEL));            // popup contents visible

        _popper.destroy();
        }

    @Override
    public Node createComponentNode()
        {
        _close_on_ok = true;
        _ok_pressed = false;

        BorderPane borders = new BorderPane();

        Button button = new Button(PUSHME_LABEL);
        borders.setCenter(button);
        button.setOnAction(event -> _popper.show(button));

        PopupDialog.makeFast();
        _popper = new PopupDialog(OK_LABEL, "Test the PopupDialog")
            {
            @Override
            protected Node createContent()
                {
                GridPane grid = new GridPane();
                CheckBox checker = new CheckBox(CHECKER_LABEL);
                grid.add(checker, 0, 0);
                return grid;
                }

            @Override
            protected boolean okPressed()
                {
                _ok_pressed = true;
                return _close_on_ok;
                }
            };

        return borders;
        }

    private PopupDialog _popper;
    private boolean _ok_pressed;
    private boolean _close_on_ok;

    private final static String PUSHME_LABEL = "pushme";
    private final static String OK_LABEL = "ok";
    private final static String CHECKER_LABEL = "check me";
    }