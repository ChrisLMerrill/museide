package org.musetest.ui.ide.commandline;

import io.airlift.airline.*;
import javafx.application.*;
import org.musetest.core.commandline.*;
import org.slf4j.*;

/**
 * Extension to the Muse command-line launcher to edit a resource in the UI.
 *
 * This class is registered as a service in /META-INF/services/org.musetest.core.commandline.MuseCommand.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@Command(name = "edit", description = "Edit a project resource (test, macro, ...)")
public class EditResourceCommand extends MuseCommand
    {
    @Arguments(description = "filename/ID of resource to edit", required = true)
    public String test_param;

    @Override
    public void run()
        {
        try
            {
            MuseApplication.PROJECT_LOCATION = project;
            MuseResourceEditorApplication.RESOURCE_ID = test_param;
            Application.launch(MuseResourceEditorApplication.class);  // TODO pass CL options
            }
        catch (Throwable e)
            {
            LOG.error("The editor encountered an unexpected error :(", e);
            }
        }

    final static Logger LOG = LoggerFactory.getLogger(EditResourceCommand.class);
    }


