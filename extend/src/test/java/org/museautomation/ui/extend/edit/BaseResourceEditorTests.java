package org.museautomation.ui.extend.edit;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.project.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class BaseResourceEditorTests extends ComponentTest
	{
	@Test
    void showComponents()
	    {
	    SampleResource resource = new SampleResource();
	    resource.setId("resource1");
	    _editor.editResource(new SimpleProject(), resource);
	    waitForUiEvents();

	    // default buttons are shown
	    Assertions.assertTrue(exists("Undo"));
	    Assertions.assertTrue(exists("Redo"));
	    Assertions.assertTrue(exists("Save"));

	    // editor UI is shown
	    Assertions.assertTrue(exists(resource.getEditText()));
	    }

	@Test
    void showLowerPanel()
	    {
	    SampleResource resource = new SampleResource();
	    resource.setId("resource1");
	    _editor.editResource(new SimpleProject(), resource);
	    waitForUiEvents();

	    // editor UI is shown
	    Assertions.assertTrue(exists(resource.getEditText()));

	    // show the lower panel
	    String lower_panel_text = "lower panel text";
	    Platform.runLater(() -> _editor.showInLowerSplitPane(new Label(lower_panel_text)));
	    waitForUiEvents();
	    Assertions.assertTrue(exists(lower_panel_text));

	    // hide the lower panel
	    Platform.runLater(() -> _editor.hideLowerSplitPane());
	    waitForUiEvents();
	    Assertions.assertFalse(exists(lower_panel_text));
	    }

	@Override
	protected Node createComponentNode()
		{
		_editor = new SampleResourceEditor();
		return _editor.getNode();
		}

	private SampleResourceEditor _editor;
	}