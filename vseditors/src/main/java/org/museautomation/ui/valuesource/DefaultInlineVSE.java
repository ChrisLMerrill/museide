package org.museautomation.ui.valuesource;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.museautomation.parsing.valuesource.*;
import org.museautomation.ui.valuesource.actions.*;
import org.museautomation.ui.valuesource.parser.*;
import org.museautomation.core.*;
import org.museautomation.core.step.*;
import org.museautomation.core.util.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.events.*;
import org.museautomation.core.values.strings.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.step.*;

import java.util.*;

/**
 * A ValueSourceEditor that fits inline with a part of the step description. I.e. it is a minimal size, a single text field.
 *
 * It converts a ValueSourceConfiguration to/from a single text string. Not all VSC options can be configured this way -
 * only those supported by the ValueSourceStringExpressionSupport for the value source type.  The String conversion is handled
 * by the ValueSouceStringExpressionSupporters.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class DefaultInlineVSE extends BaseValueSourceEditor implements ValueSourceInStepInlineEditor
	{
    public DefaultInlineVSE(MuseProject project, UndoStack undo)
        {
        super(project, undo);
        _text.textProperty().addListener((observable, old_value, new_value) ->
            {
            if (!_ignore_text_changes)
                checkInput(new_value);
            });

        _text.focusedProperty().addListener((observable, was_focused, now_focused) ->
            {
            if (now_focused)
                stopObservingSource();
            else
                {
                processChanges();
                startObservingSource();
                }
            });

        _text.addEventHandler(KeyEvent.KEY_PRESSED, event ->
            {
            if (event.getCode() == KeyCode.ENTER)
                {
                event.consume();
                processChanges();
                }
            });

        _text.setId(TEXT_ID);

        new NodeParentChangeListener(_text)
            {
            @Override
            public void onRemove()
                {
                stopObservingSource();
                }

            @Override
            public void onAdd()
                {
                startObservingSource();
                }
            };

        InputValidation.setValid(_text, false);
        }

    private void processChanges()
        {
        ValueSourceConfiguration new_source = checkInput(_text.getText());
        if (isValid() && !Objects.equals(new_source, getSource()))
            {
            // merge the new source into the existing
            merge(new_source, getSource());
            }
        }

    private ValueSourceConfiguration checkInput(String new_value)
        {
        ValueSourceConfiguration new_config;
        try
            {
            new_config = new ValueSourceExpressionParser(getProject()).parse(new_value);
            _text.setTooltip(null);
            }
        catch (ExpressionParsingException e)
            {
            new_config = null;
            _text.setTooltip(new Tooltip(e.getMessages()));
            }

        if (new_config == null)
            {
            changeValid(false);
            if (_text.disableProperty().getValue() == false)
                InputValidation.setValid(_text, false);
            return null;
            }
        else
            {
            changeValid(true);
            InputValidation.setValid(_text, true);
            return new_config;
            }
        }

    /**
     * Merge the new source into the old, preserving existing sub-sources
     */
    private void merge(ValueSourceConfiguration new_source, ValueSourceConfiguration existing)
        {
        MergeValueSourceAction merge = new MergeValueSourceAction(new_source, existing);
        stopObservingSource();
        merge.execute(getUndoStack());
        startObservingSource();
        }

	@Override
	public ExtensionSelectionPriority getPriority(StepEditContext context, StepConfiguration parent, String source_name)
		{
		return ExtensionSelectionPriority.BUILTIN;
		}

	@Override
    public void setSource(ValueSourceConfiguration source)
        {
        if (source == getSource() && !_has_stopped_listening)  // if we've stopped listening since last time we changed the field...reset it.
            return;
        stopObservingSource();

        super.setSource(source);
        if (source == null)
            return;
        
        fillField();
        startObservingSource();
        }

    private void stopObservingSource()
        {
        if (getSource() != null)
			{
			getSource().removeChangeListener(_observer);
			_has_stopped_listening = true;
			}
        }

    private void startObservingSource()
        {
        if (getSource() != null)
			getSource().addChangeListener(_observer);
        }

    private void fillField()
        {
        _has_stopped_listening = false;
        _ignore_text_changes = true;

        ValueSourceStringExpressionSupporters supporters = new ValueSourceStringExpressionSupporters(getProject());
        String editable_string = supporters.toString(getSource(), new RootStringExpressionContext(getProject()));

        if (editable_string == null)
            {
            _text.setText(getProject().getValueSourceDescriptors().get(getSource()).getInstanceDescription(getSource(), new RootStringExpressionContext(getProject())));
            _able_to_enable = false;
            _ignore_text_changes = true;
            InputValidation.setValid(_text, true);
            }
        else
            {
            _text.setText(editable_string);
            _able_to_enable = true;

            checkInput(editable_string);
            _ignore_text_changes = false;
            }
        updateDisabledState();
        }

    @Override
    public Node getNode()
        {
        return _text;
        }

    @Override
    public void requestFocus()
        {
        _text.requestFocus();
        }

    private void updateDisabledState()
        {
        _text.setEditable(_able_to_enable);
//        _text.setDisable(!_able_to_enable);
        }

    /**
     * For unit tests.
     */
    public void setFieldId(String id)
        {
        _text.setId(id);
        }

    private TextField _text = new TextField();
    private boolean _able_to_enable = true;
    private boolean _ignore_text_changes = false;
    private boolean _has_stopped_listening = false;

    private ValueSourceChangeObserver _observer = new ValueSourceChangeObserver()
        {
        @Override
        public void changeEventRaised(ChangeEvent event)
            {
            fillField();
            }
        };

    public final static String TEXT_ID = "inlineVseText";
    }
