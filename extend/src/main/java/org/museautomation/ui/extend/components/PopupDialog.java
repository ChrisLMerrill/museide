package org.museautomation.ui.extend.components;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;
import org.controlsfx.control.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class PopupDialog
    {
    public PopupDialog(String ok_button_label, String header)
        {
        _popper = new PopOver();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        grid.setVgap(5);
        _popper.setContentNode(grid);

        if (header != null)
            {
            _popper.setTitle(header);
            _popper.setHeaderAlwaysVisible(true);
            }

        grid.add(createContent(), 0, 0);

        // button area
        HBox button_holder = new HBox();
        button_holder.alignmentProperty().setValue(Pos.CENTER);
        button_holder.setSpacing(5);
        grid.add(button_holder, 0, 1);

        // ok button
        if (ok_button_label == null)
            ok_button_label = "Ok";
        _ok_button = new Button(ok_button_label);
        _ok_button.setId(OK_BUTTON_ID);
        _ok_button.setOnAction(event1 ->
            {
            if (okPressed())
                {
                _popper.hide();
                destroy();
                }
            });
        button_holder.getChildren().add(_ok_button);

        if (MAKE_FAST)
            {
            _popper.setFadeInDuration(Duration.millis(5));
            _popper.setFadeOutDuration(Duration.millis(5));
            MAKE_FAST = false;
            }
        }

    /**
     * Implement to provide content for the popup dialog
     */
    protected abstract Node createContent();

    /**
     * Implement to handle the ok event
     */
    protected abstract boolean okPressed();

    /**
     * Override to cleanup resources, deregister listeners, etc.
     *
     * Call super() to ensure the popup is closed.
     */
    public void destroy()
        {
        _popper.hide();
        }

    public void show(Node owner)
        {
        _popper.show(owner);
        }

    public void setOkButtonEnabled(boolean enabled)
        {
        _ok_button.setDisable(!enabled);
        }

    /**
     * For testing purposes only. This will cause the next constructed PopupDialog to use fast transitions.
     */
    public static void makeFast()
        {
        MAKE_FAST = true;
        }

    private final PopOver _popper;
    private final Button _ok_button;

    private static boolean MAKE_FAST = false;

    public final static String OK_BUTTON_ID = "omuc-ok-button";
    }


