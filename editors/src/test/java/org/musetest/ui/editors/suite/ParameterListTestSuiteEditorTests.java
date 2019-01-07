package org.musetest.ui.editors.suite;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.builtins.step.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.resource.csv.*;
import org.musetest.core.step.*;
import org.musetest.core.steptest.*;
import org.musetest.core.suite.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ParameterListTestSuiteEditorTests extends ComponentTest
    {
    @Test
    public void display()
        {
        ParameterListTestSuite suite = new ParameterListTestSuite();
        suite.setTestId(TEST_ID);
        suite.setDataTableId(DATA_ID);
        _editor.editResource(_project, suite);
        waitForUiEvents();

        Assert.assertTrue(exists(TEST_ID));
        Assert.assertTrue(exists(DATA_ID));
        }

    @Test
    public void configure()
        {
        ParameterListTestSuite suite = new ParameterListTestSuite();
        _editor.editResource(_project, suite);
        waitForUiEvents();

        clickOn(id(ParameterListTestSuiteEditor.TEST_FIELD_ID));
        clickOn(TEST_ID);
        waitForUiEvents();
        Assert.assertEquals(TEST_ID, suite.getTestId());                // model changed

        clickOn(id(ParameterListTestSuiteEditor.DATATABLE_FIELD_ID));
        clickOn(DATA_ID);
        waitForUiEvents();
        Assert.assertEquals(DATA_ID, suite.getDataTableId());           // model changed

        _editor.getUndoStack().undoAll();
        waitForUiEvents();

        Assert.assertNull(suite.getTestId());                           // changes reverted from model
        Assert.assertNull(suite.getDataTableId());

        Assert.assertFalse(exists(TEST_ID));                            // changes reverted in UI
        Assert.assertFalse(exists(DATA_ID));
        }

    @Override
    protected Node createComponentNode() throws Exception
        {
        _project = new SimpleProject();

        SteppedTest test = new SteppedTest(new StepConfiguration(LogMessage.TYPE_ID));
        test.setId(TEST_ID);
        _project.getResourceStorage().addResource(test);

        BasicDataTable table = new BasicDataTable(new String[] {"col1", "col2"}, new String[][] { {"data1","data2"}, {"data1","data2"} });
        table.setId(DATA_ID);
        _project.getResourceStorage().addResource(table);

        _editor = new ParameterListTestSuiteEditor();
        return _editor.getNode();
        }

    private MuseProject _project;
    private ParameterListTestSuiteEditor _editor;

    private final static String TEST_ID = "test_id1";
    private final static String DATA_ID = "datatable_id2";
    }


