package org.museautomation.ui.valuesource;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
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
    void initialParametersDisplayed()
        {
        setup();
        Assertions.assertTrue(exists(id(InlineNamedVSE.getNameFieldId(LogMessage.MESSAGE_PARAM))));
        Assertions.assertEquals(LogMessage.MESSAGE_PARAM, textOf(id(InlineNamedVSE.getNameFieldId(LogMessage.MESSAGE_PARAM))));

        Assertions.assertTrue(exists(id(InlineNamedVSE.getValueFieldId(LogMessage.MESSAGE_PARAM))));
        Assertions.assertEquals(quoted(MESSAGE), textOf(id(InlineNamedVSE.getValueFieldId(LogMessage.MESSAGE_PARAM))));

        Assertions.assertTrue(exists(NAME2));
        Assertions.assertTrue(exists(quoted(VALUE2)));
        }

    @Test
    void changeName()
        {
        setup();

        final String new_name = "new_name";
        fillFieldAndTabAway(id(InlineNamedVSE.getNameFieldId(LogMessage.MESSAGE_PARAM)), new_name);

        Assertions.assertNotNull(_source.getSource(new_name));
        Assertions.assertNull(_source.getSource("source"));
        }

    @Test
    void changeValue()
        {
        setup();

        final String new_value = "new_value";
        fillFieldAndTabAway(id(InlineNamedVSE.getValueFieldId(LogMessage.MESSAGE_PARAM)), quoted(new_value));

        Assertions.assertEquals(new_value, _source.getSource(LogMessage.MESSAGE_PARAM).getValue());
        }

    @Test
    void preventNameChangeToDuplicate()
        {
        setup();
        waitForUiEvents();

        Node name_field = lookup(NAME2).query();
        fillFieldAndTabAway(name_field, LogMessage.MESSAGE_PARAM);

        Assertions.assertNotNull(_source.getSource(NAME2));  // name was not changed
        Assertions.assertTrue(InputValidation.isShowingError(name_field));
        Assertions.assertFalse(_editor.isValid());
        }

    @Test
    void addSource()
        {
        setup();
        clickOn("#" + ValueSourceMapEditor.ADD_BUTTON_ID);
        Assertions.assertNotNull(_source.getSource("name1"));
        Assertions.assertTrue(exists(quoted(_source.getSource("name1").getValue())));
        }

    @Test
    void removeFirstSource()
        {
        setup();
        clickOn("#" + ValueSourceMapEditor.getRemoveButtonId(LogMessage.MESSAGE_PARAM));

        Assertions.assertNull(_source.getSource(LogMessage.MESSAGE_PARAM));       // was deleted from source
        Assertions.assertFalse(exists(LogMessage.MESSAGE_PARAM));        // was removed from UI
        Assertions.assertFalse(exists(id(ValueSourceMapEditor.getRemoveButtonId(LogMessage.MESSAGE_PARAM))));
        Assertions.assertFalse(exists(id(ValueSourceMapEditor.getAdvancedLinkId(LogMessage.MESSAGE_PARAM))));
        }

    @Test
    void removeLastSource()
        {
        setup();
        clickOn(id(ValueSourceMapEditor.getRemoveButtonId(NAME2)));
        waitForUiEvents();

        Assertions.assertNull(_source.getSource(NAME2));       // was deleted from source
        Assertions.assertFalse(exists(NAME2));        // was removed from UI
        Assertions.assertFalse(exists(id(ValueSourceMapEditor.getRemoveButtonId(NAME2))));
        Assertions.assertFalse(exists(id(ValueSourceMapEditor.getAdvancedLinkId(NAME2))));
        }

    @Test
    void validInvalidTransitions()
        {
        setup();

        // starts out valid
        Assertions.assertTrue(_editor.isValid());

        // make it invalid
        fillFieldAndTabAway(id(InlineNamedVSE.getValueFieldId(LogMessage.MESSAGE_PARAM)), "invalid");
        Assertions.assertFalse(_editor.isValid());

        // return it to valid
        fillFieldAndTabAway(id(InlineNamedVSE.getValueFieldId(LogMessage.MESSAGE_PARAM)), "123");
        Assertions.assertTrue(_editor.isValid());
        }

    @Test
    void useAdvancedEditor()
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
        Assertions.assertEquals(LogMessage.MESSAGE_PARAM, _pushed_name.get());
        // and an editor of the right type
        Assertions.assertTrue(_pushed_editor.get() instanceof MultimodeValueSourceEditor);
        }

    @Test
    void showAdvancedEditorAfterChange()
        {
        setup();

        // click on the more button
        clickOn("#" + ValueSourceMapEditor.getAdvancedLinkId(NAME2));

        // check the initial value shown
        Assertions.assertTrue(exists(quoted(VALUE2)), "original value not shown");

        // close advanced editor
        clickOn(id(Buttons.CANCEL_ID));

        // make a change
        final String newval = quoted("newval");
        fillFieldAndTabAway(quoted(VALUE2), newval);

        // click on the more button
        clickOn("#" + ValueSourceMapEditor.getAdvancedLinkId(NAME2));

        Assertions.assertFalse(exists(quoted(VALUE2)), "original value still showing");
        Assertions.assertTrue(exists(newval), "change value not shown");
        }

    @Test
    void showChangeMadeInAdvancedEditor()
        {
        setup();

        // open advanced editor and make a change
        clickOn(id(ValueSourceMapEditor.getAdvancedLinkId(NAME2)));
        final String newval = "newval";
        fillFieldAndTabAway(quoted(VALUE2), quoted(newval));
        clickOn(id(Buttons.SAVE_ID));
        waitForUiEvents();

        // check the change is reflected
        Assertions.assertFalse(exists(quoted(VALUE2)), "original value still showing");
        Assertions.assertTrue(exists(quoted(newval)), "new value not shown");
        }

    @Test
    void removeSourceListener() throws InterruptedException
        {
        setup();
        Assertions.assertEquals(1, _source.getListeners().size());

        ComponentRemover.waitForRemoval(_container, _editor.getNode());

        // verify the value source no longer has a listener
        Assertions.assertEquals(0, _source.getListeners().size());
        }

    @Test
    void hideParamsByName()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(CallFunction.TYPE_ID);
        source.addSource(CallFunction.ID_PARAM, ValueSourceConfiguration.forValue("function-id"));
        source.addSource("param1", ValueSourceConfiguration.forValue("param2"));
        _editor.hideSourceNamed(CallFunction.ID_PARAM);
        Platform.runLater(() -> _editor.setSource(source));
        waitForUiEvents();

        Assertions.assertFalse(exists(id(InlineNamedVSE.getValueFieldId(CallFunction.ID_PARAM))), "hidden name is not hidden");
        }

    @Test
    void showDescribedSource()
        {
        MockContainsDescribedSources container = new MockContainsDescribedSources();
        container.getSubsources().addSource("param1", ValueSourceConfiguration.forValue("value1"));
        container.getSubsources().addSource("optparam1", ValueSourceConfiguration.forValue("optvalue1"));
        Platform.runLater(() -> _editor.setSource(container.getSubsources(), container.getSubsourceDescriptors()));
        waitForUiEvents();

        Assertions.assertTrue(exists(container.getSubsourceDescriptors()[0].getDisplayName()), "Param1 name not displayed");
        Assertions.assertTrue(exists(quoted("value1")), "Param1 value not displayed");
        Assertions.assertFalse(exists(id(ValueSourceMapEditor.getRemoveButtonId("param1"))));

        Assertions.assertTrue(exists(container.getSubsourceDescriptors()[1].getDisplayName()), "Opt Param1 name not displayed");
        Assertions.assertTrue(exists(quoted("value1")), "Opt Param1 value not displayed");
        Assertions.assertTrue(exists(id(ValueSourceMapEditor.getRemoveButtonId("optparam1"))));

        Assertions.assertTrue(exists(container.getSubsourceDescriptors()[2].getDisplayName()), "Opt Param2 name not displayed");
        }

    @Test
    void noDeleteButtonForRequiredDescribedSubsource()
        {
        MockContainsDescribedSources container = new MockContainsDescribedSources();
        Platform.runLater(() -> _editor.setSource(container.getSubsources(), container.getSubsourceDescriptors()));
        waitForUiEvents();

        Assertions.assertFalse(exists(ValueSourceMapEditor.getRemoveButtonId("param1")));
        }

    @Test
    void removeOptionalDescribedSource()
        {
        MockContainsDescribedSources container = new MockContainsDescribedSources();
        container.getSubsources().addSource("optparam1", ValueSourceConfiguration.forValue("abc"));
        Platform.runLater(() -> _editor.setSource(container.getSubsources(), container.getSubsourceDescriptors()));
        waitForUiEvents();

        Assertions.assertTrue(exists(id(ValueSourceMapEditor.getRemoveButtonId("optparam1"))));
        clickOn(id(ValueSourceMapEditor.getRemoveButtonId("optparam1")));
        waitForUiEvents();
        Assertions.assertFalse(exists(id(ValueSourceMapEditor.getRemoveButtonId("optparam1"))), "delete button should be gone");
        Assertions.assertTrue(exists(id(ValueSourceMapEditor.getAddButtonId("optparam1"))), "add button should appear");

        Assertions.assertNull(container.getSubsources().getSource("optparam1"));
        }

    @Test
    void hideAddButtonWhenCustomNotAllowed()
        {
        _editor.setAllowCustomSources(false);
        waitForUiEvents();

        Assertions.assertFalse(exists(id(ValueSourceMapEditor.ADD_BUTTON_ID)));
        }

    @Test
    void showChangeMadeInAdvancedEditorForDescribedSource()
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
        Assertions.assertFalse(exists(quoted(value1)), "original value still showing");
        Assertions.assertTrue(exists(quoted(newval)), "new value not shown");
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
    static class MockContainsDescribedSources
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