package org.museautomation.ui.steptask;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.steptask.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.metadata.*;
import org.museautomation.ui.extend.edit.tags.*;

import static org.museautomation.ui.steptask.MetadataTab.DESCRIPTION_FIELD_ID;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MetadataTabTests extends ComponentTest
    {
    @Test
    void showTags()
        {
        Assertions.assertTrue(exists("tag1"));
        Assertions.assertTrue(exists("tag2"));
        }

    @Test
    void addTag()
        {
        clickOn(id(TagsEditor.ADD_BUTTON_ID));
        waitForUiEvents();
        fillFieldAndPressEnter(id(TagsEditor.ADD_FIELD_ID), "tag3");
        waitForUiEvents();
        Assertions.assertTrue(exists("tag1"));
        Assertions.assertTrue(exists("tag2"));
        Assertions.assertTrue(exists("tag3"));
        
        Assertions.assertEquals(3, _task.getTags().size());
        Assertions.assertTrue(_task.tags().hasTag("tag3"));

        _stack.undoLastAction();
        waitForUiEvents();
        Assertions.assertEquals(2, _task.getTags().size());
        Assertions.assertTrue(exists("tag1"));
        Assertions.assertTrue(exists("tag2"));
        Assertions.assertFalse(_task.tags().hasTag("tag3"));
        }

    @Test
    void deleteTags()
        {
        waitForUiEvents();
        clickOn(id(TagsEditor.DELETE_ID));
        waitForUiEvents();
        Assertions.assertFalse(exists("tag1"));
        Assertions.assertTrue(exists("tag2"));
        Assertions.assertEquals(1, _task.getTags().size());
        Assertions.assertFalse(_task.tags().hasTag("tag1"));

        clickOn(id(TagsEditor.DELETE_ID));
        waitForUiEvents();
        Assertions.assertFalse(exists("tag1"));
        Assertions.assertFalse(exists("tag2"));
        Assertions.assertEquals(0, _task.getTags().size());

        _stack.undoLastAction();
        waitForUiEvents();
        Assertions.assertFalse(exists("tag1"));
        Assertions.assertTrue(exists("tag2"));
        Assertions.assertEquals(1, _task.getTags().size());
        Assertions.assertFalse(_task.tags().hasTag("tag1"));
        Assertions.assertTrue(_task.tags().hasTag("tag2"));

        _stack.undoLastAction();
        waitForUiEvents();
        Assertions.assertTrue(exists("tag1"));
        Assertions.assertTrue(exists("tag2"));
        Assertions.assertEquals(2, _task.getTags().size());
        Assertions.assertTrue(_task.tags().hasTag("tag1"));
        Assertions.assertTrue(_task.tags().hasTag("tag2"));
        }

    @Test
    public void showAttributes()
        {
        waitForUiEvents();
        Assertions.assertTrue(exists("field1=value1"));
        Assertions.assertTrue(exists("field2=2"));
        }

    @Test
    public void addAttribute()
        {
        waitForUiEvents();
        clickOn(withStyle(MetadataEditor.ADD_BUTTON_STYLE));
        waitForUiEvents();
        fillFieldAndPressEnter(withStyle(MetadataEditor.ADD_FIELD_SYTLE), "value3=true");
        waitForUiEvents();

        Assertions.assertTrue(exists("field1=value1"));
        Assertions.assertTrue(exists("field2=2"));
        Assertions.assertTrue(exists("value3=true"));
        Assertions.assertEquals(true, _task.metadata().getMetadataField("value3"));

        _stack.undoLastAction();
        waitForUiEvents();
        Assertions.assertFalse(exists("value3=true"));
        Assertions.assertNull(_task.metadata().getMetadataField("value3"));
        }

    @Test
    public void removeAttribute()
        {
        waitForUiEvents();
        clickOn(withStyle(MetadataLabel.REMOVE_BUTTON_CLASS));
        waitForUiEvents();

        Assertions.assertFalse(exists("field1=value1"));
        Assertions.assertTrue(exists("field2=2"));
        Assertions.assertNull(_task.metadata().getMetadataField("field1"));

        _stack.undoLastAction();
        waitForUiEvents();
        Assertions.assertTrue(exists("field1=value1"));
        Assertions.assertEquals("value1", _task.metadata().getMetadataField("field1"));
        }

    @Test
    public void changeDescription()
        {
        Assertions.assertTrue(exists(DEFAULT_DESCRIPTION));

        clickOn(id(DESCRIPTION_FIELD_ID));
        clearText(id(DESCRIPTION_FIELD_ID));
        clickOn(_other_field);
        Assertions.assertNull(_task.metadata().getMetadataField(MetadataTab.METADATA_DESCRIPTION));

        final String new_description = "this is new";
        clickOn(id(DESCRIPTION_FIELD_ID));
        fillField(id(DESCRIPTION_FIELD_ID), new_description);
        clickOn(_other_field);
        Assertions.assertEquals(new_description, _task.metadata().getMetadataField(MetadataTab.METADATA_DESCRIPTION));

        final String newer_description = "new and improved!";
        clickOn(id(DESCRIPTION_FIELD_ID));
        fillField(id(DESCRIPTION_FIELD_ID), newer_description);
        clickOn(_other_field);
        Assertions.assertEquals(newer_description, _task.metadata().getMetadataField(MetadataTab.METADATA_DESCRIPTION));

        _stack.undoLastAction();
        waitForUiEvents();
        Assertions.assertEquals(new_description, _task.metadata().getMetadataField(MetadataTab.METADATA_DESCRIPTION));
        Assertions.assertEquals(new_description, textOf(id(DESCRIPTION_FIELD_ID)));

        _stack.undoLastAction();
        waitForUiEvents();
        Assertions.assertNull(_task.metadata().getMetadataField(MetadataTab.METADATA_DESCRIPTION));
        Assertions.assertEquals("", textOf(id(DESCRIPTION_FIELD_ID)));

        _stack.undoLastAction();
        waitForUiEvents();
        Assertions.assertEquals(DEFAULT_DESCRIPTION, _task.metadata().getMetadataField(MetadataTab.METADATA_DESCRIPTION));
        Assertions.assertEquals(DEFAULT_DESCRIPTION, textOf(id(DESCRIPTION_FIELD_ID)));

        final String final_description = "this is the last!";
        clickOn(id(DESCRIPTION_FIELD_ID));
        fillField(id(DESCRIPTION_FIELD_ID), final_description);
        clickOn(_other_field);
        Assertions.assertEquals(final_description, _task.metadata().getMetadataField(MetadataTab.METADATA_DESCRIPTION));
        }

    @BeforeEach
    void setup()
        {
        _task = new SteppedTask();
        _task.tags().addTag("tag1");
        _task.tags().addTag("tag2");
        _task.metadata().setMetadataField("field1", "value1");
        _task.metadata().setMetadataField("field2", 2);
        _task.metadata().setMetadataField(MetadataTab.METADATA_DESCRIPTION, DEFAULT_DESCRIPTION);
        _tab.setsetResource(_task);
        }

    @Override
    public Node createComponentNode()
        {
        _other_field = new TextField("Something to receive focus.");
        _stack = new UndoStack();
        _tab = new MetadataTab(_stack);

        TabPane tabber = new TabPane(_tab.getTab());

        BorderPane borders = new BorderPane();
        borders.setCenter(tabber);
        borders.setBottom(_other_field);

        return borders;
        }

    private MetadataTab _tab;
    private SteppedTask _task;
    private UndoStack _stack;
    private TextField _other_field;
    private final static String DEFAULT_DESCRIPTION = "description of the step";
    }