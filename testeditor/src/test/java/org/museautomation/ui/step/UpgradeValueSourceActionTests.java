package org.museautomation.ui.step;

import org.junit.jupiter.api.*;
import org.museautomation.ui.valuesource.actions.*;
import org.museautomation.ui.valuesource.mocks.*;
import org.museautomation.builtins.value.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class UpgradeValueSourceActionTests
    {
    @Test
    void addRequiredSubsource()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(StringValueSource.TYPE_ID);
        UpgradeValueSourceAction action = new UpgradeValueSourceAction(PROJECT, source, SourceWithRequiredSubsource.TYPE_ID);
        UndoStack undo_stack = new UndoStack();
        action.execute(undo_stack);

        Assertions.assertEquals(SourceWithRequiredSubsource.TYPE_ID, source.getType(), "type was not changed");
        Assertions.assertNotNull(source.getSource(), "source was not added");

        undo_stack.undoLastAction();

        Assertions.assertEquals(StringValueSource.TYPE_ID, source.getType(), "type change was not reverted");
        Assertions.assertNull(source.getSource(), "source change was not reverted");
        }

    @Test
    void removeExtraneousSubsource()
        {
        ValueSourceConfiguration subsource = ValueSourceConfiguration.forValue("abc");
        ValueSourceConfiguration source = ValueSourceConfiguration.forSource(SourceWithRequiredSubsource.TYPE_ID, subsource);
        UpgradeValueSourceAction action = new UpgradeValueSourceAction(PROJECT, source, SourceWithOptionalPrimitiveValue.TYPE_ID);
        UndoStack undo_stack = new UndoStack();
        action.execute(undo_stack);

        Assertions.assertNull(source.getSource(), "source was not removed");

        undo_stack.undoLastAction();

        Assertions.assertEquals(subsource, source.getSource(), "source removal was not reverted");
        }

    @Test
    void addRemoveNamedSource()
        {
        ValueSourceConfiguration subsource = ValueSourceConfiguration.forValue("abc");
        String extraneous_param = "other_param_name";
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithNamedSource(SourceWithRequiredSubsource.TYPE_ID, extraneous_param, subsource);
        UpgradeValueSourceAction action = new UpgradeValueSourceAction(PROJECT, source, SourceWithRequiredNamedSource.TYPE_ID);
        UndoStack undo_stack = new UndoStack();
        action.execute(undo_stack);

        Assertions.assertEquals(SourceWithRequiredNamedSource.TYPE_ID, source.getType(), "type was not changed");
        Assertions.assertNotNull(source.getSource(SourceWithRequiredNamedSource.PARAM1_NAME), "source was not added");
        Assertions.assertNull(source.getSource(extraneous_param), "extraneous source was not removed");

        undo_stack.undoLastAction();

        Assertions.assertEquals(SourceWithRequiredSubsource.TYPE_ID, source.getType(), "type change was not reverted");
        Assertions.assertNull(source.getSource(SourceWithRequiredNamedSource.PARAM1_NAME), "source change was not reverted");
        Assertions.assertNotNull(source.getSource(extraneous_param), "extraneous source removal was not reverted");
        }

    @Test
    void addSourceList()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(StringValueSource.TYPE_ID);
        UpgradeValueSourceAction action = new UpgradeValueSourceAction(PROJECT, source, SourceWithRequiredSubsourceList.TYPE_ID);
        UndoStack undo_stack = new UndoStack();
        action.execute(undo_stack);

        Assertions.assertNotNull(source.getSourceList(), "source list was not added");
        Assertions.assertNotNull(source.getSource(0), "first source was not added");

        undo_stack.undoLastAction();

        Assertions.assertNull(source.getSourceList(), "list addition was not reverted");
        }

    @Test
    void removeSourceList()
        {
        ValueSourceConfiguration subsource = ValueSourceConfiguration.forValue("param1");
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithIndexedSource(SourceWithRequiredPrimitiveValue.TYPE_ID, subsource);
        UpgradeValueSourceAction action = new UpgradeValueSourceAction(PROJECT, source, SourceWithRequiredNamedSource.TYPE_ID);
        UndoStack undo_stack = new UndoStack();
        action.execute(undo_stack);

        Assertions.assertNull(source.getSourceList(), "source list was not removed");

        undo_stack.undoLastAction();

        Assertions.assertNotNull(source.getSourceList(), "source list removal was not reverted");
        Assertions.assertEquals(subsource, source.getSource(0), "source list entry was not restored");
        }

    private final static MuseProject PROJECT = new SimpleProject();
    }