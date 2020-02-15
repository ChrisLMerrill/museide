package org.museautomation.ui.step.inline;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.museautomation.ui.step.actions.*;
import org.museautomation.ui.valuesource.*;
import org.museautomation.core.*;
import org.museautomation.core.step.*;
import org.museautomation.core.step.descriptor.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.step.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused")  // instantiated by reflection
public class InlineQuickStepEditor extends InlineStepEditor
    {
    @Override
    public ExtensionSelectionPriority getPriority(StepEditContext context, StepConfiguration step)
	    {
	    StepDescriptor descriptor = context.getProject().getStepDescriptors().get(step);
	    String inline_edit_string = descriptor.getInlineEditString();
	    if (inline_edit_string == null)
	    	return ExtensionSelectionPriority.NEVER;

	    int number_fields = parseEditString(inline_edit_string);
	    if (number_fields > 0)
	    	return ExtensionSelectionPriority.BUILTIN;
	    else
	    	return ExtensionSelectionPriority.NEVER;
	    }

    @Override
    public void edit(StepEditContext context, StepConfiguration step, EditInProgress edit, InlineStepEditorContainer container)
	    {
	    super.edit(context, step, edit, container);
	    _context = context;
	    _step = step;
	    StepDescriptor descriptor = _context.getProject().getStepDescriptors().get(_step);

	    if (_segments.size() < 1)  // else, we've already parsed
		    {
		    String inline_edit_string = descriptor.getInlineEditString();
		    if (inline_edit_string == null)
			    throw new IllegalArgumentException("Cannot edit this step. Did you check canEdit() first?");

		    int number_fields = parseEditString(inline_edit_string);
		    if (number_fields == 0)
			    throw new IllegalArgumentException("Cannot edit this step. Did you check canEdit() first?");
		    }

	    UpgradeStepToDescriptorComplianceAction upgrade_action = new UpgradeStepToDescriptorComplianceAction(_step, _context.getProject());
	    if (upgrade_action.isUpgradeNeeded())
		    upgrade_action.execute(_context.getUndo());
	    }

    private int parseEditString(String inline_edit_string)
        {
        _segments.clear();
        int number_fields = 0;
        StringTokenizer tokenizer = new StringTokenizer(inline_edit_string, "{}", true);
        while (tokenizer.hasMoreTokens())
            {
            String token = tokenizer.nextToken();
            switch (token)
                {
                case "{":
                    _segments.add(new EditSegment(null, tokenizer.nextToken()));
                    number_fields++;
                    break;
                case "}":
                    break;
                default:
                    _segments.add(new EditSegment(token, null));
                }
            }
        return number_fields;
        }

    public void requestFocus()
        {
        if (_first_field != null)
            _first_field.requestFocus();
        }

    @Override
    public Node getNode()
        {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0, 10, 0, 0));
        int next_column = buildEditFields(grid);

        Node link = buildMoreOptionsLink();
        grid.add(link, next_column, 0);

        return grid;
        }

    private int buildEditFields(GridPane grid)
        {
        int column = 0;
        for (EditSegment segment : _segments)
            {
            if (segment._label != null)
                grid.add(new Label(segment._label), column, 0);
            else
                {
                ValueSourceInStepInlineEditor editor = ValueSourceInStepInlineEditors.get(_context.getProject()).findEditor(_context, _step, segment._name);
                editor.setSource(_step.getSource(segment._name));
                grid.add(editor.getNode(), column, 0);
                GridPane.setHgrow(editor.getNode(), Priority.ALWAYS);
                _source_editors.put(segment._name, editor);
                if (_source_editors.size() == 1)
                    _first_field = editor;
                editor.getNode().setOnKeyPressed(this::handleEnterAndEscapeKeyEvents);
                }
            column++;
            }
        return column;
        }

    @Override
    public boolean isValid()
        {
        for (ValueSourceInStepInlineEditor editor : _source_editors.values())
            if (!editor.isValid())
                return false;
        return true;
        }

    private StepEditContext _context;
    private StepConfiguration _step;

    private List<EditSegment> _segments = new ArrayList<>();
    private Map<String, ValueSourceInStepInlineEditor> _source_editors = new HashMap<>();
    private ValueSourceInStepInlineEditor _first_field;

    private class EditSegment
        {
        EditSegment(String label, String name)
            {
            _label = label;
            _name = name;
            }

        String _label;
        String _name;
        }
    }


