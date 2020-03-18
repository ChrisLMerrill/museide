package org.museautomation.ui.steptask.execution;

import org.museautomation.core.*;
import org.museautomation.core.context.*;
import org.museautomation.core.events.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.steptask.*;
import org.museautomation.core.task.*;
import org.museautomation.ui.extend.edit.step.*;
import org.slf4j.*;

/**
 * Provides management of execution of a SteppedTask on a separate thread, with the ability
 * to run, pause and single-step the task.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("WeakerAccess")  // public API
public class InteractiveTaskRunner extends ThreadedTaskRunner implements Runnable
    {
    public InteractiveTaskRunner(MuseExecutionContext context, TaskConfiguration config, Breakpoints breakpoints)
        {
        super(context, config);
        _breakpoints = breakpoints;
        }

    public void start()
	    {
	    if (!_started)
		    {
		    startTask();
		    _started = true;
		    }
	    }

    @Override
    public void run()
        {
        start();

        if (_task_context instanceof SteppedTaskExecutionContext)
	        {
            SteppedTaskExecutionContext stepped_context = (SteppedTaskExecutionContext) _task_context;
	        _executor = new SteppedTaskExecutor((SteppedTask) _task, stepped_context);
	        boolean has_more = true;
	        while (!_interrupted && has_more && !_executor.terminateRequested())
	            {
	            if (!pause() || _step_requested)
	                has_more = _executor.executeNextStep();
	            _step_requested = false;
	            }
	        if (_interrupted)
		        _task_context.raiseEvent(new MuseEvent(InterruptedEventType.TYPE));

	        _completed_normally = !_interrupted;
	        }
        else
	        {
	        final String message = "Expected to be running a SteppedTask. ExecutionContext is a " + _context.getClass().getSimpleName();
	        _task_context.raiseEvent(TaskErrorEventType.create(message));
	        }

        finishTask();
        }

    private synchronized boolean pause()
        {
        checkForBreakpoint();
        if (!_pause_requested)
            return false;

        _paused = true;
        _task_context.raiseEvent(PauseTaskEventType.create(_executor.getNextStep(), _task_context));

        try
            {
            wait();
            _paused = false;
            if (!_interrupted)
	            _task_context.raiseEvent(new MuseEvent(ResumeEventType.INSTANCE));
            }
        catch (InterruptedException e)
            {
            _interrupted = true;
            }

        return true;
        }

    private void checkForBreakpoint()
        {
        if (_breakpoints.isBreakpoint(_executor.getNextStep()) && !_resume_from_breakpoint)
            _pause_requested = true;
        else
            _resume_from_breakpoint = false;
        }

    public synchronized void requestStop()
        {
        _interrupted = true;
        if (_paused)
            notify();
        else if (_thread != null)
            _thread.interrupt();
        }

    public void requestPause()
        {
        _pause_requested = true;
        }

    public synchronized void requestResume()
        {
        _resume_from_breakpoint = true;
        if (!_pause_requested)
            {
            LOG.error("cannot resume - runner is not paused");
            return;
            }

        _pause_requested = false;
        notify();
        }

    public synchronized void requestStep()
        {
        _resume_from_breakpoint = true;
        if (!_pause_requested)
            {
            LOG.error("cannot step - runner is not paused");
            return;
            }

        _step_requested = true;
        notify();
        }

    private boolean _interrupted = false;
    private boolean _pause_requested = false;
    private boolean _step_requested = false;
    private boolean _paused = false;
    private boolean _started = false;
    private SteppedTaskExecutor _executor;
    private Breakpoints _breakpoints;
    private boolean _resume_from_breakpoint = false;

    private final static Logger LOG = LoggerFactory.getLogger(InteractiveTaskRunner.class);

    public final static class ResumeEventType extends EventType
	    {
	    @Override
	    public String getTypeId()
		    {
		    return TYPE_ID;
		    }

	    @Override
	    public String getName()
		    {
		    return "Resume";
		    }

	    public final static String TYPE_ID = "resume";
	    public final static EventType INSTANCE = new ResumeEventType();
	    }
    }