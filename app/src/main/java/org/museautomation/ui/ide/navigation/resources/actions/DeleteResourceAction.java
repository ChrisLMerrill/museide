package org.museautomation.ui.ide.navigation.resources.actions;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.ui.extend.actions.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class DeleteResourceAction extends UndoableAction
    {
    /**
     * Convenience method for creating an single or multiple action from a list without caring about the size.
     */
    public static UndoableAction create(List<ResourceToken> tokens, MuseProject project)
        {
        if (tokens.size() == 1)
            return new DeleteResourceAction(tokens.get(0), project);

        CompoundAction compound = new CompoundAction();
        for (ResourceToken token : tokens)
            compound.addAction(new DeleteResourceAction(token, project));
        return compound;
        }

    public DeleteResourceAction(ResourceToken token, MuseProject project)
        {
        _token = token;
        _project = project;
        }

    @Override
    protected boolean executeImplementation()
        {
        _removed = _project.getResourceStorage().getResource(_token);
        if (_removed == null)
            {
            LOG.error("Unable to locate the resource to remove: " + _token.getId());
            return false;
            }
        boolean removed = _project.getResourceStorage().removeResource(_token);
        if (!removed)
            LOG.error("Unable to remove the resource: " + _token.getId());
        return removed;
        }

    @Override
    protected boolean undoImplementation()
        {
        ResourceToken token;
        try
            {
            token = _project.getResourceStorage().addResource(_removed);
            }
        catch (IOException e)
            {
            token = null;
            }
        if (token == null)
            {
            LOG.error("Unable to un-remove the resource: " + _removed);
            return false;
            }
        return true;
        }

    private final ResourceToken _token;
    private final MuseProject _project;

    private MuseResource _removed = null;

    private final static Logger LOG = LoggerFactory.getLogger(DeleteResourceAction.class);
    }



