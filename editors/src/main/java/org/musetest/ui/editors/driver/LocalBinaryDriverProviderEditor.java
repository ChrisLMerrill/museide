package org.musetest.ui.editors.driver;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.util.*;
import org.musetest.selenium.*;
import org.musetest.selenium.providers.*;
import org.musetest.ui.extend.actions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class LocalBinaryDriverProviderEditor implements WebdriverProviderEditor
    {
    public LocalBinaryDriverProviderEditor()
        {
        _grid = new GridPane();
        _grid.setHgap(5);
        _grid.setVgap(5);
        }

    @Override
    public void edit(WebDriverProvider provider, UndoStack undo)
        {
        if (!(provider instanceof BaseLocalDriverProvider))
            throw new IllegalArgumentException("can't edit a " + provider.getClass().getSimpleName());
        _provider = (BaseLocalDriverProvider) provider;
        _undo = undo;

        createFields();
        }

    @Override
    public Node getNode()
        {
        return _grid;
        }

    public void dispose()
        {
        if (_provider != null)
            _provider.removeChangeListener(_listener);
        }

    @Override
    public WebDriverProvider getProvider()
        {
        return _provider;
        }

    private void createFields()
        {
        Platform.runLater(() ->
            {
            _grid.getChildren().removeAll();

            Label label = new Label("Path to driver:");
            _grid.add(label, 0, 0);
            GridPane.setHgrow(label, Priority.NEVER);

            _path_field = new TextField();
            GridPane.setHgrow(_path_field, Priority.ALWAYS);
            _path_field.setId(PATH_FIELD_ID);
            String path = getCurrentPath();
            _path_field.setText(path);
            _grid.add(_path_field, 0, 1, 2, 1);

            _path_field.focusedProperty().addListener((observable, was_focused, is_focused) ->
                {
                if (was_focused && !is_focused && !Objects.equals(_path_field.getText(), getCurrentPath()))
                    new ChangeLocalDriverProviderPath(_provider, _path_field.getText(), _path_type_field.getSelectionModel().getSelectedItem()).execute(_undo);
                });
            _provider.addChangeListener(_listener);

            _path_type_field = new ChoiceBox<>();
            _path_type_field.getItems().add(PathType.ABSOLUTE);
            _path_type_field.getItems().add(PathType.RELATIVE);
            _path_type_field.getSelectionModel().select(getCurrentPathType());
            _grid.add(_path_type_field, 2, 1);
            _path_type_field.setOnAction(event ->
                {
                PathType selected_type = _path_type_field.getSelectionModel().getSelectedItem();
                if (!Objects.equals(selected_type, getCurrentPathType()))
                    new ChangeLocalDriverProviderPath(_provider, _path_field.getText(), _path_type_field.getSelectionModel().getSelectedItem()).execute(_undo);
                });

            label = new Label("Arguments:");
            _grid.add(label, 0, 2);
            _arguments_field = new TextField();
            _arguments_field.focusedProperty().addListener((observable, was_focused, is_focused) ->
                {
                if (was_focused && !is_focused && !Objects.equals(_arguments_field.getText(), argumentsAsString(_provider.getArguments())))
                    new ChangeLocalDriverProviderArguments(_provider, argumentsAsArray(_arguments_field.getText())).execute(_undo);
                });
            fillArgumentsField(_provider.getArguments());
            _grid.add(_arguments_field, 1, 2);

            label = new Label("Operating System:");
            _grid.add(label, 0, 3);
            GridPane.setHgrow(label, Priority.NEVER);
            _os_type_field = new ChoiceBox<>();
            for (OperatingSystem type : OperatingSystem.values())
                _os_type_field.getItems().add(type);
            _os_type_field.getSelectionModel().select(_provider.getOs());
            _os_type_field.setOnAction(event ->
                {
                OperatingSystem selected_os = _os_type_field.getSelectionModel().getSelectedItem();
                if (!Objects.equals(selected_os, _provider.getOs()))
                    new ChangeLocalDriverProviderOs(_provider, selected_os).execute(_undo);
                });
            GridPane.setHgrow(_os_type_field, Priority.SOMETIMES);
            _grid.add(_os_type_field, 1, 3);
            });
        }

    private void fillArgumentsField(String[] arguments)
	    {
	    _arguments_field.setText(argumentsAsString(arguments));
	    }

    private String argumentsAsString(String[] arguments)
	    {
	    if (arguments == null)
	    	return "";
	    StringBuilder argument_string = new StringBuilder();
	    for (String argument : _provider.getArguments())
		    {
		    argument_string.append(argument);
		    argument_string.append(" ");
		    }
	    return argument_string.toString();
	    }

    private String[] argumentsAsArray(String arg_string)
	    {
	    StringTokenizer tokenizer = new StringTokenizer(arg_string, " ");
	    List<String> arguments = new ArrayList<>();
	    while (tokenizer.hasMoreTokens())
		    arguments.add(tokenizer.nextToken());

	    if (arguments.size() > 0)
	        return arguments.toArray(new String[arguments.size()]);
	    else
	    	return null;
	    }

    private PathType getCurrentPathType()
        {
        PathType type = PathType.ABSOLUTE;
        if (_provider.getRelativePath() != null)
            type = PathType.RELATIVE;
        return type;
        }

    private String getCurrentPath()
        {
        String path = "";
        if (_provider.getRelativePath() != null)
            path = _provider.getRelativePath();
        else if (_provider.getAbsolutePath() != null)
            path = _provider.getAbsolutePath();
        return path;
        }

    private BaseLocalDriverProvider _provider;
    private UndoStack _undo;
    private TextField _path_field;
    private ChoiceBox<PathType> _path_type_field;
    private TextField _arguments_field;
    private ChoiceBox<OperatingSystem> _os_type_field;

    private GridPane _grid;

    private BaseLocalDriverProvider.ChangeListener _listener = new BaseLocalDriverProvider.ChangeListener()
        {
        @Override
        public void absolutePathChanged(String old_path, String new_path)
            {
            if (new_path != null)
                {
                _path_field.setText(new_path);
                _path_type_field.getSelectionModel().select(PathType.ABSOLUTE);
                }
            }

        @Override
        public void relativePathChanged(String old_path, String new_path)
            {
            if (new_path != null)
                {
                Platform.runLater(() ->
                    {
                    _path_field.setText(new_path);
                    _path_type_field.getSelectionModel().select(PathType.RELATIVE);
                    });
                }
            }

        @Override
        public void osChanged(OperatingSystem old_os, OperatingSystem new_os)
            {
            Platform.runLater(() -> _os_type_field.getSelectionModel().select(new_os));
            }

        @Override
        public void argumentsChanged(String[] old_args, String[] new_args)
	        {
	        _arguments_field.setText(argumentsAsString(new_args));
	        }
        };

    public final static String PATH_FIELD_ID = "kbdpe-path";
    public final static String RELATIVE_PATH_LABEL = PathType.RELATIVE.getLabel();
    public final static String ABSOLUTE_PATH_LABEL = PathType.ABSOLUTE.getLabel();

    enum PathType
        {
        ABSOLUTE("Absolute"),
        RELATIVE("Relative");

        PathType(String label)
            {
            _label = label;
            }

        public String getLabel()
            {
            return _label;
            }

        @Override
        public String toString()
            {
            return _label;
            }


        private String _label;
        }
    }
