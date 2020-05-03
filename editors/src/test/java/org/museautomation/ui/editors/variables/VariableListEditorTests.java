package org.museautomation.ui.editors.variables;

import javafx.application.*;
import javafx.scene.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.valuesource.*;
import org.museautomation.ui.valuesource.map.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;
import org.museautomation.core.variables.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class VariableListEditorTests extends ComponentTest
    {
    @Test
    void displayElementLocators()
        {
        setup();

        checkForVariableExists(VARIABLE_NAME1, SOURCE1);
        checkForVariableExists(VARIABLE_NAME2, SOURCE2);
        }

    @Test
    void removeFirstVariableAndUndo()
        {
        setup();
        clickOn(id(ValueSourceMapEditor.getRemoveButtonId(VARIABLE_NAME1)));

        // first variable should no longer be present
        checkForVariableNotExist(VARIABLE_NAME1, SOURCE1);
        checkForVariableExists(VARIABLE_NAME2, SOURCE2);

        Platform.runLater(() -> _editor.getUndoStack().undoLastAction());
        waitForUiEvents();

        // first variable has returned
        checkForVariableExists(VARIABLE_NAME1, SOURCE1);
        checkForVariableExists(VARIABLE_NAME2, SOURCE2);
        }

    @Test
    void removeLastVariableAndUndo()
        {
        setup();
        clickOn(id(ValueSourceMapEditor.getRemoveButtonId(VARIABLE_NAME2)));

        // second variable should no longer be present in the UI
        checkForVariableExists(VARIABLE_NAME1, SOURCE1);
        checkForVariableNotExist(VARIABLE_NAME2, SOURCE2);

        Platform.runLater(() -> _editor.getUndoStack().undoLastAction());
        waitForUiEvents();

        // first variable has returned in the UI
        checkForVariableExists(VARIABLE_NAME1, SOURCE1);
        checkForVariableExists(VARIABLE_NAME2, SOURCE2);
        }

    @Test
    void addVariableAndUndo()
        {
        setup();

        clickOn(id(ValueSourceMapEditor.ADD_BUTTON_ID));
        waitForUiEvents();

        checkForVariableExists(VARIABLE_NAME1, SOURCE1);
        checkForVariableExists(VARIABLE_NAME2, SOURCE2);
        final String newval = _list.getVariables().get("name1").getValue().toString();
        checkForVariableExists("name1", newval);

        Platform.runLater(() -> _editor.getUndoStack().undoLastAction());
        waitForUiEvents();

        // new variable removed
        checkForVariableExists(VARIABLE_NAME1, SOURCE1);
        checkForVariableExists(VARIABLE_NAME2, SOURCE2);
        checkForVariableNotExist("name1", newval);
        }

    /**
     * Verify the advanced editor can be opened.
     */
    @Test
    void useAdvancedValueSourceEditor()
        {
        setup();

        // look for a field from the VS editor
        Assertions.assertFalse(exists(id(DefaultValueSourceEditor.TYPE_FIELD_ID)));

        clickOn(id(ValueSourceMapEditor.getAdvancedLinkId(VARIABLE_NAME1)));

        Assertions.assertTrue(exists(id(DefaultValueSourceEditor.TYPE_FIELD_ID)));
        }
    private void checkForVariableExists(String name, String locator)
        {
        Assertions.assertTrue(exists(name));
        Assertions.assertTrue(exists(quoted(locator)));
        Assertions.assertNotNull(_list.namedElementLocators().getSource(name));
        }

    private void checkForVariableNotExist(String name, String locator)
        {
        Assertions.assertFalse(exists(name));
        Assertions.assertFalse(exists(quoted(locator)));
        Assertions.assertNull(_list.namedElementLocators().getSource(name));
        }

    private void setup()
        {
        _list = new VariableList();
        _list.addVariable(VARIABLE_NAME1, ValueSourceConfiguration.forValue(SOURCE1));
        _list.addVariable(VARIABLE_NAME2, ValueSourceConfiguration.forValue(SOURCE2));
        _editor.editResource(new SimpleProject(), _list);
        waitForUiEvents();
        }

    @Override
    public Node createComponentNode()
        {
        _editor = new VariableListEditor();
        return _editor.getNode();
        }

    private VariableListEditor _editor;
    private VariableList _list;
    private static String VARIABLE_NAME1 = "element1";
    private static String SOURCE1 = "source1";
    private static String VARIABLE_NAME2 = "element2";
    private static String SOURCE2 = "source2";
    }