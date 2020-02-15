package org.museautomation.ui.ide.navigation.resources.actions;

import org.museautomation.ui.ide.navigation.resources.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.util.*;
import org.museautomation.ui.extend.actions.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class CopyResourceAction extends UndoableAction
    {
    /**
     * Convenience method for creating an single or multiple action from a list without caring about the size.
     */
    public static UndoableAction create(List<ResourceToken> tokens, MuseProject project)
        {
        if (tokens.size() == 1)
            return new CopyResourceAction(tokens.get(0), project);

        CompoundAction compound = new CompoundAction();
        for (ResourceToken token : tokens)
            compound.addAction(new CopyResourceAction(token, project));
        return compound;
        }

    public CopyResourceAction(ResourceToken token, MuseProject project)
        {
        _token = token;
        _project = project;
        }

    @Override
    protected boolean executeImplementation()
        {
        MuseResource resource = _project.getResourceStorage().getResource(_token);
        MuseResource copy = Copy.withJsonSerialization(resource);
        _new_id = ResourceIdSuggestions.suggestCopy(_token.getId(), _project);
        copy.setId(_new_id);
        try
            {
            return _project.getResourceStorage().addResource(copy) != null;
            }
        catch (IOException e)
            {
            LOG.error(String.format("Unable to add resource %s to the project", resource.getId()), e);
            return false;
            }
        }

    @Override
    protected boolean undoImplementation()
        {
        ResourceToken token = _project.getResourceStorage().findResource(_new_id);
        if (token == null)
            {
            LOG.error("Unable to find the resource that was added: " + _new_id);
            return false;
            }
        if (_project.getResourceStorage().removeResource(token))
            return true;
        LOG.error("Unable to remove the resource that was added: " + _new_id);
        return false;
        }

    private final ResourceToken _token;
    private final MuseProject _project;
    private String _new_id;

    private final static Logger LOG = LoggerFactory.getLogger(CopyResourceAction.class);
    }


