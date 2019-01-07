package org.musetest.ui.extend.components;

import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import org.musetest.ui.extend.glyphs.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class GraphicNodeBuilder
    {
    public static GraphicNodeBuilder getInstance()
        {
        return INSTANCE;
        }

    public Node getImageResourceView(String filename)
        {
        Image resource = _images.get(filename);
        if (resource == null)
            {
            resource = new Image(getClass().getResourceAsStream(filename));
            _images.put(filename, resource);
            }
        return new ImageView(resource);
        }

    public Node getSpinner()
	    {
	    Node spinner = getImageResourceView("progress-spinner.gif");
	    spinner.getStyleClass().add(SPINNER_CLASS);
	    return spinner;
	    }

    public Node getEllipsis()
	    {
	    Node ellipsis = getImageResourceView("ellipsis-animated.gif");
	    ellipsis.getStyleClass().add(ELLIPSIS_CLASS);
	    return ellipsis;
	    }

    public Node getNode(String descriptor, Color color)
        {
        if (descriptor.startsWith(GLYPH))
            return Glyphs.create(descriptor.substring(GLYPH.length() + 1), color);
        else
            return null;
        }

    protected Map<String, Image> _images = new HashMap<>();

    protected final static String GLYPH = "glyph";
    public final static String SPINNER_CLASS = "spinner";
    public final static String ELLIPSIS_CLASS = "ellipsis";

    private static GraphicNodeBuilder INSTANCE = new GraphicNodeBuilder();
    }


