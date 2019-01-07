package org.musetest.ui.valuesource;

import javafx.application.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import org.musetest.core.*;
import org.musetest.core.values.descriptor.*;
import org.musetest.ui.valuesource.groups.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceTypeSelector
    {
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
        for (ValueSourceTypeGroup subgroup : types.getSubGroups())
            {
            Menu submenu = new Menu(subgroup.getName());
            addMenuEntries(submenu.getItems(), subgroup, project);
            item.add(submenu);
            }

        for (ValueSourceDescriptor descriptor : types.getValueSourceTypes())
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


