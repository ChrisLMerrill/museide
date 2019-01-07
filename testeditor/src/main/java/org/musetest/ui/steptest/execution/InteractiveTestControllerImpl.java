package org.musetest.ui.steptest.execution;

import org.musetest.core.*;
import org.musetest.core.context.*;
import org.musetest.core.events.*;
import org.musetest.core.execution.*;
import org.musetest.core.plugins.*;
import org.musetest.core.test.*;

import java.util.*;

/**
 * Manages the state of an interactive execution of a stepped test and summarizes test events
 * into InteractiveTestState change events.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class InteractiveTestControllerImpl extends BaseInteractiveTestController
	{
	public InteractiveTestControllerImpl()
		{
		}

	public InteractiveTestState getState()
		{
		return _state;
		}

	public boolean run(SteppedTestProvider test_provider)
		{
		if (_state.equals(InteractiveTestState.IDLE))
			{
			_provider = test_provider;
			InteractiveTestRunner runner = getRunner();
			runner.start();
			setState(InteractiveTestState.STARTING);
			setState(InteractiveTestState.RUNNING);
			runner.runTest();

			return true;
			}
		return false;
		}

	private InteractiveTestRunner getRunner()
		{
		if (_runner == null && _provider != null)
			{
			BasicTestConfiguration config = new BasicTestConfiguration(_provider.getTest());
			config.addPlugin(new EventListener());
			final PauseOnFailureOrError pauser = new PauseOnFailureOrError();
			config.addPlugin(pauser);

			_runner = new InteractiveTestRunner(new ProjectExecutionContext(_provider.getProject()), config);
			pauser.setRunner(_runner);
			}
		return _runner;
		}

	class EventListener implements MusePlugin, MuseEventListener
		{
		@Override
		public void eventRaised(MuseEvent event)
			{
			switch (event.getTypeId())
				{
				case EndTestEventType.TYPE_ID:
					setState(InteractiveTestState.STOPPING);
					break;
				case PauseTestEventType.TYPE_ID:
					setState(InteractiveTestState.PAUSED);
					break;
				default:
					setState(InteractiveTestState.RUNNING);
				}
			}

		@Override
		public boolean conditionallyAddToContext(MuseExecutionContext context, boolean automatic)
			{
			context.addPlugin(this);
			return true;
			}

		@Override
		public void initialize(MuseExecutionContext context)
			{
			_context = context;
			_context.addEventListener(this);
			}

		@Override
		public void shutdown()
			{
			_result = TestResult.find(getRunner().getExecutionContext());
			_runner = null;
			setState(InteractiveTestState.IDLE);
			_context.removeEventListener(this);
			}

		@Override
		public String getId()
			{
			return "no/id";
			}

		MuseExecutionContext _context;
		}


	private void setState(InteractiveTestState state)
		{
		if (state.equals(_state))
			return;
		_state = state;

		// iterate a separate list, so that listeners may unsubscribe during an event.
		List<InteractiveTestStateListener> listeners = new ArrayList<>(_listeners);
		for (InteractiveTestStateListener listener : listeners)
			listener.stateChanged(state);
		}

	/**
	 * Get the current TestRunner, or null if there is no TestRunner active.
	 */
	public TestRunner getTestRunner()
		{
		return getRunner();
		}

	@SuppressWarnings("unused") // used in GUI
	public void stop()
		{
		getRunner().requestStop();
		}

	@SuppressWarnings("unused") // used in GUI
	public void pause()
		{
		getRunner().requestPause();
		}

	public void resume()
		{
		getRunner().requestResume();
		}

	public void step()
		{
		getRunner().requestStep();
		}

/*
    public void runPastStep(SteppedTestProvider provider, StepConfiguration step)
        {
        _provider = provider;
        getRunner().getExecutionContext().addEventListener(new PauseAfterStep(getRunner(), step));
        run(provider);
        }
*/

	@SuppressWarnings("unused") // used in GUI
	public void runOneStep(SteppedTestProvider provider)
		{
		_provider = provider;
		getRunner().requestPause();
		run(provider);
		}

	public TestResult getResult()
		{
		return _result;
		}

	private InteractiveTestState _state = InteractiveTestState.IDLE;
	private SteppedTestProvider _provider;
	private InteractiveTestRunner _runner;
	private TestResult _result = null;
	}
