package org.musetest.ui.ide;

import javafx.scene.image.*;
import javafx.stage.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class IdeWindow extends Stage
    {
    public IdeWindow()
        {
        initIcons(this);
        }

    public static void initIcons(Stage stage)
        {
        stage.getIcons().add(new Image(IdeWindow.class.getResourceAsStream("/icons/Mu-icon16.png")));
        stage.getIcons().add(new Image(IdeWindow.class.getResourceAsStream("/icons/Mu-icon32.png")));
        stage.getIcons().add(new Image(IdeWindow.class.getResourceAsStream("/icons/Mu-icon64.png")));
        }
    }


