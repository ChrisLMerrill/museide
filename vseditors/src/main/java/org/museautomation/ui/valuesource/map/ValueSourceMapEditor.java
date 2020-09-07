package org.museautomation.ui.valuesource.map;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.ui.valuesource.*;
import org.museautomation.core.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.descriptor.*;
import org.museautomation.core.values.events.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.stack.*;
import org.museautomation.ui.extend.glyphs.*;
import org.museautomation.ui.extend.grid.*;
import org.slf4j.*;

import java.util.*;

/**
 * An editor for a ValueSourceMap that can be used within an EditorStack.
 * <p>
 * It allows users to add or remove named subsources, rename them and change the value.
 * It is an expert editor - it does not show the sources that are documented with
 * ValueSourceDescriptors...the users may add any subsources, regardless of which
 * sources would actually be used by the owner.
 * <p>
 * If not used within an EditorStack, a stack will be provided for editing the sub-sources.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceMapEditor implements StackableEditor
	{
	public ValueSourceMapEditor(MuseProject project, UndoStack undo_stack)
		{
		_project = project;
		_undo_stack = undo_stack;

		new NodeParentChangeListener(_grid)
			{
			@Override
			public void onRemove()
				{
				if (_container != null)
					_container.removeChangeListener(_change_listener);
				}

			@Override
			public void onAdd()
				{
				if (_container != null)
					_container.addChangeListener(_change_listener);
				}
			};
		}

	public void setSource(ContainsNamedSources subsources, SubsourceDescriptor[] descriptors)
		{
		if (_container != null)
			_container.removeChangeListener(_change_listener);
		_container = subsources;
		_change_listener = new MyChangeListener();
		if (_container != null)
			_container.addChangeListener(_change_listener);
		_descriptors = descriptors;
		if (_descriptors == null)
			_descriptors = new SubsourceDescriptor[0];
		activate();
		}

	public void setSource(ContainsNamedSources source)
		{
		setSource(source, null);
		}

	@Override
	public Node getNode()
		{
		return _grid;
		}

	@Override
	public void setStack(EditorStack stack)
		{
		_editor_stack = stack;
		}

	@Override
	public void requestFocus()
		{
		// so far, the default behavior is working fine
		}

	@Override
	public void activate()
		{
		_grid.setHgap(3);
		_grid.setVgap(3);

		Platform.runLater(() ->
		{
		_grid_rows.removeAll();

		if (_allow_custom_sources)
			{
			_add_button = new Button("Add", Glyphs.create("FA:PLUS"));
			_add_button.setId(ADD_BUTTON_ID);
			_add_button.setOnAction(event ->
				{
				int index = UniqueString.generateSuffix("name", _container.getSourceNames());
				ValueSourceConfiguration new_source = ValueSourceConfiguration.forValue("value" + index);
				new AddNamedSubsourceAction(_container, new_source, "name" + index).execute(_undo_stack);
				});
			GridPaneRow add_button_row = new GridPaneRow(_grid_rows, 0);
			add_button_row.setNode(_add_button, 0);
			}

		if (_container != null)
			{
			int row = 0;

            // add described sources
            for (SubsourceDescriptor descriptor : _descriptors)
	            {
	            ValueSourceConfiguration source = _container.getSource(descriptor.getName());
	            if (source == null && !descriptor.isOptional())
                    {
                    if (descriptor.getDefault() == null)
                        source = ValueSourceConfiguration.forValue(null);
                    else
                        source = descriptor.getDefault();
                    }
	            if (source == null)
	            	new MissingDescribedSubsourceRow(row++, descriptor);
	            else
	                new DescribedSubsourceRow(source, row++, descriptor);
	            _hidden_names.add(descriptor.getName());
	            }

			// add custom sources (if allowed)
			if (_allow_custom_sources)
				{
				SortedSet<String> sorted_names = new TreeSet<>(_container.getSourceNames());
				for (String name : sorted_names)
					if (!_hidden_names.contains(name))
						new CustomSubsourceRow(name, _container, row++);
				}
			}
		});
		}

	public static String getNewValue(int index)
		{
		return "value " + index;
		}

	public static String getAddButtonId(String name)
		{
		return ADD_BUTTON_ID_BASE + name;
		}

	@SuppressWarnings("WeakerAccess") // available for testing
	public static String getEditFieldId(String name)
		{
		return EDIT_FIELD_ID_BASE + name;
		}

	private void removeRow(int row)
		{
		_grid_rows.remove(row);
		}

	@Override
	public boolean isValid()
		{
		for (GridPaneRow row : _grid_rows.getRows())
			if (row instanceof CustomSubsourceRow && !((CustomSubsourceRow) row)._editor.isValid())
				return false;
		return true;
		}

	public ContainsNamedSources getSource()
		{
		return _container;
		}

	public void hideSourceNamed(String name)
		{
		_hidden_names.add(name);
		}

	public void setAllowCustomSources(boolean allowed)
		{
		_allow_custom_sources = allowed;
		}

	private SubsourceDescriptor getDescriptor(String name)
		{
		for (SubsourceDescriptor descriptor : _descriptors)
			if (descriptor.getName().equals(name))
				return descriptor;
		return null;
		}

	private DescribedSubsourceRow findDescribedRow(String name)
		{
		for (GridPaneRow row : _grid_rows.getRows())
			if (row instanceof DescribedSubsourceRow && ((DescribedSubsourceRow)row)._descriptor.getName().equals(name))
				return (DescribedSubsourceRow) row;
		return null;
		}

	private MissingDescribedSubsourceRow findMissingDescribedRow(String name)
		{
		for (GridPaneRow row : _grid_rows.getRows())
			if (row instanceof MissingDescribedSubsourceRow && ((MissingDescribedSubsourceRow)row)._subsource_name.equals(name))
				return (MissingDescribedSubsourceRow) row;
		return null;
		}

	private class CustomSubsourceRow extends GridPaneRowWithAdvancedEditingMode
		{
		CustomSubsourceRow(String name, ContainsNamedSources container, int row_index)
			{
			super(_grid_rows, row_index, _undo_stack);
			_source_name = name;
			_container = container;
			_editor = new InlineNamedVSE(_project, _undo_stack);
			_editor.setName(name);
			_editor.setFieldId(getEditFieldId(name));
			_editor.setSource(container.getSource(name));
			_editor.addNameChangeListener((editor1, old_name, new_name) ->
				new RenameSubsourceAction(_container, old_name, new_name).execute(_undo_stack));
			_editor.setNameValidator(name1 -> _container.getSource(name1) == null);
			GridPane.setHgrow(_editor.getNode(), Priority.ALWAYS);
			setNode(_editor.getNameNode(), 0);
			setNode(_editor.getValueNode(), 1);

			_delete_button = Buttons.createRemove(getRemoveButtonId(name));
			_delete_button.setCursor(Cursor.HAND);
			_delete_button.getStyleClass().clear();
			GridPane.setMargin(_delete_button, new Insets(3, 0, 0, 5));
			GridPane.setValignment(_delete_button, VPos.TOP);
			_delete_button.setOnAction(event ->
				{
				int row1 = GridPane.getRowIndex(_delete_button);
				CustomSubsourceRow row_to_delete = (CustomSubsourceRow) _grid_rows.getRow(row1);
				new RemoveNamedSubsourceAction(_container, row_to_delete._editor.getName()).execute(_undo_stack);
				});
			setNode(this._delete_button, 2);

			_more_link = Buttons.createLinkWithIcon("more", "FA:ANGLE_DOUBLE_RIGHT", getAdvancedLinkId(name), "edit this source", ContentDisplay.RIGHT);
			_more_link.setOnAction(event ->
				{
				if (_editor_stack == null)
					enterAdvancedMode();
				else
					{
					MultimodeValueSourceEditor sub_editor = new MultimodeValueSourceEditor(_editor.getSource(), _project, _undo_stack);
					_editor_stack.push(sub_editor, _editor.getName());
					}
				});
			setNode(this._more_link, 3);
			}

		@Override
		protected Node createAdvancedNode()
			{
			ValueSourceEditorStack editor_stack = new ValueSourceEditorStack(new EditInProgress<>()
				{
				@Override
				public void cancel()
					{
					returnToBasicMode(false);
					}

				@Override
				public void commit(ValueSourceConfiguration target)
					{
					returnToBasicMode(true);
					}
				}, _project, _undo_stack);
			MultimodeValueSourceEditor sub_editor = new MultimodeValueSourceEditor(_editor.getSource(), _project, _undo_stack);
			editor_stack.push(sub_editor, _editor.getName());
			return editor_stack.getNode();
			}

		@Override
		protected void returnToBasicMode(boolean save)
			{
			super.returnToBasicMode(save);
			if (save)
				Platform.runLater(() -> _editor.setSource(_container.getSource(_source_name)));
			}

		String _source_name;

		// basic nodes
		InlineNamedVSE _editor;
		Button _delete_button;
		Hyperlink _more_link;
		}

	private class DescribedSubsourceRow extends GridPaneRowWithAdvancedEditingMode
		{
		DescribedSubsourceRow(ValueSourceConfiguration source, int row_index, SubsourceDescriptor descriptor)
			{
			super(_grid_rows, row_index, _undo_stack);
			_descriptor = descriptor;

			_name_label = new Label(descriptor.getDisplayName());
			_name_label.setTooltip(new Tooltip(descriptor.getDescription()));
			setNode(_name_label, 0);

			_editor = new DefaultInlineVSE(_project, _undo_stack);
			_editor.setFieldId(getEditFieldId(descriptor.getName()));
			_editor.setSource(source);
			GridPane.setHgrow(_editor.getNode(), Priority.ALWAYS);
			setNode(_editor.getNode(), 1);

			if (descriptor.isOptional())
				{
				_delete_button = Buttons.createRemove(getRemoveButtonId(descriptor.getName()));
				_delete_button.setCursor(Cursor.HAND);
				_delete_button.getStyleClass().clear();
				GridPane.setMargin(_delete_button, new Insets(3, 0, 0, 5));
				GridPane.setValignment(_delete_button, VPos.TOP);
				_delete_button.setOnAction(event -> new RemoveNamedSubsourceAction(_container, descriptor.getName()).execute(_undo_stack));
				setNode(this._delete_button, 2);
				}

			// advanced editor link
			_more_link = Buttons.createLinkWithIcon("more", "FA:ANGLE_DOUBLE_RIGHT", getAdvancedLinkId(descriptor.getName()), "edit this source", ContentDisplay.RIGHT);
			_more_link.setOnAction(event ->
				{
				if (_editor_stack == null)
					enterAdvancedMode();
				else
					{
					MultimodeValueSourceEditor sub_editor = new MultimodeValueSourceEditor(_editor.getSource(), _project, _undo_stack);
					_editor_stack.push(sub_editor, descriptor.getDisplayName());
					}
				});
			setNode(this._more_link, 3);
			}

		@Override
		protected Node createAdvancedNode()
			{
			ValueSourceEditorStack editor_stack = new ValueSourceEditorStack(new EditInProgress<>()
				{
				@Override
				public void cancel()
					{
					returnToBasicMode(false);
					}

				@Override
				public void commit(ValueSourceConfiguration target)
					{
					returnToBasicMode(true);
					}
				}, _project, _undo_stack);
			MultimodeValueSourceEditor sub_editor = new MultimodeValueSourceEditor(_editor.getSource(), _project, _undo_stack);
			editor_stack.push(sub_editor, _descriptor.getDisplayName());
			return editor_stack.getNode();
			}

		@Override
		protected void returnToBasicMode(boolean save)
			{
			super.returnToBasicMode(save);
			if (save)
				Platform.runLater(() -> _editor.setSource(_container.getSource(_descriptor.getName())));
			}

		SubsourceDescriptor _descriptor;

		// basic nodes
		Label _name_label;
		DefaultInlineVSE _editor;
		Button _delete_button;
		Hyperlink _more_link;
		}

	private class MissingDescribedSubsourceRow extends GridPaneRow
		{
		MissingDescribedSubsourceRow(int row_index, SubsourceDescriptor descriptor)
			{
			super(_grid_rows, row_index);
			_subsource_name = descriptor.getName();

			_name_label = new Label(descriptor.getDisplayName());
			setNode(_name_label, 0);

			_add_button = new Button("Add");
		    _add_button.setGraphic(Glyphs.create("FA:PLUS"));
            _add_button.setId(getAddButtonId(descriptor.getName()));
            _add_button.setOnAction(event -> new AddNamedSubsourceAction(_container, descriptor.getDefault(), descriptor.getName()).execute(_undo_stack));
			setNode(_add_button, 1);
			}

		final String _subsource_name;
		// basic nodes
		Button _add_button;
		Label _name_label;
		}

	private class MyChangeListener extends NamedSourceChangeObserver
		{
		@Override
		public void namedSubsourceAdded(NamedSourceAddedEvent event, String name, ValueSourceConfiguration source)
			{
			int row = _grid_rows.size() - 1; // just before the add button
			if (_removed_sources.containsKey(source))
				row = _removed_sources.remove(source);
			final SubsourceDescriptor descriptor = getDescriptor(name);
			if (descriptor == null)
				new CustomSubsourceRow(name, _container, row);
			else
				{
				MissingDescribedSubsourceRow row_to_replace = findMissingDescribedRow(name);
				if (row_to_replace != null)
					{
					row = row_to_replace.getIndex();
					_grid_rows.remove(row_to_replace);
					}
				new DescribedSubsourceRow(source, row, descriptor);
				}
			}

		@Override
		public void namedSubsourceRemoved(NamedSourceRemovedEvent event, String name, ValueSourceConfiguration removed)
			{
			final SubsourceDescriptor descriptor = getDescriptor(name);
			if (descriptor == null)
				{
				for (int i = 0; i < _grid_rows.size(); i++)
					{
					if (_grid_rows.getRow(i) instanceof CustomSubsourceRow)
						{
						CustomSubsourceRow row = (CustomSubsourceRow) _grid_rows.getRow(i);
						if (row._editor.getSource() == removed)
							{
							removeRow(i);
							_removed_sources.put(removed, i);
							return;
							}
						}
					}
				}
			else
				{
				DescribedSubsourceRow row_to_replace = findDescribedRow(name);
				int row = _grid_rows.size() - 1;
				if (row_to_replace != null)
					{
					row = row_to_replace.getIndex();
					_grid_rows.remove(row_to_replace);
					}
				new MissingDescribedSubsourceRow(row, descriptor);
				}
			LOG.error(String.format("MapVSE could not find the row to remove (name=%s)", name));
			}

		@Override
		public void namedSubsourceRenamed(NamedSourceRenamedEvent event, String old_name, String new_name, ValueSourceConfiguration source)
			{
			for (GridPaneRow row : _grid_rows.getRows())
				if (row instanceof CustomSubsourceRow && ((CustomSubsourceRow) row)._editor.getName().equals(old_name))
					{
					((CustomSubsourceRow) row)._editor.setName(new_name);
					break;
					}
			}

		@Override
		public void namedSubsourceReplaced(NamedSourceReplacedEvent event, String name, ValueSourceConfiguration old_source, ValueSourceConfiguration new_source)
			{
			for (GridPaneRow row : _grid_rows.getRows())
				if (row instanceof CustomSubsourceRow && ((CustomSubsourceRow) row)._editor.getName().equals(name))
					{
					((CustomSubsourceRow) row)._editor.setSource(new_source);
					break;
					}
			}

		private final Map<ValueSourceConfiguration, Integer> _removed_sources = new HashMap<>();   // this is used to remember the location of removed rows, so an undo can restore them
		}

	private final MuseProject _project;
	private final UndoStack _undo_stack;
	private EditorStack _editor_stack;
	private ContainsNamedSources _container;
	private SubsourceDescriptor[] _descriptors = new SubsourceDescriptor[0];
	private final List<String> _hidden_names = new ArrayList<>();
	private boolean _allow_custom_sources = true;

	private final GridPane _grid = new GridPane();
	private final GridPaneRows _grid_rows = new GridPaneRows(_grid);
	private Button _add_button;
	private MyChangeListener _change_listener = new MyChangeListener();

	// for testability
	public static String getRemoveButtonId(String name)
		{
		return "remove[" + name + "]";
		}

	public static String getAdvancedLinkId(String name)
		{
		return "advanced[" + name + "]";
		}

	public final static String ADD_BUTTON_ID = "vsme-add";
	private final static String ADD_BUTTON_ID_BASE = "vsme-add-";
	private final static String EDIT_FIELD_ID_BASE = "vsme-edit-";

	private final static Logger LOG = LoggerFactory.getLogger(ValueSourceMapEditor.class);
	}


