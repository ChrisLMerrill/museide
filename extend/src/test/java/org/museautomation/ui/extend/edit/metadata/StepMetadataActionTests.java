package org.museautomation.ui.extend.edit.metadata;

import org.junit.jupiter.api.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class StepMetadataActionTests
	{
	@Test
    void addMetadata()
	    {
	    new AddMetadataAction(_step, "newname", "added").execute(_undo);
	    Assertions.assertEquals("added", _step.getMetadataField("newname").toString(), "value not added");

        Assertions.assertTrue(_undo.undoLastAction());
	    Assertions.assertNull(_step.getMetadataField("newname"), "add not undone");
	    }

	@Test
    void removeMetadata()
	    {
	    new RemoveMetadataAction(_step, "initial").execute(_undo);
	    Assertions.assertNull(_step.getMetadataField("initial"), "value note removed");

	    Assertions.assertTrue(_undo.undoLastAction());
	    Assertions.assertEquals("initvalue", _step.getMetadataField("initial").toString(), "remove not undone");
	    }

	@BeforeEach
    void setup()
		{
		_step.setMetadataField("initial", "initvalue");
		}

	private StepConfiguration _step = new StepConfiguration(LogMessage.TYPE_ID);
	private UndoStack _undo = new UndoStack();
	}