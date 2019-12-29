package org.musetest.ui.extend.glyphs;

import de.jensd.fx.glyphs.fontawesome.*;
import de.jensd.fx.glyphs.icons525.*;
import de.jensd.fx.glyphs.octicons.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.*;

/**
 * A utility class for creating graphics from glyph fonts. It leverages the FontAwesomeFX library:
 * https://bitbucket.org/Jerady/fontawesomefx
 *
 * Currently, only supports FontAwesome, but can be easily extended to support the other fonts
 * supported by the FontAwesomeFX package (Google Material Design Font, Material Icons, Weather Icons.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class Glyphs
    {
    /**
     * Load a glyph in the specified color
     *
     * @param glyph_code See #create(String).
     * @param color The color to render the glyph in.
     */
    public static Node create(String glyph_code, Color color)
        {
        Shape icon = (Shape) create(glyph_code);
        icon.setFill(color);
        return icon;
        }

    public static String getStyleName(String glyph_code, Color color, Integer size)
	    {
	    if (color == null)
	    	color = Color.BLACK;
	    if (size == null)
	    	size = 14;
	    String glyph_name = glyph_code.replace(":", "_");
	    return String.format("%s-R%dG%dB%d-%d", glyph_name, (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), size);
	    }

    /**
     * Load a glyph in the specified color
     *
     * @param glyph_code See #create(String).
     * @param color The color to render the glyph in.
     * @param size The size of the icon (pixels)
     */
    public static Node create(String glyph_code, Color color, int size)
        {
        Shape icon = (Shape) create(glyph_code, size);
        icon.setFill(color);
        return icon;
        }

    /**
     * Load a glyph using a 2-part string identifier.
     *
     * @param glyph_code A 2-part glyph id in the format "family:glyph". The first part identifies the font family.
     *                   Currently supports "FA" (or "FontAwesome"). Second part is either the name of the glyph, as
     *                   identified by the FontAwesomeFX enums, or the unicode char of the font glyph.
     */
    public static Node create(String glyph_code)
        {
        return create(glyph_code, 14);
        }

    /**
     * Load a glyph using a 2-part string identifier and size.
     *
     * @param glyph_code A 2-part glyph id in the format "family:glyph". The first part identifies the font family.
     *                   Currently supports "FA" (or "FontAwesome"). Second part is either the name of the glyph, as
     *                   identified by the FontAwesomeFX enums, or the unicode char of the font glyph.
     * @param size      The size of the glyph (pixels).
     */
    public static Node create(String glyph_code, int size)
        {
        StringTokenizer tokenizer = new StringTokenizer(glyph_code, ":");
        String family = tokenizer.nextToken();
        String name_char = tokenizer.nextToken();

        switch (family)
            {
            case "FontAwesome":
            case "FA":
                FontAwesomeIcon icon_fa = null;
                if (name_char.length() > 1)
                    try
                        {
                        icon_fa = FontAwesomeIcon.valueOf(name_char);
                        }
                    catch (IllegalArgumentException e)
                        {
                        // will print message later
                        }
                else
                    {
                    for (FontAwesomeIcon candidate : FontAwesomeIcon.values())
                        if (candidate.unicode().equals(name_char))
                            {
                            icon_fa = candidate;
                            break;
                            }
                    }
                if (icon_fa != null)
                    {
                    FontAwesomeIconView icon_fa_view = new FontAwesomeIconView(icon_fa);
                    icon_fa_view.setGlyphSize(size);
                    icon_fa_view.getStyleClass().add(getStyleName(glyph_code, null, size));
                    return icon_fa_view;
                    }
            case "Icons525":
            case "I5":
                Icons525 icon_525 = null;
                if (name_char.length() > 1)
                    try
                        {
                        icon_525 = Icons525.valueOf(name_char);
                        }
                    catch (IllegalArgumentException e)
                        {
                        // will print message later
                        }
                else
                    {
                    for (Icons525 candidate : Icons525.values())
                        if (candidate.unicode().equals(name_char))
                            {
                            icon_525 = candidate;
                            break;
                            }
                    }
                if (icon_525 != null)
                    {
                    Icons525View icon_525_view = new Icons525View(icon_525);
                    icon_525_view.setGlyphSize(size);
                    return icon_525_view;
                    }
            case "Octicons":
            case "OCT":
                OctIcon octicon = null;
                if (name_char.length() > 1)
                    try
                        {
                        octicon = OctIcon.valueOf(name_char);
                        }
                    catch (IllegalArgumentException e)
                        {
                        // will print message later
                        }
                else
                    {
                    for (OctIcon candidate : OctIcon.values())
                        if (candidate.unicode().equals(name_char))
                            {
                            octicon = candidate;
                            break;
                            }
                    }
                if (octicon != null)
                    {
                    OctIconView octicon_view = new OctIconView(octicon);
                    octicon_view.setGlyphSize(size);
                    return octicon_view;
                    }
            }

        System.err.println("Unable to locate glyph " + glyph_code);
        return Glyphs.create("FA:SQUARE_ALT");
        }
    }


