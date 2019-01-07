package org.musetest.ui.steptree;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepTreeDoubleClickModifiers
	{
	public StepTreeDoubleClickModifiers(boolean control_down, boolean shift_down, boolean alt_down)
		{
		_control_down = control_down;
		_shift_down = shift_down;
		_alt_down = alt_down;
		}

	final boolean _control_down;
	final boolean _shift_down;
	final boolean _alt_down;

	public static StepTreeDoubleClickModifiers CURRENT = new StepTreeDoubleClickModifiers(false, false, false);
	}


