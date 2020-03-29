package org.museautomation.ui.valuesource;

import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.ui.valuesource.list.*;
import org.museautomation.ui.valuesource.mocks.*;
import org.museautomation.builtins.condition.*;
import org.museautomation.builtins.value.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.descriptor.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.stack.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class DefaultValueSourceEditorTests extends ComponentTest
    {
    @Test
    void displayType()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(StringValueSource.TYPE_ID);
        ValueSourceDescriptor descriptor = new SimpleProject().getValueSourceDescriptors().get(source);
        _editor.setSource(source);
        waitForUiEvents();

        // verify the type
        Assertions.assertEquals(descriptor.getName(), textOf(id(DefaultValueSourceEditor.TYPE_FIELD_ID)).trim());
        }

    @Test
    void displayPrimitiveValue()
        {
        final String the_string = "abc123";
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue(the_string);
        _editor.setSource(source);
        waitForUiEvents();

        // verify the value (and nothing else) is shown
        Assertions.assertEquals(quoted(the_string), textOf(id(PrimitiveValueEditorField.INPUT_ID)));
        }

    @Test
    void displaySingleSubsource()
        {
        final String varname = "varname";
        ValueSourceConfiguration source = ValueSourceConfiguration.forSource(VariableValueSource.TYPE_ID, ValueSourceConfiguration.forValue(varname));
        _editor.setSource(source);
        waitForUiEvents();

        Assertions.assertEquals(quoted(varname), textOf(id(DefaultValueSourceEditor.SINGLE_SUBSOURCE_FIELD_ID)), "the edit field shows the wrong value");
        Assertions.assertTrue(exists(id(DefaultValueSourceEditor.SINGLE_SUBSOURCE_ADVANCED_LINK_ID)));
        Node input_field = lookup(id(DefaultValueSourceEditor.SINGLE_SUBSOURCE_FIELD_ID)).query();
        Assertions.assertEquals(_project.getValueSourceDescriptors().get(source).getSubsourceDescriptors()[0].getDescription(), ((TextInputControl) input_field).getPromptText(), "doesn't have the right prompt text");

        fillFieldAndTabAway(id(DefaultValueSourceEditor.SINGLE_SUBSOURCE_FIELD_ID), "");

        }

    @Test
    void displayNamedSubsource()
        {
        final String the_string = "abc123";
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithNamedSource(DateFormatValueSource.TYPE_ID, DateFormatValueSource.DATE_PARAM, the_string);
        _editor.setSource(source);
        waitForUiEvents();

        // verify the editor for the param is present and contains the value
        Assertions.assertEquals(quoted(the_string), textOf(id(FixedNameValueSourceEditor.getValueFieldId(DateFormatValueSource.DATE_PARAM))), "the edit field shows the wrong value");
        Assertions.assertTrue(exists(id(FixedNameValueSourceEditor.getAdvancedLinkId(DateFormatValueSource.DATE_PARAM))), "the 'more' link is missing");
        }

    @Test
    void displayIndexedSubsource()
        {
        final String string0 = "abc123";
        final String string1 = "xyz789";
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(AdditionSource.TYPE_ID);
        source.addSource(0, ValueSourceConfiguration.forValue(string0));
        source.addSource(1, ValueSourceConfiguration.forValue(string1));

        _editor.setSource(source);
        waitForUiEvents();

        // verify the values are is shown
        Assertions.assertEquals(quoted(string0), textOf(id(ValueSourceListEditor.getEditorId(0))));
        Assertions.assertEquals(quoted(string1), textOf(id(ValueSourceListEditor.getEditorId(1))));
        }

    @Test
    void changeType()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue("abc");
        _editor.setSource(source);
        waitForUiEvents();

        // change the type
        clickOn(id(DefaultValueSourceEditor.TYPE_FIELD_ID));
        ValueSourceDescriptor target_type_descriptor = _project.getValueSourceDescriptors().get(VariableValueSource.class);
        clickOn(target_type_descriptor.getName());

        Assertions.assertEquals(VariableValueSource.TYPE_ID, source.getType(), "the source type was not changed");
        Assertions.assertEquals(_project.getValueSourceDescriptors().get(source).getShortDescription(), textOf(id(DefaultValueSourceEditor.TYPE_DESCRIPTION_ID)), "the help field was not updated");
        Assertions.assertEquals(target_type_descriptor.getName(), textOf(id(DefaultValueSourceEditor.TYPE_FIELD_ID)), "the type chooser is showing the wrong type");
        }

    @Test
    void changeTypeUpdatesSourceAndFields()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithNamedSource(EqualityCondition.TYPE_ID, EqualityCondition.LEFT_PARAM, 123L);
        _editor.setSource(source);
        waitForUiEvents();

        // verify correct params displayed
        Assertions.assertTrue(exists(id(FixedNameValueSourceEditor.getValueFieldId(EqualityCondition.LEFT_PARAM))));
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(ListContainsSource.LIST_PARAM))));
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(ListContainsSource.TARGET_PARAM))));

        // change the type
        clickOn(id(DefaultValueSourceEditor.TYPE_FIELD_ID));
        clickOn(_project.getValueSourceDescriptors().get(ListContainsSource.class).getName());

        // verify the source was changed to have the newly required parameters
        Assertions.assertNotNull(source.getSource(ListContainsSource.LIST_PARAM));
        Assertions.assertNotNull(source.getSource(ListContainsSource.TARGET_PARAM));

        // verify correct params displayed
        Assertions.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(EqualityCondition.LEFT_PARAM))));
        Assertions.assertTrue(exists(id(FixedNameValueSourceEditor.getValueFieldId(ListContainsSource.LIST_PARAM))));
        Assertions.assertTrue(exists(id(FixedNameValueSourceEditor.getValueFieldId(ListContainsSource.TARGET_PARAM))));
        }

    @Test
    void addOptionalPrimitiveValue()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(SourceWithOptionalPrimitiveValue.TYPE_ID);
        _editor.setSource(source);
        waitForUiEvents();

        // press the add value button
        clickOn(id(PrimitiveValueOptionalField.ADD_BUTTON_ID));
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), "123");

        Assertions.assertNotNull(source.getValue(), "value was not added");
        Assertions.assertTrue(exists(id(PrimitiveValueEditorField.INPUT_ID)), "value field is not there");
        }

    @Test
    void removeOptionalPrimitiveValue()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(SourceWithOptionalPrimitiveValue.TYPE_ID);
        source.setValue("abc");
        _editor.setSource(source);
        waitForUiEvents();

        clickOn(id(PrimitiveValueOptionalField.DELETE_BUTTON_ID));

        Assertions.assertNull(source.getValue(), "value was not nullified");
        Assertions.assertFalse(exists(id(PrimitiveValueEditorField.INPUT_ID)), "value field is still showing");
        }

