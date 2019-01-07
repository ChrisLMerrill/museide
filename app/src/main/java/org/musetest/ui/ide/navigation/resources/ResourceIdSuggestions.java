package org.musetest.ui.ide.navigation.resources;

import org.musetest.core.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceIdSuggestions
    {
    static String suggestCopy(String base, int number)
        {
        return String.format("%s [%d]", base, number);
        }

    public static String suggestCopy(String base, MuseProject project)
        {
        int index = 1;
        String candidate = suggestCopy(base, index);
        while (project.getResourceStorage().getResource(candidate) != null)  // TODO should find, rather than get
            {
            candidate = String.format("%s [copy %d]", base, index);
            index++;
            }
        return candidate;
        }
    }


