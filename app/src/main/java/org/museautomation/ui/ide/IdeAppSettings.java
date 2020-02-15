package org.museautomation.ui.ide;

import org.museautomation.settings.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class IdeAppSettings extends BaseSettingsFile
    {
    @Override
    protected void readComplete()
        {
        _changed = false;
        }

    public double getSplitterPosition()
        {
        return _splitter_position;
        }

    public void setSplitterPosition(double position)
        {
        if (_splitter_position != position)
            {
            _splitter_position = position;
            _changed = true;
            }
        }

    @Override
    protected boolean shouldSave()
        {
        return _changed;
        }

    private double _splitter_position = 0.25;
    private boolean _changed = false;

    public static IdeAppSettings get()
        {
        if (SETTINGS == null)
            SETTINGS = (IdeAppSettings) load(IdeAppSettings.class, FILENAME, null);
        return SETTINGS;
        }

    private static IdeAppSettings SETTINGS;
    private final static String FILENAME = "ide-app-settings.json";
    }