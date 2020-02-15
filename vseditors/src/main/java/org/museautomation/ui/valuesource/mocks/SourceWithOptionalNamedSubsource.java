package org.museautomation.ui.valuesource.mocks;

import org.museautomation.core.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.descriptor.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@MuseTypeId("test-swons")
@MuseValueSourceHidden
@MuseSubsourceDescriptor(displayName = "value1", description = "a primitive value", type = SubsourceDescriptor.Type.Named, name = SourceWithOptionalNamedSubsource.OPT_PARAM, optional = true)
public class SourceWithOptionalNamedSubsource implements MuseValueSource
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

    public final static String TYPE_ID = SourceWithOptionalNamedSubsource.class.getAnnotation(MuseTypeId.class).value();
    public final static String OPT_PARAM = "param1";
    }


