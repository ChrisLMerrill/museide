package org.musetest.ui.ide;

import org.musetest.core.*;
import org.musetest.core.resource.*;

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


