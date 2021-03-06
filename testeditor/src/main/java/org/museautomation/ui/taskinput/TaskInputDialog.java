package org.museautomation.ui.taskinput;

import javafx.scene.control.*;
import org.museautomation.core.*;
import org.museautomation.core.task.*;
import org.museautomation.core.task.input.*;
import org.museautomation.ui.extend.*;
import org.museautomation.ui.extend.icons.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TaskInputDialog
    {
    public TaskInputDialog(TaskInputSet inputs, MuseExecutionContext context)
        {
        _inputs = inputs;
        _context = context;
        }

    public Dialog<List<ResolvedTaskInput>> createDialog()
        {
        final TaskInputValuesEditor editor = new TaskInputValuesEditor(_context);

        Dialog<List<ResolvedTaskInput>> dialog = new Dialog<>();
        dialog.setTitle("Input required");
        dialog.setHeaderText("The task requires these inputs:");
        Icons.setIcons(dialog.getDialogPane());
        Styles.applyToScene("ide", dialog.getDialogPane().getScene());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button ok_button = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        dialog.setResultConverter(param ->
            {
            if (param == ButtonType.OK)
                return editor.getResolvedInputs();
            else
                return Collections.emptyList();
            });
        editor.addSatisfactionChangeListener((old, satisifed) -> ok_button.setDisable(!satisifed));

        editor.setInputs(_inputs);
        dialog.getDialogPane().setContent(editor.getNode());
        dialog.getDialogPane().setMinWidth(400);
        dialog.getDialogPane().setMinHeight(150 + _inputs.all().size() * 25);
        dialog.setResizable(true);

        return dialog;
        }

    private final TaskInputSet _inputs;
    private final MuseExecutionContext _context;
    }