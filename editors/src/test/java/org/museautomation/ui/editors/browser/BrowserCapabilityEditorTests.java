package org.museautomation.ui.editors.browser;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.project.*;
import org.museautomation.selenium.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class BrowserCapabilityEditorTests extends ComponentTest
    {
    @Test
    void displayBrowser()
        {
        setup();

        check();
        }

    private void check()
        {
        Assertions.assertEquals("Chrome", textOf(id(BrowserCapabilitiesEditor.NAME_FIELD_ID)));
        Assertions.assertEquals("38", textOf(id(BrowserCapabilitiesEditor.VERSION_FIELD_ID)));
        Assertions.assertEquals("WINDOWS", textOf(id(BrowserCapabilitiesEditor.PLATFORM_FIELD_ID)));
        }

    @Test
    void changeBrowser()
        {
        setup();

        fillComboAndTabAway(id(BrowserCapabilitiesEditor.NAME_FIELD_ID), "newbrowser");
        Assertions.assertEquals("newbrowser", _browser.getName());
        Assertions.assertEquals(1, _editor.getUndoStack().getNumberOfUndoableActions());  // make sure there is not a bug where multiple (unnecesssary) changes are made for a single user change

        fillComboAndTabAway(id(BrowserCapabilitiesEditor.VERSION_FIELD_ID), "22");
        Assertions.assertEquals("22", _browser.getVersion());
        Assertions.assertEquals(2, _editor.getUndoStack().getNumberOfUndoableActions());

        fillComboAndTabAway(id(BrowserCapabilitiesEditor.PLATFORM_FIELD_ID), "PalmOS");
        Assertions.assertEquals("PalmOS", _browser.getPlatform());
        Assertions.assertEquals(3, _editor.getUndoStack().getNumberOfUndoableActions());

        _editor.getUndoStack().undoAll();

        // changed in data
        Assertions.assertEquals("Chrome", _browser.getName());
        Assertions.assertEquals("38", _browser.getVersion());
        Assertions.assertEquals("WINDOWS", _browser.getPlatform());

        // changed in UI
        check();
        }

    private void setup()
        {
        _browser = new SeleniumBrowserCapabilities();
        _browser.setId("chrome1");
        _browser.setName("Chrome");
        _browser.setVersion("38");
        _browser.setPlatform("WINDOWS");

        _editor.editResource(new SimpleProject(), _browser);
        waitForUiEvents();
        }

    @Override
    protected Node createComponentNode()
        {
        _editor = new BrowserCapabilitiesEditor();
        return _editor.getNode();
        }

    private BrowserCapabilitiesEditor _editor;
    private SeleniumBrowserCapabilities _browser;
    }