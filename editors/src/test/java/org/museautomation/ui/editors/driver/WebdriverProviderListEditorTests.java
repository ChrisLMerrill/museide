package org.museautomation.ui.editors.driver;

import javafx.application.*;
import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.project.*;
import org.museautomation.selenium.*;
import org.museautomation.selenium.providers.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class WebdriverProviderListEditorTests extends ComponentTest
    {
    @Test
    void displayProviders()
        {
        checkOriginalProvidersDisplayed();
        }

    private void checkOriginalProvidersDisplayed()
        {
        Assertions.assertTrue(exists(URL1));
        Assertions.assertTrue(exists(URL2));
        }

    @Test
    void createEditAndDeleteRemoteProvider()
        {
        int initial_size = _list.getProviders().size();

        // add a new provider
        clickOn("Add");
        clickOn(id(WebDriverProviderListEditor.ADD_TYPE_ID_BASE + "Remote"));
        Assertions.assertEquals(initial_size + 1, _list.getProviders().size());   // added to list
        Assertions.assertTrue(exists(RemoteDriverProvider.DEFAULT_URL));  // appeared in the UI
        Assertions.assertTrue(_list.getProviders().get(2) instanceof RemoteDriverProvider);
        Assertions.assertEquals(RemoteDriverProvider.DEFAULT_URL, ((RemoteDriverProvider) _list.getProviders().get(2)).getUrl());

        // edit a provider
        final String new_url = "new provider URL";
        fillFieldAndTabAway(URL1, new_url);
        waitForUiEvents();
        Assertions.assertEquals(new_url, ((RemoteDriverProvider) _list.getProviders().get(0)).getUrl()); // data changed
        Assertions.assertTrue(exists(new_url));  // displayed in UI

        // remove added provider
        clickOn(id(WebDriverProviderListEditor.DELETE_BUTTON_ID_BASE + 2));
        Assertions.assertEquals(initial_size, _list.getProviders().size());
        Assertions.assertFalse(exists(RemoteDriverProvider.DEFAULT_URL));

        // undo all changes
        Platform.runLater(() -> _editor.getUndoStack().undoAll());
        waitForUiEvents();
        checkOriginalProvidersDisplayed();
        Assertions.assertEquals(initial_size, _list.getProviders().size());   // back to original content
        }

    @Override
    public Node createComponentNode()
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
    public double getDefaultHeight()
        {
        return super.getDefaultHeight() * 2;
        }

    private WebDriverProviderListEditor _editor;
    private WebDriverProviderList _list;

    private final static String URL1 = "first url";
    private final static String URL2 = "another url";
    }