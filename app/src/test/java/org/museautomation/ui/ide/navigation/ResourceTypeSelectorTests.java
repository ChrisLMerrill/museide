package org.museautomation.ui.ide.navigation;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.museautomation.ui.ide.navigation.resources.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.resource.types.*;
import org.museautomation.core.step.*;
import org.museautomation.core.suite.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceTypeSelectorTests extends ComponentTest
    {
    @Test
    public void selectType()
        {
        Assert.assertNotNull(_selector.getSelection()); // there should be a default
        Assert.assertTrue(exists(_selector.getSelection().getName()));  // it is visible

        // select via code
        ResourceType first_type = new Macro.MacroResourceType();
        Assert.assertNotEquals(first_type, _selector.getSelection()); // make sure the one we're going to select isn't already selected
        _selector.select(first_type);
        waitForUiEvents();
        Assert.assertEquals(first_type, _selector.getSelection()); // it was selected (via code)
        Assert.assertTrue(exists(first_type.getName()));  // it is visible

        // select via GUI
        ResourceType second_type = new Function.FunctionResourceType();
        clickOn(first_type.getName()).clickOn(second_type.getName());
        Assert.assertEquals(second_type, _selector.getSelection());
        Assert.assertTrue(exists(second_type.getName()));  // it is visible
        }

    @Test
    public void selectSubtype()
        {
        ResourceType intial_type = _selector.getSelection();
        Assert.assertNotNull(intial_type); // there should be a default
        Assert.assertTrue(exists(intial_type.getName()));  // it is visible

        // select via GUI
        final ResourceType type = new MuseTaskSuite.TaskSuiteResourceType();
        final ResourceType sub_type = new IdListTaskSuite.IdListTaskSuiteSubtype();
        clickOn(intial_type.getName()).clickOn(type.getName()).clickOn(sub_type.getName());
        Assert.assertEquals(sub_type, _selector.getSelection());
        Assert.assertTrue(exists(sub_type.getName()));  // it is visible
        }

    @Override
    protected Node createComponentNode()
        {
        _selector = new ResourceTypeAndSubtypeSelector(_project);
        return _selector.getNode();
        }

    private MuseProject _project = new SimpleProject();
    private ResourceTypeAndSubtypeSelector _selector;
    }


