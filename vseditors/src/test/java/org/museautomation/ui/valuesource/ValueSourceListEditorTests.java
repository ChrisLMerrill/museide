package org.museautomation.ui.valuesource;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
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
    void editSubsource()
        {
        ValueSourceConfiguration source = setupSimpleSource();
        fillFieldAndTabAway("#" + ValueSourceListEditor.getEditorId(0), "123");
        Assertions.assertEquals(123L, source.getSourceList().get(0).getValue());
        }

    @Test
    void addSubsources()
        {
        // create a source with no indexed subsources
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue("abc");
        _editor.setSource(source);
        waitForUiEvents();

        // add a new source
        clickOn("#" + ValueSourceListEditor.ADD_BUTTON_ID);
        Assertions.assertEquals(1, source.getSourceList().size());
        fillFieldAndTabAway("#" + ValueSourceListEditor.getEditorId(0), "123");
        Assertions.assertEquals(123L, source.getSourceList().get(0).getValue());

        // add a second source
        clickOn("#" + ValueSourceListEditor.ADD_BUTTON_ID);
        fillFieldAndTabAway("#" + ValueSourceListEditor.getEditorId(1), "456");
        Assertions.assertEquals(2, source.getSourceList().size());
        Assertions.assertEquals(456L, source.getSourceList().get(1).getValue());
        }

    @Test
    void removeFirstSubsource()
        {
        ValueSourceConfiguration source = setupSimpleSource();

        // delete the first source
        clickOn("#" + ValueSourceListEditor.getDeleteButtonId(0));
        Assertions.assertEquals(1, source.getSourceList().size());
        Assertions.assertEquals(SECOND_SOURCE_VALUE, source.getSource(0).getValue());
        }

    @Test
    void removeSecondSubsource()
        {
        ValueSourceConfiguration source = setupSimpleSource();

        // delete the second source
        clickOn("#" + ValueSourceListEditor.getDeleteButtonId(1));
        Assertions.assertEquals(1, source.getSourceList().size());
        Assertions.assertEquals(FIRST_SOURCE_VALUE, source.getSource(0).getValue());
        }

    @Test
    void removeTwoSubsources()
        {
        ValueSourceConfiguration source = setupSimpleSource();

        clickOn("#" + ValueSourceListEditor.getDeleteButtonId(0));
        clickOn("#" + ValueSourceListEditor.getDeleteButtonId(1));
        Assertions.assertNull(source.getSourceList());
        }

    @Test
    void editSubsourceAdvanced()
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
        Assertions.assertEquals("[0]", _pushed_name.get());
        // and an editor of the right type
        Assertions.assertTrue(_pushed_editor.get() instanceof MultimodeValueSourceEditor);
        }

    @Test
    void initialSubsourcesDisplayed()
        {
        setupSimpleSource();

        Node first_editor = lookup("#" + ValueSourceListEditor.getEditorId(0)).query();
        Assertions.assertEquals(quoted(FIRST_SOURCE_VALUE), ((TextInputControl)first_editor).getText());

        Node second_editor = lookup("#" + ValueSourceListEditor.getEditorId(1)).query();
        Assertions.assertEquals(quoted(SECOND_SOURCE_VALUE), ((TextInputControl)second_editor).getText());
        }

    @Test
    void removeSourceListener() throws InterruptedException
        {
        ValueSourceConfiguration source = setupSimpleSource();
        _editor.setSource(source);
        Assertions.assertEquals(1, source.getListeners().size());

        ComponentRemover.waitForRemoval(_container, _editor.getNode());

        // verify the value source no longer has a listener
        Assertions.assertEquals(0, source.getListeners().size());
        }

    @Test
    void validChecks()
        {
        setupSimpleSource();

        Assertions.assertTrue(_editor.isValid(), "not setup in a valid state");

        fillFieldAndTabAway("#" + ValueSourceListEditor.getEditorId(0), "123aaa");
        Assertions.assertFalse(_editor.isValid(), "should have changed to invalid");

        fillFieldAndTabAway("#" + ValueSourceListEditor.getEditorId(0), "123");
        Assertions.assertTrue(_editor.isValid(), "should have changed to back to valid");
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
    public Node createComponentNode()
        {
        _container = new StackPane();
        _editor = new ValueSourceListEditor(new SimpleProject(), new UndoStack());
        _container.getChildren().add(_editor.getNode());
        return _container;
        }

    @Override
    public double getDefaultHeight()
        {
        return 400;
        }

    private StackPane _container;
    private ValueSourceListEditor _editor;

    private final static String FIRST_SOURCE_VALUE = "value1";
    private final static String SECOND_SOURCE_VALUE = "value2";
    }