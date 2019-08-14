package org.musetest.ui.ide;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import net.christophermerrill.ShadowboxFx.*;
import org.musetest.ui.extend.components.*;
import org.musetest.ui.ide.navigation.*;
import org.musetest.ui.settings.*;
import org.slf4j.*;

import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class IdeApplication extends Application
    {
    public static void main(String[] args)
        {
        String log_level = EnvironmentSettings.get().getLogLevel();
        if (log_level != null)
            ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.valueOf(log_level));
        Application.launch(IdeApplication.class, args);
        }

    @Override
    public void start(Stage stage)
        {
        APP = this;
        determineVersionNumber();

        ShadowboxPane shadowbox = new ShadowboxPane();
        _splitter.setDividerPositions(0.25);
        shadowbox.getChildren().add(_splitter);

        NavigatorView navigator = new NavigatorView(_editors, stage);
        _splitter.getItems().add(navigator.getNode());
        _splitter.getItems().add(_editor_tabs);

        stage.setTitle("MuseIDE " + _version);
        IdeWindow.initIcons(stage);
        Scene scene = new Scene(shadowbox, 700, 500);
        scene.getStylesheets().add(getClass().getResource("/ide.css").toExternalForm());
        stage.setScene(scene);
        StageSettings.get("ide-main-stage.json").register(stage);
        stage.show();

        stage.setOnCloseRequest(event ->
            {
            if (_editors.hasUnsavedChanges())
                {
                final AtomicReference<String> error = new AtomicReference<>();
                boolean close = SaveChangesDialog.createShowAndWait(
                    () -> error.set(_editors.saveAllChanges()),
                    _editors::revertAllChanges);


                if (!close || error.get() != null)
                    {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Unable to save a resource");
                    alert.setContentText(error.get());
                    alert.showAndWait();
                    event.consume();
                    }
                }
            });
        }

    private void determineVersionNumber()
        {
        _version = "(dev)";
        try
	        {
	        String build = BuildVersion.getBuildVersion();
	        if (build != null)
	        	_version = build;
	        }
        catch (Throwable t)
	        {
	        // ok
	        }
        }

    @Override
    public void stop() throws Exception
        {
        super.stop();
        Closer.get().closeAll();
        }

    public IdeTabs getTabs()
        {
        return _editors;
        }

    public static IdeApplication getInstance()
        {
        return APP;
        }

    private final TabPane _editor_tabs = new TabPane();
    private final IdeTabs _editors = new IdeTabs(_editor_tabs);
    private final SplitPane _splitter = new SplitPane();
    private String _version;

    private static IdeApplication APP = null;
    }


