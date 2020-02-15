package org.museautomation.ui.editors.driver;

import org.museautomation.selenium.providers.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ChangeLocalDriverProviderArguments extends UndoableAction
    {
    public ChangeLocalDriverProviderArguments(BaseLocalDriverProvider provider, String[] arguments)
        {
        _provider = provider;
        _new_arguments = arguments;
        }

    @Override
    protected boolean undoImplementation()
        {
        _provider.setArguments(_old_arguments);
        return true;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_arguments = _provider.getArguments();
        _provider.setArguments(_new_arguments);
        return true;
        }

    private BaseLocalDriverProvider _provider;
    private String[] _new_arguments;
    private String[] _old_arguments;
    }


