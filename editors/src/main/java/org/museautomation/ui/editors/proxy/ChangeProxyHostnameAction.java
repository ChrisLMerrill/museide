package org.museautomation.ui.editors.proxy;

import org.museautomation.builtins.network.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ChangeProxyHostnameAction extends UndoableAction
    {
    ChangeProxyHostnameAction(NetworkProxyConfiguration proxy, String new_hostname)
        {
        _proxy = proxy;
        _new_hostname = new_hostname;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_hostname = _proxy.getHostname();
        _proxy.setHostname(_new_hostname);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _proxy.setHostname(_old_hostname);
        return true;
        }

    private final NetworkProxyConfiguration _proxy;
    private final String _new_hostname;
    private String _old_hostname;
    }


