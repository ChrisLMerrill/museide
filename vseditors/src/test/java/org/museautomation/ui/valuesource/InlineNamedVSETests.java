package org.museautomation.ui.valuesource;

import javafx.scene.*;
import javafx.scene.input.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.components.*;
import org.testfx.api.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class InlineNamedVSETests extends ComponentTest
	{
	// TODO There really should be more tests for this component!


	@Test
    void showErrorWithWhitespaceInName()
	    {
	    final Node name_field = _editor.getNameNode();
	    Assertions.assertTrue(InputValidation.isShowingValid(name_field));

	    final FxRobotInterface robot = clickOn(name_field);
	    robot.write("a1_-");
	    Assertions.assertTrue(InputValidation.isShowingValid(name_field));

	    robot.write(" ");
	    Assertions.assertFalse(InputValidation.isShowingValid(name_field));

	    robot.write("Z");
	    Assertions.assertFalse(InputValidation.isShowingValid(name_field));
	    }

	@Test
    void dontShowErrorWhenRenamingBackToOriginal()
	    {
	    final Node name_field = _editor.getNameNode();
	    _editor.setSource(ValueSourceConfiguration.forValue("abc"));
	    _editor.setName("name1");
	    _editor.setNameValidator(name -> !name.equals("name1"));
	    waitForUiEvents();
	    Assertions.assertTrue(InputValidation.isShowingValid(name_field));

	    final FxRobotInterface robot = clickOn(name_field);
	    robot.push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.DELETE);
	    robot.push().write("name");
	    Assertions.assertTrue(InputValidation.isShowingValid(name_field));

	    robot.write("1");
	    Assertions.assertTrue(InputValidation.isShowingValid(name_field));
	    }

	@Override
    public Node createComponentNode()
		{
		_editor = new InlineNamedVSE(new SimpleProject(), new UndoStack());
		return _editor.getNode();
		}

	private InlineNamedVSE _editor;
	}