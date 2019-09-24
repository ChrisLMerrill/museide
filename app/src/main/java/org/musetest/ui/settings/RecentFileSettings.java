package org.musetest.ui.settings;

import com.fasterxml.jackson.annotation.*;
import org.musetest.core.*;
import org.musetest.core.resource.*;
import org.musetest.core.resource.storage.*;
import org.musetest.settings.*;
import org.musetest.ui.extend.components.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RecentFileSettings extends BaseSettingsFile
    {
    public File suggestRecentFolder(MuseProject project)
        {
        String path = getRecentPath();
        if (path == null)
            {
            ResourceStorage storage = project.getResourceStorage();
            if (storage instanceof FolderIntoMemoryResourceStorage)
                return ((FolderIntoMemoryResourceStorage) storage).getBaseLocation();
            return null;
            }
        return new File(path);
        }

    @JsonIgnore
    public String getRecentPath()
        {
        return _files.get(GENERAL_KEY);
        }

    public void setRecentPath(String path)
        {
        _files.put(GENERAL_KEY, path);
        }

    /**
     * Required for JSON serialization. Not recommended for general use.
     */
    public HashMap<String, String> getFiles()
        {
        return _files;
        }

    /**
     * Required for JSON serialization. Not recommended for general use.
     */
    public void setFiles(HashMap<String, String> files)
        {
        _files = files;
        }

    private HashMap<String, String> _files = new HashMap<>();

    public static RecentFileSettings get()
        {
        if (SETTINGS == null)
            {
            SETTINGS = (RecentFileSettings) load(RecentFileSettings.class, FILENAME, null);
            Closer.get().add(SETTINGS);
            }
        return SETTINGS;
        }

    private static RecentFileSettings SETTINGS;
    private final static String FILENAME = "RecentFiles.json";

    private final String GENERAL_KEY = "general";
    }


