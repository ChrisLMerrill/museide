package org.museautomation.ui.valuesource.mocks;

import org.museautomation.core.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.descriptor.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@MuseTypeId("test-swrsl")
@MuseValueSourceHidden
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


