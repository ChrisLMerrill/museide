package org.musetest.ui.step;

import org.junit.*;
import org.musetest.core.project.*;
import org.musetest.core.step.*;
import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.step.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UpgradeStepActionTests
    {
    /**
     * After upgrading a step, non-documented sources should not be removed.
     */
    @Test
    public void dontDeleteSources()
        {
        StepConfiguration step = new StepConfiguration(CallFunction.TYPE_ID);
        step.addSource("param1", ValueSourceConfiguration.forValue("value1"));

        UpgradeStepToDescriptorComplianceAction action = new UpgradeStepToDescriptorComplianceAction(step, new SimpleProject());
        action.execute(new UndoStack());

        Assert.assertTrue("param1 was removed", step.getSource("param1") != null);
        Assert.assertEquals("wrong number of sources after upgrade", 2, step.getSources().size());
        }
    }


