package org.musetest.ui.extend.javafx;

import javafx.scene.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class Styles
	{
	public static void addStyle(Node node, String style)
		{
		if (!node.getStyleClass().contains(style))
			node.getStyleClass().add(style);
		}

	public static void removeStyle(Node node, String style)
		{
		//noinspection StatementWithEmptyBody
		while (node.getStyleClass().remove(style))
			;
		}

	public static String getDefaultTreeStyles()
		{
		return Styles.class.getResource("Trees.css").toExternalForm();
		}
	}


