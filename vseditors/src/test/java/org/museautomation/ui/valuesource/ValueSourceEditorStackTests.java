package org.museautomation.ui.valuesource;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.builtins.value.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceEditorStackTests extends ComponentTest
    {
    @Test
    void displaySource()
        {
        final String string = "string_value";
        ValueSourceConfiguration config = ValueSourceConfiguration.forValue(string);
        _stack.setSource(config);
        waitForUiEvents();

        Assertions.assertTrue(exists(quoted(string)));
        }

    @Test
    void cancelChanges()
        {
        final String string = "string_value";
        ValueSourceConfiguration config = ValueSourceConfiguration.forValue(string);
        _stack.setSource(config);
        waitForUiEvents();

        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), "123");
        waitForUiEvents();

        clickOn(id(Buttons.CANCEL_ID));

        Assertions.assertEquals(string, config.getValue());
        }

    @Test
    void commitChanges()
        {
        final String string = "string_value";
        ValueSourceConfiguration config = ValueSourceConfiguration.forValue(string);
        _stack.setSource(config);
        waitForUiEvents();

        final String new_value = "a123";
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), quoted(new_value));
        waitForUiEvents();

        clickOn(id(Buttons.SAVE_ID));

        Assertions.assertEquals(new_value, config.getValue());
        }

    @Test
    void viewSubsource()
        {
        final String varname = "var1";
        ValueSourceConfiguration config = ValueSourceConfiguration.forTypeWithSource(VariableValueSource.TYPE_ID, ValueSourceConfiguration.forValue(varname));
        _stack.setSource(config);
        waitForUiEvents();

        // verify showing a variable source
        Assertions.assertEquals(_project.getValueSourceDescriptors().get(VariableValueSource.TYPE_ID).getName(), textOf(id(DefaultValueSourceEditor.TYPE_FIELD_ID)));

        clickOn(id(DefaultValueSourceEditor.SINGLE_SUBSOURCE_ADVANCED_LINK_ID));
        // after clicking more> link, should be showing string source
        Assertions.assertEquals(_project.getValueSourceDescriptors().get(StringValueSource.TYPE_ID).getName(), textOf(id(DefaultValueSourceEditor.TYPE_FIELD_ID)));
        }

    @Override
    protected Node createComponentNode()
        {
        _project = new SimpleProject();
        _stack = new ValueSourceEditorStack(new EditInProgress<>()
            {
            @Override public void cancel() { }
            @Override public void commit(ValueSourceConfiguration target) { }
            }, _project, new UndoStack());
        return _stack.getNode();
        }

    private ValueSourceEditorStack _stack;
    private SimpleProject _project;
    }