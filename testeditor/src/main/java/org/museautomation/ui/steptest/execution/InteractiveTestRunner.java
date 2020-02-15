package org.museautomation.ui.steptest.execution;

import org.museautomation.core.*;
import org.museautomation.core.context.*;
import org.museautomation.core.events.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.steptest.*;
import org.museautomation.core.test.*;
import org.slf4j.*;

/**
 * Provides management of execution of a SteppedTest on a separate thread, with the ability
 * to run, pause and single-step the test.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("WeakerAccess")  // public API
public class InteractiveTestRunner extends ThreadedTestRunner implements Runnable
    {
    public InteractiveTestRunner(MuseExecutionContext context, TestConfiguration config)
        {
        super(context, config);
        }

    public void start()
	    {
	    if (!_started)
		    {
		    startTest();
		    _started = true;
		    }
	    }

    @Override
    public void run()
        {
        start();

        if (_test_context instanceof SteppedTestExecutionContext)
	        {
	        SteppedTestExecutionContext stepped_context = (SteppedTestExecutionContext) _test_context;
	        _executor = new SteppedTestExecutor((SteppedTest) _test, stepped_context);
	        boolean has_more = true;
	        while (!_interrupted && has_more && !_executor.terminateRequested())
	            {
	            if (!pause() || _step_requested)
	                has_more = _executor.executeNextStep();
	            _step_requested = false;
	            }
	        if (_interrupted)
		        _test_context.raiseEvent(new MuseEvent(InterruptedEventType.TYPE));

	        _completed_normally = !_interrupted;
	        }
        else
	        {
	        final String message = "Expected to be running a stepped test. ExecutionContext is a " + _context.getClass().getSimpleName();
	        _test_context.raiseEvent(TestErrorEventType.create(message));
	        }

        finishTest();
        _thread = null;
        }

    private synchronized boolean pause()
        {
        if (!_pause_requested)
            return false;

        _paused = true;
        _test_context.raiseEvent(PauseTestEventType.create(_executor.getNextStep(), _test_context));

        try
            {
            wait();
            _paused = false;
            if (!_interrupted)
	            _test_context.raiseEvent(new MuseEvent(ResumeEventType.INSTANCE));
            }
        catch (InterruptedException e)
            {
            _interrupted = true;
            }

        return true;
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
    private SteppedTestExecutor _executor;
    private Thread _thread;

    private final static Logger LOG = LoggerFactory.getLogger(InteractiveTestRunner.class);

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