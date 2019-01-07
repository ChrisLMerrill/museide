package org.musetest.ui.valuesource.mocks;

import org.musetest.core.*;
import org.musetest.core.values.*;
import org.musetest.core.values.descriptor.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@MuseTypeId("test-swopv")
@MuseSubsourceDescriptor(displayName = "value1", description = "a primitive value", type = SubsourceDescriptor.Type.Value, optional = true)
public class SourceWithOptionalPrimitiveValue implements MuseValueSource
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

    public final static String TYPE_ID = SourceWithOptionalPrimitiveValue.class.getAnnotation(MuseTypeId.class).value();
    }


