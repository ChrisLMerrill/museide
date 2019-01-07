package org.musetest.ui.seideimport;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ImportCandidatesPane
    {
    public ImportCandidatesPane(MuseProject project)
        {
        _project = project;
        _grid = new GridPane();
        _grid.getStyleClass().add("dialog-pane");
        _grid.setVgap(5);
        _grid.setHgap(5);
        _grid.setPadding(new Insets(5));

        _table = new ImportCandidatesTable();
        _grid.add(_table.getNode(), 0, 0, 2, 1);

        _delete_source_files_checkbox = new CheckBox("Delete source file(s) after import");
        _delete_source_files_checkbox.setId(DELETE_FILES_CHECKBOX_ID);
        _grid.add(_delete_source_files_checkbox, 0, 1, 2, 1);

        _import_button = new Button();
        _import_button.setId(IMPORT_BUTTON_ID);
        GridPane.setHgrow(_import_button, Priority.ALWAYS);
        GridPane.setHalignment(_import_button, HPos.RIGHT);
        _grid.add(_import_button, 0, 2);
        _import_button.setOnAction(event -> _listener.importButtonPressed());

        Button cancel_button = new Button("Cancel");
        cancel_button.setId(CANCEL_BUTTON_ID);
        _grid.add(cancel_button, 1, 2);
        cancel_button.setOnAction(event -> _listener.cancelButtonPressed());
        }

    public Node getNode()
        {
        return _grid;
        }

    public void setCandidates(ImportCandidates candidates)
        {
        _candidates = candidates;
        _table.setCandidates(candidates);
        _candidates.onChange(this::updateDisplay);

        updateDisplay();
        }

    private void updateDisplay()
        {
        Platform.runLater(() ->
            {
            _import_button.setText(getActionButtonLabel());
            _import_button.setDisable(_candidates.getEnabledCount() == 0);
            });
        }

    public ImportSeleniumIdeTestsAction getAction()
        {
        return new ImportSeleniumIdeTestsAction(_candidates, _delete_source_files_checkbox.isSelected(), _project);
        }

    public String getActionButtonLabel()
        {
        return String.format("Import %d tests", _candidates.getEnabledCount());
        }

    public void setButtonListener(ImportPaneButtonListener listener)
        {
        _listener = listener;
        }

    private final MuseProject _project;
    private ImportCandidates _candidates;

    private final GridPane _grid;
    private final ImportCandidatesTable _table;
    private final CheckBox _delete_source_files_checkbox;
    private final Button _import_button;
    private ImportPaneButtonListener _listener;

    public interface ImportPaneButtonListener
        {
        void importButtonPressed();
        void cancelButtonPressed();
        }

    public final static String DELETE_FILES_CHECKBOX_ID = "omus-icp-delete";
    public final static String IMPORT_BUTTON_ID = "omus-icp-import";
    public final static String CANCEL_BUTTON_ID = "omus-icp-cancel";
    }


