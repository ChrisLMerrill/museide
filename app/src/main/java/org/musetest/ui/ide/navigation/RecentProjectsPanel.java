package org.musetest.ui.ide.navigation;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.resource.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RecentProjectsPanel
    {
    public RecentProjectsPanel(NavigatorView navigator)
        {
        _navigator = navigator;

        RecentProjectSettings settings = RecentProjectSettings.get();
        if (settings.getProjects().size() > 0)
            {
            Label heading = new Label("Recent projects:");
            GridPane.setHalignment(heading, HPos.CENTER);
            GridPane.setHgrow(heading, Priority.ALWAYS);
            _grid.add(heading, 0, 0);

            int row = 1;
            for (RecentProject project : settings.getProjects())
                {
                File folder = new File(project.getLocation());
                if (folder.exists())
                    {
                    Hyperlink link = new Hyperlink(folder.getName());
                    GridPane.setHalignment(link, HPos.CENTER);
                    GridPane.setHgrow(link, Priority.ALWAYS);
                    _grid.add(link, 0, row++);

                    link.setOnAction(event -> _navigator.openProject(folder));
                    }
                }
            }
        }

    public Node getNode()
        {
        return _grid;
        }

    private final NavigatorView _navigator;
    private GridPane _grid = new GridPane();
    }


