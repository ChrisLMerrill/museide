package org.museautomation.ui.editors.resource;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.valuesource.map.*;
import org.museautomation.core.project.*;
import org.museautomation.core.resource.generic.*;
import org.museautomation.core.resource.types.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.descriptor.*;
import org.museautomation.ui.extend.actions.*;

public class GenericResourceEditPaneTests extends ComponentTest
	{
	@Test
    void display()
		{
		Assertions.assertTrue(exists(new MockResourceType().getDescriptor().getShortDescription()), "short description not displayed");
		Assertions.assertTrue(exists(new MockResourceType().getDescriptor().getSubsourceDescriptors()[0].getDisplayName()), "Param1 name not displayed");
		Assertions.assertTrue(exists(quoted("value1")), "Param1 value not displayed");
		}

	@Test
    void changeParameter()
		{
		fillFieldAndTabAway(quoted("value1"), quoted("newval"));
		waitForUiEvents();

		Assertions.assertTrue(exists(quoted("newval")), "new value not displayed");
		Assertions.assertEquals("newval", _resource.parameters().getSource("param1").getValue());
		}

	@Test
    void removeOptionalParameter()
		{
		clickOn(id(ValueSourceMapEditor.getRemoveButtonId("optparam1")));
		waitForUiEvents();

		Assertions.assertFalse(exists(quoted("optvalue1")), "value still displayed");
		Assertions.assertNull(_resource.parameters().getSource("optparam1"));
		}

	@Override
	protected Node createComponentNode()
		{
		GenericResourceConfigEditPane pane = new GenericResourceConfigEditPane(_project, _undo);

		_resource = new MockResource();
		_resource.parameters().addSource("param1", ValueSourceConfiguration.forValue("value1"));
		_resource.parameters().addSource("optparam1", ValueSourceConfiguration.forValue("optvalue1"));
		pane.setResource(_resource);

		BorderPane main = new BorderPane();
		main.setCenter(pane.getNode());
		main.setBottom(new TextField("field to accept focus"));
		return main;
		}

	private SimpleProject _project = new SimpleProject();
	private UndoStack _undo = new UndoStack();
	private MockResource _resource;

	class MockResourceType extends ResourceType
		{
		MockResourceType()
			{
			super("mock-resource-for-editing", "Mock Resource", MockResource.class);
			}

		@Override
		public ResourceDescriptor getDescriptor()
			{
			return new DefaultResourceDescriptor(new MockResourceType(), "a short description of the resource type");
			}
		}

	@MuseSubsourceDescriptor(displayName = "Param 1", description = "A description of param 1", type = SubsourceDescriptor.Type.Named, name = "param1")
	@MuseSubsourceDescriptor(displayName = "Opt Param1", description = "an optional parameter", type = SubsourceDescriptor.Type.Named, name = "optparam1", optional =  true)
	@MuseSubsourceDescriptor(displayName = "Opt Param2", description = "second optional parameter", type = SubsourceDescriptor.Type.Named, name = "optparam2", optional =  true)
	class MockResource extends GenericResourceConfiguration
		{
		@Override
		public ResourceType getType()
			{
			return new MockResourceType();
			}
		}
	}