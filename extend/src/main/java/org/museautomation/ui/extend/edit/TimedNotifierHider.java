package org.museautomation.ui.extend.edit;

import javafx.application.*;
import org.controlsfx.control.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TimedNotifierHider implements Runnable
    {
    public TimedNotifierHider(NotificationPane notifier, long time_ms)
        {
        _notifier = notifier;
        _delay = time_ms;
        new Thread(this).start();
        }

    @Override
    public void run()
        {
        try
            {
            Thread.sleep(_delay);
            }
        catch (InterruptedException e)
            {
            // ok
            }
        Platform.runLater(() ->
	        {
	        if (_notifier.isShowing())
	            _notifier.hide();
	        });
        }

    private NotificationPane _notifier;
    private long _delay;
    }


