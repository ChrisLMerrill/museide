package org.museautomation.ui.editors.driver;

import javafx.scene.*;
import javafx.scene.layout.*;
import org.museautomation.core.*;
import org.museautomation.selenium.*;
import org.museautomation.ui.extend.actions.*;

/**
 * For drivers that don't need to reference a local executable (Safari and (old) Firefox).
 *
 * There actually isn't anything to edit, so this is just a placeholder.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UnEditableDriverProviderEditor implements WebdriverProviderEditor
    {
    @Override
    public void edit(WebDriverProvider provider, UndoStack undo, MuseProject project)
        {
        _provider = provider;
        }

    @Override
    public Node getNode()
        {
        return _box;
        }

    public void dispose()
        {
        }

    @Override
    public WebDriverProvider getProvider()
        {
        return _provider;
        }

    private WebDriverProvider _provider;

    private final HBox _box = new HBox();
    }