package org.museautomation.ui.ide.navigation.resources.actions;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.resource.types.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class CreateResourceAction extends UndoableAction
    {
    public CreateResourceAction(ResourceType type, String id, MuseProject project)
        {
        _type = type;
        _id = id;
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
            MuseResource resource = _type.create();
            resource.setId(_id);
            _token = _project.getResourceStorage().addResource(resource);
            return true;
            }
        catch (Exception e)
            {
            _error_message = e.getMessage();
            }
        return false;
        }

    public ResourceToken getToken()
        {
        return _token;
        }

    public ResourceType getType()
        {
        return _type;
        }

    public String getId()
        {
        return _id;
        }

    public String getErrorMessage()
        {
        return _error_message;
        }

    private final ResourceType _type;
    private final String _id;
    private final MuseProject _project;
    private ResourceToken _token;

    private String _error_message = null;
    }


