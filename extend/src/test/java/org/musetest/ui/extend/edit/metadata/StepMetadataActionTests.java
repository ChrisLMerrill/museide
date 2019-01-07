package org.musetest.ui.extend.edit.metadata;

import org.junit.*;
import org.musetest.builtins.step.*;
import org.musetest.core.step.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepMetadataActionTests
	{
	@Test
	public void addMetadata()
	    {
	    new AddMetadataAction(_step, "newname", "added").execute(_undo);
	    Assert.assertEquals("value not added", "added", _step.getMetadataField("newname"));

	    _undo.undoLastAction();
	    Assert.assertNull("add not undone", _step.getMetadataField("newname"));
	    }

	@Test
	public void removeMetadata()
	    {
	    new RemoveMetadataAction(_step, "initial").execute(_undo);
	    Assert.assertNull("value note removed", _step.getMetadataField("initial"));

	    _undo.undoLastAction();
	    Assert.assertEquals("remove not undone", "initvalue", _step.getMetadataField("initial"));
	    }

	@Before
	public void setup()
		{
		_step.setMetadataField("initial", "initvalue");
		}

	private StepConfiguration _step = new StepConfiguration(LogMessage.TYPE_ID);
	private UndoStack _undo = new UndoStack();
	}


