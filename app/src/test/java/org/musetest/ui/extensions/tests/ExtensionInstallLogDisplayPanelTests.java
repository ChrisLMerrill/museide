package org.musetest.ui.extensions.tests;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.extensions.install.*;
import org.musetest.ui.extensions.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExtensionInstallLogDisplayPanelTests extends ComponentTest
    {
    @Test
    public void displayLogMessages()
        {
        _log.recordMessage("message1");
        Node text = lookup(id(ExtensionInstallLogDisplayPanel.MESSAGES_TEXT_ID)).query();
        Assert.assertTrue(textOf(text).contains("message1"));
        }

    @Override
    protected Node createComponentNode()
        {
        _log = new ExtensionInstallLog(new File(System.getProperty("user.dir")));
        ExtensionInstallLogDisplayPanel panel = new ExtensionInstallLogDisplayPanel(_log);
        return panel.getNode();
        }

    private ExtensionInstallLog _log;
    }


