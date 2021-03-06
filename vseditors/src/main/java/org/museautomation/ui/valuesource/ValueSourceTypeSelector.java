package org.museautomation.ui.valuesource;

import javafx.application.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import org.museautomation.ui.valuesource.groups.*;
import org.museautomation.core.*;
import org.museautomation.core.values.descriptor.*;

import java.util.*;
import java.util.stream.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceTypeSelector
    {
    @SuppressWarnings("WeakerAccess")  // public API
    public ValueSourceTypeSelector(MuseProject project)
        {
        _button = new MenuButton();
        _button.setTooltip(new Tooltip("Choose a value source type"));
        _button.popupSideProperty().setValue(Side.BOTTOM);

        ValueSourceTypeGroup group = ValueSourceTypeGroups.get(project);
        addMenuEntries(_button.getItems(), group, project);
        }

    private void addMenuEntries(ObservableList<MenuItem> item, ValueSourceTypeGroup types, MuseProject project)   // for future use, the project is needed to lookup icons (see StepTypeSelector).
        {
        List<ValueSourceTypeGroup> subgroups = types.getSubGroups();
        subgroups.sort(Comparator.comparing(ValueSourceTypeGroup::getName));
        for (ValueSourceTypeGroup subgroup : subgroups)
            {
            Menu submenu = new Menu(subgroup.getName());
            addMenuEntries(submenu.getItems(), subgroup, project);
            item.add(submenu);
            }

        List<ValueSourceDescriptor> vs_types = types.getValueSourceTypes();
        vs_types.sort(Comparator.comparing(ValueSourceDescriptor::getName));
        vs_types = vs_types.stream().filter(descriptor -> !descriptor.hideFromUI()).collect(Collectors.toList());
        for (ValueSourceDescriptor descriptor : vs_types)
            {
            MenuItem sub_item = new MenuItem(descriptor.getName());
            sub_item.setOnAction(event -> typeSelected(descriptor));
            item.add(sub_item);
            }
        }


    public MenuButton getButton()
        {
        return _button;
        }

    /**
     * Override this to respond to changes.
     */
    protected void typeSelected(ValueSourceDescriptor descriptor)
        {
        setType(descriptor);
        }

    public void setType(ValueSourceDescriptor descriptor)
        {
        _selected = descriptor;
        Platform.runLater(() -> getButton().setText(descriptor.getName()));
        }

    public ValueSourceDescriptor getSelectedTypeDescriptor()
        {
        return _selected;
        }

    private MenuButton _button;
    private ValueSourceDescriptor _selected;
    }


