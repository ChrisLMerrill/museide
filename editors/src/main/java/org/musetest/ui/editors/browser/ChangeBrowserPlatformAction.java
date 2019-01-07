package org.musetest.ui.editors.browser;

import org.musetest.selenium.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ChangeBrowserPlatformAction extends UndoableAction
    {
    ChangeBrowserPlatformAction(SeleniumBrowserCapabilities browser, String new_platform)
        {
        _browser = browser;
        _new_platform = new_platform;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_platform = _browser.getPlatform();
        _browser.setPlatform(_new_platform);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _browser.setPlatform(_old_platform);
        return true;
        }

    private SeleniumBrowserCapabilities _browser;
    private String _new_platform;
    private String _old_platform;
    }


