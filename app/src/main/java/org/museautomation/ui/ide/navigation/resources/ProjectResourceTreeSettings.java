package org.museautomation.ui.ide.navigation.resources;

import org.museautomation.core.*;
import org.museautomation.settings.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProjectResourceTreeSettings extends ProjectSettingsFile
    {
    public String getNodeFactoryName()
        {
        return _node_factory;
        }

    public void setNodeFactoryName(String name)
        {
        _node_factory = name;
        }

    private String _node_factory = null;

    public static ProjectResourceTreeSettings get(MuseProject project)
        {
        ProjectResourceTreeSettings settings = project.getProjectSettings(ProjectResourceTreeSettings.class);
        if (settings == null)
            {
            settings = loadFromProject(ProjectResourceTreeSettings.class, FILENAME, null, project);
            project.putProjectSettings(settings);
            }
        return settings;
        }

    public static ProjectResourceTreeSettings get(File project_folder)
        {
        return loadFromProject(ProjectResourceTreeSettings.class, FILENAME, null, new File(project_folder, ".muse"));
        }

    private final static String FILENAME = "resource-tree.json";
    }