package org.museautomation.ui.valuesource;

import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.museautomation.ui.valuesource.list.*;
import org.museautomation.ui.valuesource.map.*;
import org.museautomation.builtins.step.*;
import org.museautomation.builtins.value.*;
import org.museautomation.builtins.value.logic.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.stack.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused")
public class ExpertValueSourceEditorTests extends ComponentTest
    {
    @Test
    public void displayAndChangePrimitiveValue()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue(INT_VALUE);
        _editor.setSource(source);
        waitForUiEvents();

        // the value is displayed
        Node text = lookup(id(PrimitiveValueEditorField.INPUT_ID)).query();
        Assert.assertEquals(Long.toString(INT_VALUE), ((TextInputControl) text).getText());

        // change the value
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), Long.toString(INT_VALUE2));

        // check change to the underlying source
        Assert.assertEquals(INT_VALUE2, source.getValue());
        }

    @Test
    public void displayAndChangeType()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(NotValueSource.TYPE_ID);
        _editor.setSource(source);

        waitForUiEvents();

        // the value is displayed
        Node chooser = lookup(id(ExpertValueSourceEditor.TYPE_FIELD_ID)).query();
        Assert.assertEquals(_project.getValueSourceDescriptors().get(NotValueSource.TYPE_ID).getName(), ((MenuButton) chooser).getText());

        // change the value
        clickOn(chooser);
        clickOn(_project.getValueSourceDescriptors().get(VariableValueSource.TYPE_ID).getName());

        // check change to the underlying source
        Assert.assertEquals(VariableValueSource.TYPE_ID, source.getType());
        }

    @Test
    public void primitiveValidation()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue(INT_VALUE);
        _editor.setSource(source);
        waitForUiEvents();

        Assert.assertTrue(_editor.isValid());

        // type something invalid
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), "a1");
        Assert.assertFalse(_editor.isValid());

        // back to valid...
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), "11");
        Assert.assertTrue(_editor.isValid());
        }

    @Test
    public void showInitialSubsource()
        {
        final String varname = "var1";
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithSource(VariableValueSource.TYPE_ID, varname);
        _editor.setSource(source);
        waitForUiEvents();

        Node subsource_field = lookup(id(ExpertValueSourceEditor.SUBSOURCE_EDITOR_ID)).query();
        Assert.assertEquals(quoted(varname), ((TextInputControl)subsource_field).getText());
        }

    @Test
    public void addAndSetSubsource()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue(INT_VALUE);
        _editor.setSource(source);
        waitForUiEvents();

        // no subsource and no subsource editor
        Assert.assertNull(source.getSource());
        Assert.assertFalse(exists(id(ExpertValueSourceEditor.SUBSOURCE_EDITOR_ID)));

        // add a subsource
        clickOn(id(ExpertValueSourceEditor.ADD_SUBSOURCE_ID));

        // subsource editor now present
        Assert.assertTrue(exists(id(ExpertValueSourceEditor.SUBSOURCE_EDITOR_ID)));

        // type something in subsource editor
        fillFieldAndTabAway(id(ExpertValueSourceEditor.SUBSOURCE_EDITOR_ID), "123");

        // change was made to the original source
        Assert.assertEquals(123L, source.getSource().getValue());
        }

    @Test
    public void removeSubsource()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithSource(VariableValueSource.TYPE_ID, "var1");
        _editor.setSource(source);
        waitForUiEvents();

        clickOn(id(ExpertValueSourceEditor.REMOVE_SUBSOURCE_ID));

        Assert.assertNull(source.getSource());
        }

    @Test
    public void editAdvancedSubsource()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithSource(VariableValueSource.TYPE_ID, "var1");
        _editor.setSource(source);
        waitForUiEvents();

        // verify the displayed type is 'variable'
        Node chooser = lookup(id(ExpertValueSourceEditor.TYPE_FIELD_ID)).query();
        Assert.assertEquals(_project.getValueSourceDescriptors().get(VariableValueSource.TYPE_ID).getName(), ((MenuButton) chooser).getText());

        // go to editor for the subsource & switch to expert
        clickOn(id(ExpertValueSourceEditor.ADVANCED_SUBSOURCE_ID));
        clickOn(id(MultimodeValueSourceEditor.SWITCH_TO_EXPERT_ID));

        // now the displayed type should be string
        chooser = lookup(id(ExpertValueSourceEditor.TYPE_FIELD_ID)).query();
        Assert.assertEquals(_project.getValueSourceDescriptors().get(StringValueSource.TYPE_ID).getName(), ((MenuButton) chooser).getText());

        // and the breadcrumb should be displayed
        Assert.assertTrue(exists(ExpertValueSourceEditor.SUBSOURCE_BREADCRUMB_LABEL));

        // now change the string value
        fillFieldAndTabAway(quoted("var1"), quoted("abc"));

        // return to the previous editor
        clickOn(ROOT_BREADCRUMB_LABEL);

        // ensure the new value is displayed
        Node subsource_field = lookup(id(ExpertValueSourceEditor.SUBSOURCE_EDITOR_ID)).query();
        Assert.assertEquals(quoted("abc"), ((TextInputControl)subsource_field).getText());
        }

    @Test
    public void editAdvancedNamedSource()
        {
        final String message = "messageN";
        final String newval = quoted("new_value");
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(LogMessage.TYPE_ID);
        source.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue(message));
        _editor.setSource(source);
        waitForUiEvents();

        // verify the displayed type is correct
        Node chooser = lookup(id(ExpertValueSourceEditor.TYPE_FIELD_ID)).query();
        Assert.assertEquals(_project.getValueSourceDescriptors().get(LogMessage.TYPE_ID).getName(), ((MenuButton) chooser).getText());

        // go to expert editor for the param
        clickOn(id(ValueSourceMapEditor.getAdvancedLinkId(LogMessage.MESSAGE_PARAM)));
        clickOn(id(MultimodeValueSourceEditor.SWITCH_TO_EXPERT_ID));

        // now the displayed type should be string
        chooser = lookup(id(ExpertValueSourceEditor.TYPE_FIELD_ID)).query();
        Assert.assertEquals(_project.getValueSourceDescriptors().get(StringValueSource.TYPE_ID).getName(), ((MenuButton) chooser).getText());

        // and the breadcrumb should be displayed
        Assert.assertTrue(exists(LogMessage.MESSAGE_PARAM));

        // now change the string value
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), newval);

        // return to the previous editor
        clickOn(ROOT_BREADCRUMB_LABEL);

        // ensure the new value is displayed
        Node subsource_field = lookup(id(InlineNamedVSE.getValueFieldId(LogMessage.MESSAGE_PARAM))).query();
        Assert.assertEquals(newval, ((TextInputControl)subsource_field).getText());
        }

    @Test
    public void editAdvancedIndexedSource()
        {
        final String param1 = "param1";
        final String param2 = "param2";
        final String newval = "newval";
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(AdditionSource.TYPE_ID);
        source.addSource(0, ValueSourceConfiguration.forValue(param1));
        source.addSource(1, ValueSourceConfiguration.forValue(param2));
        _editor.setSource(source);
        waitForUiEvents();

        // verify the displayed type is correct
        Node chooser = lookup(id(ExpertValueSourceEditor.TYPE_FIELD_ID)).query();
        Assert.assertEquals(_project.getValueSourceDescriptors().get(AdditionSource.TYPE_ID).getName(), ((MenuButton) chooser).getText());

        // go to expert editor for the first param
        clickOn(id(ValueSourceListEditor.getAdvancedLinkId(0)));
        clickOn(id(MultimodeValueSourceEditor.SWITCH_TO_EXPERT_ID));

        // now the displayed type should be string
        chooser = lookup(id(ExpertValueSourceEditor.TYPE_FIELD_ID)).query();
        Assert.assertEquals(_project.getValueSourceDescriptors().get(StringValueSource.TYPE_ID).getName(), ((MenuButton) chooser).getText());

        // and the breadcrumb should be displayed
        Assert.assertTrue(exists("[0]"));

        // now change the string value
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), quoted(newval));

        // return to the previous editor
        clickOn(ROOT_BREADCRUMB_LABEL);

        // ensure the new value is displayed
        Node subsource_field = lookup(id(ValueSourceListEditor.getEditorId(0))).query();
        Assert.assertEquals(quoted(newval), ((TextInputControl)subsource_field).getText());
        }

    @Override
    protected Node createComponentNode()
        {
        _project = new SimpleProject();
        _editor = new ExpertValueSourceEditor(_project, new UndoStack());

        EditorStack stack = new EditorStack(new EditInProgress.NoopEdit(), new UndoStack())
            {
            @Override
            protected void notifyEditCommit()
                {

                }
            };

        stack.push(_editor, ROOT_BREADCRUMB_LABEL);

        return stack.getNode();
        }

    @Override
    protected double getDefaultHeight()
        {
        return 400;
        }

    private ExpertValueSourceEditor _editor;
    private SimpleProject _project;

    private final static long INT_VALUE = 123;
    private final static long INT_VALUE2 = 456;
    private final static String ROOT_BREADCRUMB_LABEL = "root";
    }


