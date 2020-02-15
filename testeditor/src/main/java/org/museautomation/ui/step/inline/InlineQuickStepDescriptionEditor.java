package org.museautomation.ui.step.inline;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.ui.step.actions.*;
import org.museautomation.core.*;
import org.museautomation.core.step.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused")  // instantiated by reflection
public class InlineQuickStepDescriptionEditor extends InlineStepEditor
    {
    @Override
    public ExtensionSelectionPriority getPriority(StepEditContext context, StepConfiguration step)
	    {
	    return ExtensionSelectionPriority.FALLBACK;
	    }

    @Override
    public void edit(StepEditContext context, StepConfiguration step, EditInProgress edit, InlineStepEditorContainer container)
	    {
	    super.edit(context, step, edit, container);
	    _context = context;
        _step = step;
	    }

    public void requestFocus()
        {
        _editor.requestFocus();
        }

    @Override
    public Node getNode()
        {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0, 10, 0, 0));

        grid.add(new Label("description: "), 0, 0);

        String description = (String) _step.getMetadataField(StepConfiguration.META_DESCRIPTION);
        if (description == null)
            description = "";
        _custom_description = description;

        GridPane.setHgrow(_editor, Priority.ALWAYS);
        grid.add(_editor, 1, 0);
        _editor.setText(_custom_description);
        _editor.setOnKeyPressed(this::handleEnterAndEscapeKeyEvents);

        grid.add(buildMoreOptionsLink(), 2, 0);

        Button save_button = buildSaveButton();
        grid.add(save_button, 3, 0);
        GridPane.setMargin(save_button, new Insets(2));

        Button cancel_button = buildCancelButton();
        grid.add(cancel_button, 4, 0);
        GridPane.setMargin(cancel_button, new Insets(2));

        return grid;
        }

    protected void saveEdit()
        {
        String text = _editor.getText();
        boolean same = _custom_description.equals(text);
        if (!same)
            {
            if (text.trim().length() == 0)
                text = null;
            new ChangeStepMetadataAction(_step, text).execute(_context.getUndo());
            super.saveEdit();
            }
        else
            _edit.cancel();
        }

    @Override
    public boolean isValid()
        {
        return true;
        }

    private StepConfiguration _step;
    private StepEditContext _context;

    private TextField _editor = new TextField();
    private String _custom_description;
    }


