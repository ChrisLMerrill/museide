package org.musetest.ui.valuesource;

import org.musetest.core.*;
import org.musetest.core.step.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.edit.step.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused")  // used in IDE
public class ValueSourceInStepInlineEditors
	{
	public static ValueSourceInStepInlineEditors get(MuseProject project)
		{
		ValueSourceInStepInlineEditors editors = EDITORS.get(project);
		if (editors == null)
			{
			editors = new ValueSourceInStepInlineEditors(project);
			EDITORS.put(project, editors);
			}
		return editors;
		}

	private ValueSourceInStepInlineEditors(MuseProject project)
		{
		List<Class> implementors = project.getClassLocator().getImplementors(ValueSourceInStepInlineEditor.class);
		for (Class the_class : implementors)
			{
			if (!Modifier.isAbstract(the_class.getModifiers()))
				{
				try
					{
					final Constructor constructor = the_class.getConstructor(MuseProject.class, UndoStack.class);
					if (constructor != null)
						_editor_constructors.add(constructor);
					}
				catch (Exception e)
					{
					LOG.error(String.format("Unable to instantiate a %s. Does it have a public no-args constructor?", the_class.getSimpleName()));
					// ignore this one
					}
				}
			}
		}

	public ValueSourceInStepInlineEditor findEditor(StepEditContext context, StepConfiguration step, String source_name)
		{
		ExtensionSelectionPriority rank = ExtensionSelectionPriority.NEVER;
		ValueSourceInStepInlineEditor highest = null;
		for (Constructor constructor : _editor_constructors)
			{
			try
				{
				ValueSourceInStepInlineEditor editor = (ValueSourceInStepInlineEditor) constructor.newInstance(context.getProject(), context.getUndo());
				final ExtensionSelectionPriority priority = editor.getPriority(context, step, source_name);
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

	private List<Constructor<ValueSourceInStepInlineEditor>> _editor_constructors = new ArrayList<>();

	private static Map<MuseProject, ValueSourceInStepInlineEditors> EDITORS = new HashMap<>();

	private final static Logger LOG = LoggerFactory.getLogger(ValueSourceInStepInlineEditors.class);
	}
