package org.musetest.ui.steptest;

import org.musetest.core.context.*;
import org.musetest.core.execution.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MockTestRunner implements TestRunner
	{
	@Override
	public TestExecutionContext getExecutionContext()
		{
		return _context;
		}

	@Override
	public void runTest()
		{

		}

	@Override
	public Boolean completedNormally()
		{
		return true;
		}

	public void setExecutionContext(TestExecutionContext context)
		{
		_context = context;
		}

	private TestExecutionContext _context;
	}


