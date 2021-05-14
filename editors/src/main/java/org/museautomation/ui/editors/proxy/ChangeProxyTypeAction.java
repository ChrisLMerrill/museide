package org.museautomation.ui.editors.proxy;

import org.museautomation.selenium.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ChangeProxyTypeAction extends UndoableAction
    {
    ChangeProxyTypeAction(ProxyConfiguration proxy, ProxyConfiguration.ProxyConfigType new_type)
        {
        _proxy = proxy;
        _new_type = new_type;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_type = _proxy.getProxyType();
        _proxy.setProxyType(_new_type);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _proxy.setProxyType(_old_type);
        return true;
        }

    private ProxyConfiguration _proxy;
    private ProxyConfiguration.ProxyConfigType _new_type;
    private ProxyConfiguration.ProxyConfigType _old_type;
    }


