package org.museautomation.ui.event;

import javafx.application.*;
import org.museautomation.core.*;
import org.museautomation.core.execution.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * Routes events from a live test into an EventTable
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class EventTableController implements InteractiveTestStateListener, MuseEventListener
    {
    public EventTableController(EventTable table, InteractiveTestController controller)
        {
        _table = table;
        _controller = controller;
        _controller.addListener(this);
        }

    public void setTest(MuseTest test)
        {
        _test = test;
        }

    @Override
    public void stateChanged(InteractiveTestState state)
        {
        if (state.equals(InteractiveTestState.STARTING))
            {
            _runner = _controller.getTestRunner();
            if (_test != null && _runner.getExecutionContext().getTest() != _test)
                return;
            _table.clear();
            _table.addEvents(_runner.getExecutionContext().getEventLog());
            _runner.getExecutionContext().addEventListener(this);
            }
        else if (state.equals(InteractiveTestState.IDLE) && _runner != null)  // test stopped
            {
            _runner.getExecutionContext().removeEventListener(this);
            _runner = null;
            }
        }

    @Override
    public void eventRaised(MuseEvent event)
        {
        Platform.runLater(() -> _table.addEvent(event));
        }

    private final EventTable _table;
    private final InteractiveTestController _controller;

    private TestRunner _runner = null;
    private MuseTest _test;
    }


