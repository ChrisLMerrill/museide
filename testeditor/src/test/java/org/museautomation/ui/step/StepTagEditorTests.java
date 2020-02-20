package org.museautomation.ui.step;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
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
	public void removeTag()
	    {
	    StepConfiguration step = new StepConfiguration(LogMessage.TYPE_ID);
	    step.addTag("tag1");
	    _editor.setStep(step);
	    waitForUiEvents();

	    Assert.assertTrue("tag not shown", exists("tag1"));
	    clickOn(id(TagsEditor.DELETE_ID));
	    Assert.assertFalse("tag was not removed", step.hasTag("tag1"));

	    _undo.undoLastAction();
	    waitForUiEvents();
	    Assert.assertTrue("tag was not restored", step.hasTag("tag1"));
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

