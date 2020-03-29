package org.museautomation.ui.step;

import org.junit.jupiter.api.*;
import org.museautomation.ui.step.actions.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class UpgradeStepActionTests
    {
    /**
     * After upgrading a step, non-documented sources should not be removed.
     */
    @Test
    void dontDeleteSources()
        {
        StepConfiguration step = new StepConfiguration(CallFunction.TYPE_ID);
        step.addSource("param1", ValueSourceConfiguration.forValue("value1"));

        UpgradeStepToDescriptorComplianceAction action = new UpgradeStepToDescriptorComplianceAction(step, new SimpleProject());
        action.execute(new UndoStack());

        Assertions.assertNotNull(step.getSource("param1"), "param1 was removed");
        Assertions.assertEquals(2, step.getSources().size(), "wrong number of sources after upgrade");
        }
    }