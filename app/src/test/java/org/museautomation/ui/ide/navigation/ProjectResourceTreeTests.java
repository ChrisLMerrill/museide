package org.museautomation.ui.ide.navigation;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.ide.*;
import org.museautomation.ui.ide.navigation.resources.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.step.*;
import org.museautomation.core.steptask.*;
import org.museautomation.ui.extend.actions.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProjectResourceTreeTests extends ComponentTest
    {
    @Test
    void displayTests()
        {
        waitForUiEvents();

        _tree.expandAll();
        waitForUiEvents();

        Assertions.assertTrue(exists(new MuseTask.TaskResourceType().getName() + "s"));  // the "Test" node is visible
        Assertions.assertTrue(exists(TEST1_ID)); // the test id is visible
        }

    @Test
    void editResource()
        {
        _tree.expandAll();
        waitForUiEvents();

        doubleClickOn(TEST1_ID);

        Assertions.assertEquals(_project, _editors._project_edited, "wrong project edited");
        Assertions.assertEquals(_project.getResourceStorage().findResource(TEST1_ID), _editors._resource_edited, "wrong token edited");
        }

    @Override
    protected Node createComponentNode() throws IOException
        {
        SteppedTask test = new SteppedTask(new StepConfiguration(LogMessage.TYPE_ID));
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

    static private class MockResourceEditors implements ResourceEditors
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
        public void revertAllChanges() { }

        @Override
        public void closeAll() { }

        ResourceToken _resource_edited;
        MuseProject _project_edited;
        }
    }