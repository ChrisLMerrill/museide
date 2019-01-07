package org.musetest.ui.seideimport;

import de.jensd.fx.glyphs.fontawesome.*;
import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.resource.storage.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ImportCandidatesTableTests extends ComponentTest
    {
    @Test
    public void displayCandidates()
        {
        File import_folder = TestResources.getFile(PROJECT_FOLDER, getClass());
        File[] import_files = import_folder.listFiles((dir, name) -> (name.endsWith(".html")));

        MuseProject project = new SimpleProject(new FolderIntoMemoryResourceStorage(TestResources.getFile(PROJECT_FOLDER, getClass())));
        ImportCandidates candidates = ImportCandidates.build(project, import_files);
        _table.setCandidates(candidates);
        waitForUiEvents();

        Assert.assertTrue(exists(ImportCandidatesTable.ID_COLUMN_TITLE));
        Assert.assertTrue(exists(ImportCandidatesTable.IMPORT_ENABLED_COLUMN_TITLE));
        //Assert.assertEquals(5, TableViews.numberOfRowsIn(id(ImportCandidatesTable.TABLE_ID))); TODO: find another way to do this

        List<ImportCandidate> candidate_list = candidates.all();
        for (int row = 0; row < candidate_list.size(); row++)
            {
            ImportCandidate candidate = candidate_list.get(row);

            Assert.assertEquals("resource id is not correct", candidate.getResourceId(), textOf(tableCell(id(ImportCandidatesTable.TABLE_ID), row, ImportCandidatesTable.ID_COLUMN_NUM)));

            TableCell icon_cell = tableCell(id(ImportCandidatesTable.TABLE_ID), row, ImportCandidatesTable.ICON_COLUMN_NUM);
            Assert.assertTrue("icon is incorrect", candidate.getStatus().getGlyphName().contains((nodeOfClass(FontAwesomeIconView.class, icon_cell)).getGlyphName()));

            TableCell import_enabled_cell = tableCell(id(ImportCandidatesTable.TABLE_ID), row, ImportCandidatesTable.IMPORT_ENABLED_COLUMN_NUM);
            CheckBox import_enabled = nodeOfClass(CheckBox.class, import_enabled_cell);
            switch (candidate.getStatus())
                {
                case Ready:
                case Warning:
                    Assert.assertNotNull("import checkbox is missing", import_enabled);
                    Assert.assertTrue("import checkbox is not selected", import_enabled.isSelected());
                    break;
                case DuplicateId:
                    Assert.assertNotNull("import checkbox is missing", import_enabled);
                    Assert.assertFalse("import checkbox should not be selected", import_enabled.isSelected());
                    break;
                case Fail:
                    Assert.assertNull("import checkbox should not be there", import_enabled);
                    break;
                }
            }
        }

    @Test
    public void disableReadyCandidate()
        {
        MuseProject project = new SimpleProject(new FolderIntoMemoryResourceStorage(TestResources.getFile(PROJECT_FOLDER, getClass())));

        String import_filename = "import-ok.html";
        File import_file = new File(TestResources.getFile(PROJECT_FOLDER, getClass()), import_filename);
        ImportCandidates candidates = ImportCandidates.build(project, import_file);
        ImportCandidate ready_candidate = candidates.get(import_filename);
        Assert.assertTrue(ready_candidate.getEnabled());

        _table.setCandidates(candidates);
        waitForUiEvents();
        //Assert.assertEquals(1, TableViews.numberOfRowsIn(id(ImportCandidatesTable.TABLE_ID))); TODO: find another way to do this

        TableCell import_enabled_cell = tableCell(id(ImportCandidatesTable.TABLE_ID), 0, ImportCandidatesTable.IMPORT_ENABLED_COLUMN_NUM);
        CheckBox import_enabled_checkbox = from(import_enabled_cell.getGraphic()).lookup(".check-box").query();
        clickOn(import_enabled_checkbox);

        Assert.assertFalse("the candidate is still enabled", ready_candidate.getEnabled());
        }

    @Test
    public void enableDuplicateCandidate()
        {
        MuseProject project = new SimpleProject(new FolderIntoMemoryResourceStorage(TestResources.getFile(PROJECT_FOLDER, getClass())));

        String import_filename = "test1.html";
        File import_file = new File(TestResources.getFile(PROJECT_FOLDER, getClass()), import_filename);
        ImportCandidates candidates = ImportCandidates.build(project, import_file);
        ImportCandidate duplicate_candidate = candidates.get(import_filename);
        Assert.assertFalse(duplicate_candidate.getEnabled());

        _table.setCandidates(candidates);
        waitForUiEvents();
        //Assert.assertEquals(1, TableViews.numberOfRowsIn(id(ImportCandidatesTable.TABLE_ID))); TODO: find another way to do this

        TableCell import_enabled_cell = tableCell(id(ImportCandidatesTable.TABLE_ID), 0, ImportCandidatesTable.IMPORT_ENABLED_COLUMN_NUM);
        CheckBox import_enabled_checkbox = from(import_enabled_cell.getGraphic()).lookup(".check-box").query();
        clickOn(import_enabled_checkbox);

        Assert.assertTrue("the candidate should be enabled", duplicate_candidate.getEnabled());
        }

    @Override
    protected Node createComponentNode() throws Exception
        {
        _table = new ImportCandidatesTable();
        return _table.getNode();
        }

    private ImportCandidatesTable _table;

    private final static String PROJECT_FOLDER =  "projects/import";
    }


