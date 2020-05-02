package org.museautomation.ui.steptask.execution;

import org.museautomation.core.*;
import org.museautomation.core.context.*;
import org.museautomation.core.events.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.plugins.*;
import org.museautomation.core.task.*;
import org.museautomation.core.task.input.*;
import org.museautomation.ui.extend.edit.step.*;
import org.museautomation.ui.steptree.*;

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

	public InteractiveTaskState getState()
		{
		return _state;
		}

    public Breakpoints getBreakpoints()
        {
        return _breakpoints;
        }

    public boolean run(SteppedTaskProvider task_provider)
		{
		if (_state.equals(InteractiveTaskState.IDLE))
			{
			_task_provider = task_provider;
			InteractiveTaskRunner runner = getRunner();
			runner.start();
			setState(InteractiveTaskState.STARTING);
			setState(InteractiveTaskState.RUNNING);
			runner.runTask();

			return true;
			}
		return false;
		}

	private InteractiveTaskRunner getRunner()
		{
		if (_runner == null && _task_provider != null)
			{
			BasicTaskConfiguration config = new BasicTaskConfiguration(_task_provider.getTask());
			config.addPlugin(new EventListener());
			final PauseOnFailureOrError pauser = new PauseOnFailureOrError();
			config.addPlugin(pauser);

			_runner = new InteractiveTaskRunner(new ProjectExecutionContext(_task_provider.getProject()), config, _breakpoints);
            for (TaskInputProvider provider : _input_providers)
                _runner.addInputProvider(provider);
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
				case EndTaskEventType.TYPE_ID:
					setState(InteractiveTaskState.STOPPING);
					break;
				case PauseTaskEventType.TYPE_ID:
					setState(InteractiveTaskState.PAUSED);
					break;
				default:
					setState(InteractiveTaskState.RUNNING);
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
			_result = TaskResult.find(getRunner().getExecutionContext());
			_runner = null;
			setState(InteractiveTaskState.IDLE);
			_context.removeEventListener(this);
			}

		@Override
		public String getId()
			{
			return "no/id";
			}

		MuseExecutionContext _context;
		}


	private void setState(InteractiveTaskState state)
		{
		if (state.equals(_state))
			return;
		_state = state;

		// iterate a separate list, so that listeners may unsubscribe during an event.
		List<InteractiveTaskStateListener> listeners = new ArrayList<>(_listeners);
		for (InteractiveTaskStateListener listener : listeners)
			listener.stateChanged(state);
		}

	/**
	 * Get the current TestRunner, or null if there is no TestRunner active.
	 */
	public TaskRunner getTestRunner()
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
	public void runOneStep(SteppedTaskProvider provider)
		{
		_task_provider = provider;
		getRunner().requestPause();
		run(provider);
		}

    public void addInputProvider(TaskInputProvider provider)
        {
        _input_providers.add(provider);
        }

	public TaskResult getResult()
		{
		return _result;
		}

	private InteractiveTaskState _state = InteractiveTaskState.IDLE;
	private SteppedTaskProvider _task_provider;
	private InteractiveTaskRunner _runner;
	private TaskResult _result = null;
    private final Breakpoints _breakpoints = new TaskBreakpoints();
    private final List<TaskInputProvider> _input_providers = new ArrayList<>();
	}
