package org.museautomation.ui.ide.commandline;

import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.ui.extend.icons.*;
import org.museautomation.ui.settings.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class MuseApplication extends Application
    {
    @Override
    public void init() throws Exception
        {
        File project_file;
        if (PROJECT_LOCATION == null)
            project_file = new File(System.getProperty("user.dir"));
        else
            project_file = new File(PROJECT_LOCATION);
        if (!project_file.exists() || !project_file.isDirectory())
            {
            LOG.error("The project location should specify a folder (relative or absolute): " + PROJECT_LOCATION);
            return;
            }

        _project = ProjectFactory.create(project_file, new HashMap<>()); // TODO pass CL options
        }

    protected void showApplicationWindow(Stage stage, Scene scene, String name)
        {
        stage.setScene(scene);
        StageSettings.get(getSettingsName()).register(stage);
        stage.setTitle(name);
        Icons.setIcons(stage);
        stage.show();
        }

    protected abstract String getSettingsName();

    @Override
    public void stop()
        {
        Closer.get().closeAll();
        }

    protected MuseProject _project;

    public static String PROJECT_LOCATION;

    final static Logger LOG = LoggerFactory.getLogger(MuseApplication.class);
    }


