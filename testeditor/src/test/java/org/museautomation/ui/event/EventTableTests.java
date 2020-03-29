package org.museautomation.ui.event;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.*;
import org.museautomation.core.events.*;
import org.museautomation.core.project.*;
import org.museautomation.core.util.*;

import java.util.concurrent.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class EventTableTests extends ComponentTest
    {
    @Test
    void displayEventLog()
        {
        _table.addEvents(_log);
        waitForUiEvents();

        Assertions.assertTrue(exists(_event1.getAttribute(MuseEvent.DESCRIPTION).toString()), "event1 not displayed");
        Assertions.assertTrue(exists(_event2.getAttribute(MuseEvent.DESCRIPTION).toString()), "event2 not displayed");
        }

    @Test
    void displayLiveEvents()
        {
        _table.addEvent(_event1);
        waitForUiEvents();
        Assertions.assertTrue(exists(_event1.getAttribute(MuseEvent.DESCRIPTION).toString()), "event1 not displayed");
        Assertions.assertTrue(exists(DurationFormat.formatMinutesSeconds(0L)), "time 1 not displayed");

        _table.addEvent(_event2);
        waitForUiEvents();
        Assertions.assertTrue(exists(_event2.getAttribute(MuseEvent.DESCRIPTION).toString()), "event2 not displayed");
        Assertions.assertTrue(exists(DurationFormat.formatMinutesSeconds(TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS))), "time 2 not displayed");
        }

    @Test
    void clearEvents()
        {
        _table.addEvent(_event1);
        _table.addEvent(_event2);
        waitForUiEvents();

        // displaying events is verified in other tests...no need to duplicate here

        _table.clear();
        waitForUiEvents();

        Assertions.assertFalse(exists(_event1.getAttribute(MuseEvent.DESCRIPTION).toString()), "event1 not cleared");
        Assertions.assertFalse(exists(_event2.getAttribute(MuseEvent.DESCRIPTION).toString()), "event2 not cleared");
        }

    @Override
    protected Node createComponentNode()
        {
        _event1 = mock(MuseEvent.class);
        when(_event1.getTypeId()).thenReturn("mockevent");
        when(_event1.getAttribute(MuseEvent.DESCRIPTION)).thenReturn("event1");
        when(_event1.getTimestamp()).thenReturn(0L);
        when(_event1.hasTag(MuseEvent.FAILURE)).thenReturn(false);
        when(_event1.hasTag(MuseEvent.ERROR)).thenReturn(false);

        _event2 = mock(MuseEvent.class);
        when(_event2.getTypeId()).thenReturn("mockevent");
        when(_event2.getAttribute(MuseEvent.DESCRIPTION)).thenReturn("event2");
        when(_event2.getTimestamp()).thenReturn(TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
        when(_event2.hasTag(MuseEvent.FAILURE)).thenReturn(false);
        when(_event2.hasTag(MuseEvent.ERROR)).thenReturn(false);

        EventLogger logger = new EventLogger();
        logger.eventRaised(_event1);
        logger.eventRaised(_event2);
        _log = logger.getLog();

        _table = new EventTable(new SimpleProject());
        return _table.getNode();
        }

    private EventTable _table;
    private MuseEvent _event1;
    private MuseEvent _event2;
    private EventLog _log;
    }