package org.museautomation.ui.valuesource;

import org.museautomation.core.values.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class PlaceholderValueSourceStringExpressionSupport extends BaseValueSourceStringExpressionSupport
    {
    @Override
    public String toString(ValueSourceConfiguration config, StringExpressionContext context, int depth)
	    {
	    if (PlaceholderValueSource.TYPE_ID.equals(config.getType()))
            return "\"enter a value source here\"";
	    else
	        return null;
	    }
    }
