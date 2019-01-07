package org.musetest.ui.step;

import com.google.common.base.*;
import org.musetest.core.*;
import org.musetest.core.step.descriptor.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepTypeEditor extends StepTypeSelector
    {
    public StepTypeEditor(MuseProject project)
        {
        super(project);
        _project = project;
        }

    public StepTypeEditor()
        {
        super();
        }

    @Override
    public void setProject(MuseProject project)
        {
        super.setProject(project);
        _project = project;
        }

    public void setType(String type)
        {
        if (Objects.equal(type, _type))
            return;
        _type = type;
        setIconAndLabel(_project.getStepDescriptors().get(type));
        }

    private void setIconAndLabel(StepDescriptor descriptor)
        {
        getButton().setGraphic(StepGraphicBuilder.getInstance().getStepIcon(descriptor, _project));
        getButton().setText(descriptor.getName() + "  ");
        }

    @Override
    public void typeSelected(StepDescriptor descriptor)
        {
        setIconAndLabel(descriptor);
        _type = descriptor.getType();
        if (_listener != null)
            _listener.stepTypeChanged(descriptor.getType());
        }

    public void setChangeHandler(StepTypeEditListener listener)
        {
        _listener = listener;
        }

    public String getSelectedType()
        {
        return _type;
        }

    private String _type;
    private MuseProject _project;
    private StepTypeEditListener _listener = null;
    }


