package org.musetest.ui.steptree;

import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.ui.extend.edit.step.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

public class StepCellRenderers
	{
	public static StepCellRenderers get(MuseProject project)
		{
		StepCellRenderers renderers = RENDERERS.get(project);
		if (renderers == null)
			{
			renderers = new StepCellRenderers(project);
			RENDERERS.put(project, renderers);
			}
		return renderers;
		}

	private StepCellRenderers(MuseProject project)
		{
		List<Class> implementors = project.getClassLocator().getImplementors(StepCellRenderer.class);
		for (Class the_class : implementors)
			{
			if (!Modifier.isAbstract(the_class.getModifiers()))
				{
				try
					{
					the_class.getDeclaredConstructor().newInstance();  // we can successfully instantiate without an exception
					_renderer_classes.add(the_class);
					}
				catch (Exception e)
					{
					LOG.error(String.format("Unable to instantiate a %s. Does it have a public no-args constructor?", the_class.getSimpleName()));
					// ignore this one
					}
				}
			}
		}

	public StepCellRenderer findRenderer(StepEditContext context, StepConfiguration step)
		{
		ExtensionSelectionPriority rank = ExtensionSelectionPriority.NEVER;
		StepCellRenderer highest = null;
		for (Class renderer_class : _renderer_classes)
			{
			try
				{
				StepCellRenderer renderer = (StepCellRenderer) renderer_class.getDeclaredConstructor().newInstance();
				renderer.configure(context, step);
				final ExtensionSelectionPriority priority = renderer.getPriority();
				if (priority.ordinal() > rank.ordinal())
					{
					rank = priority;
					highest = renderer;
					}
				}
			catch (Exception e)
				{
				LOG.error("Unexpected failure. Why are we unable to instantiate this StepCellRenderer here?", e);  // should have caught in constructor!
				}
			}
		return highest;
		}

	private List<Class<? extends StepCellRenderer>> _renderer_classes = new ArrayList<>();

	private static Map<MuseProject, StepCellRenderers> RENDERERS = new HashMap<>();

	private final static Logger LOG = LoggerFactory.getLogger(StepCellRenderers.class);
	}