package org.museautomation.ui.step;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.step.*;
import org.museautomation.ui.extend.edit.tags.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepTagEditorTests extends ComponentTest
	{
	@Test
    void removeTag()
	    {
	    StepConfiguration step = new StepConfiguration(LogMessage.TYPE_ID);
	    step.tags().addTag("tag1");
	    _editor.setStep(step);
	    waitForUiEvents();

	    Assertions.assertTrue(exists("tag1"), "tag not shown");
	    clickOn(id(TagsEditor.DELETE_ID));
	    Assertions.assertFalse(step.tags().hasTag("tag1"), "tag was not removed");

	    _undo.undoLastAction();
	    waitForUiEvents();
	    Assertions.assertTrue(step.tags().hasTag("tag1"), "tag was not restored");
	    }

	@Override
	protected Node createComponentNode()
		{
		_undo = new UndoStack();
		_editor = new StepTagEditor(new RootStepEditContext(new SimpleProject(), _undo, null));
		return _editor.getNode();
		}

	@Override
	protected boolean fillToWidthAndHeight()
		{
		return false;
		}

	private StepTagEditor _editor;
	private UndoStack _undo;
	}