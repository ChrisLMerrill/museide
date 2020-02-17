package org.museautomation.ui.steptask.execution;

import org.museautomation.core.execution.*;
import org.museautomation.ui.extend.edit.step.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class BaseInteractiveTestController implements InteractiveTestController
	{
	public void addListener(InteractiveTaskStateListener listener)
		{
		if (!_listeners.contains(listener))
			_listeners.add(listener);
		}

	@SuppressWarnings("unused") // used in GUI
	public void removeListener(InteractiveTaskStateListener listener)
		{
		_listeners.remove(listener);
		}

	protected List<InteractiveTaskStateListener> _listeners = new ArrayList<>();
	}


