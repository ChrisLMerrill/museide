package org.museautomation.ui.ide.commandline;

import io.airlift.airline.*;
import javafx.application.*;
import org.museautomation.core.commandline.*;
import org.slf4j.*;

/**
 * Extension to the Muse command-line launcher to open the project navigator.
 *
 * This class is registered as a service in /META-INF/services/org.museautomation.core.commandline.MuseCommand.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@Command(name = "navigate", description = "Navigate a project")
public class NavigateProjectCommand extends MuseCommand
    {
    @Override
    public void run()
        {
        try
            {
            MuseApplication.PROJECT_LOCATION = project;
            Application.launch(MuseProjectNavigatorApplication.class);  // TODO pass CL options
            //LauncherImpl.launchApplication(MuseProjectNavigatorApplication.class, ProjectPreloaderSplash.class, new String[0]);  // TODO pass CL options
            }
        catch (Throwable e)
            {
            LOG.error("The editor encountered an unexpected error :(", e);
            }
        }

    private final static Logger LOG = LoggerFactory.getLogger(NavigateProjectCommand.class);
    }


