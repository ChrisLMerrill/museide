package org.museautomation.ui.ide.navigation.resources;

import javafx.application.*;
import javafx.stage.*;
import org.museautomation.ui.ide.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.ui.extend.edit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MultiWindowResourceEditors implements ResourceEditors
    {
    @Override
    public boolean editResource(ResourceToken token, MuseProject project)
        {
        MuseResource resource = project.getResourceStorage().getResource(token);
        MuseResourceEditor editor = EditorSelector.get(project).get(resource);
        if (editor == null)
            return false;

        editor.editResource(project, resource);
        Platform.runLater(() ->
            {
            Stage stage = new IdeWindow();
            stage.setTitle(resource.getId());
            stage.setScene(editor.getScene());
            stage.show();
            });
        return true;
        }

    @Override
    public boolean hasUnsavedChanges()
        {
        // TODO
        return false;
        }

    @Override
    public String saveAllChanges()
        {
        // TODO
        return null;
        }

    @Override
    public void revertAllChanges()
        {
        // TODO
        }

    @Override
    public void closeAll()
        {
        // TODO
        }
    }


