package org.musetest.ui.steptest;

import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.resource.*;
import org.musetest.core.resource.storage.*;

import java.io.*;

/**
 * This class allows debugging of the editor without building and running the full IDE.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class EditorApp extends Application
    {
    public static void main(String[] args)
        {
        Application.launch(args);
        }

    @Override
    public void start(Stage stage)
        {
        stage.setTitle("StepTestEditor app");
        stage.setWidth(800);
        stage.setHeight(600);

        String project_parm = getParameters().getUnnamed().get(0);
        String test_param = getParameters().getUnnamed().get(1);

        ResourceStorage storage = new FolderIntoMemoryResourceStorage(new File(project_parm));
        MuseProject project = new SimpleProject(storage);
        project.open();
        MuseResource resource = project.getResourceStorage().getResource(test_param);
        StepTestEditor editor = new StepTestEditor();
        if (editor.canEdit(resource))
            {
            editor.editResource(project, resource);
            stage.setScene(new Scene(editor.getNode()));
            stage.show();
            }
        else
            throw new IllegalArgumentException(String.format("The resource %s is not a SteppedTest.", resource.getId()));
        }
    }


