package org.museautomation.ui.valuesource.mocks;

import org.museautomation.core.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.descriptor.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@MuseTypeId("test-swrpv")
@MuseValueSourceHidden
@MuseSubsourceDescriptor(displayName = "Primitive Value", description = "a primitive value", type = SubsourceDescriptor.Type.Value)
public class SourceWithRequiredPrimitiveValue implements MuseValueSource
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

    public final static String TYPE_ID = SourceWithRequiredPrimitiveValue.class.getAnnotation(MuseTypeId.class).value();
    }


