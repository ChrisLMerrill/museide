package org.museautomation.ui.ide.commandline;

import javafx.application.*;
import javafx.stage.*;
import org.museautomation.ui.ide.*;
import org.museautomation.core.*;
import org.museautomation.ui.extend.edit.*;
import org.slf4j.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MuseResourceEditorApplication extends MuseApplication
    {
    @Override
    public void init() throws Exception
        {
        super.init();

        //
        // Get the resource
        //
        _resource = _project.getResourceStorage().getResource(RESOURCE_ID);
        String id = RESOURCE_ID;
        while (_resource == null && id.contains("."))
            {
            id = id.substring(0, id.lastIndexOf("."));
            _resource = _project.getResourceStorage().getResource(id);
            }
        if (_resource == null)
            {
            String error = String.format("Resource with id %s was not found in the project.", RESOURCE_ID);
            LOG.error(error);
            notifyPreloader(new Preloader.ErrorNotification(null, error, null));
            return;
            }

        _editor = EditorSelector.get(_project).get(_resource);
        if (_editor != null)
            return;

        String error = String.format("No editor found for resource type %s (id=%s).", _resource.getType().getName(), RESOURCE_ID);
        LOG.error(error);
        notifyPreloader(new Preloader.ErrorNotification(null, error, null));
        }

    @Override
    public void start(Stage stage) throws Exception
        {
        if (_resource == null || _editor == null)
            return;

        _editor.editResource(_project, _resource);
        showApplicationWindow(stage, _editor.getScene(), _resource.getId());
        _editor.requestFocus();
        }

    @Override
    protected String getSettingsName()
        {
        return "editor-app-" + _resource.getType().getTypeId();
        }

    private MuseResource _resource;
    private MuseResourceEditor _editor;

    public static String RESOURCE_ID;

    final static Logger LOG = LoggerFactory.getLogger(MuseResourceEditorApplication.class);
    }


