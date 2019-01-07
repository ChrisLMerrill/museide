package org.musetest.ui.valuesource.list;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.*;
import org.musetest.core.values.*;
import org.musetest.core.values.events.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.edit.stack.*;
import org.musetest.ui.extend.glyphs.*;
import org.musetest.ui.extend.grid.*;
import org.musetest.ui.valuesource.*;
import org.slf4j.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceListEditor implements StackableEditor, Validatable
    {
    public ValueSourceListEditor(MuseProject project, UndoStack undo_stack)
        {
        _project = project;
        _undo_stack = undo_stack;

        new NodeParentChangeListener(_grid)
            {
            @Override
            public void onRemove()
                {
                if (_root_source != null)
                    _root_source.removeChangeListener(_change_listener);
                }

            @Override
            public void onAdd()
                {
                if (_root_source != null)
                    _root_source.addChangeListener(_change_listener);
                }
            };
        }

    public void setSource(ContainsIndexedSources source)
        {
        if (_root_source != null)
            _root_source.removeChangeListener(_change_listener);
        _root_source = source;
        _change_listener = new MyChangeListener();
        _root_source.addChangeListener(_change_listener);
        activate();
        }

    @Override
    public void setStack(EditorStack stack)
        {
        _editor_stack = stack;
        }

    @Override
    public void requestFocus()
        {
        _grid.getChildren().get(0).requestFocus();
        }

    @Override
    public boolean isValid()
        {
        for (GridPaneRow row : _grid_rows.getRows())
            if (row instanceof ListRow && !((ListRow) row)._editor.isValid())
                return false;
        return true;
        }

    public void activate()
        {
        _grid.setHgap(3);
        _grid.setVgap(3);

        Platform.runLater(() ->
            {
            _grid_rows.removeAll();

            if (_add_button == null)
                {
                _add_button = new Button("Add", Glyphs.create("FA:PLUS"));
                _add_button.setOnAction(event ->
                    {
                    int row = 0;
                    List<ValueSourceConfiguration> list = _root_source.getSourceList();
                    if (list != null)
                        row = list.size();
                    ValueSourceConfiguration new_source = ValueSourceConfiguration.forValue("value #" + (row+1) );
                    new AddIndexedSubsourceAction(_root_source, row, new_source).execute(_undo_stack);
                    });
                _add_button.setId(ADD_BUTTON_ID);
                }
            GridPaneRow add_button_row = new GridPaneRow(_grid_rows, 0);
            add_button_row.setNode(_add_button, 0);

            if (_root_source != null)
                {
                List<ValueSourceConfiguration> indexed_sources = _root_source.getSourceList();
                if (indexed_sources != null)
                    {
                    int row = 0;
                    for (ValueSourceConfiguration subsource : indexed_sources)
                        addAtRow(subsource, row++);
                    }
                }
            });
        }

    public Node getNode()
        {
        return _grid;
        }

    private void addAtRow(ValueSourceConfiguration source, int row)
        {
        final ListRow list_row = new ListRow(_grid_rows, row);

        //
        // editor
        //
        DefaultInlineVSE editor = new DefaultInlineVSE(_project, _undo_stack);
        editor.setSource(source);
        editor.getNode().setId(getEditorId(row));
        GridPane.setHgrow(editor.getNode(), Priority.ALWAYS);
        list_row.setEditor(editor);

        //
        // delete button
        //
        Button delete_button = Buttons.createRemove();
        delete_button.setCursor(Cursor.HAND);
        delete_button.setId(getDeleteButtonId(row));
        GridPane.setMargin(delete_button, new Insets(3, 0, 0, 5));
        GridPane.setValignment(delete_button, VPos.CENTER);
        list_row.setDeleteButton(delete_button);
        delete_button.setOnAction(event ->
            new RemoveIndexedSubsourceAction(_root_source, _grid_rows.getRows().indexOf(list_row)).execute(_undo_stack));

        //
        // details button
        //
        Hyperlink advanced = Buttons.createLinkWithIcon("more", "FA:ANGLE_DOUBLE_RIGHT", getAdvancedLinkId(row), "edit this source", ContentDisplay.RIGHT);
        advanced.setOnAction(event ->
            {
            MultimodeValueSourceEditor sub_editor = new MultimodeValueSourceEditor(source, _project, _undo_stack);
            _editor_stack.push(sub_editor, "[" + row + "]");
            });
        GridPane.setValignment(advanced, VPos.CENTER);
        list_row.setDetailsLink(advanced);
        }

    private void removeRow(int row)
        {
        ListRow list_row = (ListRow) _grid_rows.remove(row);
        //noinspection ResultOfMethodCallIgnored
        list_row._editor.getSource();
        }

    public static String getAdvancedLinkId(int index)
        {
        return "advanced[" + index + "]";
        }

    public static String getEditorId(int index)
        {
        return EDITOR_ID_BASE + index;
        }

    public static String getDeleteButtonId(int index)
        {
        return DELETE_ID_BASE + index;
        }

    private MuseProject _project;
    private UndoStack _undo_stack;
    private ContainsIndexedSources _root_source;
    private EditorStack _editor_stack;

    private GridPane _grid = new GridPane();
    private GridPaneRows _grid_rows = new GridPaneRows(_grid);
    private Button _add_button;

    private class ListRow extends GridPaneRow
        {
        ListRow(GridPaneRows rows, int row_index)
            {
            super(rows, row_index);
            }

        public void setEditor(DefaultInlineVSE editor)
            {
            if (_editor != null)
                throw new IllegalStateException("node is already set");
            setNode(editor.getNode(), 0);
            _editor = editor;
            }

        void setDeleteButton(Button delete_button)
            {
            setNode(delete_button, 1);
            }

        void setDetailsLink(Hyperlink link)
            {
            setNode(link, 2);
            }

        private DefaultInlineVSE _editor;
        }

    class MyChangeListener extends IndexedSourceChangeObserver
        {
        @Override
        public void indexedSubsourceAdded(IndexedSourceAddedEvent event, int index, ValueSourceConfiguration source)
            {
            addAtRow(source, index);
            }

        @Override
        public void indexedSubsourceRemoved(IndexedSourceRemovedEvent event, int index, ValueSourceConfiguration removed)
            {
            ValueSourceConfiguration at_index = ((ListRow) _grid_rows.getRow(index))._editor.getSource();
            if (!(at_index == removed))
                {
                LOG.error("the source to removed from the base source does not match source on the row corresponding to the index!");
                return;
                }
            removeRow(index);
            }

        @Override
        public void indexedSubsourceReplaced(IndexedSourceReplacedEvent event, int index, ValueSourceConfiguration old_source, ValueSourceConfiguration new_source)
            {
            ((ListRow) _grid_rows.getRow(index))._editor.setSource(new_source);
            }
        }
    private MyChangeListener _change_listener;

    public final static String ADD_BUTTON_ID = "list-add";
    private final static String EDITOR_ID_BASE = "list-editor-";
    private final static String DELETE_ID_BASE = "list-delete-";

    private final static Logger LOG = LoggerFactory.getLogger(ValueSourceListEditor.class);
    }
