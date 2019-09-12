package org.musetest.ui.editors.suite;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.builtins.step.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.step.*;
import org.musetest.core.steptest.*;
import org.musetest.core.suite.*;
import org.musetest.ui.extend.components.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class IdListTestSuiteEditorTests extends ComponentTest
    {
    @Test
    public void displayList() throws IOException
        {
        int num_tests = 3;
        IdListTestSuite suite = setupTests(num_tests);
        Platform.runLater(() -> _editor.editResource(_project, suite));
        waitForUiEvents();

        for (int i = 0; i < num_tests; i++)
            Assert.assertTrue(exists(createTestId(i)));  // all tests displayed
        }

/*
    @Test
    public void okDisabledUntilSomethingSelected() throws IOException
        {
        IdListTestSuite suite = setupTests(3);
        Platform.runLater(() -> _editor.editResource(_project, suite));
        waitForUiEvents();

        final String new_test_id = "new-test";
        createTest(new_test_id);

        clickOn(id(IdListTestSuiteEditor.ADD_BUTTON_ID));

        Button ok_button = lookup(id(PopupDialog.OK_BUTTON_ID)).query();
        Assert.assertTrue(ok_button.disabledProperty().getValue());

        moveTo(new_test_id).moveBy(-100, 0).clickOn();                          // check something
        Assert.assertFalse(ok_button.disabledProperty().getValue());            // ok button enabled

        moveTo(new_test_id).moveBy(-100, 0).clickOn();                          // un-check it
        Assert.assertTrue(ok_button.disabledProperty().getValue());             // ok button disabled

        // cleanup
        moveTo(new_test_id).moveBy(-100, 0).clickOn();                          // re-check it
        clickOn(id(PopupDialog.OK_BUTTON_ID));                                  // dismiss the pop-up - else, the popup interferes with next test
        waitForUiEvents();
        }
*/

    @Test
    public void addTestToSuite() throws IOException
        {
        PopupDialog.makeFast();
        IdListTestSuite suite = setupTests(3);
        Platform.runLater(() -> _editor.editResource(_project, suite));
        waitForUiEvents();

        final String new_test_id = "new-test";
        createTest(new_test_id);

        clickOn(id(IdListTestSuiteEditor.ADD_BUTTON_ID));
        // TODO: how to verify existing tests are not shown in suggestion list?  Can't just check for exists(), because they are in the other list
        moveTo(new_test_id).moveBy(-100, 0).clickOn();
        clickOn(id(PopupDialog.OK_BUTTON_ID));
        waitForUiEvents();

        Assert.assertTrue(exists(new_test_id));  // displayed in list
        Assert.assertTrue(suite.getTestIds().contains(new_test_id));  // added to suite

        // undo
        _editor.getUndoStack().undoLastAction();
        waitForUiEvents();
        Assert.assertFalse(exists(new_test_id));  // not displayed in list
        Assert.assertFalse(suite.getTestIds().contains(new_test_id));  // not in suite
        }

    @Test
    public void removeTestsFromSuite() throws IOException, InterruptedException
        {
        IdListTestSuite suite = setupTests(5);
        Platform.runLater(() -> _editor.editResource(_project, suite));
        waitForUiEvents();

        String removed1 = createTestId(1);
        String removed2 = createTestId(3);
        clickOn(removed1);
        press(KeyCode.CONTROL).clickOn(removed2).release(KeyCode.CONTROL);
        push(KeyCode.DELETE);
        waitForUiEvents();

        Assert.assertFalse(exists(removed1));  // not displayed in list
        Assert.assertFalse(exists(removed2));  // not displayed in list
        Assert.assertFalse(suite.getTestIds().contains(removed1));  // removed from suite
        Assert.assertFalse(suite.getTestIds().contains(removed2));  // removed from suite

        // undo
        _editor.getUndoStack().undoLastAction();
        waitForUiEvents();
        Assert.assertTrue(exists(removed1));  // returned to list
        Assert.assertTrue(exists(removed2));  // returned to list
        Assert.assertTrue(suite.getTestIds().contains(removed1));  // exists in suite
        Assert.assertTrue(suite.getTestIds().contains(removed2));  // exists in suite
        }

    @Test
    public void listOfAddableTests() throws IOException
        {
        IdListTestSuite suite = setupTests(2);
        final String new_test_1 = "new-test1";
        createTest(new_test_1);
        final String new_test_2 = "new-test2";
        createTest(new_test_2);

        List<String> unused_test_ids = new UnusedTests(_project, suite).getUnusedTestIds();

        Assert.assertTrue(unused_test_ids.contains(new_test_1));  // new tests should be in the list
        Assert.assertTrue(unused_test_ids.contains(new_test_2));
        Assert.assertFalse(unused_test_ids.contains(createTestId(0)));  // previously existsing tets shoudl not
        Assert.assertFalse(unused_test_ids.contains(createTestId(1)));
        }

    private IdListTestSuite setupTests(int num_tests) throws IOException
        {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < num_tests; i++)
            {
            String id = createTestId(i);
            ids.add(id);
            createTest(id);
            }
        IdListTestSuite suite = new IdListTestSuite();
        suite.setTestIds(ids);
        return suite;
        }

    private MuseTest createTest(String id) throws IOException
        {
        SteppedTest test = new SteppedTest(new StepConfiguration(LogMessage.TYPE_ID));
        test.setId(id);
        _project.getResourceStorage().addResource(test);
        return test;
        }

    private String createTestId(int test_num)
        {
        return "test-" + test_num;
        }

    @Override
    protected Node createComponentNode()
        {
        _project = new SimpleProject();
        _editor = new IdListTestSuiteEditor();
        return _editor.getNode();
        }

    private MuseProject _project;
    private IdListTestSuiteEditor _editor;
    }


