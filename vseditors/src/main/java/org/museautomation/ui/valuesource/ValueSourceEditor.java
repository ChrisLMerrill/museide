package org.museautomation.ui.valuesource;

import javafx.scene.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.edit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ValueSourceEditor extends ValidationStateSource
    {
    void setSource(ValueSourceConfiguration source);
    ValueSourceConfiguration getSource();
    Node getNode();
    @SuppressWarnings("unused")
    void requestFocus();
    }

