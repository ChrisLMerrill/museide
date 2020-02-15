package org.museautomation.ui.steptest;

import org.museautomation.ui.step.*;
import org.museautomation.ui.step.actions.*;
import org.museautomation.ui.steptree.*;
import org.museautomation.core.*;
import org.museautomation.core.step.*;
import org.museautomation.core.step.descriptor.*;
import org.museautomation.ui.extend.glyphs.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class AddStepButton extends StepTypeSelector
    {
    public AddStepButton(MuseProject project, StepTree2 tree)
        {
        super(project);
        _project = project;
        _tree = tree;
        getButton().disableProperty().setValue(true);
        getButton().setText("Add");
        getButton().setGraphic(Glyphs.create("FA:PLUS"));
        }

    @Override
    public void typeSelected(StepDescriptor descriptor)
        {
        StepConfiguration step = StepConfiguration.create(_project, descriptor);
        List<StepConfiguration> steps = new ArrayList<>();
        steps.add(step);
        final StepConfiguration selected = _tree.getSingleSelection();
        StepConfiguration parent = _tree.getSelectionParent();
        int index = 0;
        if (parent == null)
        	parent = selected;
        else
        	index = parent.getChildren().indexOf(selected) + 1;
        InsertStepsAction insert_action = new InsertStepsAction(parent, steps, index);
        insert_action.execute(_tree.getUndoStack());

/*
        // Tell the tree to edit the cell.  A delay is needed because the tree may not have lazily propogated its changes
        // yet, and this seems to give it the necessary time.
        // ref: http://stackoverflow.com/questions/29863095/programmatically-edit-treeview-treeitem
        PauseTransition p = new PauseTransition( Duration.millis(100) );
        p.setOnFinished(event ->
            {
            _tree.getSelectionModel().clearSelection();
            TreeItem<DisposableIsmHolder> new_item = insert_action.getInsertedItems().get(0);

            StepDescriptor descriptor1 = _project.getStepDescriptors().get(step);
            String inline_edit_string = descriptor1.getInlineEditString();
            if (inline_edit_string != null && inline_edit_string.contains("{"))
                _tree.edit(new_item);
            });
        p.play();
*/
        }

    private StepTree2 _tree;
    private MuseProject _project;
    }
