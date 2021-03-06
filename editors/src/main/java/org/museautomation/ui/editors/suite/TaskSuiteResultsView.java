package org.museautomation.ui.editors.suite;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import org.museautomation.core.*;
import org.museautomation.core.events.*;
import org.museautomation.ui.extend.events.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TaskSuiteResultsView
    {
    TaskSuiteResultsView(List<TaskResult> results, Map<TaskResult, EventLog> logs, MuseProject project)
	    {
        _splitter = new SplitPane();
        _splitter.setOrientation(Orientation.VERTICAL);

        TestSuiteResultsList tests_list = new TestSuiteResultsList();
        tests_list.setResults(results);
        _splitter.getItems().add(tests_list.getNode());

        _event_table = new EventTable(project);
        _event_table.setEmptyEventsHint("Select a task result to view the events");
        _splitter.getItems().add(_event_table.getNode());

        tests_list.addSelectionListener(result ->
            {
            _event_table.clear();
            if (result != null)
                _event_table.addEvents(logs.get(result));
            });
        }

    public Node getNode()
        {
        return _splitter;
        }

    private final SplitPane _splitter;
    private final EventTable _event_table;
    }