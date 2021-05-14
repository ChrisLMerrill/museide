package org.museautomation.ui.editors.proxy;

import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.project.*;
import org.museautomation.selenium.*;
import org.museautomation.ui.editors.browser.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ProxyConfigurationEditorTests extends ComponentTest
    {
    @Test
    void displayAndChangeFields()
        {
        ProxyConfiguration original = createHttpProxy();
        _editor.editResource(new SimpleProject(), original);
        waitForUiEvents();

        checkDisplayed(original);

        @SuppressWarnings("rawtypes")
        ComboBox combo = this.lookup(id(ProxyConfigurationEditor.TYPE_FIELD_ID)).queryComboBox();
        interact(() -> combo.getSelectionModel().select(ProxyConfiguration.ProxyConfigType.None.name()));
//        fillComboAndTabAway(id(ProxyConfigurationEditor.TYPE_FIELD_ID), ProxyConfiguration.ProxyConfigType.None.name());
        ProxyConfiguration changed = createHttpProxy();
        changed.setProxyType(ProxyConfiguration.ProxyConfigType.None);
        checkDisplayed(changed);
        Assertions.assertEquals(changed, original);

        changed.setHostname("newhostname");
        fillFieldAndTabAway(id(ProxyConfigurationEditor.HOSTNAME_FIELD_ID), changed.getHostname());
        checkDisplayed(changed);
        Assertions.assertEquals(changed, original);

        changed.setPort(77);
        fillFieldAndTabAway(id(ProxyConfigurationEditor.PORT_FIELD_ID), changed.getPort().toString());
        checkDisplayed(changed);
        Assertions.assertEquals(changed, original);

        changed.setPacUrl("new_pac_url");
        fillFieldAndTabAway(id(ProxyConfigurationEditor.URL_FIELD_ID), changed.getPacUrl());
        checkDisplayed(changed);
        Assertions.assertEquals(changed, original);

        _editor.getUndoStack().undoAll();
        waitForUiEvents();

        // changed in data
        Assertions.assertEquals(original, createHttpProxy());

        // changed in UI
        checkDisplayed(original);
        }

    private ProxyConfiguration createHttpProxy()
        {
        ProxyConfiguration proxy = new ProxyConfiguration();
        proxy.setProxyType(ProxyConfiguration.ProxyConfigType.Fixed);
        proxy.setHostname("proxyhost.com");
        proxy.setPort(1234);
        return proxy;
        }

    private void checkDisplayed(ProxyConfiguration proxy)
        {
        Assertions.assertEquals(proxy.getProxyType().name(), textOf(id(ProxyConfigurationEditor.TYPE_FIELD_ID)));
        Assertions.assertEquals(proxy.getHostname(), textOf(id(ProxyConfigurationEditor.HOSTNAME_FIELD_ID)));
        Assertions.assertEquals(proxy.getPort().toString(), textOf(id(ProxyConfigurationEditor.PORT_FIELD_ID)));
        Assertions.assertEquals(proxy.getPacUrl(), textOf(id(ProxyConfigurationEditor.URL_FIELD_ID)));
        }

    @Override
    public Node createComponentNode()
        {
        _editor = new ProxyConfigurationEditor();
        return _editor.getNode();
        }

    private ProxyConfigurationEditor _editor;
    }