package org.museautomation.ui.editors.suite;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TestSuiteResultsListTests extends ComponentTest
    {
    @Test
    void displayResults()
        {
        Assertions.assertTrue(exists("success"));
        Assertions.assertTrue(exists("failure: failed"));
        Assertions.assertTrue(exists("error: errored"));
        }

    @Test
    void filterResults()
        {
        clickOn(id(TestSuiteResultsList.SUCCESS_FILTER_BUTTON_ID));
        Assertions.assertFalse(exists("success"), "successes not hidden by pressing the success filter button");

        clickOn(id(TestSuiteResultsList.FAILURE_FILTER_BUTTON_ID));
        Assertions.assertFalse(exists("failure"), "failures not hidden by pressing the failure filter button");

        clickOn(id(TestSuiteResultsList.ERROR_FILTER_BUTTON_ID));
        Assertions.assertFalse(exists("error"), "errors not hidden by pressing the error filter button");
        }

    @Test
    void selectionEvent()
        {
        AtomicReference<TaskResult> selected = new AtomicReference<>(null);
        _table.addSelectionListener(selected::set);
        clickOn("failure: failed");

        Assertions.assertEquals(_results.get(1), selected.get());
        }

    @Override
    public Node createComponentNode()
        {
        _results = new ArrayList<>();
        _results.add(TaskResult.create("success", "success", "passed"));
        _results.add(TaskResult.create("failure", "failure", "failed", TaskResult.FailureType.Failure, "failed"));
        _results.add(TaskResult.create("error", "error", "errored", TaskResult.FailureType.Error, "errored"));

        _table = new TestSuiteResultsList();
        _table.setResults(_results);
        return _table.getNode();
        }

    private TestSuiteResultsList _table;
    private List<TaskResult> _results;
    }