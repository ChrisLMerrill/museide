package org.museautomation.ui.extend.glyphs;

import javafx.scene.*;
import javafx.scene.image.*;
import javafx.stage.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class Icons
    {
    public static void setIcons(Stage stage)
        {
        stage.getIcons().add(new Image(Icons.class.getResourceAsStream("/icons/Mu-icon16.png")));
        stage.getIcons().add(new Image(Icons.class.getResourceAsStream("/icons/Mu-icon32.png")));
        stage.getIcons().add(new Image(Icons.class.getResourceAsStream("/icons/Mu-icon64.png")));
        }

    public static void setIcons(Node node)
        {
        setIcons((Stage)node.getScene().getWindow());
        }
    }