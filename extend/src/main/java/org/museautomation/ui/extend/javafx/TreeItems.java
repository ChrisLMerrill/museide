package org.museautomation.ui.extend.javafx;

import javafx.scene.control.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TreeItems
	{
	public static <T> TreeItem<T> getRoot(TreeItem<T> item)
		{
		TreeItem<T> candidate = item;
		while (candidate.getParent() != null)
			candidate = candidate.getParent();
		return candidate;
		}
	}


