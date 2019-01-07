package org.musetest.ui.seideimport;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.resource.storage.*;
import org.musetest.ui.seideimport.*;

import java.io.*;
import java.util.concurrent.atomic.*;

import static org.musetest.ui.seideimport.ImportCandidatesPane.IMPORT_BUTTON_ID;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ImportCandidatesPaneTests extends ComponentTest
    {
    @Test
    public void buttonCountUpdate()
        {
        setupCandidates();

        int initial_count = _candidates.getEnabledCount();
        Assert.assertEquals("should start with 2 enabled", 2, initial_count);
        Assert.assertTrue("button label should reflect the current count", _import_pane.getActionButtonLabel().contains(Integer.toString(initial_count)));
        Assert.assertTrue("button label should be displayed", exists(_import_pane.getActionButtonLabel()));

        TableCell import_enabled_cell = tableCell(id(ImportCandidatesTable.TABLE_ID), 3, ImportCandidatesTable.IMPORT_ENABLED_COLUMN_NUM);
        CheckBox import_enabled_checkbox = nodeOfClass(CheckBox.class, import_enabled_cell);
        clickOn(import_enabled_checkbox);
        Assert.assertTrue("button label should be increased by one", _import_pane.getActionButtonLabel().contains(Integer.toString(initial_count + 1)));
        Assert.assertTrue("button label should be displayed", exists(_import_pane.getActionButtonLabel()));

        clickOn(import_enabled_checkbox);
        Assert.assertTrue("button label should return to the initial value", _import_pane.getActionButtonLabel().contains(Integer.toString(initial_count)));
        Assert.assertTrue("button label should be displayed", exists(_import_pane.getActionButtonLabel()));
        }

    @Test
    public void importButtonDisabled()
        {
        setupCandidates();
        for (ImportCandidate candidate : _candidates.allEnabledCandidates())
            candidate.setEnabled(false);
        waitForUiEvents();

        Button button = lookup(id(IMPORT_BUTTON_ID)).query();
        Assert.assertTrue("button should be disabled", button.isDisabled());
        }

    @Test
    public void deleteFilesCheckbox()
        {
        setupCandidates();

        ImportSeleniumIdeTestsAction action = _import_pane.getAction();
        Assert.assertFalse("delete files should be off by default", action.isDeleteSourceFilesEnabled());

        clickOn(id(ImportCandidatesPane.DELETE_FILES_CHECKBOX_ID));
        action = _import_pane.getAction();
        Assert.assertTrue("delete files should be true after clicking checkbox", action.isDeleteSourceFilesEnabled());

        clickOn(id(ImportCandidatesPane.DELETE_FILES_CHECKBOX_ID));
        action = _import_pane.getAction();
        Assert.assertFalse("delete files should be false after clicking checkbox again", action.isDeleteSourceFilesEnabled());
        }

    @Test
    public void importButton()
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

        clickOn(id(IMPORT_BUTTON_ID));

        Assert.assertTrue("import not pressed", _import_pressed.get());
        }

    @Test

    public void cancelButton()
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

        Assert.assertTrue("import not pressed", _cancel_pressed.get());
        }

    private void setupCandidates()
        {
        File import_folder = TestResources.getFile(PROJECT_FOLDER, getClass());
        File[] import_files = import_folder.listFiles((dir, name) -> (name.endsWith(".html")));

        _candidates = ImportCandidates.build(_project, import_files);
        Platform.runLater(() -> _import_pane.setCandidates(_candidates));
        waitForUiEvents();
        }

    @Override
    protected Node createComponentNode() throws Exception
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


