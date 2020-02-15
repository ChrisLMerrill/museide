package org.museautomation.ui.editors.driver;

import org.museautomation.selenium.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RemoveProviderAction extends UndoableAction
    {
    public RemoveProviderAction(WebDriverProviderList list, WebDriverProvider to_delete)
        {
        _list = list;
        _to_delete = to_delete;
        }

    @Override
    protected boolean executeImplementation()
        {
        _index = _list.getProviders().indexOf(_to_delete);
        _list.remove(_to_delete);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _list.add(_index, _to_delete);
        return true;
        }

    private WebDriverProviderList _list;
    private WebDriverProvider _to_delete;
    private int _index;
    }


