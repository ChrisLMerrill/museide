package org.musetest.ui.editors.driver;

import javafx.scene.*;
import org.musetest.selenium.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface WebdriverProviderEditor
    {
    /**
     * Edit the provider.
     */
    void edit(WebDriverProvider provider, UndoStack stack);

    /**
     * Get the JavaFX Node for the editor
     */
    Node getNode();

    /**
     * Call this when the editor is no longer used. Allows editor to dispose of resources, de-register listeners, etc.
     */
    void dispose();

    WebDriverProvider getProvider();
    }

