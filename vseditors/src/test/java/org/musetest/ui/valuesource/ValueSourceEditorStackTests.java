package org.musetest.ui.valuesource;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.builtins.value.*;
import org.musetest.core.project.*;
import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.edit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceEditorStackTests extends ComponentTest
    {
    @Test
    public void displaySource()
        {
        final String string = "string_value";
        ValueSourceConfiguration config = ValueSourceConfiguration.forValue(string);
        _stack.setSource(config);
        waitForUiEvents();

        Assert.assertTrue(exists(quoted(string)));
        }

    @Test
    public void cancelChanges()
        {
        final String string = "string_value";
        ValueSourceConfiguration config = ValueSourceConfiguration.forValue(string);
        _stack.setSource(config);
        waitForUiEvents();

        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), "123");
        waitForUiEvents();

        clickOn(id(Buttons.CANCEL_ID));

        Assert.assertEquals(string, config.getValue());
        }

    @Test
    public void commitChanges()
        {
        final String string = "string_value";
        ValueSourceConfiguration config = ValueSourceConfiguration.forValue(string);
        _stack.setSource(config);
        waitForUiEvents();

        final String new_value = "a123";
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), quoted(new_value));
        waitForUiEvents();

        clickOn(id(Buttons.SAVE_ID));

        Assert.assertEquals(new_value, config.getValue());
        }

    @Test
    public void viewSubsource()
        {
        final String varname = "var1";
        ValueSourceConfiguration config = ValueSourceConfiguration.forTypeWithSource(VariableValueSource.TYPE_ID, ValueSourceConfiguration.forValue(varname));
        _stack.setSource(config);
        waitForUiEvents();

        // verify showing a variable source
        Assert.assertEquals(_project.getValueSourceDescriptors().get(VariableValueSource.TYPE_ID).getName(), textOf(id(DefaultValueSourceEditor.TYPE_FIELD_ID)));

        clickOn(id(DefaultValueSourceEditor.SINGLE_SUBSOURCE_ADVANCED_LINK_ID));
        // after clicking more> link, should be showing string source
        Assert.assertEquals(_project.getValueSourceDescriptors().get(StringValueSource.TYPE_ID).getName(), textOf(id(DefaultValueSourceEditor.TYPE_FIELD_ID)));
        }

    @Override
    protected Node createComponentNode()
        {
        _canceled = false;
        _committed = false;
        _project = new SimpleProject();
        _stack = new ValueSourceEditorStack(new EditInProgress<ValueSourceConfiguration>()
            {
            @Override
            public void cancel()
                {
                _canceled = true;
                }

            @Override
            public void commit(ValueSourceConfiguration target)
                {
                _committed = true;
                }
            }, _project, new UndoStack());
        return _stack.getNode();
        }

    private ValueSourceEditorStack _stack;
    private boolean _canceled;
    private boolean _committed;
    private SimpleProject _project;
    }


