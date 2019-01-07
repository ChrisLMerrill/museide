package org.musetest.ui.valuesource;

import org.musetest.core.*;
import org.musetest.core.values.*;
import org.musetest.core.values.descriptor.*;

/**
 * This VS is used when creating required sub-sources. It serves only to be displayed
 * in the GUI with no value. No GUI element should parse user input into this.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@MuseTypeId("placeholder")
@MuseValueSourceTypeGroup(MuseValueSourceTypeGroup.DONT_SHOW)
@MuseStringExpressionSupportImplementation(PlaceholderValueSourceStringExpressionSupport.class)
public class PlaceholderValueSource implements MuseValueSource
    {
    @Override
    public Object resolveValue(MuseExecutionContext context) throws ValueSourceResolutionError
        {
        return null;
        }

    @Override
    public String getDescription()
        {
        return null;
        }

    public final static String TYPE_ID = PlaceholderValueSource.class.getAnnotation(MuseTypeId.class).value();
    }


