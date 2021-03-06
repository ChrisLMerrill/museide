package org.museautomation.ui.valuesource.mocks;

import org.museautomation.core.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.descriptor.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@MuseTypeId("test-swrns")
@MuseValueSourceHidden
@MuseSubsourceDescriptor(displayName = "Named Subsource", description = "required named subsource", type = SubsourceDescriptor.Type.Named, name = SourceWithRequiredNamedSource.PARAM1_NAME)
public class SourceWithRequiredNamedSource implements MuseValueSource
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

    public final static String TYPE_ID = SourceWithRequiredNamedSource.class.getAnnotation(MuseTypeId.class).value();
    public final static String PARAM1_NAME = "name1";
    }


