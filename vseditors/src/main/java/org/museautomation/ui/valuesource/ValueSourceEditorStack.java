package org.museautomation.ui.valuesource;

import javafx.application.*;
import org.museautomation.core.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.stack.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceEditorStack extends EditorStack
    {
    public ValueSourceEditorStack(EditInProgress<ValueSourceConfiguration> edit, MuseProject project, UndoStack undo_stack)
        {
        super(edit, undo_stack);
        _project = project;
        }

    public void setSource(ValueSourceConfiguration config)
        {
        _config = config;
        MultimodeValueSourceEditor editor = new MultimodeValueSourceEditor(_config, _project, getUndoStack());
        Platform.runLater(() -> push(editor, _project.getValueSourceDescriptors().get(_config).getName()));
        }

    @Override
    protected void notifyEditCommit()
        {
        _edit.commit(_config);
        }

    private MuseProject _project;
    private ValueSourceConfiguration _config;
    }


