package org.musetest.ui.settings;

import javafx.stage.*;
import org.musetest.settings.*;
import org.musetest.ui.extend.components.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StageSettings extends BaseSettingsFile
    {
    public void register(Stage stage)
        {
        _stage = stage;

        if (_width == 0)
            return;  // don't apply until they've been saved from useful settings
        stage.setX(_x);
        stage.setY(_y);
        stage.setWidth(_width);
        stage.setHeight(_height);
        Closer.get().add(this);
        }

    @Override
    public void close() throws IOException
        {
        _x = _stage.getX();
        _y = _stage.getY();
        _width = _stage.getWidth();
        _height = _stage.getHeight();

        super.close();
        }

    @SuppressWarnings("unused")  // required for serialization
    public double getX()
        {
        return _x;
        }

    @SuppressWarnings("unused")  // required for serialization
    public void setX(double x)
        {
        _x = x;
        }

    @SuppressWarnings("unused")  // required for serialization
    public double getY()
        {
        return _y;
        }

    @SuppressWarnings("unused")  // required for serialization
    public void setY(double y)
        {
        _y = y;
        }

    @SuppressWarnings("unused")  // required for serialization
    public double getWidth()
        {
        return _width;
        }

    @SuppressWarnings("unused")  // required for serialization
    public void setWidth(double width)
        {
        _width = width;
        }

    @SuppressWarnings("unused")  // required for serialization
    public double getHeight()
        {
        return _height;
        }

    @SuppressWarnings("unused")  // required for serialization
    public void setHeight(double height)
        {
        _height = height;
        }

    private transient Stage _stage;
    private double _x = 0;
    private double _y = 0;
    private double _width = 0;
    private double _height = 0;

    public static StageSettings get()
        {
        return get(FILENAME);
        }

    public static StageSettings get(String name)
        {
        StageSettings settings = SETTINGS.get(name);
        if (settings == null)
            {
            settings = (StageSettings) load(StageSettings.class, name, null);
            Closer.get().add(settings);
            SETTINGS.put(name, settings);
            }
        return settings;
        }

    private static Map<String, StageSettings> SETTINGS = new HashMap<>();
    private final static String FILENAME = "Editor-stage.json";
    }


