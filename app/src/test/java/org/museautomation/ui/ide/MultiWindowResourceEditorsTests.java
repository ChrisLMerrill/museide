package org.museautomation.ui.ide;

import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.steptask.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.ide.navigation.resources.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MultiWindowResourceEditorsTests extends ComponentTest
    {
    @Test
    void openResourceEditor() throws IOException
        {
        MuseProject project = new SimpleProject();
        StepConfiguration step = new StepConfiguration(LogMessage.TYPE_ID);
        step.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("message1"));
        SteppedTask test = new SteppedTask(step);
        test.setId(TEST_ID);
        project.getResourceStorage().addResource(test);

        int num_windows = listWindows().size();
        boolean edited = new MultiWindowResourceEditors().editResource(project.getResourceStorage().findResource(TEST_ID), project);
        Assertions.assertTrue(edited);
        waitForUiEvents();

        Assertions.assertEquals(num_windows + 1, listWindows().size());
        Assertions.assertNotNull(window(TEST_ID));
        }

    @Override
    protected Node createComponentNode()
        {
        return new Label("placeholder node");
        }

    private final static String TEST_ID = "taskid123";
    }