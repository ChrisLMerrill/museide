package org.musetest.ui.step.inline;

import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.ui.extend.edit.step.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

public class InlineStepEditors
	{
	public static InlineStepEditors get(MuseProject project)
		{
		InlineStepEditors editors = EDITORS.get(project);
		if (editors == null)
			{
			editors = new InlineStepEditors(project);
			EDITORS.put(project, editors);
			}
		return editors;
		}

	private InlineStepEditors(MuseProject project)
		{
		List<Class> implementors = project.getClassLocator().getImplementors(InlineStepEditor.class);
		for (Class the_class : implementors)
			{
			if (!Modifier.isAbstract(the_class.getModifiers()))
				{
				try
					{
					the_class.getDeclaredConstructor().newInstance();  // we can successfully instantiate without an exception
					_editor_classes.add(the_class);
					}
				catch (Exception e)
					{
					LOG.error(String.format("Unable to instantiate a %s. Does it have a public no-args constructor?", the_class.getSimpleName()));
					// ignore this one
					}
				}
			}
		}

	public InlineStepEditor findEditor(StepEditContext context, StepConfiguration step)
		{
		ExtensionSelectionPriority rank = ExtensionSelectionPriority.NEVER;
		InlineStepEditor highest = null;
		for (Class editor_class : _editor_classes)
			{
			if (!Modifier.isAbstract(editor_class.getModifiers()))
				try
					{
					InlineStepEditor editor = (InlineStepEditor) editor_class.getDeclaredConstructor().newInstance();
					final ExtensionSelectionPriority priority = editor.getPriority(context, step);
					if (priority.ordinal() > rank.ordinal())
						{
						rank = priority;
						highest = editor;
						}
					}
				catch (Exception e)
					{
					LOG.error("Unexpected failure. Why are we unable to instantiate this editor here?", e);  // should have caught in constructor!
					}
			}
		return highest;
		}

	private List<Class> _editor_classes = new ArrayList<>();

	private static Map<MuseProject, InlineStepEditors> EDITORS = new HashMap<>();

	private final static Logger LOG = LoggerFactory.getLogger(InlineStepEditors.class);
	}