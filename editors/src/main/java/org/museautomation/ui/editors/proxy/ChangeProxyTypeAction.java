package org.museautomation.ui.editors.proxy;

import org.museautomation.builtins.network.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ChangeProxyTypeAction extends UndoableAction
    {
    ChangeProxyTypeAction(NetworkProxyConfiguration proxy, NetworkProxyConfiguration.ProxyConfigType new_type)
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

    private final NetworkProxyConfiguration _proxy;
    private final NetworkProxyConfiguration.ProxyConfigType _new_type;
    private NetworkProxyConfiguration.ProxyConfigType _old_type;
    }


