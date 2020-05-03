package org.museautomation.ui.taskinput;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TaskInputValueEditorRowTests extends ComponentTest
    {
    @Test
    public void displayInput()
        {
        setupInput(new StringValueType(), false, ValueSourceConfiguration.forValue("default-val"));
        waitForUiEvents();

        assertTrue(exists(_input.getName()));
        assertEquals(0, textOf(id(TaskInputValueEditorRow.VALUE_FIELD_ID)).length());
        assertTrue(exists(id(TaskInputValueEditorRow.USE_DEFAULT_ID)));
        assertTrue(exists(_input.getType().getName()));
        assertTrue(exists(id(TaskInputValueEditorRow.REQUIRED_SATISFIED_ICON_ID)));

        assertFalse(_row.isSatisfied());
        }

    @Test
    public void noInputWellBehaved()
        {
        setupInput(new StringValueType(), false, ValueSourceConfiguration.forValue(DEF_VAL));
        waitForUiEvents();
        assertNull(_row.getResolvedInput());
        }


    @Test
    public void displayRequiredInputNoDefault()
        {
        setupInput(new StringValueType(), true, null);
        waitForUiEvents();

        assertTrue(exists(_input.getName()));
        assertEquals(0, textOf(id(TaskInputValueEditorRow.VALUE_FIELD_ID)).length());
        assertFalse(exists(id(TaskInputValueEditorRow.USE_DEFAULT_ID)));
        assertTrue(exists(_input.getType().getName()));
        assertTrue(exists(id(TaskInputValueEditorRow.REQUIRED_NOT_SATISFIED_ICON_ID)));

        assertFalse(_row.isSatisfied());
        }

    @Test
    public void fillRequiredValueField()
        {
        setupInput(new StringValueType(), true, null);
        waitForUiEvents();

        final String val1 = "value1";

        assertFalse(_row.isSatisfied());
        assertTrue(exists(id(TaskInputValueEditorRow.REQUIRED_NOT_SATISFIED_ICON_ID)));
        fillFieldAndTabAway(id(TaskInputValueEditorRow.VALUE_FIELD_ID), quoted(val1));
        assertTrue(_row.isSatisfied());
        assertEquals(quoted(val1), textOf(id(TaskInputValueEditorRow.VALUE_FIELD_ID)));
        assertTrue(exists(id(TaskInputValueEditorRow.REQUIRED_SATISFIED_ICON_ID)));

        ResolvedTaskInput resolved = _row.getResolvedInput();
        assertEquals(NAME1, resolved.getName());
        assertEquals(val1, resolved.getValue());
        }

    @Test
    public void fillRequiredValueFieldWithDefault()
        {
        setupInput(new StringValueType(), true, ValueSourceConfiguration.forValue(DEF_VAL));
        waitForUiEvents();

        assertFalse(_row.isSatisfied());
        assertTrue(exists(id(TaskInputValueEditorRow.REQUIRED_NOT_SATISFIED_ICON_ID)));
        clickOn(id(TaskInputValueEditorRow.USE_DEFAULT_ID));
        assertEquals(quoted(DEF_VAL), textOf(id(TaskInputValueEditorRow.VALUE_FIELD_ID)));
        assertTrue(_row.isSatisfied());
        assertTrue(exists(id(TaskInputValueEditorRow.REQUIRED_SATISFIED_ICON_ID)));

        ResolvedTaskInput resolved = _row.getResolvedInput();
        assertEquals(NAME1, resolved.getName());
        assertEquals(DEF_VAL, resolved.getValue());
        }

    private void setupInput(MuseValueType type, boolean required, ValueSourceConfiguration default_val)
        {
        _input = new TaskInput(NAME1, type.getId(), required);
        _input.setDefault(default_val);
        _row.setInput(_input);
        Platform.runLater(() -> _row.addToGrid(_grid, 0));
        waitForUiEvents();
        }

    @Override
    public Node createComponentNode()
        {
        _row = new TaskInputValueEditorRow(new ProjectExecutionContext(new SimpleProject()));
        BorderPane node = new BorderPane();
        node.setCenter(_grid);
        node.setBottom(new Button("control to accept focus"));
        return node;
        }

    private final GridPane _grid = new GridPane();
    private TaskInputValueEditorRow _row;
    private TaskInput _input;

    private final static String NAME1 = "name1";
    private final static String DEF_VAL = "default-val";
    }