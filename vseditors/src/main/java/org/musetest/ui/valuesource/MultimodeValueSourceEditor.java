package org.musetest.ui.valuesource;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.*;
import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.edit.stack.*;

/**
 * Can handle multiple editing UIs and facilitates switching between them (e.g. default and expert).
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MultimodeValueSourceEditor implements StackableEditor, ValueSourceEditor
    {
    public MultimodeValueSourceEditor(ValueSourceConfiguration source, MuseProject project, UndoStack undo)
        {
        _source = source;
        _project = project;
        _undo = undo;
        useDefaultEditor();
        }

    public MultimodeValueSourceEditor(MuseProject project, UndoStack undo)
        {
        _project = project;
        _undo = undo;
        useDefaultEditor();
        }

    private void useExpertEditor()
        {
        ExpertValueSourceEditor editor = new ExpertValueSourceEditor(_project, _undo);
        if (_source != null)
            editor.setSource(_source);

        Hyperlink link = Buttons.createLinkWithIcon("default mode", "FA:MAGIC", SWITCH_TO_DEFAULT_ID, "Expert editor", ContentDisplay.LEFT);
        link.setOnAction(event -> useDefaultEditor());
        editor.addModeSwitchLink(link);

        setup(editor);
        }

    private void useDefaultEditor()
        {
        DefaultValueSourceEditor editor = new DefaultValueSourceEditor(_project, _undo);
        if (_source != null)
            editor.setSource(_source);

        Hyperlink link = Buttons.createLinkWithIcon("expert mode", "FA:GRADUATION_CAP", SWITCH_TO_EXPERT_ID, "Expert editor", ContentDisplay.LEFT);
        link.setOnAction(event -> useExpertEditor());
        editor.addModeSwitchLink(link);

        setup(editor);
        }

    private void setup(StackableEditor editor)
        {
        _node.getChildren().clear();
        editor.setStack(_stack);
        _current_editor = editor;
        _node.getChildren().add(editor.getNode());
        editor.activate();
        editor.requestFocus();
        }

    @Override
    public Node getNode()
        {
        return _node;
        }

    @Override
    public void setStack(EditorStack stack)
        {
        _stack = stack;
        _current_editor.setStack(stack);
        }

    @Override
    public void requestFocus()
        {
        _current_editor.requestFocus();
        }

    @Override
    public void activate()
        {
        _current_editor.activate();
        }

    @Override
    public boolean isValid()
        {
        return _current_editor.isValid();
        }

    @Override
    public void setSource(ValueSourceConfiguration source)
        {
        _source = source;
        ((ValueSourceEditor) _current_editor).setSource(source);
        }

    @Override
    public ValueSourceConfiguration getSource()
        {
        return _source;
        }

    @Override
    public void addValidationStateListener(ValidationStateListener listener)
        {

        }

    @Override
    public void removeValidationStateListener(ValidationStateListener listener)
        {

        }

    private ValueSourceConfiguration _source;
    private MuseProject _project;
    private UndoStack _undo;

    private StackPane _node = new StackPane();
    private EditorStack _stack;
    private StackableEditor _current_editor;

    public final static String SWITCH_TO_EXPERT_ID = "switch-to-expert";
    public final static String SWITCH_TO_DEFAULT_ID = "switch-to-expert";
    }


