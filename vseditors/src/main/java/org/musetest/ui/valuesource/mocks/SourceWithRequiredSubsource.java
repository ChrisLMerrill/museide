package org.musetest.ui.valuesource.mocks;

import org.musetest.core.*;
import org.musetest.core.values.*;
import org.musetest.core.values.descriptor.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@MuseTypeId("test-swrss")
@MuseValueSourceHidden
@MuseSubsourceDescriptor(displayName = "Subsource", description = "the only subsource", type = SubsourceDescriptor.Type.Single)
public class SourceWithRequiredSubsource implements MuseValueSource
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

    public final static String TYPE_ID = SourceWithRequiredSubsource.class.getAnnotation(MuseTypeId.class).value();
    }


