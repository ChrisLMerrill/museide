package org.musetest.ui.editors.suite;

import org.musetest.core.context.*;
import org.musetest.core.test.*;
import org.musetest.core.values.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MockTest extends BaseMuseTest
    {
    MockTest(String id)
        {
        setId(id);
        }

    @Override
    protected boolean executeImplementation(TestExecutionContext context)
        {
        return true;
        }

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


