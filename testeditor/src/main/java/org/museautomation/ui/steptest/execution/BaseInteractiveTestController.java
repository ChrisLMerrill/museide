package org.museautomation.ui.steptest.execution;

import org.museautomation.core.execution.*;
import org.museautomation.ui.extend.edit.step.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class BaseInteractiveTestController implements InteractiveTestController
	{
	public void addListener(InteractiveTestStateListener listener)
		{
		if (!_listeners.contains(listener))
			_listeners.add(listener);
		}

	@SuppressWarnings("unused") // used in GUI
	public void removeListener(InteractiveTestStateListener listener)
		{
		_listeners.remove(listener);
		}

	protected List<InteractiveTestStateListener> _listeners = new ArrayList<>();
	}


