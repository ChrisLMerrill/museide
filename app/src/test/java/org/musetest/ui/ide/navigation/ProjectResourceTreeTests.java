package org.musetest.ui.ide.navigation;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.builtins.step.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.resource.*;
import org.musetest.core.step.*;
import org.musetest.core.steptest.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.ide.*;
import org.musetest.ui.ide.navigation.resources.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProjectResourceTreeTests extends ComponentTest
    {
    @Test
    public void displayTests()
        {
        waitForUiEvents();

        _tree.expandAll();
        waitForUiEvents();

        Assert.assertTrue(exists(_project.getName()));  // the project node is visible
        Assert.assertTrue(exists(new MuseTest.TestResourceType().getName() + "s"));  // the "Test" node is visible
        Assert.assertTrue(exists(TEST1_ID)); // the test id is visible
        }

    @Test
    public void editResource()
        {
        _tree.expandAll();
        waitForUiEvents();

        doubleClickOn(TEST1_ID);

        Assert.assertEquals("wrong project edited", _project, _editors._project_edited);
        Assert.assertEquals("wrong token edited", _project.getResourceStorage().findResource(TEST1_ID), _editors._resource_edited);
        }

    @Override
    protected Node createComponentNode() throws IOException
        {
        SteppedTest test = new SteppedTest(new StepConfiguration(LogMessage.TYPE_ID));
        test.setId(TEST1_ID);
        _project = new SimpleProject();
        _project.getResourceStorage().addResource(test);

        _editors = new MockResourceEditors();
        _tree = new ProjectResourceTree(_project, new ResourceTreeOperationHandler(_project, _editors, new UndoStack()));
        return _tree.getNode();
        }

    @Override
    protected double getDefaultHeight()
        {
        return 800;
        }

    private MuseProject _project;
    private ProjectResourceTree _tree;
    private MockResourceEditors _editors;

    private final static String TEST1_ID = "test1";

    private class MockResourceEditors implements ResourceEditors
        {
        @Override
        public boolean editResource(ResourceToken resource, MuseProject project)
            {
            _resource_edited = resource;
            _project_edited = project;
            return true;
            }

        @Override
        public boolean hasUnsavedChanges()
            {
            return false;
            }

        @Override
        public String saveAllChanges()
            {
            return null;
            }

        @Override
        public void revertAllChanges()
            {

            }

        @Override
        public void closeAll()
            {

            }

        ResourceToken _resource_edited;
        MuseProject _project_edited;
        }
    }


