package org.musetest.ui.ide.navigation;

import com.fasterxml.jackson.annotation.*;
import org.musetest.settings.*;
import org.musetest.ui.extend.components.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RecentProjectSettings extends BaseSettingsFile
    {
    public void addProject(String path)
        {
        int index = findProject(path);
        if (index == -1)
            {
            _projects.add(0, new RecentProject(path));
            _changed = true;
            }
        else if (index > 0)
            {
            RecentProject project = _projects.remove(index);
            _projects.add(0, project); // move to beginning of list
            _changed = true;
            }

        if (_projects.size() > 10)  // if list getting too long, prune one from the end.
            _projects.remove(_projects.size() - 1);
        }

    private int findProject(String path)
        {
        for (int i = 0; i < _projects.size(); i++)
            {
            RecentProject project = _projects.get(i);
            if (path.equals(project.location))
                return i;
            }
        return -1;
        }

    @SuppressWarnings("unused")
    public List<RecentProject> getProjects()
        {
        return _projects;
        }

    protected boolean shouldSave()
        {
        return _changed;
        }

    /**
     * Required for JSON serialization. Don't call directly.
     */
    @SuppressWarnings("unused")
    public void setProjects(List<RecentProject> projects)
        {
        _projects = projects;
        }

    public static RecentProjectSettings get()
        {
        return get(FILENAME);
        }

    public static RecentProjectSettings get(String name)
        {
        if (SETTINGS == null)
            {
            SETTINGS = (RecentProjectSettings) load(RecentProjectSettings.class, name, null);
            Closer.get().add(SETTINGS);
            }
        return SETTINGS;
        }

    private List<RecentProject> _projects = new ArrayList<>();
    private boolean _changed = false;

    private static RecentProjectSettings SETTINGS;
    private final static String FILENAME = "recent-projects.json";
    }


