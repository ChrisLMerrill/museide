package org.musetest.ui.ide.navigation.resources.actions;

import org.musetest.core.*;
import org.musetest.core.resource.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class AddResourceAction extends UndoableAction
    {
    public AddResourceAction(MuseResource resource, MuseProject project)
        {
        _resource = resource;
        _project = project;
        }

    @Override
    protected boolean undoImplementation()
        {
        return _project.getResourceStorage().removeResource(_token);
        }

    @Override
    protected boolean executeImplementation()
        {
        try
            {
            _token = _project.getResourceStorage().addResource(_resource);
            return true;
            }
        catch (Exception e)
            {
            return false;
            }
        }

    public ResourceToken getToken()
        {
        return _token;
        }

    private final MuseResource _resource;
    private final MuseProject _project;
    private ResourceToken _token;
    }


