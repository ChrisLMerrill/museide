package org.museautomation.ui.steptest;

import org.museautomation.ui.steptask.execution.*;
import org.museautomation.core.*;
import org.museautomation.core.execution.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MockInteractiveTestController extends BaseInteractiveTestController
	{
	@Override
	public boolean run(SteppedTaskProvider test_provider)
		{
		return false;
		}

	@Override
	public void runOneStep(SteppedTaskProvider provider)
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
	public InteractiveTaskState getState()
		{
		return _state;
		}

	@Override
	public TaskResult getResult()
		{
		return null;
		}

	@Override
	public TaskRunner getTestRunner()
		{
		return _runner;
		}

	public void setRunner(TaskRunner runner)
		{
		_runner = runner;
		}

	public void raiseStateChangeEvent(InteractiveTaskState state)
		{
		_state = state;
		List<InteractiveTaskStateListener> listeners = new ArrayList<>(_listeners);
		for (InteractiveTaskStateListener listener : listeners)
			listener.stateChanged(state);
		}

	private TaskRunner _runner;
	private InteractiveTaskState _state = InteractiveTaskState.IDLE;
	}