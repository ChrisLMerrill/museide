package org.musetest.ui.ide;

import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.builtins.step.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.step.*;
import org.musetest.core.steptest.*;
import org.musetest.core.values.*;
import org.musetest.ui.ide.navigation.resources.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MultiWindowResourceEditorsTests extends ComponentTest
    {
    @Test
    public void openResourceEditor() throws IOException
        {
        MuseProject project = new SimpleProject();
        StepConfiguration step = new StepConfiguration(LogMessage.TYPE_ID);
        step.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("message1"));
        SteppedTest test = new SteppedTest(step);
        test.setId(TEST_ID);
        project.getResourceStorage().addResource(test);

        int num_windows = listWindows().size();
        boolean edited = new MultiWindowResourceEditors().editResource(project.getResourceStorage().findResource(TEST_ID), project);
        Assert.assertTrue(edited);
        waitForUiEvents();

        Assert.assertEquals(num_windows + 1, listWindows().size());
        Assert.assertNotNull(window(TEST_ID));
        }

    @Override
    protected Node createComponentNode()
        {
        return new Label("placeholder node");
        }

    private final static String TEST_ID = "testid123";
    }


