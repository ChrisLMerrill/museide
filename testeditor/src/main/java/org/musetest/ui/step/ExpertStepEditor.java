package org.musetest.ui.step;

import com.google.common.base.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.step.*;
import org.musetest.ui.step.actions.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.edit.metadata.*;
import org.musetest.ui.extend.edit.stack.*;
import org.musetest.ui.extend.edit.step.*;
import org.musetest.ui.valuesource.map.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExpertStepEditor implements StackableEditor
    {
    ExpertStepEditor(StepEditContext context, StepConfiguration step)
        {
        _step = step;

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 20));
        grid.setHgap(5);
        grid.setVgap(5);

        grid.add(new Label("Description"), 0, 0);
        _description = new TextField();
        _description.setId(DESCRIPTION_FIELD_ID);
        _description.setPromptText("enter a custom step description");
        _description.focusedProperty().addListener((observable, oldValue, newValue) ->
            {
            Object original = _step.getMetadataField(StepConfiguration.META_DESCRIPTION);
            if (!Objects.equal(_description.getText(), original))
                changeDescription();
            });
        grid.add(_description, 1, 0);
        GridPane.setHgrow(_description, Priority.ALWAYS);

        grid.add(new Label("Tags"), 0, 1);
        _tags = new StepTagEditor(context);
        grid.add(_tags.getNode(), 1, 1);
        grid.add(new Label("Attributes"), 0, 2);
        _metadata = new MetadataEditor();
        _metadata.setAddListener((name, value) -> new AddMetadataAction(_step, name, value).execute(context.getUndo()));
        _metadata.setRemoveListener((name, value) -> new RemoveMetadataAction(_step, name).execute(context.getUndo()));
        grid.add(_metadata.getNode(), 1, 2);

        grid.add(new Label("Step Type"), 0, 3);
        _type_chooser = new StepTypeEditor(context.getProject());
        _type_chooser.getButton().setId(TYPE_FIELD_ID);
        _type_chooser.setChangeHandler(this::changeType);
        grid.add(_type_chooser.getButton(), 1, 3);

        _map_editor = new ValueSourceMapEditor(context.getProject(), context.getUndo());
        _map_editor.setStack(_stack);
        grid.add(_map_editor.getNode(), 0, 4, 2, 1);
        _root = grid;
        }

    @Override
    public Node getNode()
        {
        return _root;
        }

    private void changeDescription()
        {
        new ChangeStepMetadataAction(_step, _description.getText()).execute(getUndoStack());
        }

    private void changeType(String type)
        {
        new ChangeStepTypeAction(_step, type).execute(getUndoStack());
        }

    private UndoStack getUndoStack()
        {
        return _stack.getUndoStack();
        }

    public void addModeSwitchLink(Node link)
        {
        GridPane.setHalignment(link, HPos.RIGHT);
        _root.add(link, 1, 5, 1, 1);
        }

    @Override
    public void setStack(EditorStack stack)
        {
        _stack = stack;
        _map_editor.setStack(_stack);
        }

    @Override
    public void requestFocus()
        {
        _description.requestFocus();
        }

    @Override
    public void activate()
        {
        StepConfiguration step = _step;
        _description.setText((String) step.getMetadataField(StepConfiguration.META_DESCRIPTION));
        _tags.setStep(step);
        _step_meta_adapter = new StepMetadataAdapter(step);
        _metadata.setMetadata(_step_meta_adapter);
        _type_chooser.setType(step.getType());
        _map_editor.setSource(step);
        }

    @Override
    public boolean isValid()
        {
        return _map_editor.isValid();
        }

    public void destroy()
	    {
	    _tags.setStep(null);
	    _step_meta_adapter.destroy();
	    }

    private final StepConfiguration _step;

    private GridPane _root;
    private EditorStack _stack;
    private TextField _description;
    private StepTagEditor _tags;
    private StepMetadataAdapter _step_meta_adapter;
    private MetadataEditor _metadata;
    private StepTypeEditor _type_chooser;
    private ValueSourceMapEditor _map_editor;

    public final static String DESCRIPTION_FIELD_ID = "description";
    public final static String TYPE_FIELD_ID = "type";
    }
