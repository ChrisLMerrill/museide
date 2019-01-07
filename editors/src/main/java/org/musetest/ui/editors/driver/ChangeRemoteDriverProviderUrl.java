package org.musetest.ui.editors.driver;

import org.musetest.selenium.providers.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ChangeRemoteDriverProviderUrl extends UndoableAction
    {
    public ChangeRemoteDriverProviderUrl(RemoteDriverProvider provider, String new_value)
        {
        _provider = provider;
        _new_value = new_value;
        }

    @Override
    protected boolean undoImplementation()
        {
        _provider.setUrl(_old_value);
        return true;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_value = _provider.getUrl();
        _provider.setUrl(_new_value);
        return true;
        }

    private RemoteDriverProvider _provider;
    private String _new_value;
    private String _old_value;
    }


