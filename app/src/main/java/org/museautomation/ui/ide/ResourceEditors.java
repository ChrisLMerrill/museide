package org.museautomation.ui.ide;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;

/**
 * Creates and manages a set of ResourceEditors.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ResourceEditors
    {
    boolean editResource(ResourceToken token, MuseProject project);

    boolean hasUnsavedChanges();
    String saveAllChanges();
    void revertAllChanges();

    void closeAll();
    }


