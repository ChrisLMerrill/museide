package org.museautomation.ui.valuesource;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.museautomation.ui.valuesource.list.*;
import org.museautomation.builtins.value.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.stack.*;

import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceListEditorTests extends ComponentTest
    {
    @Test
    public void editSubsource()
        {
        ValueSourceConfiguration source = setupSimpleSource();
        fillFieldAndTabAway("#" + ValueSourceListEditor.getEditorId(0), "123");
        Assert.assertEquals(123L, source.getSourceList().get(0).getValue());
        }

    @Test
    public void addSubsources()
        {
        // create a source with no indexed subsources
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue("abc");
        _editor.setSource(source);
        waitForUiEvents();

        // add a new source
        clickOn("#" + ValueSourceListEditor.ADD_BUTTON_ID);
        Assert.assertEquals(1, source.getSourceList().size());
        fillFieldAndTabAway("#" + ValueSourceListEditor.getEditorId(0), "123");
        Assert.assertEquals(123L, source.getSourceList().get(0).getValue());

        // add a second source
        clickOn("#" + ValueSourceListEditor.ADD_BUTTON_ID);
        fillFieldAndTabAway("#" + ValueSourceListEditor.getEditorId(1), "456");
        Assert.assertEquals(2, source.getSourceList().size());
        Assert.assertEquals(456L, source.getSourceList().get(1).getValue());
        }

    @Test
    public void removeFirstSubsource()
        {
        ValueSourceConfiguration source = setupSimpleSource();

        // delete the first source
        clickOn("#" + ValueSourceListEditor.getDeleteButtonId(0));
        Assert.assertEquals(1, source.getSourceList().size());
        Assert.assertEquals(SECOND_SOURCE_VALUE, source.getSource(0).getValue());
        }

    @Test
    public void removeSecondSubsource()
        {
        ValueSourceConfiguration source = setupSimpleSource();

        // delete the second source
        clickOn("#" + ValueSourceListEditor.getDeleteButtonId(1));
        Assert.assertEquals(1, source.getSourceList().size());
        Assert.assertEquals(FIRST_SOURCE_VALUE, source.getSource(0).getValue());
        }

    @Test
    public void removeTwoSubsources()
        {
        ValueSourceConfiguration source = setupSimpleSource();

        clickOn("#" + ValueSourceListEditor.getDeleteButtonId(0));
        clickOn("#" + ValueSourceListEditor.getDeleteButtonId(1));
        Assert.assertNull(source.getSourceList());
        }

    @Test
    public void editSubsourceAdvanced()
        {
        setupSimpleSource();

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

        // go to expert editor for first sub-source
        clickOn("#" + ValueSourceListEditor.getAdvancedLinkId(0));

        // verify the expected name was sent
        Assert.assertEquals("[0]", _pushed_name.get());
        // and an editor of the right type
        Assert.assertTrue(_pushed_editor.get() instanceof MultimodeValueSourceEditor);
        }

    @Test
    public void initialSubsourcesDisplayed()
        {
        setupSimpleSource();

        Node first_editor = lookup("#" + ValueSourceListEditor.getEditorId(0)).query();
        Assert.assertEquals(quoted(FIRST_SOURCE_VALUE), ((TextInputControl)first_editor).getText());

        Node second_editor = lookup("#" + ValueSourceListEditor.getEditorId(1)).query();
        Assert.assertEquals(quoted(SECOND_SOURCE_VALUE), ((TextInputControl)second_editor).getText());
        }

    @Test
    public void removeSourceListener() throws InterruptedException
        {
        ValueSourceConfiguration source = setupSimpleSource();
        _editor.setSource(source);
        Assert.assertEquals(1, source.getListeners().size());

        ComponentRemover.waitForRemoval(_container, _editor.getNode());

        // verify the value source no longer has a listener
        Assert.assertEquals(0, source.getListeners().size());
        }

    @Test
    public void validChecks()
        {
        setupSimpleSource();

        Assert.assertTrue("not setup in a valid state", _editor.isValid());

        fillFieldAndTabAway("#" + ValueSourceListEditor.getEditorId(0), "123aaa");
        Assert.assertFalse("should have changed to invalid", _editor.isValid());

        fillFieldAndTabAway("#" + ValueSourceListEditor.getEditorId(0), "123");
        Assert.assertTrue("should have changed to back to valid", _editor.isValid());
        }

    private ValueSourceConfiguration setupSimpleSource()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(AdditionSource.TYPE_ID);
        source.addSource(0, ValueSourceConfiguration.forValue(FIRST_SOURCE_VALUE));
        source.addSource(1, ValueSourceConfiguration.forValue(SECOND_SOURCE_VALUE));

        Platform.runLater(() -> _editor.setSource(source));
        waitForUiEvents();

        return source;
        }

    @Override
    protected Node createComponentNode()
        {
        _container = new StackPane();
        _editor = new ValueSourceListEditor(new SimpleProject(), new UndoStack());
        _container.getChildren().add(_editor.getNode());
        return _container;
        }

    @Override
    protected double getDefaultHeight()
        {
        return 400;
        }

    private StackPane _container;
    private ValueSourceListEditor _editor;

    private final static String FIRST_SOURCE_VALUE = "value1";
    private final static String SECOND_SOURCE_VALUE = "value2";
    }


