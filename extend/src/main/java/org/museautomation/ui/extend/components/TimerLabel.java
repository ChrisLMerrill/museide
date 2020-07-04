package org.museautomation.ui.extend.components;

import javafx.application.*;
import javafx.scene.control.*;

import java.text.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TimerLabel extends Label
    {
    public TimerLabel(String format)
        {
        _format = format;
        _formatter = new SimpleDateFormat(_format, Locale.getDefault());
        _formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        }

    public void setInitalValue(long inital_value)
        {
        _initial_value = inital_value;
        update();
        }

    public void start()
        {
        _start_time = System.currentTimeMillis();
        if (_initial_value != null)
            _start_time -= _initial_value;
        update();
        long next_update = 1000 - getElapsed() % 1000;
        _timer = new Timer();
        _timer.schedule(new TimerTask()
            {
            @Override
            public void run()
                {
                update();
                }
            }, next_update, 1000);
        }

    public void stop()
        {
        if (_timer != null)
            _timer.cancel();
        }

    public void clear()
        {
        _start_time = null;
        _initial_value = null;
        update();
        }

    private long getElapsed()
        {
        if (_start_time == null && _initial_value != null)
            return _initial_value;
        else if (_start_time != null)
            return System.currentTimeMillis() - _start_time;
        else
            return 0;
        }

    private void update()
        {
        Platform.runLater(() ->
            {
            long elapsed = getElapsed();
            setText(_formatter.format(new Date(elapsed)));
            });
        }

    private String _format;
    private Long _initial_value = null;
    private Long _start_time = null;
    private SimpleDateFormat _formatter = null;
    private Timer _timer;
    }


