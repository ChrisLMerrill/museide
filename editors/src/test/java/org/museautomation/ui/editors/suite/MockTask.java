package org.museautomation.ui.editors.suite;

import org.museautomation.core.context.*;
import org.museautomation.core.task.*;
import org.museautomation.core.values.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MockTask extends BaseMuseTask
    {
    MockTask(String id)
        {
        setId(id);
        }

    @Override
    protected boolean executeImplementation(TaskExecutionContext context)
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


