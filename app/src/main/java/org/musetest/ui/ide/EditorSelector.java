package org.musetest.ui.ide;

import org.musetest.core.*;
import org.musetest.ui.editors.resource.*;
import org.musetest.ui.editors.text.*;
import org.musetest.ui.extend.edit.*;

import java.util.*;

/**
 * Chooses an editor for a resource from the list of editors that report
 * the ability to edit a resource.
 *
 * The current implementation treats TextResourceEditor as the fall-back
 * option for every resource type, but never the first choice.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class EditorSelector
    {
    public static EditorSelector get(MuseProject project)
        {
        return new EditorSelector(project);
        }

    private EditorSelector(MuseProject project)
        {
        _project = project;
        }

    /**
     * Get the preferred editor for a resource. If none are available, it
     * will return a text editor. This is a naive implementation which assumes
     * there will only be one preferred editor and one fallback choice.
     */
    public MuseResourceEditor get(MuseResource resource)
        {
        MuseResourceEditor fallback = null;
        MuseResourceEditor preferred = null;

        // find an editor for this resource and launch it.
        List<MuseResourceEditor> possible_editors = _project.getClassLocator().getInstances(MuseResourceEditor.class);
        for (MuseResourceEditor candidate : possible_editors)
            {
            if (candidate.canEdit(resource))
                {
                if (candidate instanceof GenericResourceEditor)
                    fallback = candidate;
                else
                    preferred = candidate;
                }
            }

        if (preferred != null)
            return preferred;
        if (fallback != null)
            return fallback;
        return new TextResourceEditor();
        }

    private MuseProject _project;
    }


