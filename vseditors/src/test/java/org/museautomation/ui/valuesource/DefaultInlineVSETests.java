package org.museautomation.ui.valuesource;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.builtins.value.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.events.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.components.*;

import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused")
public class DefaultInlineVSETests extends ComponentTest
    {
    @Test
    void initiallyDisplayNullSource()
        {
        TextField field = lookup("#" + DefaultInlineVSE.TEXT_ID).query();
        waitForUiEvents();
        Assertions.assertEquals("", field.getText());
        Assertions.assertTrue(field.isEditable());
        Assertions.assertTrue(InputValidation.isShowingError(field), "should indicate invalid input");
        }

    @Test
    void workWithEmptySource()
        {
        ValueSourceConfiguration source = new ValueSourceConfiguration();
        _editor.setSource(source);
        waitForUiEvents();
        TextField field = lookup("#" + DefaultInlineVSE.TEXT_ID).query();
        waitForUiEvents();
        Assertions.assertEquals("", field.getText());
        Assertions.assertTrue(field.isEditable());
        Assertions.assertTrue(InputValidation.isShowingError(field), "should indicate invalid input");

        AtomicReference<ValueChangeEvent> event_notified = new AtomicReference<>(null);
        source.addChangeListener(new ValueSourceChangeObserver()
            {
            @Override
            public void valueChanged(ValueChangeEvent event, Object old_value, Object new_value)
                {
                event_notified.set(event);
                }
            });

        final String text = "some text";
        fillFieldAndTabAway("#" + DefaultInlineVSE.TEXT_ID, quoted(text));
        waitForUiEvents();

        Assertions.assertNotNull(event_notified.get());
        Assertions.assertEquals(text, _editor.getSource().getValue());
        }

    @Test
    void setSource()
        {
        final String text = "some text";
        _editor.setSource(ValueSourceConfiguration.forValue(text));
        waitForUiEvents();

        TextField field = lookup("#" + DefaultInlineVSE.TEXT_ID).query();
        Assertions.assertTrue(field.isEditable());
        Assertions.assertEquals(quoted(text), field.getText());
        }

    @Test
    void changeSource()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue("blah blah");
        _editor.setSource(source);
        waitForUiEvents();

        AtomicReference<ValueChangeEvent> event_notified = new AtomicReference<>(null);
        source.addChangeListener(new ValueSourceChangeObserver()
            {
            @Override
            public void valueChanged(ValueChangeEvent event, Object old_value, Object new_value)
                {
                event_notified.set(event);
                }
            });

        final String text = "some text";
        fillFieldAndTabAway("#" + DefaultInlineVSE.TEXT_ID, quoted(text));
        waitForUiEvents();

        Assertions.assertNotNull(event_notified.get());
        Assertions.assertEquals(text, _editor.getSource().getValue());
        }

    @Test
    void removeSourceListener() throws InterruptedException
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forValue("any old source will do");
        _editor.setSource(source);
        Assertions.assertEquals(1, source.getListeners().size());

        ComponentRemover.waitForRemoval(_container, _editor.getNode());

        // verify the value source no longer has a listener
        Assertions.assertEquals(0, source.getListeners().size());
        }

    /**
     * For a source that is not text-editable (not ValueSourceStringExpressionSupporter)
     */
    @Test
    void nonEditableSource()
        {
        ValueSourceConfiguration source = ValueSourceConfiguration.forType(NameValuePairSource.TYPE_ID);
        source.addSource(NameValuePairSource.NAME_PARAM, ValueSourceConfiguration.forValue("name1"));
        source.addSource(NameValuePairSource.VALUE_PARAM, ValueSourceConfiguration.forValue("value1"));
        _editor.setSource(source);

        TextField field = lookup("#" + DefaultInlineVSE.TEXT_ID).query();
        Assertions.assertFalse(field.isEditable());
//        Assert.assertTrue(field.getText().contains("name1"));
//        Assert.assertTrue(field.getText().contains("value1"));
        }

    @Override
    public Node createComponentNode()
        {
        _container = new BorderPane();
        _editor = new DefaultInlineVSE(new SimpleProject(), new UndoStack());
        _container.setCenter(_editor.getNode());
        _container.setBottom(new Button("no-op"));
        return _container;
        }

    private DefaultInlineVSE _editor;
    private BorderPane _container;
    }