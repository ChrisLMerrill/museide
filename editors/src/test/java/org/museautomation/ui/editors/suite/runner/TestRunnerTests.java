package org.museautomation.ui.editors.suite.runner;

import org.junit.jupiter.api.*;
import org.museautomation.builtins.plugins.results.*;
import org.museautomation.builtins.plugins.suite.*;
import org.museautomation.core.*;
import org.museautomation.core.context.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.project.*;
import org.museautomation.core.task.*;
import org.museautomation.ui.editors.suite.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class TestRunnerTests
    {
    @Test
    void unexpectedExceptionInThreadedRunner()
        {
        MuseProject project = new SimpleProject();
        MuseTask test = new MockTaskWithAction()
            {
            @Override
            protected boolean executeImplementation(TaskExecutionContext context)
                {
                throw new RuntimeException("unexpected failure");
                }
            };

        BasicTaskConfiguration config = new BasicTaskConfiguration(test);
        config.addPlugin(new TaskResultCollectorConfiguration().createPlugin());
        TaskRunner runner = new BlockingThreadedTaskRunner(new ProjectExecutionContext(project), config);

        runner.runTask();
        TaskResult result = TaskResult.find(runner.getExecutionContext());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(TaskResult.FailureType.Error, result.getFailures().get(0).getType());
        }
    }