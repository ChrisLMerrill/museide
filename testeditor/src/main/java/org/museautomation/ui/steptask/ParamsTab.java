package org.museautomation.ui.steptask;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.core.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.valuesource.map.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ParamsTab
    {
    Tab getTab()
        {
        return _tab;
        }

    ParamsTab(MuseProject project, UndoStack undo_stack, MuseTask task)
        {
        BorderPane params_pane = new BorderPane();
        params_pane.setPadding(new Insets(5));

        Label heading = new Label("Default parameters for the task:");
        params_pane.setTop(heading);
        ValueSourceMapEditor initial_values_editor = new ValueSourceMapEditor(project, undo_stack);

        // setup a source to contain the map
        ValueSourceConfiguration fake_source = new ValueSourceConfiguration();
        if (task.getDefaultVariables() == null)
            task.setDefaultVariables(new HashMap<>());
        fake_source.setSourceMap(task.getDefaultVariables());
        initial_values_editor.setSource(fake_source);

        ScrollPane scroller = new ScrollPane();
        scroller.setStyle("-fx-background-color:transparent;");
        scroller.setFitToWidth(true);
        scroller.setContent(initial_values_editor.getNode());
        params_pane.setCenter(scroller);
        _tab.setContent(params_pane);
        _tab.closableProperty().setValue(false);
        }

    private Tab _tab = new Tab("Parameters");
    }