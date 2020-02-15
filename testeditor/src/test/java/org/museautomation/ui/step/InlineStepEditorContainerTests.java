package org.museautomation.ui.step;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.museautomation.ui.step.inline.*;
import org.museautomation.ui.valuesource.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class InlineStepEditorContainerTests extends ComponentTest
    {
    @Test
    public void fillEmptyParameter()
        {
        fillFieldAndTabAway(id(DefaultInlineVSE.TEXT_ID), quoted("abc"));

        Assert.assertEquals("entered value is not there!", quoted("abc"), textOf(id(DefaultInlineVSE.TEXT_ID)));

        Assert.assertNotNull("source was not set", _step.getSource(ReturnStep.VALUE_PARAM));
        }

    @Override
    protected Node createComponentNode()
        {
        _step = new StepConfiguration(ReturnStep.TYPE_ID);
        _step.addSource(ReturnStep.VALUE_PARAM, ValueSourceConfiguration.forValue(""));
        final SimpleProject project = new SimpleProject();
        EditInProgress.NoopEdit edit = new EditInProgress.NoopEdit();
        UndoStack undo = new UndoStack();
        StepEditContext context = new RootStepEditContext(project, undo, null);
        InlineStepEditorContainerImplementation container = new InlineStepEditorContainerImplementation(context, _step, edit, false);
        return container.getNode();
        }

    private StepConfiguration _step;
    }


