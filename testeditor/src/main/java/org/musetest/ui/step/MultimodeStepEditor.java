package org.musetest.ui.step;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.step.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.edit.stack.*;
import org.musetest.ui.extend.edit.step.*;

/**
 * Can handle multiple editing UIs and facilitates switching between them (e.g. default and expert).
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MultimodeStepEditor implements StackableEditor
    {
    public MultimodeStepEditor(StepEditContext context, StepConfiguration step)
        {
        _context = context;
        _step = step;
        useDefaultEditor();
        }

    private void useExpertEditor()
        {
        ExpertStepEditor editor = new ExpertStepEditor(_context, _step);

        Hyperlink link = Buttons.createLinkWithIcon("default mode", "FA:MAGIC", SWITCH_TO_DEFAULT_ID, "Default editor", ContentDisplay.LEFT);
        link.setOnAction(event -> useDefaultEditor());
        editor.addModeSwitchLink(link);

        setup(editor);
        }

    private void useDefaultEditor()
        {
        DefaultStepEditor editor = new DefaultStepEditor(_context);
        editor.setStep(_step);

        Hyperlink link = Buttons.createLinkWithIcon("expert mode", "FA:GRADUATION_CAP", SWITCH_TO_EXPERT_ID, "Expert editor", ContentDisplay.LEFT);
        link.setOnAction(event -> useExpertEditor());
        editor.addModeSwitchLink(link);

        setup(editor);
        }

    private void setup(StackableEditor editor)
        {
        if (_current_editor instanceof ExpertStepEditor)
   		    ((ExpertStepEditor)_current_editor).destroy();
   	    else if (_current_editor instanceof DefaultStepEditor)
   		    ((DefaultStepEditor)_current_editor).destroy();
        _current_editor = editor;
        _node.getChildren().clear();
        _node.getChildren().add(editor.getNode());
        editor.setStack(_stack);
        editor.activate();
        editor.requestFocus();
        }

    @Override
    public Node getNode()
        {
        return _node;
        }

    @Override
    public void setStack(EditorStack stack)
        {
        _stack = stack;
        _current_editor.setStack(stack);
        }

    @Override
    public void requestFocus()
        {
        _current_editor.requestFocus();
        }

    @Override
    public void activate()
        {
        _current_editor.activate();
        }

    @Override
    public boolean isValid()
        {
        return _current_editor.isValid();
        }

    public void destroy()
	    {
	    if (_current_editor instanceof ExpertStepEditor)
		    ((ExpertStepEditor)_current_editor).destroy();
	    else if (_current_editor instanceof DefaultStepEditor)
		    ((DefaultStepEditor)_current_editor).destroy();
	    }

    private final StackPane _node = new StackPane();
    private final StepEditContext _context;
    private final StepConfiguration _step;
    private EditorStack _stack;
    private StackableEditor _current_editor;

    public final static String SWITCH_TO_EXPERT_ID = "switch-to-expert";
    public final static String SWITCH_TO_DEFAULT_ID = "switch-to-expert";
    }


