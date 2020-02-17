package org.museautomation.ui.editors.suite;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.resource.csv.*;
import org.museautomation.core.step.*;
import org.museautomation.core.steptask.*;
import org.museautomation.core.suite.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ParameterListTaskSuiteEditorTests extends ComponentTest
    {
    @Test
    public void display()
        {
        ParameterListTaskSuite suite = new ParameterListTaskSuite();
        suite.setTaskId(TEST_ID);
        suite.setDataTableId(DATA_ID);
        _editor.editResource(_project, suite);
        waitForUiEvents();

        Assert.assertTrue(exists(TEST_ID));
        Assert.assertTrue(exists(DATA_ID));
        }

    @Test
    public void configure()
        {
        ParameterListTaskSuite suite = new ParameterListTaskSuite();
        _editor.editResource(_project, suite);
        waitForUiEvents();

        clickOn(id(ParameterListTaskSuiteEditor.TEST_FIELD_ID));
        clickOn(TEST_ID);
        waitForUiEvents();
        Assert.assertEquals(TEST_ID, suite.getTaskId());                // model changed

        clickOn(id(ParameterListTaskSuiteEditor.DATATABLE_FIELD_ID));
        clickOn(DATA_ID);
        waitForUiEvents();
        Assert.assertEquals(DATA_ID, suite.getDataTableId());           // model changed

        _editor.getUndoStack().undoAll();
        waitForUiEvents();

        Assert.assertNull(suite.getTaskId());                           // changes reverted from model
        Assert.assertNull(suite.getDataTableId());

        Assert.assertFalse(exists(TEST_ID));                            // changes reverted in UI
        Assert.assertFalse(exists(DATA_ID));
        }

    @Override
    protected Node createComponentNode() throws Exception
        {
        _project = new SimpleProject();

        SteppedTask test = new SteppedTask(new StepConfiguration(LogMessage.TYPE_ID));
        test.setId(TEST_ID);
        _project.getResourceStorage().addResource(test);

        BasicDataTable table = new BasicDataTable(new String[] {"col1", "col2"}, new String[][] { {"data1","data2"}, {"data1","data2"} });
        table.setId(DATA_ID);
        _project.getResourceStorage().addResource(table);

        _editor = new ParameterListTaskSuiteEditor();
        return _editor.getNode();
        }

    private MuseProject _project;
    private ParameterListTaskSuiteEditor _editor;

    private final static String TEST_ID = "test_id1";
    private final static String DATA_ID = "datatable_id2";
    }


