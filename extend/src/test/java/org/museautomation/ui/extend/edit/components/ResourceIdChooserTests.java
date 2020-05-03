package org.museautomation.ui.extend.edit.components;

import javafx.scene.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.variables.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceIdChooserTests extends ComponentTest
    {
    @Test
    void showChoices()
        {
        Assertions.assertTrue(exists(LIST1_ID));            // can see the initial selection
        Assertions.assertFalse(exists(LIST2_ID));           // cannot see this choice

        clickOn(id(ResourceIdChooser.CHOOSER_ID));      // expand selections
        Assertions.assertTrue(exists(LIST2_ID));            // can see the other choice, now
        }

    @Test
    void changeSelection()
        {
        clickOn(id(ResourceIdChooser.CHOOSER_ID));  // expand choices
        clickOn(LIST2_ID);                          // make another choice

        Assertions.assertEquals(LIST2_ID, _chooser.getSelectedId());
        }

    @Override
    public Node createComponentNode() throws Exception
        {
        MuseProject project = new SimpleProject();
        VariableList list1 = new VariableList();
        list1.setId(LIST1_ID);
        project.getResourceStorage().addResource(list1);
        VariableList list2 = new VariableList();
        list2.setId(LIST2_ID);
        project.getResourceStorage().addResource(list2);

        _chooser = new ResourceIdChooser(project, new VariableList.VariableListResourceType(), LIST1_ID);

        GridPane grid = new GridPane();
        grid.add(_chooser, 0, 0);
        return grid;
        }

    private ResourceIdChooser _chooser;

    private final static String LIST1_ID = "list1";
    private final static String LIST2_ID = "list2";
    }