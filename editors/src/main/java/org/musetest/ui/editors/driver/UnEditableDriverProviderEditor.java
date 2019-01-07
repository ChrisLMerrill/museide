package org.musetest.ui.editors.driver;

import javafx.scene.*;
import javafx.scene.layout.*;
import org.musetest.selenium.*;
import org.musetest.ui.extend.actions.*;

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
    public void edit(WebDriverProvider provider, UndoStack undo)
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

    private HBox _box = new HBox();
    }


