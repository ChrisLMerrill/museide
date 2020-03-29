package org.museautomation.ui.ide.navigation;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.ide.navigation.resources.*;
import org.museautomation.ui.ide.navigation.resources.actions.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.steptask.*;
import org.museautomation.core.variables.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.components.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class CreateResourcePanelTests extends ComponentTest
    {
    @Test
    void createResource()
        {
        final String resource_id = setup();

        Assertions.assertTrue(exists(new VariableList.VariableListResourceType().getName()));
        Node node = lookup(resource_id).query();
        Assertions.assertNotNull(node);
        Assertions.assertFalse(InputValidation.isShowingError(node));

        CreateResourceAction action = _panel.getAction();
        Assertions.assertNotNull(action, "no action created");
        Assertions.assertTrue(action.getType() instanceof VariableList.VariableListResourceType);
        Assertions.assertEquals(resource_id, action.getId());
        }

    @Test
    void duplicateId()
        {
        final String resource_id = setup();
        Node node = lookup(resource_id).query();
        fillFieldAndTabAway(node, TESTID);

        Assertions.assertTrue(InputValidation.isShowingError(node));
        Assertions.assertNull(_panel.getAction(), "should not create action when provided ID already exists in repository");
        }

    @Test
    void blankId()
        {
        final String resource_id = setup();
        Node node = lookup(resource_id).query();
        clearFieldAndTabAway(resource_id);

        Assertions.assertTrue(InputValidation.isShowingError(node));
        Assertions.assertNull(_panel.getAction(), "should not create action when no ID is provided");
        }

    @Test
    void changeDefaults()
        {
        final String resource_id = setup();

        clickOn(new VariableList.VariableListResourceType().getName());
        clickOn(new MuseTask.TaskResourceType().getName());

        final String changed_id = "changed";
        fillFieldAndTabAway(resource_id, changed_id);

        CreateResourceAction action = _panel.getAction();
        Assertions.assertTrue(action.getType() instanceof MuseTask.TaskResourceType);
        Assertions.assertEquals(changed_id, action.getId());
        }

    private String setup()
        {
        _panel.setType(new VariableList.VariableListResourceType());
        final String new_id = "newlist";
        _panel.setId(new_id);
        waitForUiEvents();
        return new_id;
        }

    @Override
    protected Node createComponentNode() throws IOException
        {
        MuseProject project = new SimpleProject();
        SteppedTest test1 = new SteppedTest();
        test1.setId(TESTID);
        project.getResourceStorage().addResource(test1);

        _panel = new CreateResourcePanel(project, new UndoStack());
        return _panel.getNode();
        }

    private CreateResourcePanel _panel;
    private final static String TESTID = "test1";
    }