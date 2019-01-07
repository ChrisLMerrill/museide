package org.musetest.ui.extend.components;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TimerLabelTests extends ComponentTest
    {
    @Test
    public void startAndIncrement()
        {
        Assert.assertEquals("", textOf(id(TIMER)));

        _timer.setInitalValue(1990);
        waitForUiEvents();
        Assert.assertEquals("1", textOf(id(TIMER)));

        _timer.start();
        sleep(20);
        waitForUiEvents();
        Assert.assertEquals("2", textOf(id(TIMER)));

        _timer.stop();
        }

    @Test
    public void stopTimer()
        {
        _timer.setInitalValue(1950);
        waitForUiEvents();
        Assert.assertEquals("1", textOf(id(TIMER)));

        sleep(30);
        _timer.stop();
        waitForUiEvents();
        Assert.assertEquals("1", textOf(id(TIMER)));  // should not have incremented yet

        sleep(100);
        waitForUiEvents();
        Assert.assertEquals("1", textOf(id(TIMER)));  // STILL should not have incremented, if it stopped as expected
        }

    @Override
    protected Node createComponentNode() throws Exception
        {
        _timer = new TimerLabel("s");
        _timer.setId(TIMER);
        return _timer;
        }

    private TimerLabel _timer;
    private final static String TIMER = "TIMER";
    }


