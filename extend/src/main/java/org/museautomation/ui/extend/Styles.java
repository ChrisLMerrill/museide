package org.museautomation.ui.extend;

import javafx.scene.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class Styles
    {
    public static String get(String name)
        {
        return Styles.class.getResource(String.format("css/%s.css", name)).toExternalForm();
        }

    public static void applyToScene(String name, Scene scene)
        {
        scene.getStylesheets().add(Styles.class.getResource(String.format("css/%s.css", name)).toExternalForm());
        }
    }