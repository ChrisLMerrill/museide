package org.museautomation.ui.editors.suite;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.suite.*;

import java.util.*;

/**
 * A list of test Ids in the project that are not used by the test suite.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UnusedTests
    {
    public UnusedTests(MuseProject project, IdListTestSuite suite)
        {
        _project = project;
        _suite = suite;
        }

    public List<String> getUnusedTestIds()
        {
        List<String> unused = new ArrayList<>();
        addUnusedTestIds(unused);
        return unused;
        }

    public void addUnusedTestIds(List<String> target_list)
        {
        // add tests
    List<ResourceToken> tests = _project.getResourceStorage().findResources(new ResourceQueryParameters(new MuseTest.TestResourceType()));
        for (ResourceToken token : tests)
            if (!_suite.getTestIds().contains(token.getId()))
                target_list.add(token.getId());

        // add test suites
        List<ResourceToken> suites = _project.getResourceStorage().findResources(new ResourceQueryParameters(new MuseTestSuite.TestSuiteResourceType()));
        for (ResourceToken token : suites)
            if (!_suite.getTestIds().contains(token.getId()))
                target_list.add(token.getId());
        }

    private MuseProject _project;
    private IdListTestSuite _suite;
    }


