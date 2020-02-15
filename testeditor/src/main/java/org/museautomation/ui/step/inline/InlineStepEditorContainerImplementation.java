package org.museautomation.ui.step.inline;

import javafx.scene.*;
import javafx.scene.layout.*;
import org.museautomation.ui.step.*;
import org.museautomation.core.step.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * Responsible for choosing and switching between the quick and full editors within the containing node.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class InlineStepEditorContainerImplementation implements InlineStepEditorContainer, Validatable
    {
    public InlineStepEditorContainerImplementation(StepEditContext context, StepConfiguration step, EditInProgress edit, boolean start_in_full_mode)
        {
        _context = context;
        _step = step;
        _edit = edit;

        _grid = new StackPane();
        _quick_editor = InlineStepEditors.get(context.getProject()).findEditor(context, step);
        _quick_editor.edit(context, step, edit, this);

        if (start_in_full_mode)
            moreEditOptionsRequested();
        else
            try
                {
                lessEditOptionsRequested();
                }
            catch (Exception e)
                {
                moreEditOptionsRequested();
                }
        }

    public void requestFocus()
        {
        _quick_editor.requestFocus();
        }

    public Node getNode()
        {
        return _grid;
        }

    public void moreEditOptionsRequested()
        {
        _grid.getChildren().clear();
        if (_full_editor == null)
            {
            _full_editor = new StepEditorStack(_context, _edit);
            _full_editor.setStep(_step);
            _full_node = _full_editor.getNode();
            }
        _grid.getChildren().add(_full_node);
        _full_editor.requestFocus();
        _validatable_editor = _full_editor;
        }

    @SuppressWarnings("unused,WeakerAccess") // public API
    public void lessEditOptionsRequested()
        {
        _grid.getChildren().clear();
        if (_quick_editor_node == null)
            {
            _quick_editor_node = _quick_editor.getNode();
            GridPane.setFillWidth(_quick_editor_node, true);
            }
        _grid.getChildren().add(_quick_editor_node);
        _quick_editor.requestFocus();
        _validatable_editor = _quick_editor;
        }

    @Override
    public boolean isValid()
        {
        return _validatable_editor.isValid();
        }

    public void destroy()
	    {
	    if (_full_editor != null)
	        _full_editor.destroy();
	    }

    private final StepEditContext _context;
    private final StepConfiguration _step;
    private final EditInProgress _edit;

    private InlineStepEditor _quick_editor;
    private Node _quick_editor_node;
    private StepEditorStack _full_editor;
    private Node _full_node;
    private final StackPane _grid;
    private Validatable _validatable_editor;
    }


