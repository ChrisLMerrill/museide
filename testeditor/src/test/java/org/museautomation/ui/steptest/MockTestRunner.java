package org.museautomation.ui.steptest;

import org.museautomation.core.context.*;
import org.museautomation.core.execution.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MockTestRunner implements TaskRunner
	{
	@Override
	public TaskExecutionContext getExecutionContext()
		{
		return _context;
		}

	@Override
	public void runTask() { }

	@Override
	public Boolean completedNormally()
		{
		return true;
		}

	public void setExecutionContext(TaskExecutionContext context)
		{
		_context = context;
		}

	private TaskExecutionContext _context;
	}


