package org.museautomation.ui.extend.components;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TimerLabelTests extends ComponentTest
    {
    @Test
    void startAndIncrement()
        {
        Assertions.assertEquals("", textOf(id(TIMER)));

        _timer.setInitalValue(1990);
        waitForUiEvents();
        Assertions.assertEquals("1", textOf(id(TIMER)));

        _timer.start();
        sleep(20);
        waitForUiEvents();
        Assertions.assertEquals("2", textOf(id(TIMER)));

        _timer.stop();
        }

    @Test
    void stopTimer()
        {
        _timer.setInitalValue(1950);
        waitForUiEvents();
        Assertions.assertEquals("1", textOf(id(TIMER)));

        sleep(30);
        _timer.stop();
        waitForUiEvents();
        Assertions.assertEquals("1", textOf(id(TIMER)));  // should not have incremented yet

        sleep(100);
        waitForUiEvents();
        Assertions.assertEquals("1", textOf(id(TIMER)));  // STILL should not have incremented, if it stopped as expected
        }

    @Override
    protected Node createComponentNode()
        {
        _timer = new TimerLabel("s");
        _timer.setId(TIMER);
        return _timer;
        }

    private TimerLabel _timer;
    private final static String TIMER = "TIMER";
    }