package org.museautomation.ui.steptask;

import org.museautomation.settings.*;
import org.museautomation.ui.extend.components.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TaskEditorSettings extends BaseSettingsFile
    {
    @SuppressWarnings("unused")  // required for serialization
    public double getSplitterPos()
        {
        return _splitter_pos;
        }

    @SuppressWarnings("unused")  // required for serialization
    public void setSplitterPos(double splitter_pos)
        {
        _splitter_pos = splitter_pos;
        }

    private double _splitter_pos = 0.80;

    public static TaskEditorSettings get()
        {
        return get(FILENAME);
        }

    public static TaskEditorSettings get(String name)
        {
        TaskEditorSettings settings = SETTINGS.get(name);
        if (settings == null)
            {
            settings = (TaskEditorSettings) load(TaskEditorSettings.class, name, null);
            Closer.get().add(settings);
            SETTINGS.put(name, settings);
            }
        return settings;
        }

    private static final Map<String, TaskEditorSettings> SETTINGS = new HashMap<>();
    private final static String FILENAME = "TaskEditor.json";
    }


