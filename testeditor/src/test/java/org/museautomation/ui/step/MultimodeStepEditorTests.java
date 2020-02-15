package org.museautomation.ui.step;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.selenium.steps.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.step.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MultimodeStepEditorTests extends ComponentTest
    {
    @Test
    public void editorModes()
        {
        // verify default editor is displayed initially
        Assert.assertNotNull(lookup("#" + DefaultStepEditor.DESCRIPTION_FIELD_ID).query());

        // switch to expert
        Node switch_link = lookup("#" + MultimodeStepEditor.SWITCH_TO_EXPERT_ID).query();
        Assert.assertNotNull(switch_link);
        clickOn(switch_link);

        // verify expert is showing
        Node field_on_expert_editor = lookup("#" + ExpertStepEditor.DESCRIPTION_FIELD_ID).query();
        Assert.assertNotNull(field_on_expert_editor);

        // switch back to default mode
        switch_link = lookup("#" + MultimodeStepEditor.SWITCH_TO_DEFAULT_ID).query();
        Assert.assertNotNull(switch_link);
        clickOn(switch_link);

        // verify default is showing
        Assert.assertNotNull(lookup("#" + DefaultStepEditor.DESCRIPTION_FIELD_ID).query());
        }

    @Before
    public void setupStep()
        {
        StepConfiguration step = new StepConfiguration(CloseBrowser.TYPE_ID);
        _editor = new MultimodeStepEditor(new RootStepEditContext(_project, new UndoStack(), null), step);
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
    private MultimodeStepEditor _editor;
    }
