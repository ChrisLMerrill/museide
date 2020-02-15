package org.museautomation.ui.valuesource;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.museautomation.ui.valuesource.map.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.descriptor.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.stack.*;

import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceMapEditorTests extends ComponentTest
    {
    @Test
    public void initialParametersDisplayed()
        {
        setup();
        Assert.assertTrue(exists(id(InlineNamedVSE.getNameFieldId(LogMessage.MESSAGE_PARAM))));
        Assert.assertEquals(LogMessage.MESSAGE_PARAM, textOf(id(InlineNamedVSE.getNameFieldId(LogMessage.MESSAGE_PARAM))));

        Assert.assertTrue(exists(id(InlineNamedVSE.getValueFieldId(LogMessage.MESSAGE_PARAM))));
        Assert.assertEquals(quoted(MESSAGE), textOf(id(InlineNamedVSE.getValueFieldId(LogMessage.MESSAGE_PARAM))));

        Assert.assertTrue(exists(NAME2));
        Assert.assertTrue(exists(quoted(VALUE2)));
        }

    @Test
    public void changeName()
        {
        setup();

        final String new_name = "new_name";
        fillFieldAndTabAway(id(InlineNamedVSE.getNameFieldId(LogMessage.MESSAGE_PARAM)), new_name);

        Assert.assertNotNull(_source.getSource(new_name));
        Assert.assertNull(_source.getSource("source"));
        }

    @Test
    public void changeValue()
        {
        setup();

        final String new_value = "new_value";
        fillFieldAndTabAway(id(InlineNamedVSE.getValueFieldId(LogMessage.MESSAGE_PARAM)), quoted(new_value));

        Assert.assertEquals(new_value, _source.getSource(LogMessage.MESSAGE_PARAM).getValue());
        }

    @Test
    public void preventNameChangeToDuplicate()
        {
        setup();
        waitForUiEvents();

        Node name_field = lookup(NAME2).query();
        fillFieldAndTabAway(name_field, LogMessage.MESSAGE_PARAM);

        Assert.assertNotNull(_source.getSource(NAME2));  // name was not changed
        Assert.assertTrue(InputValidation.isShowingError(name_field));
        Assert.assertFalse(_editor.isValid());
        }

    @Test
    public void addSource()
        {
        setup();
        clickOn("#" + ValueSourceMapEditor.ADD_BUTTON_ID);
        Assert.assertNotNull(_source.getSource("name1"));
        Assert.assertTrue(exists(quoted(_source.getSource("name1").getValue())));
        }

    @Test
    public void removeFirstSource()
        {
        setup();
        clickOn("#" + ValueSourceMapEditor.getRemoveButtonId(LogMessage.MESSAGE_PARAM));

        Assert.assertNull(_source.getSource(LogMessage.MESSAGE_PARAM));       // was deleted from source
        Assert.assertFalse(exists(LogMessage.MESSAGE_PARAM));        // was removed from UI
        Assert.assertFalse(exists(id(ValueSourceMapEditor.getRemoveButtonId(LogMessage.MESSAGE_PARAM))));
        Assert.assertFalse(exists(id(ValueSourceMapEditor.getAdvancedLinkId(LogMessage.MESSAGE_PARAM))));
        }

    @Test
    public void removeLastSource()
        {
        setup();
        clickOn(id(ValueSourceMapEditor.getRemoveButtonId(NAME2)));
        waitForUiEvents();

        Assert.assertNull(_source.getSource(NAME2));       // was deleted from source
        Assert.assertFalse(exists(NAME2));        // was removed from UI
        Assert.assertFalse(exists(id(ValueSourceMapEditor.getRemoveButtonId(NAME2))));
        Assert.assertFalse(exists(id(ValueSourceMapEditor.getAdvancedLinkId(NAME2))));
        }

    @Test
    public void validInvalidTransitions()
        {
        setup();

        // starts out valid
        Assert.assertTrue(_editor.isValid());

        // make it invalid
        fillFieldAndTabAway(id(InlineNamedVSE.getValueFieldId(LogMessage.MESSAGE_PARAM)), "invalid");
        Assert.assertFalse(_editor.isValid());

        // return it to valid
        fillFieldAndTabAway(id(InlineNamedVSE.getValueFieldId(LogMessage.MESSAGE_PARAM)), "123");
        Assert.assertTrue(_editor.isValid());
        }

    @Test
    public void useAdvancedEditor()
        {
        setup();

        // setup receiver for EditorStack push action
        final AtomicReference<String> _pushed_name = new AtomicReference<>(null);
        final AtomicReference<StackableEditor> _pushed_editor = new AtomicReference<>(null);
        _editor.setStack(new EditorStack(null, new UndoStack())
            {
            @Override
            public void push(StackableEditor editor, String name)
                {
                _pushed_name.set(name);
                _pushed_editor.set(editor);
                }

            @Override
            protected void notifyEditCommit()
                {

                }
            });

        // go to a VS editor
        clickOn("#" + ValueSourceMapEditor.getAdvancedLinkId(LogMessage.MESSAGE_PARAM));

        // verify the expected name was sent
        Assert.assertEquals(LogMessage.MESSAGE_PARAM, _pushed_name.get());
        // and an editor of the right type
        Assert.assertTrue(_pushed_editor.get() instanceof MultimodeValueSourceEditor);
        }

    @Test
    public void showAdvancedEditorAfterChange()
        {
        setup();

        // click on the more button
        clickOn("#" + ValueSourceMapEditor.getAdvancedLinkId(NAME2));

        // check the initial value shown
        Assert.assertTrue("original value not shown", exists(quoted(VALUE2)));

        // close advanced editor
        clickOn(id(Buttons.CANCEL_ID));

        // make a change
        final String newval = quoted("newval");
        fillFieldAndTabAway(quoted(VALUE2), newval);

        // click on the more button
        clickOn("#" + ValueSourceMapEditor.getAdvancedLinkId(NAME2));

        Assert.assertFalse("original value still showing", exists(quoted(VALUE2)));
        Assert.assertTrue("change value not shown", exists(newval));
        }

    @Test
    public void showChangeMadeInAdvancedEditor()
        {
        setup();

        // open advanced editor and make a change
        clickOn(id(ValueSourceMapEditor.getAdvancedLinkId(NAME2)));
        final String newval = "newval";
        fillFieldAndTabAway(quoted(VALUE2), quoted(newval));
        clickOn(id(Buttons.SAVE_ID));
        waitForUiEvents();

        // check the change is reflected
        Assert.assertFalse("original value still showing", exists(quoted(VALUE2)));
        Assert.assertTrue("new value not shown", exists(quoted(newval)));
        }

    @Test
    public void removeSourceListener() throws InterruptedException
        {
        setup();
        Assert.assertEquals(1, _source.getListeners().size());

        ComponentRemover.waitForRemoval(_container, _editor.getNode());

        // verify the value source no longer has a listener
        Assert.assertEquals(0, _source.getListeners().size());
        }

    @Test
    public void hideParamsByName()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(CallFunction.TYPE_ID);
        source.addSource(CallFunction.ID_PARAM, ValueSourceConfiguration.forValue("function-id"));
        source.addSource("param1", ValueSourceConfiguration.forValue("param2"));
        _editor.hideSourceNamed(CallFunction.ID_PARAM);
        Platform.runLater(() -> _editor.setSource(source));
        waitForUiEvents();

        Assert.assertFalse("hidden name is not hidden", exists(id(InlineNamedVSE.getValueFieldId(CallFunction.ID_PARAM))));
        }

    @Test
    public void showDescribedSource()
        {
        MockContainsDescribedSources container = new MockContainsDescribedSources();
        container.getSubsources().addSource("param1", ValueSourceConfiguration.forValue("value1"));
        container.getSubsources().addSource("optparam1", ValueSourceConfiguration.forValue("optvalue1"));
        Platform.runLater(() -> _editor.setSource(container.getSubsources(), container.getSubsourceDescriptors()));
        waitForUiEvents();

        Assert.assertTrue("Param1 name not displayed", exists(container.getSubsourceDescriptors()[0].getDisplayName()));
        Assert.assertTrue("Param1 value not displayed", exists(quoted("value1")));
        Assert.assertFalse(exists(id(ValueSourceMapEditor.getRemoveButtonId("param1"))));

        Assert.assertTrue("Opt Param1 name not displayed", exists(container.getSubsourceDescriptors()[1].getDisplayName()));
        Assert.assertTrue("Opt Param1 value not displayed", exists(quoted("value1")));
        Assert.assertTrue(exists(id(ValueSourceMapEditor.getRemoveButtonId("optparam1"))));

        Assert.assertTrue("Opt Param2 name not displayed", exists(container.getSubsourceDescriptors()[2].getDisplayName()));
        }

    @Test
    public void noDeleteButtonForRequiredDescribedSubsource()
        {
        MockContainsDescribedSources container = new MockContainsDescribedSources();
        Platform.runLater(() -> _editor.setSource(container.getSubsources(), container.getSubsourceDescriptors()));
        waitForUiEvents();

        Assert.assertFalse(exists(ValueSourceMapEditor.getRemoveButtonId("param1")));
        }

    @Test
    public void removeOptionalDescribedSource()
        {
        MockContainsDescribedSources container = new MockContainsDescribedSources();
        container.getSubsources().addSource("optparam1", ValueSourceConfiguration.forValue("abc"));
        Platform.runLater(() -> _editor.setSource(container.getSubsources(), container.getSubsourceDescriptors()));
        waitForUiEvents();

        Assert.assertTrue(exists(id(ValueSourceMapEditor.getRemoveButtonId("optparam1"))));
        clickOn(id(ValueSourceMapEditor.getRemoveButtonId("optparam1")));
        waitForUiEvents();
        Assert.assertFalse("delete button should be gone", exists(id(ValueSourceMapEditor.getRemoveButtonId("optparam1"))));
        Assert.assertTrue("add button should appear", exists(id(ValueSourceMapEditor.getAddButtonId("optparam1"))));

        Assert.assertNull(container.getSubsources().getSource("optparam1"));
        }

    @Test
    public void hideAddButtonWhenCustomNotAllowed()
        {
        _editor.setAllowCustomSources(false);
        waitForUiEvents();

        Assert.assertFalse(exists(id(ValueSourceMapEditor.ADD_BUTTON_ID)));
        }

    @Test
    public void showChangeMadeInAdvancedEditorForDescribedSource()
        {
        MockContainsDescribedSources container = new MockContainsDescribedSources();
        final String value1 = "value1";
        container.getSubsources().addSource("param1", ValueSourceConfiguration.forValue(value1));
        Platform.runLater(() -> _editor.setSource(container.getSubsources(), container.getSubsourceDescriptors()));
        waitForUiEvents();

        // open advanced editor and make a change
        clickOn(id(ValueSourceMapEditor.getAdvancedLinkId("param1")));
        final String newval = "newval";
        fillFieldAndTabAway(quoted(value1), quoted(newval));
        clickOn(id(Buttons.SAVE_ID));
        waitForUiEvents();

        // check the change is reflected
        Assert.assertFalse("original value still showing", exists(quoted(value1)));
        Assert.assertTrue("new value not shown", exists(quoted(newval)));
        }

    private void setup()
        {
        _source = ValueSourceConfiguration.forType(LogMessage.TYPE_ID);
        _source.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue(MESSAGE));
        _source.addSource(NAME2, ValueSourceConfiguration.forValue(VALUE2));
        Platform.runLater(() -> _editor.setSource(_source));
        waitForUiEvents();
        }

    @Override
    protected Node createComponentNode()
        {
        _container = new StackPane();
        _editor = new ValueSourceMapEditor(new SimpleProject(), new UndoStack());
        _container.getChildren().add(_editor.getNode());
        return _container;
        }

    @Override
    protected double getDefaultHeight()
        {
        return 400;
        }

    private ValueSourceMapEditor _editor;
    private ValueSourceConfiguration _source;
    private StackPane _container;


    private static String VALUE2 = "value2";
    private static String NAME2 = "source2";
    private static String MESSAGE = "this is a message";

    @MuseSubsourceDescriptor(displayName = "Param 1", description = "A description of param 1", type = SubsourceDescriptor.Type.Named, name = "param1")
   	@MuseSubsourceDescriptor(displayName = "Opt Param1", description = "an optional parameter", type = SubsourceDescriptor.Type.Named, name = "optparam1", optional =  true)
   	@MuseSubsourceDescriptor(displayName = "Opt Param2", description = "second optional parameter", type = SubsourceDescriptor.Type.Named, name = "optparam2", optional =  true)
    class MockContainsDescribedSources
	    {
	    SubsourceDescriptor[] getSubsourceDescriptors()
		    {
		    SubsourceDescriptor[] descriptors = SubsourceDescriptor.getSubsourceDescriptors(this.getClass());
		    if (descriptors == null)
			    descriptors = new SubsourceDescriptor[0];
		    return descriptors;
		    }

	    ContainsNamedSources getSubsources()
		    {
		    return _subsources;
		    }

	    private NamedSourcesContainer _subsources = new NamedSourcesContainer();
	    }
    }
