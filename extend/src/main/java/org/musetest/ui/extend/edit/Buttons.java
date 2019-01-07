package org.musetest.ui.extend.edit;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import org.musetest.ui.extend.glyphs.*;

import javax.annotation.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class Buttons
    {
    public static Button createSave()
        {
        return createImageButton("FA:CHECK_CIRCLE", Color.GREEN, SAVE_ID, "Save");
        }
    public final static String SAVE_ID = "save";

    public static Button createCancel()
        {
        return createImageButton("FA:TIMES_CIRCLE", Color.DARKRED, CANCEL_ID, "Cancel");
        }
    public final static String CANCEL_ID = "cancel";

    public static Button createClose(@Nullable Integer size)
        {
        if (size == null)
            return createImageButton("FA:TIMES_CIRCLE", Color.DARKGRAY, CLOSE_ID, "Close");
        else
            return createImageButton("FA:TIMES_CIRCLE", Color.DARKGRAY, CLOSE_ID, "Close", size);
        }
    public final static String CLOSE_ID = "close";

    public static Button createRemove()
        {
        return createRemove(14);
        }

    public static Button createRemove(int size)
        {
        return createImageButton("FA:MINUS_CIRCLE", Color.DARKRED, "remove", "Remove", size);
        }

    public static Button createRemove(String id)
        {
        return createImageButton("FA:MINUS_CIRCLE", Color.DARKRED, id, "Remove");
        }

    public static Button createAdd()
        {
        return createAdd(14);
        }

    public static Button createAdd(int size)
        {
        return createImageButton("FA:PLUS_CIRCLE", Color.GREEN, "add", "Add", size);
        }

    public static Button createUpArrow()
        {
        return createImageButton("FA:ARROW_UP", Color.DARKGRAY.darker().darker().darker(), "uparrow", "Move Up", 14);
        }

    public static Button createDownArrow()
        {
        return createImageButton("FA:ARROW_DOWN", Color.DARKGRAY.darker().darker().darker(), "downarrow", "Move Down", 14);
        }

    public static Hyperlink createLinkWithIcon(String link_text, String glyph_name, String id, String tooltip, ContentDisplay icon_alignment)
        {
        Hyperlink link = new Hyperlink(link_text);
        link.setGraphic(Glyphs.create(glyph_name));
        link.setId(id);
        link.setTooltip(new Tooltip(tooltip));
        link.setContentDisplay(icon_alignment);
        return link;
        }

    private static Button createImageButton(String glyph_name, Color color, String id, String tooltip)
        {
        return createImageButton(glyph_name, color, id, tooltip, 14);
        }

    private static Button createImageButton(String glyph_name, Color color, String id, String tooltip, int size)
        {
        final Node colored_glyph = Glyphs.create(glyph_name, color, size);
        final Node grey_glyph = Glyphs.create(glyph_name, Color.GRAY, size);
        Button button = new Button(null, grey_glyph);
        button.getStyleClass().clear();
        button.setCursor(Cursor.HAND);
        button.setId(id);
        button.setTooltip(new Tooltip(tooltip));
        button.setOnMouseEntered(e -> button.setGraphic(colored_glyph));
        button.setOnMouseExited(e -> button.setGraphic(grey_glyph));
        return button;
        }
    }


