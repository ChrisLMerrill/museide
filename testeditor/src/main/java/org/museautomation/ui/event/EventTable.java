package org.museautomation.ui.event;

import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.util.*;
import org.museautomation.core.*;
import org.museautomation.core.events.*;
import org.museautomation.core.util.*;
import org.museautomation.ui.extend.javafx.*;

/**
 * Displays MuseEvents as provided, or from an EventLog.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class EventTable
    {
    public EventTable(MuseProject project)
        {
        _event_types = EventTypes.get(project);
        _table.setPlaceholder(new Label("Select a test result to view the Event Log"));

        TableColumn<MuseEvent, Long> time_column = new TableColumn<>();
        _table.getColumns().add(time_column);
        time_column.setText("Time");
        time_column.prefWidthProperty().setValue(100);
        time_column.setCellValueFactory(new PropertyValueFactory("timestamp"));
        time_column.setCellFactory(new Callback<TableColumn<MuseEvent, Long>, TableCell<MuseEvent, Long>>()
            {
            @Override
            public TableCell<MuseEvent, Long> call(TableColumn<MuseEvent, Long> param)
                {
                return new TableCell<MuseEvent, Long>()
                    {
                    @Override
                    protected void updateItem(Long time, boolean empty)
                        {
                        super.updateItem(time, empty);
                        if (empty)
                            setText(null);
                        else
                            setText(DurationFormat.formatMinutesSeconds(time - _first_event_time));
                        }
                    };
                }
            });

        TableColumn<MuseEvent, String> description_column = new TableColumn<>();
        _table.getColumns().add(description_column);
        description_column.setText("Event");
        description_column.prefWidthProperty().bind(_table.widthProperty().subtract(time_column.prefWidthProperty()));
        description_column.setCellValueFactory(p ->
	        {
	        final EventType type = _event_types.findType(p.getValue());
	        String description = type.getDescription(p.getValue());
	        if (description == null)
	        	description = type.getName();
	        return new ReadOnlyObjectWrapper<>(description);
	        });
        description_column.setCellFactory(new Callback<TableColumn<MuseEvent, String>, TableCell<MuseEvent, String>>()
            {
            @Override
            public TableCell<MuseEvent, String> call(TableColumn<MuseEvent, String> param)
                {
                return new TableCell<MuseEvent, String>()
                    {
                    @Override
                    protected void updateItem(String description, boolean empty)
                        {
                        super.updateItem(description, empty);

                        MuseEvent event = (MuseEvent) getTableRow().getItem();
                        if (empty || event == null)
                            {
                            setText(null);
                            setTooltip(null);
                            Styles.removeStyle(this, ERROR_STYLE);
                            }
                        else
                            {
                            setText(description);
                            setTooltip(new Tooltip(description));

                            if (event.hasTag(MuseEvent.WARNING))
	                            Styles.addStyle(this, WARNING_STYLE);
                            else
	                            Styles.removeStyle(this, WARNING_STYLE);

                            if (event.hasTag(MuseEvent.FAILURE) || event.hasTag(MuseEvent.ERROR))
	                            Styles.addStyle(this, ERROR_STYLE);
                            else
	                            Styles.removeStyle(this, ERROR_STYLE);
                            }
                        }
                    };
                }
            });

        _table.setItems(_events);
        }

    public Node getNode()
        {
        return _table;
        }

    public void addEvents(EventLog log)
        {
        if (log == null)
        	return;  // Don't NPE when no log...just show empty table.
        if (log.getEvents().size() > 0)
            _first_event_time = log.getEvents().get(0).getTimestamp();
        _events.clear();
        _events.addAll(log.getEvents());
        }

    public void addEvent(MuseEvent event)
        {
        if (event.getTimestamp() < _first_event_time)
            _first_event_time = event.getTimestamp();
        Platform.runLater(() ->
            {
            _events.add(event);
            _table.scrollTo(_events.size() - 1);
            });
        }

    public void clear()
        {
        _events.clear();
        _first_event_time = Long.MAX_VALUE;
        }

    private TableView<MuseEvent> _table = new TableView();
    private ObservableList<MuseEvent> _events = FXCollections.observableArrayList();

    private long _first_event_time = Long.MAX_VALUE;
    private final EventTypes _event_types;
    private final static String ERROR_STYLE = "error";
    private final static String WARNING_STYLE = "error";
    }