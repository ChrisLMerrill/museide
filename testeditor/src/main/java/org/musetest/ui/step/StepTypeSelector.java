package org.musetest.ui.step;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import org.musetest.core.*;
import org.musetest.core.step.descriptor.*;
import org.musetest.ui.step.groups.*;
import org.musetest.ui.extend.glyphs.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class StepTypeSelector
    {
    public StepTypeSelector(MuseProject project)
        {
        _button = new MenuButton("", Glyphs.create("FA:PLUS"));
        _button.setTooltip(new Tooltip("Select step type"));
        _button.popupSideProperty().setValue(Side.BOTTOM);

        setProject(project);
        }

    public StepTypeSelector()
        {
        _button = new MenuButton("", Glyphs.create("FA:PLUS"));
        _button.setTooltip(new Tooltip("Select step type"));
        _button.popupSideProperty().setValue(Side.BOTTOM);
        }

    public void setProject(MuseProject project)
        {
        StepTypeGroup group = StepTypeGroups.get(project);
        addMenuEntries(_button.getItems(), group, project);
        }

    private void addMenuEntries(ObservableList<MenuItem> item, StepTypeGroup types, MuseProject project)
        {
        item.clear();

        List<StepTypeGroup> step_groups = types.getSubGroups();
        step_groups.sort(Comparator.comparing(StepTypeGroup::getName));
        for (StepTypeGroup subgroup : step_groups)
            {
            Menu submenu = new Menu(subgroup.getName());
            addMenuEntries(submenu.getItems(), subgroup, project);
            item.add(submenu);
            }

        List<StepDescriptor> step_types = types.getStepTypes();
        step_types.sort(Comparator.comparing(StepDescriptor::getName));
        for (StepDescriptor descriptor : step_types)
            {
            MenuItem sub_item = new MenuItem(descriptor.getName(), StepGraphicBuilder.getInstance().getStepIcon(descriptor, project));
            sub_item.setOnAction(event -> typeSelected(descriptor));
            item.add(sub_item);
            }
        }

    public abstract void typeSelected(StepDescriptor descriptor);

    public MenuButton getButton()
        {
        return _button;
        }

    private final MenuButton _button;
    }


