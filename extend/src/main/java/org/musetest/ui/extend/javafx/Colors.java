package org.musetest.ui.extend.javafx;

import javafx.scene.paint.*;
import org.musetest.core.step.descriptor.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class Colors
    {
    public static Color from(ColorDescriptor descriptor)
        {
        if (descriptor instanceof RgbColorDescriptor)
            {
            RgbColorDescriptor c = (RgbColorDescriptor) descriptor;
            return new Color(c._red, c._green, c._blue, 1.0);
            }
        throw new IllegalArgumentException("Unsupported ColorDescriptor type: " + descriptor.getClass().getSimpleName());
        }
    }