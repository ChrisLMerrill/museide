package org.musetest.ui.valuesource.mocks;

import org.musetest.core.*;
import org.musetest.core.values.*;
import org.musetest.core.values.descriptor.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@MuseTypeId("test-swrsl")
@MuseSubsourceDescriptor(displayName = "Subsource List", description = "an array of data", type = SubsourceDescriptor.Type.List)
public class SourceWithRequiredSubsourceList implements MuseValueSource
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

    public final static String TYPE_ID = SourceWithRequiredSubsourceList.class.getAnnotation(MuseTypeId.class).value();
    }


