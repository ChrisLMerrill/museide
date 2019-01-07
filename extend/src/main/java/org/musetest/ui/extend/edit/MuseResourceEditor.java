package org.musetest.ui.extend.edit;

import javafx.scene.*;
import org.musetest.core.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface MuseResourceEditor
    {
    boolean canEdit(MuseResource resource);
    void editResource(MuseProject project, MuseResource resource);
    Scene getScene();
    Node getNode();
    ValidationStateSource getValidationStateSource();
    void requestFocus();

    /**
     * This editor no longer needed. Clean up resources and de-register listeners.
     */
    void dispose();

    /**
     * @return true if there are unsaved changes.
     */
    boolean isChanged();

    /**
     * Revert all (unsaved) changes in this editor.
     */
    void revertChanges();

    /**
     * Revert all (unsaved) changes in this editor.
     */
    String saveChanges();
    }

