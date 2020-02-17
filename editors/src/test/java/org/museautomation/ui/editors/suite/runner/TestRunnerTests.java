package org.museautomation.ui.editors.suite.runner;

import org.junit.*;
import org.museautomation.core.*;
import org.museautomation.core.context.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.project.*;
import org.museautomation.core.task.*;
import org.museautomation.core.task.plugins.*;
import org.museautomation.ui.editors.suite.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TestRunnerTests
    {
    @Test
    public void unexpectedExceptionInThreadedRunner()
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

        Assert.assertNotNull(result);
        Assert.assertEquals(TaskResult.FailureType.Error, result.getFailures().get(0).getType());
        }
    }
