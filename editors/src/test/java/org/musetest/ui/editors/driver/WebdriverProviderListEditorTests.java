package org.musetest.ui.editors.driver;

import javafx.application.*;
import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.core.project.*;
import org.musetest.selenium.*;
import org.musetest.selenium.providers.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class WebdriverProviderListEditorTests extends ComponentTest
    {
    @Test
    public void displayProviders()
        {
        checkOriginalProvidersDisplayed();
        }

    private void checkOriginalProvidersDisplayed()
        {
        Assert.assertTrue(exists(URL1));
        Assert.assertTrue(exists(URL2));
        }

    @Test
    public void createEditAndDeleteRemoteProvider()
        {
        int initial_size = _list.getProviders().size();

        // add a new provider
        clickOn("Add");
        clickOn(id(WebDriverProviderListEditor.ADD_TYPE_ID_BASE + "Remote"));
        Assert.assertEquals(initial_size + 1, _list.getProviders().size());   // added to list
        Assert.assertTrue(exists(RemoteDriverProvider.DEFAULT_URL));  // appeared in the UI
        Assert.assertTrue(_list.getProviders().get(2) instanceof RemoteDriverProvider);
        Assert.assertEquals(RemoteDriverProvider.DEFAULT_URL, ((RemoteDriverProvider) _list.getProviders().get(2)).getUrl());

        // edit a provider
        final String new_url = "new provider URL";
        fillFieldAndTabAway(URL1, new_url);
        waitForUiEvents();
        Assert.assertEquals(new_url, ((RemoteDriverProvider) _list.getProviders().get(0)).getUrl()); // data changed
        Assert.assertTrue(exists(new_url));  // displayed in UI

        // remove added provider
        clickOn(id(WebDriverProviderListEditor.DELETE_BUTTON_ID_BASE + 2));
        Assert.assertEquals(initial_size, _list.getProviders().size());
        Assert.assertFalse(exists(RemoteDriverProvider.DEFAULT_URL));

        // undo all changes
        Platform.runLater(() -> _editor.getUndoStack().undoAll());
        waitForUiEvents();
        checkOriginalProvidersDisplayed();
        Assert.assertEquals(initial_size, _list.getProviders().size());   // back to original content
        }

    @Override
    protected Node createComponentNode()
        {
        _editor = new WebDriverProviderListEditor();
        _list = new WebDriverProviderList();

        RemoteDriverProvider provider1 = new RemoteDriverProvider();
        provider1.setUrl(URL1);
        _list.add(provider1);

        RemoteDriverProvider provider2 = new RemoteDriverProvider();
        provider2.setUrl(URL2);
        _list.add(provider2);

        _editor.editResource(new SimpleProject(), _list);
        return _editor.getNode();
        }

    @Override
    protected double getDefaultHeight()
        {
        return super.getDefaultHeight() * 2;
        }

    private WebDriverProviderListEditor _editor;
    private WebDriverProviderList _list;

    private final static String URL1 = "first url";
    private final static String URL2 = "another url";
    }


