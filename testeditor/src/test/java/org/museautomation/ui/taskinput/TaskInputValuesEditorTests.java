package org.museautomation.ui.taskinput;

import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.*;
import org.museautomation.builtins.valuetypes.*;
import org.museautomation.core.context.*;
import org.museautomation.core.project.*;
import org.museautomation.core.task.*;
import org.museautomation.core.task.input.*;
import org.museautomation.core.values.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TaskInputValuesEditorTests extends ComponentTest
    {
    @Test
    public void provideInputValues()
        {
        TaskInput input1 = createInput("name1", new StringValueType(), true, null);
        TaskInput input2 = createInput("name2", new StringValueType(), true, ValueSourceConfiguration.forValue("default2"));
        TaskInput input3 = createInput("name3", new IntegerValueType(), false, null);
        TaskInput input4 = createInput("name4", new BooleanValueType(), false, ValueSourceConfiguration.forValue(true));
        List<TaskInput> inputs = new ArrayList<>();
        inputs.add(input1);
        inputs.add(input2);
        inputs.add(input3);
        inputs.add(input4);

        // set the inputs
        _editor.setInputs(inputs);
        waitForUiEvents();

        // verify each is displayed
        assertTrue(exists(input1.getName()));
        assertTrue(exists(input2.getName()));
        assertTrue(exists(input3.getName()));
        assertTrue(exists(input4.getName()));

        // verify not valid
        assertFalse(_editor.isSatisfied());

        // fill the required fields
        fillFieldAndTabAway(lookup(id(TaskInputValueEditorRow.VALUE_FIELD_ID)).nth(0).query(), quoted("val1"));
        clickOn(lookup(id(TaskInputValueEditorRow.USE_DEFAULT_ID)).nth(0).queryButton()); // in the second row

        // verify valid
        assertTrue(_editor.isSatisfied());

        // edit a non-required with invalid value
        fillFieldAndTabAway(lookup(id(TaskInputValueEditorRow.VALUE_FIELD_ID)).nth(2).query(), quoted("val1"));
        assertFalse(_editor.isSatisfied());  // should now be invalid

        // edit a non-required with valid value
        fillFieldAndTabAway(lookup(id(TaskInputValueEditorRow.VALUE_FIELD_ID)).nth(2).query(), "123");
        assertTrue(_editor.isSatisfied());  // should now be valid

        // verify the values collected
        List<ResolvedTaskInput> resolved_list = _editor.getResolvedInputs();

        assertEquals(input1.getName(), resolved_list.get(0).getName());
        assertEquals("val1", resolved_list.get(0).getValue());

        assertEquals(input2.getName(), resolved_list.get(1).getName());
        assertEquals("default2", resolved_list.get(1).getValue());

        assertEquals(input3.getName(), resolved_list.get(2).getName());
        assertEquals(123L, resolved_list.get(2).getValue());

        assertEquals(3, resolved_list.size());
        }


    private TaskInput createInput(String name, MuseValueType type, boolean required, ValueSourceConfiguration default_val)
        {
        TaskInput input = new TaskInput(name, type.getId(), required);
        input.setDefault(default_val);
        return input;
        }

    @Override
    public Node createComponentNode()
        {
        _editor = new TaskInputValuesEditor(new ProjectExecutionContext(new SimpleProject()));
        return _editor.getNode();
        }

    private TaskInputValuesEditor _editor;
    }