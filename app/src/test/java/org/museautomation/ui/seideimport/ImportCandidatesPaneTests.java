package org.museautomation.ui.seideimport;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.resource.storage.*;

import java.io.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ImportCandidatesPaneTests extends ComponentTest
    {
    @Test
    void buttonCountUpdate()
        {
        setupCandidates();

        int initial_count = _candidates.getEnabledCount();
        Assertions.assertEquals(2, initial_count, "should start with 2 enabled");
        Assertions.assertTrue(_import_pane.getActionButtonLabel().contains(Integer.toString(initial_count)), "button label should reflect the current count");
        Assertions.assertTrue(exists(_import_pane.getActionButtonLabel()), "button label should be displayed");

        TableCell import_enabled_cell = tableCell(id(ImportCandidatesTable.TABLE_ID), 3, ImportCandidatesTable.IMPORT_ENABLED_COLUMN_NUM);
        CheckBox import_enabled_checkbox = nodeOfClass(CheckBox.class, import_enabled_cell);
        clickOn(import_enabled_checkbox);
        Assertions.assertTrue(_import_pane.getActionButtonLabel().contains(Integer.toString(initial_count + 1)), "button label should be increased by one");
        Assertions.assertTrue(exists(_import_pane.getActionButtonLabel()), "button label should be displayed");

        clickOn(import_enabled_checkbox);
        Assertions.assertTrue(_import_pane.getActionButtonLabel().contains(Integer.toString(initial_count)), "button label should return to the initial value");
        Assertions.assertTrue(exists(_import_pane.getActionButtonLabel()), "button label should be displayed");
        }

    @Test
    void importButtonDisabled()
        {
        setupCandidates();
        for (ImportCandidate candidate : _candidates.allEnabledCandidates())
            candidate.setEnabled(false);
        waitForUiEvents();

        Button button = lookup(id(ImportCandidatesPane.IMPORT_BUTTON_ID)).query();
        Assertions.assertTrue(button.isDisabled(), "button should be disabled");
        }

    @Test
    void deleteFilesCheckbox()
        {
        setupCandidates();

        ImportSeleniumIdeTestsAction action = _import_pane.getAction();
        Assertions.assertFalse(action.isDeleteSourceFilesEnabled(), "delete files should be off by default");

        clickOn(id(ImportCandidatesPane.DELETE_FILES_CHECKBOX_ID));
        action = _import_pane.getAction();
        Assertions.assertTrue(action.isDeleteSourceFilesEnabled(), "delete files should be true after clicking checkbox");

        clickOn(id(ImportCandidatesPane.DELETE_FILES_CHECKBOX_ID));
        action = _import_pane.getAction();
        Assertions.assertFalse(action.isDeleteSourceFilesEnabled(), "delete files should be false after clicking checkbox again");
        }

    @Test
    void importButton()
        {
        setupCandidates();

        AtomicBoolean _import_pressed = new AtomicBoolean(false);
        _import_pane.setButtonListener(new ImportCandidatesPane.ImportPaneButtonListener()
            {
            @Override
            public void importButtonPressed()
                {
                _import_pressed.set(true);
                }

            public void cancelButtonPressed()
                {
                }
            });

        clickOn(id(ImportCandidatesPane.IMPORT_BUTTON_ID));

        Assertions.assertTrue(_import_pressed.get(), "import not pressed");
        }

    @Test
    void cancelButton()
        {
        setupCandidates();

        AtomicBoolean _cancel_pressed = new AtomicBoolean(false);
        _import_pane.setButtonListener(new ImportCandidatesPane.ImportPaneButtonListener()
            {
            public void importButtonPressed() { }

            @Override
            public void cancelButtonPressed()
                {
                _cancel_pressed.set(true);
                }
            });

        clickOn(id(ImportCandidatesPane.CANCEL_BUTTON_ID));

        Assertions.assertTrue(_cancel_pressed.get(), "import not pressed");
        }

    private void setupCandidates()
        {
        File import_folder = TestResources.getFile(PROJECT_FOLDER, getClass());
        File[] import_files = import_folder.listFiles((dir, name) -> (name.endsWith(".html")));
        if (import_files == null)
            Assertions.fail("no files to import");

        _candidates = ImportCandidates.build(_project, import_files);
        Platform.runLater(() -> _import_pane.setCandidates(_candidates));
        waitForUiEvents();
        }

    @Override
    protected Node createComponentNode()
        {
        _project = new SimpleProject(new FolderIntoMemoryResourceStorage(TestResources.getFile(PROJECT_FOLDER, getClass())));
        _import_pane = new ImportCandidatesPane(_project);
        return _import_pane.getNode();
        }


    @Override
    protected double getDefaultHeight()
        {
        return super.getDefaultHeight() + 100;
        }

    private ImportCandidatesPane _import_pane;
    private ImportCandidates _candidates;
    private MuseProject _project;

    private final static String PROJECT_FOLDER = "projects/import";
    }