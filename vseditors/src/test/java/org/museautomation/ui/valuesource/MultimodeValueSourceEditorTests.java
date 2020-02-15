package org.museautomation.ui.valuesource;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.museautomation.builtins.value.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MultimodeValueSourceEditorTests extends ComponentTest
    {
    @Test
    public void editorModes()
        {
        // verify default editor is displayed initially
        Assert.assertNotNull(lookup("#" + DefaultValueSourceEditor.TYPE_FIELD_ID).query());
// TODO this should only be the case if a default editor is supported

        // switch to expert
        Node switch_link = lookup("#" + MultimodeValueSourceEditor.SWITCH_TO_EXPERT_ID).query();
        Assert.assertNotNull(switch_link);
        clickOn(switch_link);

        // verify expert is showing
        Node field_on_expert_editor = lookup("#" + ExpertValueSourceEditor.TYPE_FIELD_ID).query();
        Assert.assertNotNull(field_on_expert_editor);

        // switch back to default mode
        switch_link = lookup("#" + MultimodeValueSourceEditor.SWITCH_TO_DEFAULT_ID).query();
        Assert.assertNotNull(switch_link);
        clickOn(switch_link);

        // verify default is showing
        Assert.assertNotNull(lookup(id(DefaultValueSourceEditor.TYPE_FIELD_ID)).query());
        // and the type is correct
        Assert.assertEquals(_project.getValueSourceDescriptors().get(_editor.getSource()).getName(), textOf(id(DefaultValueSourceEditor.TYPE_FIELD_ID)));
        }

    @Before
    public void setupStep()
        {
        _editor = new MultimodeValueSourceEditor(_project, new UndoStack());
        _editor.setSource(ValueSourceConfiguration.forType(StringValueSource.TYPE_ID));
        _node.getChildren().removeAll();
        Platform.runLater(() -> _node.getChildren().add(_editor.getNode()) );

        waitForUiEvents();
        }

    @Override
    protected Node createComponentNode()
        {
        return _node;
        }

    private StackPane _node = new StackPane();
    private SimpleProject _project = new SimpleProject();
    private MultimodeValueSourceEditor _editor;
    }


