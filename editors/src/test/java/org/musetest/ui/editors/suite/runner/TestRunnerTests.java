package org.musetest.ui.editors.suite.runner;

import org.junit.*;
import org.musetest.core.*;
import org.musetest.core.context.*;
import org.musetest.core.execution.*;
import org.musetest.core.project.*;
import org.musetest.core.test.*;
import org.musetest.core.test.plugins.*;
import org.musetest.ui.editors.suite.*;

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
