package org.musetest.ui.step;

import org.junit.*;
import org.musetest.builtins.value.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.valuesource.actions.*;
import org.musetest.ui.valuesource.mocks.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UpgradeValueSourceActionTests
    {
    @Test
    public void addRequiredSubsource()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(StringValueSource.TYPE_ID);
        UpgradeValueSourceAction action = new UpgradeValueSourceAction(PROJECT, source, SourceWithRequiredSubsource.TYPE_ID);
        UndoStack undo_stack = new UndoStack();
        action.execute(undo_stack);

        Assert.assertEquals("type was not changed", SourceWithRequiredSubsource.TYPE_ID, source.getType());
        Assert.assertNotNull("source was not added", source.getSource());

        undo_stack.undoLastAction();

        Assert.assertEquals("type change was not reverted", StringValueSource.TYPE_ID, source.getType());
        Assert.assertNull("source change was not reverted", source.getSource());
        }

    @Test
    public void removeExtraneousSubsource()
        {
        ValueSourceConfiguration subsource = ValueSourceConfiguration.forValue("abc");
        ValueSourceConfiguration source = ValueSourceConfiguration.forSource(SourceWithRequiredSubsource.TYPE_ID, subsource);
        UpgradeValueSourceAction action = new UpgradeValueSourceAction(PROJECT, source, SourceWithOptionalPrimitiveValue.TYPE_ID);
        UndoStack undo_stack = new UndoStack();
        action.execute(undo_stack);

        Assert.assertNull("source was not removed", source.getSource());

        undo_stack.undoLastAction();

        Assert.assertEquals("source removal was not reverted", subsource, source.getSource());
        }

    @Test
    public void addRemoveNamedSource()
        {
        ValueSourceConfiguration subsource = ValueSourceConfiguration.forValue("abc");
        String extraneous_param = "other_param_name";
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithNamedSource(SourceWithRequiredSubsource.TYPE_ID, extraneous_param, subsource);
        UpgradeValueSourceAction action = new UpgradeValueSourceAction(PROJECT, source, SourceWithRequiredNamedSource.TYPE_ID);
        UndoStack undo_stack = new UndoStack();
        action.execute(undo_stack);

        Assert.assertEquals("type was not changed", SourceWithRequiredNamedSource.TYPE_ID, source.getType());
        Assert.assertNotNull("source was not added", source.getSource(SourceWithRequiredNamedSource.PARAM1_NAME));
        Assert.assertNull("extraneous source was not removed", source.getSource(extraneous_param));

        undo_stack.undoLastAction();

        Assert.assertEquals("type change was not reverted", SourceWithRequiredSubsource.TYPE_ID, source.getType());
        Assert.assertNull("source change was not reverted", source.getSource(SourceWithRequiredNamedSource.PARAM1_NAME));
        Assert.assertNotNull("extraneous source removal was not reverted", source.getSource(extraneous_param));
        }

    @Test
    public void addSourceList()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(StringValueSource.TYPE_ID);
        UpgradeValueSourceAction action = new UpgradeValueSourceAction(PROJECT, source, SourceWithRequiredSubsourceList.TYPE_ID);
        UndoStack undo_stack = new UndoStack();
        action.execute(undo_stack);

        Assert.assertNotNull("source list was not added", source.getSourceList());
        Assert.assertNotNull("first source was not added", source.getSource(0));

        undo_stack.undoLastAction();

        Assert.assertNull("list addition was not reverted", source.getSourceList());
        }

    @Test
    public void removeSourceList()
        {
        ValueSourceConfiguration subsource = ValueSourceConfiguration.forValue("param1");
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithIndexedSource(SourceWithRequiredPrimitiveValue.TYPE_ID, subsource);
        UpgradeValueSourceAction action = new UpgradeValueSourceAction(PROJECT, source, SourceWithRequiredNamedSource.TYPE_ID);
        UndoStack undo_stack = new UndoStack();
        action.execute(undo_stack);

        Assert.assertNull("source list was not removed", source.getSourceList());

        undo_stack.undoLastAction();

        Assert.assertNotNull("source list removal was not reverted", source.getSourceList());
        Assert.assertEquals("source list entry was not restored", subsource, source.getSource(0));
        }

    private final static MuseProject PROJECT = new SimpleProject();
    }


