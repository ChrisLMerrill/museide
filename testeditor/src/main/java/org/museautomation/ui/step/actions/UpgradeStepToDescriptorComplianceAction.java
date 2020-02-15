package org.museautomation.ui.step.actions;

import org.museautomation.ui.valuesource.*;
import org.museautomation.core.*;
import org.museautomation.core.step.*;
import org.museautomation.core.step.descriptor.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.descriptor.*;
import org.museautomation.ui.extend.actions.*;

import java.util.*;

/**
 * Upgrades a StepConfiguration to be compliant with the descriptor -- most importantly
 * ensure all the required parameters are present.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UpgradeStepToDescriptorComplianceAction extends UndoableAction
    {
    public UpgradeStepToDescriptorComplianceAction(StepConfiguration step, MuseProject project)
        {
        _step = step;
        _project = project;
        analyzeSources();
        }

    public boolean isUpgradeNeeded()
        {
        return _sources_to_add.size() > 0;
        }

    @Override
    protected boolean undoImplementation()
        {
        for (String name : _sources_to_add.keySet())
            _step.removeSource(name);
        return true;
        }

    @Override
    protected boolean executeImplementation()
        {
        for (String name : _sources_to_add.keySet())
            _step.addSource(name, _sources_to_add.get(name));
        return true;
        }

    /**
     * Determine what sources need to be added and remove. Put them in _sources_to_add and _sources_to_remove.
     */
    private void analyzeSources()
        {
        StepDescriptor step_descriptor = _project.getStepDescriptors().get(_step);
        SubsourceDescriptor[] descriptors = step_descriptor.getSubsourceDescriptors();

        for (int i = 0; i < descriptors.length; i++)
            {
            SubsourceDescriptor descriptor = descriptors[i];
            ValueSourceConfiguration source = _step.getSource(descriptor.getName());
            if (source == null && !descriptor.isOptional())
                {
                source = ValueSourceConfiguration.forType(PlaceholderValueSource.TYPE_ID); // add the (empty) source
                _sources_to_add.put(descriptor.getName(), source);
                }
            }
        }

    private StepConfiguration _step;
    private MuseProject _project;
    private Map<String, ValueSourceConfiguration> _sources_to_add = new HashMap<>();
    }


