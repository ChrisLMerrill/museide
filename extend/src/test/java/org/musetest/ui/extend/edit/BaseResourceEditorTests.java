package org.musetest.ui.extend.edit;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.core.project.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class BaseResourceEditorTests extends ComponentTest
	{
	@Test
	public void showComponents()
	    {
	    SampleResource resource = new SampleResource();
	    resource.setId("resource1");
	    _editor.editResource(new SimpleProject(), resource);
	    waitForUiEvents();

	    // default buttons are shown
	    Assert.assertTrue(exists("Undo"));
	    Assert.assertTrue(exists("Redo"));
	    Assert.assertTrue(exists("Save"));

	    // editor UI is shown
	    Assert.assertTrue(exists(resource.getEditText()));
	    }

	@Test
	public void showLowerPanel()
	    {
	    SampleResource resource = new SampleResource();
	    resource.setId("resource1");
	    _editor.editResource(new SimpleProject(), resource);
	    waitForUiEvents();

	    // editor UI is shown
	    Assert.assertTrue(exists(resource.getEditText()));

	    // show the lower panel
	    String lower_panel_text = "lower panel text";
	    Platform.runLater(() ->
		    {
		    _editor.showInLowerSplitPane(new Label(lower_panel_text));
		    });
	    waitForUiEvents();
	    Assert.assertTrue(exists(lower_panel_text));

	    // hide the lower panel
	    Platform.runLater(() ->
		    {
		    _editor.hideLowerSplitPane();
		    });
	    waitForUiEvents();
	    Assert.assertFalse(exists(lower_panel_text));
	    }

	@Override
	protected Node createComponentNode()
		{
		_editor = new SampleResourceEditor();
		return _editor.getNode();
		}

	SampleResourceEditor _editor;
	}
