package org.museautomation.ui.steptask;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.core.*;
import org.museautomation.core.metadata.*;
import org.museautomation.core.step.events.*;
import org.museautomation.core.util.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.metadata.*;
import org.museautomation.ui.extend.edit.tags.*;
import org.museautomation.ui.step.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MetadataTab
    {
    public MetadataTab(UndoStack stack)
        {
        _stack = stack;
        _grid.setPadding(new Insets(10, 10, 10, 20));
        _grid.setHgap(10);
        _grid.setVgap(10);
        _tab.setContent(_grid);
        _tab.setClosable(false);
        _tab.setText("Metadata");
        layoutControls();
        _tags_editor.setAddListener(this::addTag);
        _tags_editor.setDeleteListener(this::deleteTag);

        _meta_editor.setAddListener((name, value) -> new AddMetadataAction(_resource.metadata(), name, value).execute(_stack));
        _meta_editor.setRemoveListener((name, value) -> new RemoveMetadataAction(_resource.metadata(), name).execute(_stack));

        _text.setId(DESCRIPTION_FIELD_ID);
        _text.focusedProperty().addListener((observable, old_val, new_val) ->
            {
            if (old_val && !new_val)
                {
                String newdesc = _text.getText().length() > 0 ? _text.getText() : null;
                String olddesc = _last_description.length() > 0 ? _last_description : null;
                if (Objects.equals(olddesc, newdesc))
                    return;
                UndoableAction action;
                if (newdesc == null)
                    action = new RemoveMetadataAction(_resource.metadata(), METADATA_DESCRIPTION);
                else
                    action = new AddMetadataAction(_resource.metadata(), METADATA_DESCRIPTION, newdesc);
                runMetadataChangeAction(action);
                }
            });
        }

    private void layoutControls()
        {
        _grid.getChildren().clear();

        int row = 0;
        _grid.add(new Label("Tags"), 0, row);
        _grid.add(_tags_editor.getNode(), 1, row);
        GridPane.setFillWidth(_tags_editor.getNode(), true);

        row = 1;
        _grid.add(new Label("Attributes"), 0, row);
        _grid.add(_meta_editor.getNode(), 1, row);
        GridPane.setFillWidth(_meta_editor.getNode(), true);

        row = 2;
        Label description_label = new Label("Description");
        GridPane.setValignment(description_label, VPos.TOP);
        _grid.add(description_label, 0, row);
        _text.setPrefRowCount(15);
        _text.setPrefColumnCount(20);
        GridPane.setFillWidth(_text, true);
        GridPane.setHgrow(_text, Priority.ALWAYS);
        _grid.add(_text, 1, row);
        }

    public Tab getTab()
        {
        return _tab;
        }

    public void setsetResource(MuseResource resource)
        {
        _resource = resource;
        _tags_editor.setTags(resource);
        _resource.tags().addListener(_tag_listener);
        _meta_editor.setMetadata(_resource.metadata());

        Object description = _resource.metadata().getMetadataField(METADATA_DESCRIPTION);
        if (description != null)
            {
            _last_description = description.toString();
            _text.textProperty().set(_last_description);
            }

        setupMetadataListener();
        }

    private void setupMetadataListener()
        {
        _resource.metadata().addChangeListener(_metadata_listener);
        }

    private void removeMetadataListener()
        {
        _resource.metadata().removeChangeListener(_metadata_listener);
        }

    private void runMetadataChangeAction(UndoableAction action)
        {
        removeMetadataListener();
        action.execute(_stack);
        setupMetadataListener();
        }

    private void addTag(String tag)
        {
        new AddTagAction(_resource, tag).execute(_stack);
        }

    private void deleteTag(String tag)
        {
        new RemoveTagAction(_resource, tag).execute(_stack);
        }

    private MuseResource _resource;
    private UndoStack _stack;
    private Tab _tab = new Tab();
    private GridPane _grid = new GridPane();
    private TagsEditor _tags_editor = new TagsEditor();
    private MetadataEditor _meta_editor = new MetadataEditor();
    private TextArea _text = new TextArea();
    private String _last_description = "";
    private TagContainer.TagChangeListener _tag_listener = new TagContainer.TagChangeListener()
        {
        @Override
        public void tagAdded(String tag)
            {
            _tags_editor.refresh();
            }

        @Override
        public void tagRemoved(String tag)
            {
            _tags_editor.refresh();
            }
        };

    private ChangeEventListener _metadata_listener = new ChangeEventListener()
        {
        @Override
        public void changeEventRaised(ChangeEvent e)
            {
            if (_resource.metadata() == e.getTarget() && e instanceof MetadataChangeEvent)
                {
                MetadataChangeEvent event = (MetadataChangeEvent) e;
                if (METADATA_DESCRIPTION.equals(event.getName()))
                    {
                    Object value = event.getNewValue();
                    if (value == null)
                        _last_description = "";
                    else
                        _last_description = value.toString();
                    _text.textProperty().set(_last_description);
                    }
                }
            }
        };

    final static String METADATA_DESCRIPTION = "_description";
    final static String DESCRIPTION_FIELD_ID = "desc-field";
    }