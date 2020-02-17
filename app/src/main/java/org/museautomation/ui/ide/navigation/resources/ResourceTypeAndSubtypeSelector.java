package org.museautomation.ui.ide.navigation.resources;

import javafx.application.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.types.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceTypeAndSubtypeSelector
    {
    public ResourceTypeAndSubtypeSelector(MuseProject project)
        {
        _project = project;

        _button = new MenuButton("");
        _button.setTooltip(new Tooltip("Select resource type"));
        _button.popupSideProperty().setValue(Side.BOTTOM);

        addMenuEntries();
        }

    public Node getNode()
        {
        return _button;
        }

    private void addMenuEntries()
        {
        ObservableList<MenuItem> items = _button.getItems();
        List<ResourceType> types = new ArrayList<>(_project.getResourceTypes().getPrimary());
        types.sort(Comparator.comparing(ResourceType::getName));
        ResourceType default_selection = null;
        for (ResourceType type : types)
            {
            if (!type.isInternalUseOnly())
                {
                List<ResourceSubtype> subtypes = _project.getResourceTypes().getSubtypesOf(type);
                subtypes.sort(Comparator.comparing(ResourceSubtype::getName));
                if (subtypes.size() == 0)
                    {
                    if (default_selection == null)
                        default_selection = type;
                    MenuItem item = new MenuItem(type.getName());
                    item.setOnAction(event -> select(type));
                    items.add(item);
                    }
                else
                    {
                    Menu menu = new Menu(type.getName());
                    items.add(menu);
                    for (ResourceSubtype subtype : subtypes)
                        {
                        if (!subtype.isInternalUseOnly())
                            {
                            MenuItem subitem = new MenuItem(subtype.getName());
                            subitem.setOnAction(event -> select(subtype));
                            menu.getItems().add(subitem);
                            }
                        }
                    }
                }
            }
        if (LAST_SELECTED != null)
            select(LAST_SELECTED);
        else
        	select(default_selection);
        }

    public ResourceType getSelection()
        {
        return _selected;
        }

    public boolean select(ResourceType type)
        {
        List<ResourceSubtype> subtypes = _project.getResourceTypes().getSubtypesOf(type);
        final ResourceType selected = subtypes.size() > 0 ? subtypes.get(0) : type;

        Platform.runLater(() ->
            {
            _selected = selected;
            _button.setText(selected.getName());
            });
        LAST_SELECTED = selected;
        return true;
        }

    private final MuseProject _project;
    private final MenuButton _button;
    private ResourceType _selected;

    private static ResourceType LAST_SELECTED = null;
    }


