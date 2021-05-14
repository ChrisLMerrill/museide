package org.museautomation.ui.editors.proxy;

import org.museautomation.builtins.network.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ChangeProxyPacUrlAction extends UndoableAction
    {
    ChangeProxyPacUrlAction(NetworkProxyConfiguration proxy, String new_url)
        {
        _proxy = proxy;
        _new_url = new_url;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_url = _proxy.getPacUrl();
        _proxy.setPacUrl(_new_url);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _proxy.setPacUrl(_old_url);
        return true;
        }

    private final NetworkProxyConfiguration _proxy;
    private final String _new_url;
    private String _old_url;
    }


