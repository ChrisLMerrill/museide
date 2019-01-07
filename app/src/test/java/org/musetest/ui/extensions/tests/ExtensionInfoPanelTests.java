package org.musetest.ui.extensions.tests;

import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.extensions.*;
import org.musetest.ui.extensions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExtensionInfoPanelTests extends ComponentTest
    {
    @Test
    public void displayInfo()
        {
        final ExtensionInfo info = createExtension("#1");
        _panel.setInfo(info);
        waitForUiEvents();

        Assert.assertEquals(info.getDisplayNameVersion(), textOf(id(ExtensionInfoPanel.NAME_LABEL_ID)));
        Assert.assertEquals(info.getExtensionDesc(), textOf(id(ExtensionInfoPanel.EXTENSION_DESCRIPTION_LABEL_ID)));
        Assert.assertEquals(info.getExtensionAuthor(), textOf(id(ExtensionInfoPanel.EXTENSION_AUTHOR_LABEL_ID)));
        }

    @Test
    public void displayButtons()
        {
        Button b1 = new Button("B1");
        b1.setId("b1");
        Button b2 = new Button("B2");
        b2.setId("b2");
        _panel.setButtons(b1, b2);

        final ExtensionInfo info1 = createExtension("#1");
        _panel.setInfo(info1);
        waitForUiEvents();

        Assert.assertTrue(exists(id("b1")));
        Assert.assertTrue(exists(id("b2")));
        }

    static ExtensionInfo createExtension(String extra_id)
        {
        final String ext_name = "Extension " + extra_id;
        final String ver_name = "version-" + extra_id;
        final String description = "description of the extension: " + extra_id;
        final ExtensionInfo info = new ExtensionInfo(1L, ext_name, "Joe Developer", 2L, ver_name);
        info.setExtensionDesc(description);
        return info;
        }

    @Override
    protected Node createComponentNode() throws Exception
        {
        _panel = new ExtensionInfoPanel();
        return _panel.getNode();
        }

    private ExtensionInfoPanel _panel;
    }


