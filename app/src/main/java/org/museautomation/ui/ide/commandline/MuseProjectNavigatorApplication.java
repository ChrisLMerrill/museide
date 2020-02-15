package org.museautomation.ui.ide.commandline;

import javafx.stage.*;
import org.museautomation.ui.ide.navigation.resources.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MuseProjectNavigatorApplication extends MuseApplication
    {
    @Override
    public void start(Stage stage) throws Exception
        {
        ProjectNavigator navigator = new ProjectNavigator(_project, _editors);
        showApplicationWindow(stage, navigator.getScene(), _project.getName());
        navigator.requestFocus();
        }

    @Override
    protected String getSettingsName()
        {
        return "navigator-app";
        }

    private MultiWindowResourceEditors _editors = new MultiWindowResourceEditors();
    }


