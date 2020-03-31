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
    public UnusedTests(MuseProject project, IdListTaskSuite suite)
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

    void addUnusedTestIds(List<String> target_list)
        {
        // add tests
        List<ResourceToken<MuseResource>> tests = _project.getResourceStorage().findResources(new ResourceQueryParameters(new MuseTask.TaskResourceType()));
        for (ResourceToken<MuseResource> token : tests)
            if (!_suite.getTaskIds().contains(token.getId()))
                target_list.add(token.getId());

        // add test suites
        List<ResourceToken<MuseResource>> suites = _project.getResourceStorage().findResources(new ResourceQueryParameters(new MuseTaskSuite.TaskSuiteResourceType()));
        for (ResourceToken<MuseResource> token : suites)
            if (!_suite.getTaskIds().contains(token.getId()))
                target_list.add(token.getId());
        }

    private MuseProject _project;
    private IdListTaskSuite _suite;
    }


