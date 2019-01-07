package org.musetest.ui.ide;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProjectPreloaderSplash extends Preloader
    {
    @Override
    public void start(Stage stage) throws Exception
        {
        _splash = stage;
        _splash.setTitle("MuseIDE");
        _splash.setScene(createSpashScene());
        _splash.show();
        }

    private Scene createSpashScene()
        {
        _root = new BorderPane();
        _root.setCenter(new ImageView(new Image(getClass().getResourceAsStream("/icons/big-progress.gif"))));

        _status = new Label("Loading project...");
        _root.setBottom(_status);
        _status.setMaxWidth(Double.MAX_VALUE);
        _status.setAlignment(Pos.CENTER);

        return new Scene(_root, SPLASH_WIDTH, SPLASH_HEIGHT);
        }

    @Override
    public void handleStateChangeNotification(StateChangeNotification notification)
        {
        if (notification.getType() == StateChangeNotification.Type.BEFORE_START)
            {
            if (!_application_error)
                _splash.close();
            }
        }

    @Override
    public void handleApplicationNotification(PreloaderNotification info)
        {
        if (info instanceof ErrorNotification)
            {
            // TODO create a pretty failure UI
            Text error = new Text(((ErrorNotification) info).getDetails());
            error.setWrappingWidth(SPLASH_WIDTH - 20);
            error.setTextAlignment(TextAlignment.CENTER);
            _root.setCenter(error);
            _status.setText("loading failed");
            _application_error = true;
            }
        }

    private Stage _splash;
    private Label _status;
    private boolean _application_error = false;
    private BorderPane _root;

    private final static int SPLASH_WIDTH = 300;
    private final static int SPLASH_HEIGHT = 300;
    }


