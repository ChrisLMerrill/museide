package org.musetest.ui.editors.suite;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.core.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TestSuiteResultsListTests extends ComponentTest
    {
    @Test
    public void displayResults()
        {
        Assert.assertTrue(exists("success"));
        Assert.assertTrue(exists("failure: failed"));
        Assert.assertTrue(exists("error: errored"));
        }

    @Test
    public void filterResults()
        {
        clickOn(id(TestSuiteResultsList.SUCCESS_FILTER_BUTTON_ID));
        Assert.assertFalse("successes not hidden by pressing the success filter button", exists("success"));

        clickOn(id(TestSuiteResultsList.FAILURE_FILTER_BUTTON_ID));
        Assert.assertFalse("failures not hidden by pressing the failure filter button", exists("failure"));

        clickOn(id(TestSuiteResultsList.ERROR_FILTER_BUTTON_ID));
        Assert.assertFalse("errors not hidden by pressing the error filter button", exists("error"));
        }

    @Test
    public void selectionEvent()
        {
        AtomicReference<TestResult> selected = new AtomicReference<>(null);
        _table.addSelectionListener(selected::set);
        clickOn("failure: failed");

        Assert.assertEquals(_results.get(1), selected.get());
        }

    @Override
    protected Node createComponentNode()
        {
        _results = new ArrayList<>();
        _results.add(TestResult.create("success", "success", "passed"));
        _results.add(TestResult.create("failure", "failure", "failed", TestResult.FailureType.Failure, "failed"));
        _results.add(TestResult.create("error", "error", "errored", TestResult.FailureType.Error, "errored"));

        _table = new TestSuiteResultsList();
        _table.setResults(_results);
        return _table.getNode();
        }

    private TestSuiteResultsList _table;
    private List<TestResult> _results;
    }


