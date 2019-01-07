package org.musetest.ui.valuesource;

import javafx.scene.*;
import javafx.scene.input.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.core.project.*;
import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.components.*;
import org.testfx.api.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class InlineNamedVSETests extends ComponentTest
	{
	// TODO There really should be more tests for this component!


	@Test
	public void showErrorWithWhitespaceInName()
	    {
	    final Node name_field = _editor.getNameNode();
	    Assert.assertTrue(InputValidation.isShowingValid(name_field));

	    final FxRobotInterface robot = clickOn(name_field);
	    robot.write("a1_-");
	    Assert.assertTrue(InputValidation.isShowingValid(name_field));

	    robot.write(" ");
	    Assert.assertFalse(InputValidation.isShowingValid(name_field));

	    robot.write("Z");
	    Assert.assertFalse(InputValidation.isShowingValid(name_field));
	    }

	@Test
	public void dontShowErrorWhenRenamingBackToOriginal()
	    {
	    final Node name_field = _editor.getNameNode();
	    _editor.setSource(ValueSourceConfiguration.forValue("abc"));
	    _editor.setName("name1");
	    _editor.setNameValidator(name -> !name.equals("name1"));
	    waitForUiEvents();
	    Assert.assertTrue(InputValidation.isShowingValid(name_field));

	    final FxRobotInterface robot = clickOn(name_field);
	    robot.push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.DELETE);
	    robot.push().write("name");
	    Assert.assertTrue(InputValidation.isShowingValid(name_field));

	    robot.write("1");
	    Assert.assertTrue(InputValidation.isShowingValid(name_field));
	    }

	@Override
	protected Node createComponentNode()
		{
		_editor = new InlineNamedVSE(new SimpleProject(), new UndoStack());
		return _editor.getNode();
		}

	private InlineNamedVSE _editor;
	}