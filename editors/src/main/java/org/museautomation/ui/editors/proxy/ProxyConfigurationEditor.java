package org.museautomation.ui.editors.proxy;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.core.*;
import org.museautomation.selenium.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.ui.extend.edit.*;
import org.slf4j.*;

import java.util.*;
import java.util.stream.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProxyConfigurationEditor extends BaseResourceEditor
    {
    public ProxyConfigurationEditor()
        {
        _grid = new GridPane();
        _grid.setPadding(new Insets(5));
        _grid.setVgap(5);
        _grid.setPrefWidth(400);
        _grid.setPrefHeight(200);
        createFields();
        }

    private void createFields()
        {
        _grid.add(new Label("Type"), 0, 0);
        _type_field = new ComboBox<>();
        _grid.add(_type_field, 1, 0);
        _type_field.setEditable(true);
        _type_field.setId(TYPE_FIELD_ID);
        _type_field.getSelectionModel().selectedItemProperty().addListener((observable, old_value, new_value) ->
            {
            try
                {
                ProxyConfiguration.ProxyConfigType new_type = ProxyConfiguration.ProxyConfigType.valueOf(new_value);
                if (!Objects.equals(new_type, _proxy.getProxyType()))
                    new ChangeProxyTypeAction(_proxy, new_type).execute(getUndoStack());
                }
            catch (IllegalArgumentException e)
                {
                LOG.error("Unable to change ProxyType to " + new_value, e);
                }
            });

        _grid.add(new Label("Proxy Server"), 0, 1);
        _hostname_field = new TextField();
        _hostname_field.setEditable(true);
        _hostname_field.setId(HOSTNAME_FIELD_ID);
        _hostname_field.focusedProperty().addListener((observable, old_value, focused) ->
            {
            String hostname = _hostname_field.textProperty().getValue();
            if (!focused && !Objects.equals(hostname, _proxy.getHostname()))
                new ChangeProxyHostnameAction(_proxy, hostname).execute(getUndoStack());
            });
        _grid.add(_hostname_field, 1, 1);

        _grid.add(new Label("Proxy Port"), 0, 2);
        _port_field = new TextField();
        _port_field.setEditable(true);  
        _port_field.setId(PORT_FIELD_ID);
        _port_field.focusedProperty().addListener((observable, old_value, focused) ->
            {
            if (!focused)
                try
                    {
                    Integer new_port;
                    if (_port_field.textProperty().getValue().trim().length() == 0)
                        new_port = null;
                    else
                        new_port = Integer.parseInt(_port_field.textProperty().getValue());
                    InputValidation.setValid(_port_field, true);
                    if (!Objects.equals(new_port, _proxy.getPort()))
                        new ChangeProxyPortAction(_proxy, new_port).execute(getUndoStack());
                    }
                catch (NumberFormatException e)
                    {
                    InputValidation.setValid(_port_field, false);
                    }
            });
        _grid.add(_port_field, 1, 2);

        _grid.add(new Label("Autoconfig URL"), 0, 3);
        _url_field = new TextField();
        _url_field.setEditable(true);
        _url_field.setId(URL_FIELD_ID);
        _url_field.focusedProperty().addListener((observable, old_value, focused) ->
            {
            String url = _url_field.textProperty().getValue();
            if (!focused && !Objects.equals(url, _proxy.getPacUrl()))
                new ChangeProxyPacUrlAction(_proxy, url).execute(getUndoStack());
            });
        _grid.add(_url_field, 1, 3);
        }

    @Override
    public void dispose()
        {
        _grid.getChildren().removeAll();
        if (_proxy != null)
            _proxy.removeChangeListener(_listener);
        }

    @Override
    protected Parent getEditorArea()
        {
        return _grid;
        }

    @Override
    public boolean canEdit(MuseResource resource)
        {
        return resource instanceof ProxyConfiguration;
        }

    @Override
    public void editResource(MuseProject project, MuseResource resource)
        {
        super.editResource(project, resource);
        if (_proxy != null)
            _proxy.removeChangeListener(_listener);
        _proxy = (ProxyConfiguration) resource;
        fillFields();
        }

    private void fillFields()
        {
        Platform.runLater(() ->
            {
            _type_field.getItems().addAll(Arrays.stream(ProxyConfiguration.ProxyConfigType.values()).map(ProxyConfiguration.ProxyConfigType::name).collect(Collectors.toList()));
            _type_field.getSelectionModel().select(_proxy.getProxyType().toString());

            _hostname_field.setText(_proxy.getHostname());
            _port_field.setText(Integer.toString(_proxy.getPort()));
            _url_field.setText(_proxy.getPacUrl());

            _proxy.addChangeListener(_listener);
            });
        }

    @Override
    public ValidationStateSource getValidationStateSource()
        {
        return null;
        }

    @Override
    public void requestFocus()
        {
        _type_field.requestFocus();
        }

    private ProxyConfiguration _proxy;

    private final GridPane _grid;
    private ComboBox<String> _type_field;
    private TextField _hostname_field;
    private TextField _port_field;
    private TextField _url_field;
//    private TextField _exceptions_field;
    private final ProxyConfigChangeListener _listener = new ProxyConfigChangeListener();

    private class ProxyConfigChangeListener implements ProxyConfiguration.ChangeListener
        {
        public void hostnameChanged(String old_value, String new_hostname)
            {
            if (!Objects.equals(_hostname_field.textProperty().getValue(), new_hostname))
                _hostname_field.setText(new_hostname);
            }

        public void pacUrlChanged(String old_value, String new_url)
            {
            if (!Objects.equals(_url_field.textProperty().getValue(), new_url))
                _url_field.setText(new_url);
            }

        public void portChanged(Integer old_value, Integer new_port)
            {
            if (new_port == null)
                {
                _port_field.textProperty().setValue("");
                return;
                }
            if (!Objects.equals(_port_field.textProperty().getValue(), new_port.toString()))
                _port_field.setText(new_port.toString());
            }

        public void typeChanged(ProxyConfiguration.ProxyConfigType old_value, ProxyConfiguration.ProxyConfigType new_type)
            {
            if (!Objects.equals(_type_field.getSelectionModel().getSelectedItem(), new_type.name()))
                _type_field.getSelectionModel().select(new_type.name());
            }
        }

    public final static String TYPE_FIELD_ID = "pce-type";
    public final static String HOSTNAME_FIELD_ID = "pce-server";
    public final static String PORT_FIELD_ID = "pce-port";
    public final static String URL_FIELD_ID = "pce-url";
//    public final static String EXCPETIONS_FIELD_ID = "pce-exceptions";

    final static Logger LOG = LoggerFactory.getLogger(ProxyConfigurationEditor.class);
    }
