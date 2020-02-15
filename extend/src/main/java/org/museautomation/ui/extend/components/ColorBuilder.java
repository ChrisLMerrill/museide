package org.museautomation.ui.extend.components;

import javafx.scene.paint.*;
import org.museautomation.core.step.descriptor.*;

import java.util.*;

/**
 * Responsible for turning ColorDescriptors into JavaFX Colors.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ColorBuilder
    {
    public static Color getColor(ColorDescriptor descriptor)
        {
        Color cached = COLORS.get(descriptor);
        if (cached != null)
            return cached;
        if (descriptor instanceof RgbColorDescriptor)
            {
            RgbColorDescriptor rgb = (RgbColorDescriptor) descriptor;
            Color new_color = new Color(rgb._red, rgb._green, rgb._blue, 1.0f);
            COLORS.put(descriptor, new_color);
            return new_color;
            }
        throw new IllegalArgumentException(String.format("ColorDescriptors of type %s are not supported here", descriptor.getClass().getSimpleName()));
        }

    private static Map<ColorDescriptor, Color> COLORS = new HashMap<>();
    }