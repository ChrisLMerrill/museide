package org.museautomation.ui.ide.navigation.resources.actions;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.resource.storage.*;
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
        _path = null;
        }

    public CreateResourceAction(ResourceType type, String id, MuseProject project, String path)
        {
        _type = type;
        _id = id;
        _project = project;
        if (path != null && path.length() > 0)
            _path = path;
        else
            _path = null;
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
            if (_path != null)
                resource.metadata().setMetadataField(FolderIntoMemoryResourceStorage.PATH_ATTRIBUTE_NAME, _path);
            _token = _project.getResourceStorage().addResource(resource);
            return true;
            }
        catch (Exception e)
            {
            _error_message = e.getMessage();
            }
        return false;
        }

    public ResourceToken<MuseResource> getToken()
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
    private final String _path;
    private final MuseProject _project;
    private ResourceToken<MuseResource> _token;

    private String _error_message = null;
    }