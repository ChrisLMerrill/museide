package org.musetest.ui.steptest;

import org.musetest.core.*;
import org.musetest.core.execution.*;
import org.musetest.ui.steptest.execution.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MockInteractiveTestController extends BaseInteractiveTestController
	{
	@Override
	public boolean run(SteppedTestProvider test_provider)
		{
		return false;
		}

	@Override
	public void runOneStep(SteppedTestProvider provider)
		{

		}

	@Override
	public void stop()
		{

		}

	@Override
	public void pause()
		{

		}

	@Override
	public void resume()
		{

		}

	@Override
	public void step()
		{

		}

	@Override
	public InteractiveTestState getState()
		{
		return _state;
		}

	@Override
	public TestResult getResult()
		{
		return null;
		}

	@Override
	public TestRunner getTestRunner()
		{
		return _runner;
		}

	public void setRunner(TestRunner runner)
		{
		_runner = runner;
		}

	public void raiseStateChangeEvent(InteractiveTestState state)
		{
		_state = state;
		List<InteractiveTestStateListener> listeners = new ArrayList<>(_listeners);
		for (InteractiveTestStateListener listener : listeners)
			listener.stateChanged(state);
		}

	private TestRunner _runner;
	private InteractiveTestState _state = InteractiveTestState.IDLE;
	}