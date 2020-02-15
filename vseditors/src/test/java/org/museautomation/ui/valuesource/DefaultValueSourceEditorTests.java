package org.museautomation.ui.valuesource;

import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
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
    public void displayType()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(StringValueSource.TYPE_ID);
        ValueSourceDescriptor descriptor = new SimpleProject().getValueSourceDescriptors().get(source);
        _editor.setSource(source);
        waitForUiEvents();

        // verify the type
        Assert.assertEquals(descriptor.getName(), textOf(id(DefaultValueSourceEditor.TYPE_FIELD_ID)).trim());
        }

    @Test
    public void displayPrimitiveValue()
        {
        final String the_string = "abc123";
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue(the_string);
        _editor.setSource(source);
        waitForUiEvents();

        // verify the value (and nothing else) is shown
        Assert.assertEquals(quoted(the_string), textOf(id(PrimitiveValueEditorField.INPUT_ID)));
        }

    @Test
    public void displaySingleSubsource()
        {
        final String varname = "varname";
        ValueSourceConfiguration source = ValueSourceConfiguration.forSource(VariableValueSource.TYPE_ID, ValueSourceConfiguration.forValue(varname));
        _editor.setSource(source);
        waitForUiEvents();

        Assert.assertEquals("the edit field shows the wrong value", quoted(varname), textOf(id(DefaultValueSourceEditor.SINGLE_SUBSOURCE_FIELD_ID)));
        Assert.assertTrue(exists(id(DefaultValueSourceEditor.SINGLE_SUBSOURCE_ADVANCED_LINK_ID)));
        Node input_field = lookup(id(DefaultValueSourceEditor.SINGLE_SUBSOURCE_FIELD_ID)).query();
        Assert.assertEquals("doesn't have the right prompt text", _project.getValueSourceDescriptors().get(source).getSubsourceDescriptors()[0].getDescription(), ((TextInputControl) input_field).getPromptText());

        fillFieldAndTabAway(id(DefaultValueSourceEditor.SINGLE_SUBSOURCE_FIELD_ID), "");

        }

    @Test
    public void displayNamedSubsource()
        {
        final String the_string = "abc123";
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithNamedSource(DateFormatValueSource.TYPE_ID, DateFormatValueSource.DATE_PARAM, the_string);
        _editor.setSource(source);
        waitForUiEvents();

        // verify the editor for the param is present and contains the value
        Assert.assertEquals("the edit field shows the wrong value", quoted(the_string), textOf(id(FixedNameValueSourceEditor.getValueFieldId(DateFormatValueSource.DATE_PARAM))));
        Assert.assertTrue("the 'more' link is missing", exists(id(FixedNameValueSourceEditor.getAdvancedLinkId(DateFormatValueSource.DATE_PARAM))));
        }

    @Test
    public void displayIndexedSubsource()
        {
        final String string0 = "abc123";
        final String string1 = "xyz789";
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(AdditionSource.TYPE_ID);
        source.addSource(0, ValueSourceConfiguration.forValue(string0));
        source.addSource(1, ValueSourceConfiguration.forValue(string1));

        _editor.setSource(source);
        waitForUiEvents();

        // verify the values are is shown
        Assert.assertEquals(quoted(string0), textOf(id(ValueSourceListEditor.getEditorId(0))));
        Assert.assertEquals(quoted(string1), textOf(id(ValueSourceListEditor.getEditorId(1))));
        }

    @Test
    public void changeType()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue("abc");
        _editor.setSource(source);
        waitForUiEvents();

        // change the type
        clickOn(id(DefaultValueSourceEditor.TYPE_FIELD_ID));
        ValueSourceDescriptor target_type_descriptor = _project.getValueSourceDescriptors().get(VariableValueSource.class);
        clickOn(target_type_descriptor.getName());

        Assert.assertEquals("the source type was not changed", VariableValueSource.TYPE_ID, source.getType());
        Assert.assertEquals("the help field was not updated", _project.getValueSourceDescriptors().get(source).getShortDescription(), textOf(id(DefaultValueSourceEditor.TYPE_DESCRIPTION_ID)));
        Assert.assertEquals("the type chooser is showing the wrong type", target_type_descriptor.getName(), textOf(id(DefaultValueSourceEditor.TYPE_FIELD_ID)));
        }

    @Test
    public void changeTypeUpdatesSourceAndFields()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithNamedSource(EqualityCondition.TYPE_ID, EqualityCondition.LEFT_PARAM, 123L);
        _editor.setSource(source);
        waitForUiEvents();

        // verify correct params displayed
        Assert.assertTrue(exists(id(FixedNameValueSourceEditor.getValueFieldId(EqualityCondition.LEFT_PARAM))));
        Assert.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(DateFormatValueSource.DATE_PARAM))));
        Assert.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(DateFormatValueSource.FORMAT_PARAM))));

        // change the type
        clickOn(id(DefaultValueSourceEditor.TYPE_FIELD_ID));
        clickOn(_project.getValueSourceDescriptors().get(DateFormatValueSource.class).getName());

        // verify the source was changed to have the newly required parameters
        Assert.assertNotNull(source.getSource(DateFormatValueSource.DATE_PARAM));
        Assert.assertNotNull(source.getSource(DateFormatValueSource.FORMAT_PARAM));

        // verify correct params displayed
        Assert.assertFalse(exists(id(FixedNameValueSourceEditor.getValueFieldId(EqualityCondition.LEFT_PARAM))));
        Assert.assertTrue(exists(id(FixedNameValueSourceEditor.getValueFieldId(DateFormatValueSource.DATE_PARAM))));
        Assert.assertTrue(exists(id(FixedNameValueSourceEditor.getValueFieldId(DateFormatValueSource.FORMAT_PARAM))));
        }

    @Test
    public void addOptionalPrimitiveValue()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(SourceWithOptionalPrimitiveValue.TYPE_ID);
        _editor.setSource(source);
        waitForUiEvents();

        // press the add value button
        clickOn(id(PrimitiveValueOptionalField.ADD_BUTTON_ID));
        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), "123");

        Assert.assertNotNull("value was not added", source.getValue());
        Assert.assertTrue("value field is not there", exists(id(PrimitiveValueEditorField.INPUT_ID)));
        }

    @Test
    public void removeOptionalPrimitiveValue()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(SourceWithOptionalPrimitiveValue.TYPE_ID);
        source.setValue("abc");
        _editor.setSource(source);
        waitForUiEvents();

        clickOn(id(PrimitiveValueOptionalField.DELETE_BUTTON_ID));

        Assert.assertNull("value was not nullified", source.getValue());
        Assert.assertFalse("value field is still showing", exists(id(PrimitiveValueEditorField.INPUT_ID)));
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
    public void addOptionalNamedSubsource()
        {
        final String the_string = "abc123";
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithNamedSource(DateFormatValueSource.TYPE_ID, DateFormatValueSource.DATE_PARAM, the_string);
        _editor.setSource(source);
        waitForUiEvents();

        // add the source
        clickOn(id(FixedNameValueSourceEditor.getAddButtonId(DateFormatValueSource.FORMAT_PARAM)));

        Assert.assertNotNull("subsource was not added", source.getSource(DateFormatValueSource.FORMAT_PARAM));
        }

    @Test
    public void removeOptionalNamedSource()
        {
        final String the_string = "abc123";
        ValueSourceConfiguration source = ValueSourceConfiguration.forTypeWithNamedSource(SourceWithOptionalNamedSubsource.TYPE_ID, SourceWithOptionalNamedSubsource.OPT_PARAM, the_string);
        _editor.setSource(source);
        _editor.activate();
        waitForUiEvents();

        Assert.assertNotNull("test not setup correctly", source.getSource(SourceWithOptionalNamedSubsource.OPT_PARAM));

        // remove the value
        clickOn(id(FixedNameValueSourceEditor.getDeleteButtonId(SourceWithOptionalNamedSubsource.OPT_PARAM)));

        Assert.assertNull("subsource was not removed", source.getSource(SourceWithOptionalNamedSubsource.OPT_PARAM));
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
    public void invalidWhenRequiredValueIsInvalid()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(SourceWithRequiredPrimitiveValue.TYPE_ID);
        _editor.setSource(source);
        waitForUiEvents();

        Assert.assertFalse("should be invalid", _editor.isValid());

        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), quoted("abc"));
        Assert.assertTrue("should be valid", _editor.isValid());

        fillFieldAndTabAway(id(PrimitiveValueEditorField.INPUT_ID), "a1b2c");
        Assert.assertFalse("should be invalid", _editor.isValid());
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


