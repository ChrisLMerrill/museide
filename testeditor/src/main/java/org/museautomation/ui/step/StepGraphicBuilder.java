package org.museautomation.ui.step;

import javafx.scene.*;
import javafx.scene.image.*;
import org.museautomation.core.*;
import org.museautomation.core.step.descriptor.*;
import org.museautomation.core.util.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.ui.extend.glyphs.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepGraphicBuilder
	{
	public static StepGraphicBuilder getInstance()
		{
		return INSTANCE;
		}

	public Node getStepIcon(StepDescriptor descriptor, MuseProject project)
		{
		String name = descriptor.getIconDescriptor();
		if (name == null)
			return _blank;

		// if using something other than classpath lookup
		if (!name.startsWith(StepGraphicBuilder.CLASS_RESOURCE + ":"))
			return GraphicNodeBuilder.getInstance().getNode(name, ColorBuilder.getColor(descriptor.getIconColor()));
		Class step_class = new TypeLocator(project).getClassForTypeId(descriptor.getType());

		// already in the cache?
		Image image = _step_icons.get(name);
		if (image != null)
			return new ImageView(image);

		image = loadImageFromClasspathResource(name, step_class);
		if (image != null)
			_step_icons.put(name, image);
		return new ImageView(image);
		}

	private Image loadImageFromClasspathResource(String descriptor, Class the_class)
		{
		StringTokenizer tokenizer = new StringTokenizer(descriptor, ":");
		if (tokenizer.nextToken().equals(CLASS_RESOURCE))
			{
			String filename = tokenizer.nextToken();
			Exception thrown = null;
			try
				{
				InputStream resource = the_class.getResourceAsStream(filename);
				if (resource != null)
					return new Image(resource);
				}
			catch (Exception e)
				{
				thrown = e;
				}
			LOG.error(String.format("Unable to load image from descriptor %s (class %s): does not exist", descriptor, the_class.getSimpleName()), thrown);
			}
		return null;
		}

	private Node _blank = Glyphs.create("FA:SQUARE_ALT");
	private Map<String, Image> _step_icons = new HashMap<>();

	private final static String CLASS_RESOURCE = "class";
	private static StepGraphicBuilder INSTANCE = new StepGraphicBuilder();

	private final static Logger LOG = LoggerFactory.getLogger(StepGraphicBuilder.class);
	}