/*
    @Test
    public void addOptionalSubsource()
        {

        Assert.fail("this test is not yet finished");
        }

    @Test
    public void removeOptionalSubsource()
        {

        Assert.fail("this test is not yet finished");
        }
*/

    @Test
    void addOptionalNamedSubsource()
        {
        final String the_string = "abc123";
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithNamedSource(DateFormatValueSource.TYPE_ID, DateFormatValueSource.DATE_PARAM, the_string);
        _editor.setSource(source);
        waitForUiEvents();

        // add the source
        clickOn(id(FixedNameValueSourceEditor.getAddButtonId(DateFormatValueSource.FORMAT_PARAM)));

        Assertions.assertNotNull(source.getSource(DateFormatValueSource.FORMAT_PARAM), "subsource was not added");
        }

    @Test
    void removeOptionalNamedSource()
        {
        final String the_string = "abc123";
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithNamedSource(SourceWithOptionalNamedSubsource.TYPE_ID, SourceWithOptionalNamedSubsource.OPT_PARAM, the_string);
        _editor.setSource(source);
        _editor.activate();
        waitForUiEvents();

        Assertions.assertNotNull(source.getSource(SourceWithOptionalNamedSubsource.OPT_PARAM), "test not setup correctly");

        // remove the value
        clickOn(id(FixedNameValueSourceEditor.getDeleteButtonId(SourceWithOptionalNamedSubsource.OPT_PARAM)));

        Assertions.assertNull(source.getSource(SourceWithOptionalNamedSubsource.OPT_PARAM), "subsource was not removed");
        }

/*
    @Test
    public void addOptionalIndexedSubsource()
        {

        Assert.fail("this test is not yet finished");
        }

    @Test
    public void removeOptionalIndexedSubsource()
        {

        Assert.fail("this test is not yet finished");
        }
*/

    @Test
    void invalidWhenRequiredValueIsInvalid()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(SourceWithRequiredPrimitiveValue.TYPE_ID);
        _editor.setSource(source);
        waitForUiEvents();

        Assertions.assertFalse(_editor.isValid(), "should be invalid");

        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), quoted("abc"));
        Assertions.assertTrue(_editor.isValid(), "should be valid");

        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), "a1b2c");
        Assertions.assertFalse(_editor.isValid(), "should be invalid");
        }

    @Override
    protected Node createComponentNode()
        {
        UndoStack undo = new UndoStack();
        _editor = new DefaultValueSourceEditor(_project, undo);
        _editor.setStack(new EditorStack(new EditInProgress.NoopEdit(), undo)
	        {
	        @Override
	        protected void notifyEditCommit()
		        {

		        }
	        });
        return _editor.getNode();
        }

    private DefaultValueSourceEditor _editor;
    private SimpleProject _project = new SimpleProject();
    }