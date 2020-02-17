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
public class EventTableController implements InteractiveTaskStateListener, MuseEventListener
    {
    public EventTableController(EventTable table, InteractiveTestController controller)
        {
        _table = table;
        _controller = controller;
        _controller.addListener(this);
        }

    public void setTask(MuseTask task)
        {
        _task = task;
        }

    @Override
    public void stateChanged(InteractiveTaskState state)
        {
        if (state.equals(InteractiveTaskState.STARTING))
            {
            _runner = _controller.getTestRunner();
            if (_task != null && _runner.getExecutionContext().getTask() != _task)
                return;
            _table.clear();
            _table.addEvents(_runner.getExecutionContext().getEventLog());
            _runner.getExecutionContext().addEventListener(this);
            }
        else if (state.equals(InteractiveTaskState.IDLE) && _runner != null)  // test stopped
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

    private TaskRunner _runner = null;
    private MuseTask _task;
    }


