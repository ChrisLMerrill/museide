package org.museautomation.ui.editors.suite;

import org.museautomation.core.test.*;
import org.museautomation.core.values.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class MockTestWithAction extends BaseMuseTest
    {
    @Override
    public Map<String, ValueSourceConfiguration> getDefaultVariables()
        {
        return null;
        }

    @Override
    public void setDefaultVariables(Map<String, ValueSourceConfiguration> default_variables)
        {

        }

    @Override
    public void setDefaultVariable(String name, ValueSourceConfiguration source)
        {

        }
    }


