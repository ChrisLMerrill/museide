package org.musetest.ui.editors.browser;

import org.musetest.selenium.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ChangeBrowserVersionAction extends UndoableAction
    {
    ChangeBrowserVersionAction(SeleniumBrowserCapabilities browser, String new_version)
        {
        _browser = browser;
        _new_version = new_version;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_version = _browser.getVersion();
        _browser.setVersion(_new_version);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _browser.setVersion(_old_version);
        return true;
        }

    private SeleniumBrowserCapabilities _browser;
    private String _new_version;
    private String _old_version;
    }


