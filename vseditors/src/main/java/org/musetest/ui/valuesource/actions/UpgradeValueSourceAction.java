package org.musetest.ui.valuesource.actions;

import org.musetest.core.*;
import org.musetest.core.values.*;
import org.musetest.core.values.descriptor.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.valuesource.*;
import org.musetest.ui.valuesource.list.*;
import org.musetest.ui.valuesource.map.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UpgradeValueSourceAction extends CompoundAction
    {
    public UpgradeValueSourceAction(MuseProject project, ValueSourceConfiguration source, String new_type)
        {
        _project = project;
        _source = source;
        _new_type = new_type;
        }

    @Override
    protected boolean executeImplementation()
        {
        if (getSize() == 0)
            {
            if (_new_type != null)
                addAction(new ChangeSourceTypeAction(_source, _new_type));
            String source_type = _new_type;
            if (source_type == null)
                source_type = _source.getType();
            SubsourceDescriptor[] descriptors = _project.getValueSourceDescriptors().get(source_type).getSubsourceDescriptors();

            /**
             * Add missing (required) subsources
             */
            for (SubsourceDescriptor descriptor : descriptors)
                {
                if (!descriptor.isOptional())
                switch (descriptor.getType())
                    {
                    case Named:
                        if (_source.getSource(descriptor.getName()) == null)
                            addAction(new AddNamedSubsourceAction(_source, placeholderSource(), descriptor.getName()));
                        break;
                    case Single:
                        if (_source.getSource() == null)
                            addAction(new SubSourceChangeAction(_source, placeholderSource()));
                        break;
                    case List:
                        if (_source.getSourceList() == null || _source.getSourceList().size() == 0)
                            addAction(new AddIndexedSubsourceAction(_source, 0, placeholderSource()));
                        break;
                    }
                }

            /**
             * Remove undocumented subsources
             */
            for (String name : _source.getSourceNames())
                if (!namedDescriptorExists(name, descriptors))
                    addAction(new RemoveNamedSubsourceAction(_source, name));
            if (_source.getSource() != null && !subsourceDescriptorExists(descriptors))
                addAction(new SubSourceChangeAction(_source, null));
            if (_source.getValue() != null && !valueDescriptorExists(descriptors))
                addAction(new SourceValueChangeAction(_source, null));
            if (_source.getSourceList() != null && _source.getSourceList().size() > 0 && !subsourceListDescriptorExists(descriptors))
                addAction(new RemoveAllIndexedSubsourcesAction(_source));
            }

        return super.executeImplementation();
        }

    private boolean namedDescriptorExists(String name, SubsourceDescriptor[] descriptors)
        {
        for (SubsourceDescriptor descriptor : descriptors)
            if (descriptor.getType().equals(SubsourceDescriptor.Type.Named) && name.equals(descriptor.getName()))
                return true;
        return false;
        }

    private boolean subsourceDescriptorExists(SubsourceDescriptor[] descriptors)
        {
        for (SubsourceDescriptor descriptor : descriptors)
            if (descriptor.getType().equals(SubsourceDescriptor.Type.Single))
                return true;
        return false;
        }

    private boolean valueDescriptorExists(SubsourceDescriptor[] descriptors)
        {
        for (SubsourceDescriptor descriptor : descriptors)
            if (descriptor.getType().equals(SubsourceDescriptor.Type.Value))
                return true;
        return false;
        }

    private boolean subsourceListDescriptorExists(SubsourceDescriptor[] descriptors)
        {
        for (SubsourceDescriptor descriptor : descriptors)
            if (descriptor.getType().equals(SubsourceDescriptor.Type.List))
                return true;
        return false;
        }

    private ValueSourceConfiguration placeholderSource()
        {
        return ValueSourceConfiguration.forType(PlaceholderValueSource.TYPE_ID);
        }

    private final MuseProject _project;
    private final ValueSourceConfiguration _source;
    private final String _new_type;
    }


