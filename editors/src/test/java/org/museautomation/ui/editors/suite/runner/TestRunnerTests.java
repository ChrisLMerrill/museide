package org.museautomation.ui.editors.suite.runner;

import org.junit.*;
import org.museautomation.ui.editors.suite.*;
import org.museautomation.core.*;
import org.museautomation.core.context.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.project.*;
import org.museautomation.core.test.*;
import org.museautomation.core.test.plugins.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TestRunnerTests
    {
    @Test
    public void unexpectedExceptionInThreadedRunner()
        {
        MuseProject project = new SimpleProject();
        MuseTest test = new MockTestWithAction()
            {
            @Override
            protected boolean executeImplementation(TestExecutionContext context)
                {
                throw new RuntimeException("unexpected failure");
                }
            };

        BasicTestConfiguration config = new BasicTestConfiguration(test);
        config.addPlugin(new TestResultCollectorConfiguration().createPlugin());
        TestRunner runner = new BlockingThreadedTestRunner(new ProjectExecutionContext(project), config);

        runner.runTest();
        TestResult result = TestResult.find(runner.getExecutionContext());

        Assert.assertNotNull(result);
        Assert.assertEquals(TestResult.FailureType.Error, result.getFailures().get(0).getType());
        }
    }
