package org.museautomation.ui.editors.driver;

import org.museautomation.core.util.*;
import org.museautomation.selenium.providers.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ChangeLocalDriverProviderOs extends UndoableAction
    {
    public ChangeLocalDriverProviderOs(BaseLocalDriverProvider provider, OperatingSystem os)
        {
        _provider = provider;
        _new_os = os;
        }

    @Override
    protected boolean undoImplementation()
        {
        _provider.setOs(_old_os);
        return true;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_os = _provider.getOs();
        _provider.setOs(_new_os);
        return true;
        }

    private BaseLocalDriverProvider _provider;
    private OperatingSystem _new_os;
    private OperatingSystem _old_os;
    }


