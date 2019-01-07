package org.musetest.ui.extend.components;

import org.slf4j.*;

import java.io.*;
import java.util.*;

/**
 * Closes stuff when the application exits.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class Closer
    {
    public void closeAll()
        {
        for (Closeable closeable : _closeables)
            try
                {
                closeable.close();
                }
            catch (IOException e)
                {
                LOG.error("Unable to close: " + closeable);
                }
        }

    public void add(Closeable closeable)
        {
        _closeables.add(closeable);
        }

    private List<Closeable> _closeables = new ArrayList<>();

    public static Closer get()
        {
        if (INSTANCE == null)
            INSTANCE = new Closer();
        return INSTANCE;
        }

    private static Closer INSTANCE = null;

    final static Logger LOG = LoggerFactory.getLogger(Closer.class);
    }


