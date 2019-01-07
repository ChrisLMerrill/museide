package org.musetest.ui.extensions.tests;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.extensions.*;
import org.musetest.ui.extensions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExtensionInfoListPanelTests extends ComponentTest
    {
    @Test
    public void displayList()
        {
        List<ExtensionInfo> list = new ArrayList<>();
        ExtensionInfo info1 = ExtensionInfoPanelTests.createExtension("1");
        list.add(info1);
        ExtensionInfo info2 = ExtensionInfoPanelTests.createExtension("2");
        list.add(info2);

        _panel.setInfo(list);
        waitForUiEvents();

        Node subpane1 = lookup(id(ExtensionInfoListPanel.getSubpanelId(0))).query();
        Assert.assertNotNull(subpane1);
        Assert.assertEquals(info1.getDisplayNameVersion(), textOf(subpane1.lookup(id(ExtensionInfoPanel.NAME_LABEL_ID))));

        Node subpane2 = lookup(id(ExtensionInfoListPanel.getSubpanelId(1))).query();
        Assert.assertNotNull(subpane2);
        Assert.assertEquals(info2.getDisplayNameVersion(), textOf(subpane2.lookup(id(ExtensionInfoPanel.NAME_LABEL_ID))));
        }

    @Override
    protected Node createComponentNode() throws Exception
        {
        _panel = new ExtensionInfoListPanel();
        return _panel.getNode();
        }

    private ExtensionInfoListPanel _panel;
    }


