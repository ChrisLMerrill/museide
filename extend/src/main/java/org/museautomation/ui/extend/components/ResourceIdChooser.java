package org.museautomation.ui.extend.components;

import javafx.application.*;
import javafx.scene.control.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.resource.types.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceIdChooser extends ComboBox<String>
    {
    @SuppressWarnings("unused")  // public API
    public ResourceIdChooser(MuseProject project, ResourceType type)
        {
        this(project, type, null);
        }

    @SuppressWarnings("WeakerAccess")  // public API
    public ResourceIdChooser(MuseProject project, ResourceType type, String initial_id)
        {
        setId(CHOOSER_ID);

        List<ResourceToken<MuseResource>> tokens = project.getResourceStorage().findResources(new ResourceQueryParameters(type));
        for (ResourceToken<MuseResource> token : tokens)
            getItems().add(token.getId());
        if (initial_id != null)
            getSelectionModel().select(initial_id);
        }

    public String getSelectedId()
        {
        return getSelectionModel().getSelectedItem();
        }

    @SuppressWarnings("unused")  // public API
    public void selectId(String id)
        {
        Platform.runLater(() -> getSelectionModel().select(id));
        }

    public final static String CHOOSER_ID = "omuc-ric-id";
    }


