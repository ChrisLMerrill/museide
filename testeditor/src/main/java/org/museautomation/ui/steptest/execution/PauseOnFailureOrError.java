package org.museautomation.ui.steptest.execution;

import org.museautomation.core.*;
import org.museautomation.core.events.*;
import org.museautomation.core.plugins.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class PauseOnFailureOrError implements MuseEventListener, MusePlugin
    {
    public void setRunner(InteractiveTestRunner runner)
	    {
	    _runner = runner;
	    }

    @Override
    public void eventRaised(MuseEvent event)
        {
        if (event.hasTag(MuseEvent.ERROR) || event.hasTag(MuseEvent.FAILURE))
            _runner.requestPause();
        else if (event.getTypeId().equals(EndTestEventType.TYPE_ID))
        	_context.removeEventListener(this);
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
	    _context.removeEventListener(this);
	    }

    @Override
    public String getId()
	    {
	    return "no/id";
	    }

    private InteractiveTestRunner _runner;
    private MuseExecutionContext _context;
    }