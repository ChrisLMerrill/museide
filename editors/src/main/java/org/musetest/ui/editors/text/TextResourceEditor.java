package org.musetest.ui.editors.text;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.fxmisc.richtext.*;
import org.musetest.core.*;
import org.musetest.ui.extend.edit.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TextResourceEditor extends BaseResourceEditor
    {
    public TextResourceEditor()
        {
        _borders = new BorderPane();
        _text = new TextField();
        _editor = new CodeArea();
        _borders.setCenter(_editor);

        _editor.setOnKeyTyped(event ->
            {
            if (!_changed)
                {
                _changed = true;
                for (ValidationStateListener listener : _validation_listeners)
                    listener.validationStateChanged(_validation_source, true);
                }
            });
        }

    @Override
    protected Parent getEditorArea()
        {
        return _borders;
        }

    @Override
    public boolean canEdit(MuseResource resource)
        {
        return false;
        }

    @Override
    public void editResource(MuseProject project, MuseResource resource)
        {
        // TODO re-work this to be a UnknownResourceEditor. Model unknown resources and use this to access/edit.
//        try
//            {
//            String text = CharStreams.toString(new InputStreamReader(resource.getOrigin().asStream()));
            String text = "visual editing for this resource is not yet supported";
            _text.setText(text);
            _editor.replaceText(0, 0, text);
//            }
/*
        catch (IOException e)
            {
            _editor.replaceText(0, 0, "Unable to read text from the resource.\n" + e.getMessage());
            _editor.setEditable(false);

//            _text.setText("Unable to read text from the resource.\n" + e.getMessage());
            _text.setEditable(false);
            _valid = false;
            }
*/

        super.editResource(project, resource);
        }

    @Override
    public ValidationStateSource getValidationStateSource()
        {
        return _validation_source;
        }

    @Override
    public void requestFocus()
        {
        _text.requestFocus();
        }

    private BorderPane _borders;
    private TextField _text;
    private CodeArea _editor;
    private boolean _changed = false;
    private Set<ValidationStateListener> _validation_listeners = new HashSet<>();
    private ValidationStateSource _validation_source = new ValidationStateSource()
        {
        @Override
        public boolean isValid()
            {
            return true;
            }

        @Override
        public void addValidationStateListener(ValidationStateListener listener)
            {
            _validation_listeners.add(listener);
            }

        @Override
        public void removeValidationStateListener(ValidationStateListener listener)
            {
            _validation_listeners.remove(listener);
            }
        };
    }


