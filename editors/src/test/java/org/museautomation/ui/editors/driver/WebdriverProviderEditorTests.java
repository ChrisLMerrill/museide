package org.museautomation.ui.editors.driver;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.util.OperatingSystem;
import org.museautomation.selenium.providers.*;
import org.museautomation.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class WebdriverProviderEditorTests extends ComponentTest
    {
    @Test
    void remoteDriverEditor()
        {
        RemoteDriverProviderEditor editor = new RemoteDriverProviderEditor();
        Platform.runLater(() -> _root.setTop(editor.getNode()));
        waitForUiEvents();
        RemoteDriverProvider remote = new RemoteDriverProvider();
        remote.setUrl("url1");

        UndoStack undo = new UndoStack();
        editor.edit(remote, undo);
        waitForUiEvents();
        Assertions.assertEquals("url1", textOf(id(RemoteDriverProviderEditor.URL_FIELD_ID)));

        fillFieldAndTabAway(id(RemoteDriverProviderEditor.URL_FIELD_ID), "url2");
        Assertions.assertEquals(1, undo.getNumberOfUndoableActions());
        Assertions.assertEquals("url2", remote.getUrl());

        undo.undoLastAction();
        Assertions.assertEquals("url1", remote.getUrl());
        Assertions.assertEquals("url1", textOf(id(RemoteDriverProviderEditor.URL_FIELD_ID)));
        }

    @Test
    void localBinaryDriverEditor()
        {
        UndoStack undo = new UndoStack();
        LocalBinaryDriverProviderEditor editor = new LocalBinaryDriverProviderEditor();
        Platform.runLater(() -> _root.setTop(editor.getNode()));
        waitForUiEvents();

        // setup provider
        final String start_path = "/starting/path/to/driver";
        final String changed_path = "/new/path";
        ChromeDriverProvider chrome = new ChromeDriverProvider();
        chrome.setOs(OperatingSystem.Windows);
        chrome.setRelativePath(start_path);

        editor.edit(chrome, undo);
        waitForUiEvents();

        // check path shown correctly
        Assertions.assertEquals(start_path, textOf(id(LocalBinaryDriverProviderEditor.PATH_FIELD_ID)));
        Assertions.assertTrue(exists(LocalBinaryDriverProviderEditor.RELATIVE_PATH_LABEL));

        // switch path type to absolute
        clickOn(LocalBinaryDriverProviderEditor.RELATIVE_PATH_LABEL).clickOn(LocalBinaryDriverProviderEditor.ABSOLUTE_PATH_LABEL);
        Assertions.assertTrue(exists(LocalBinaryDriverProviderEditor.ABSOLUTE_PATH_LABEL));
        Assertions.assertEquals(start_path, chrome.getAbsolutePath());
        Assertions.assertNull(chrome.getRelativePath());
        Assertions.assertEquals(1, undo.getNumberOfUndoableActions());

        // change the path
        fillFieldAndTabAway(id(LocalBinaryDriverProviderEditor.PATH_FIELD_ID), changed_path);
        Assertions.assertEquals(changed_path, chrome.getAbsolutePath());
        Assertions.assertEquals(2, undo.getNumberOfUndoableActions());

        // change the OS
        clickOn(OperatingSystem.Windows.name()).clickOn(OperatingSystem.Linux.name());
        Assertions.assertEquals(OperatingSystem.Linux, chrome.getOs());

        // revert the OS change
        undo.undoLastAction();
        Assertions.assertEquals(OperatingSystem.Windows, chrome.getOs());

        // revert the path change
        undo.undoLastAction();
        Assertions.assertEquals(start_path, chrome.getAbsolutePath());
        Assertions.assertNull(chrome.getRelativePath());

        // revert the path type change
        undo.undoLastAction();
        Assertions.assertEquals(start_path, chrome.getRelativePath());
        Assertions.assertNull(chrome.getAbsolutePath());
        }

    @Test
    void changeToRelativePath()
        {
        UndoStack undo = new UndoStack();
        LocalBinaryDriverProviderEditor editor = new LocalBinaryDriverProviderEditor();
        Platform.runLater(() -> _root.setTop(editor.getNode()));
        waitForUiEvents();

        // setup provider
        final String start_path = "/starting/path/to/driver";
        ChromeDriverProvider chrome = new ChromeDriverProvider();
        chrome.setOs(OperatingSystem.Windows);
        chrome.setAbsolutePath(start_path);

        editor.edit(chrome, undo);
        waitForUiEvents();

        // switch path type to relative
        clickOn(LocalBinaryDriverProviderEditor.ABSOLUTE_PATH_LABEL).clickOn(LocalBinaryDriverProviderEditor.RELATIVE_PATH_LABEL);
        waitForUiEvents();

        Assertions.assertEquals(1, undo.getNumberOfUndoableActions());
        Assertions.assertEquals(start_path, chrome.getRelativePath());
        Assertions.assertNull(chrome.getAbsolutePath());
        }
    @Override
    protected Node createComponentNode()
        {
        _root.setBottom(new Button("accepts focus"));
        return _root;
        }

    private BorderPane _root = new BorderPane();
    }