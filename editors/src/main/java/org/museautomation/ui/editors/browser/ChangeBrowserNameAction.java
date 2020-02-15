package org.museautomation.ui.editors.browser;

import org.museautomation.selenium.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ChangeBrowserNameAction extends UndoableAction
    {
    ChangeBrowserNameAction(SeleniumBrowserCapabilities browser, String new_name)
        {
        _browser = browser;
        _new_name = new_name;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_name = _browser.getName();
        _browser.setName(_new_name);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _browser.setName(_old_name);
        return true;
        }

    private SeleniumBrowserCapabilities _browser;
    private String _new_name;
    private String _old_name;
    }


