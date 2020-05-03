package org.museautomation.ui.seideimport;

import de.jensd.fx.glyphs.fontawesome.*;
import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.resource.storage.*;
import org.testfx.service.query.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ImportCandidatesTableTests extends ComponentTest
    {
    @Test
    void displayCandidates()
        {
        File import_folder = TestResources.getFile(PROJECT_FOLDER, getClass());
        File[] import_files = import_folder.listFiles((dir, name) -> (name.endsWith(".html")));
        if (import_files == null)
            Assertions.fail("no files to import");

        MuseProject project = new SimpleProject(new FolderIntoMemoryResourceStorage(TestResources.getFile(PROJECT_FOLDER, getClass())));
        ImportCandidates candidates = ImportCandidates.build(project, import_files);
        _table.setCandidates(candidates);
        waitForUiEvents();

        Assertions.assertTrue(exists(ImportCandidatesTable.ID_COLUMN_TITLE));
        Assertions.assertTrue(exists(ImportCandidatesTable.IMPORT_ENABLED_COLUMN_TITLE));
        //Assert.assertEquals(5, TableViews.numberOfRowsIn(id(ImportCandidatesTable.TABLE_ID))); TODO: find another way to do this

        List<ImportCandidate> candidate_list = candidates.all();
        for (int row = 0; row < candidate_list.size(); row++)
            {
            ImportCandidate candidate = candidate_list.get(row);

            Assertions.assertEquals(candidate.getResourceId(), textOf(tableCell(id(ImportCandidatesTable.TABLE_ID), row, ImportCandidatesTable.ID_COLUMN_NUM)), "resource id is not correct");

            TableCell icon_cell = tableCell(id(ImportCandidatesTable.TABLE_ID), row, ImportCandidatesTable.ICON_COLUMN_NUM);
            Assertions.assertTrue(candidate.getStatus().getGlyphName().contains((nodeOfClass(FontAwesomeIconView.class, icon_cell)).getGlyphName()), "icon is incorrect");

            TableCell import_enabled_cell = tableCell(id(ImportCandidatesTable.TABLE_ID), row, ImportCandidatesTable.IMPORT_ENABLED_COLUMN_NUM);
            CheckBox import_enabled;
            try
                {
                import_enabled = nodeOfClass(CheckBox.class, import_enabled_cell);
                }
            catch (EmptyNodeQueryException e)
                {
                import_enabled = null;
                }
            switch (candidate.getStatus())
                {
                case Ready:
                case Warning:
                    Assertions.assertNotNull(import_enabled, "import checkbox is missing");
                    Assertions.assertTrue(import_enabled.isSelected(), "import checkbox is not selected");
                    break;
                case DuplicateId:
                    Assertions.assertNotNull(import_enabled, "import checkbox is missing");
                    Assertions.assertFalse(import_enabled.isSelected(), "import checkbox should not be selected");
                    break;
                case Fail:
                    Assertions.assertNull(import_enabled, "import checkbox should not be there");
                    break;
                }
            }
        }

    @Test
    void disableReadyCandidate()
        {
        MuseProject project = new SimpleProject(new FolderIntoMemoryResourceStorage(TestResources.getFile(PROJECT_FOLDER, getClass())));

        String import_filename = "import-ok.html";
        File import_file = new File(TestResources.getFile(PROJECT_FOLDER, getClass()), import_filename);
        ImportCandidates candidates = ImportCandidates.build(project, import_file);
        ImportCandidate ready_candidate = candidates.get(import_filename);
        Assertions.assertTrue(ready_candidate.getEnabled());

        _table.setCandidates(candidates);
        waitForUiEvents();
        //Assert.assertEquals(1, TableViews.numberOfRowsIn(id(ImportCandidatesTable.TABLE_ID))); TODO: find another way to do this

        TableCell import_enabled_cell = tableCell(id(ImportCandidatesTable.TABLE_ID), 0, ImportCandidatesTable.IMPORT_ENABLED_COLUMN_NUM);
        CheckBox import_enabled_checkbox = from(import_enabled_cell.getGraphic()).lookup(".check-box").query();
        clickOn(import_enabled_checkbox);

        Assertions.assertFalse(ready_candidate.getEnabled(), "the candidate is still enabled");
        }

    @Test
    void enableDuplicateCandidate()
        {
        MuseProject project = new SimpleProject(new FolderIntoMemoryResourceStorage(TestResources.getFile(PROJECT_FOLDER, getClass())));

        String import_filename = "test1.html";
        File import_file = new File(TestResources.getFile(PROJECT_FOLDER, getClass()), import_filename);
        ImportCandidates candidates = ImportCandidates.build(project, import_file);
        ImportCandidate duplicate_candidate = candidates.get(import_filename);
        Assertions.assertFalse(duplicate_candidate.getEnabled());

        _table.setCandidates(candidates);
        waitForUiEvents();
        //Assert.assertEquals(1, TableViews.numberOfRowsIn(id(ImportCandidatesTable.TABLE_ID))); TODO: find another way to do this

        TableCell import_enabled_cell = tableCell(id(ImportCandidatesTable.TABLE_ID), 0, ImportCandidatesTable.IMPORT_ENABLED_COLUMN_NUM);
        CheckBox import_enabled_checkbox = from(import_enabled_cell.getGraphic()).lookup(".check-box").query();
        clickOn(import_enabled_checkbox);

        Assertions.assertTrue(duplicate_candidate.getEnabled(), "the candidate should be enabled");
        }

    @Override
    public Node createComponentNode()
        {
        _table = new ImportCandidatesTable();
        return _table.getNode();
        }

    private ImportCandidatesTable _table;

    private final static String PROJECT_FOLDER =  "projects/import";
    }