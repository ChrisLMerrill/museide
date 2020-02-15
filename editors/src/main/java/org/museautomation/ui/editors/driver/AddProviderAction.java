package org.museautomation.ui.editors.driver;

import org.museautomation.selenium.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class AddProviderAction extends UndoableAction
    {
    public AddProviderAction(WebDriverProviderList list, WebDriverProvider to_add)
        {
        _list = list;
        _to_add = to_add;
        }

    @Override
    protected boolean executeImplementation()
        {
        _list.add(_to_add);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _list.remove(_to_add);
        return true;
        }

    private WebDriverProviderList _list;
    private WebDriverProvider _to_add;
    }


