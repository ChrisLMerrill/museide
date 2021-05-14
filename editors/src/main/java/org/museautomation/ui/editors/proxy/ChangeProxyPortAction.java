package org.museautomation.ui.editors.proxy;

import org.museautomation.builtins.network.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ChangeProxyPortAction extends UndoableAction
    {
    ChangeProxyPortAction(NetworkProxyConfiguration proxy, Integer new_port)
        {
        _proxy = proxy;
        _new_port = new_port;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_port = _proxy.getPort();
        _proxy.setPort(_new_port);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _proxy.setPort(_old_port);
        return true;
        }

    private final NetworkProxyConfiguration _proxy;
    private final Integer _new_port;
    private Integer _old_port;
    }


