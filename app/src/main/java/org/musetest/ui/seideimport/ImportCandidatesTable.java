package org.musetest.ui.seideimport;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import net.christophermerrill.ShadowboxFx.*;
import org.musetest.ui.extend.glyphs.*;

/**
 * UI for a ImportCandidates, which includes a list of ImportCandidate.
 *
 * Shows the user the candidates, along with a status icon, status message and the import state (on/off)
 * for each candidate.
 *
 * User may change the import state (if allowed) of each candidate - i.e. disable the import of a specific candidate
 * or enable one that was initially disabled (due to a duplicate id, for example).
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ImportCandidatesTable
    {
    public ImportCandidatesTable()
        {
        _view.setId(TABLE_ID);
        _view.setColumnResizePolicy((param) -> true);

        TableColumn<ImportCandidate, ImportCandidate.Status> icon_column = new TableColumn("");
        icon_column.setCellValueFactory(new PropertyValueFactory<>("status"));
        icon_column.setCellFactory(column -> new TableCell<ImportCandidate, ImportCandidate.Status>()
            {
            @Override
            protected void updateItem(ImportCandidate.Status status, boolean empty)
                {
                if (empty)
                    setGraphic(null);
                else
                    setGraphic(new CenteredPane(Glyphs.create(status.getGlyphName(), status.getGlyphColor(), 18)));
                }
            });
        icon_column.setPrefWidth(25);
        _view.getColumns().add(ICON_COLUMN_NUM, icon_column);

        TableColumn<ImportCandidate, ImportCandidate> import_enabled_column = new TableColumn("Import?");
        import_enabled_column.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
        import_enabled_column.setCellFactory(param -> new TableCell<ImportCandidate, ImportCandidate>()
            {
            @Override
            protected void updateItem(ImportCandidate candidate, boolean empty)
                {
                if (!empty)
                    switch (candidate.getStatus())
                        {
                        case Ready:
                        case DuplicateId:
                        case Warning:
                            final CheckBox checkbox = new CheckBox();
                            checkbox.setSelected(candidate.getEnabled());
                            checkbox.setOnAction(event -> candidate.setEnabled(checkbox.isSelected()));
                            setGraphic(new CenteredPane(checkbox));
                            return;
                        }
                setGraphic(null);
                }
            });
        import_enabled_column.setPrefWidth(100);
        _view.getColumns().add(IMPORT_ENABLED_COLUMN_NUM, import_enabled_column);

        _id_column = new TableColumn(ID_COLUMN_TITLE);
        _id_column.setCellValueFactory(new PropertyValueFactory<>("resourceId"));
        _id_column.setPrefWidth(100);
        _view.getColumns().add(ID_COLUMN_NUM, _id_column);

        TableColumn notes_column = new TableColumn(NOTES_COLUMN_TITLE);
        notes_column.setCellValueFactory(new PropertyValueFactory<>("comments"));
        notes_column.setPrefWidth(550);
        _view.getColumns().add(NOTES_COLUMN_NUM, notes_column);
        }

    public Node getNode()
        {
        return _view;
        }

    public void setCandidates(ImportCandidates candidates)
        {
        ObservableList<ImportCandidate> rows = FXCollections.observableArrayList(candidates.all());
        _view.setItems(rows);
        }

    private TableView<ImportCandidate> _view = new TableView();
    private final TableColumn<ImportCandidate, String> _id_column;

    public final static String ID_COLUMN_TITLE = "Test id";
    public final static String NOTES_COLUMN_TITLE = "Notes";
    public final static String IMPORT_ENABLED_COLUMN_TITLE = "Import?";

    public static final String TABLE_ID = "ImportCandidateTable";
    public static final int ICON_COLUMN_NUM = 0;
    public static final int IMPORT_ENABLED_COLUMN_NUM = 1;
    public static final int ID_COLUMN_NUM = 2;
    public static final int NOTES_COLUMN_NUM = 3;
    }


