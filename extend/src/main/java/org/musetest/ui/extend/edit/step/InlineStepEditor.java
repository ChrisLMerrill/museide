package org.musetest.ui.extend.edit.step;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.glyphs.*;

/**
 * The base for various inline step editors.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class InlineStepEditor implements Validatable
    {
    public abstract ExtensionSelectionPriority getPriority(StepEditContext context, StepConfiguration step);
    public void edit(StepEditContext context, StepConfiguration step, EditInProgress edit, InlineStepEditorContainer container)
	    {
	    _edit = edit;
	    _container = container;
	    }

    public abstract void requestFocus();
    public abstract Node getNode();

    protected Button buildSaveButton()
        {
        Button save_button = Buttons.createSave();
        save_button.setOnAction(event -> saveEdit());
        return save_button;
        }

    protected void saveEdit()
        {
        _edit.commit(null);
        }

    protected Button buildCancelButton()
        {
        Button cancel_button = Buttons.createCancel();
        cancel_button.setOnAction(event -> cancelEdit());
        return cancel_button;
        }

    private void cancelEdit()
        {
        _edit.cancel();
        }

    public Node buildMoreOptionsLink()
        {
        Hyperlink full_editor_link = new Hyperlink("more options");
        full_editor_link.setGraphic(Glyphs.create("FA:ANGLE_DOUBLE_DOWN"));
        full_editor_link.setOnAction(event -> _container.moreEditOptionsRequested());
        return full_editor_link;
        }

    public void handleEnterAndEscapeKeyEvents(KeyEvent event)
        {
        if (event.getCode() == KeyCode.ESCAPE)
            {
            event.consume();
            cancelEdit();
            }
        else if (event.getCode() == KeyCode.ENTER)
            {
            event.consume();
            saveEdit();
            }
        }

    protected EditInProgress _edit;

    private InlineStepEditorContainer _container;
    }


