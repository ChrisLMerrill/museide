package org.musetest.ui.editors.driver;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.core.util.OperatingSystem;
import org.musetest.selenium.providers.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class WebdriverProviderEditorTests extends ComponentTest
    {
    @Test
    public void remoteDriverEditor()
        {
        RemoteDriverProviderEditor editor = new RemoteDriverProviderEditor();
        Platform.runLater(() -> _root.setTop(editor.getNode()));
        waitForUiEvents();
        RemoteDriverProvider remote = new RemoteDriverProvider();
        remote.setUrl("url1");

        UndoStack undo = new UndoStack();
        editor.edit(remote, undo);
        waitForUiEvents();
        Assert.assertEquals("url1", textOf(id(RemoteDriverProviderEditor.URL_FIELD_ID)));

        fillFieldAndTabAway(id(RemoteDriverProviderEditor.URL_FIELD_ID), "url2");
        Assert.assertEquals(1, undo.getNumberOfUndoableActions());
        Assert.assertEquals("url2", remote.getUrl());

        undo.undoLastAction();
        Assert.assertEquals("url1", remote.getUrl());
        Assert.assertEquals("url1", textOf(id(RemoteDriverProviderEditor.URL_FIELD_ID)));
        }

    @Test
    public void localBinaryDriverEditor()
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
        Assert.assertEquals(start_path, textOf(id(LocalBinaryDriverProviderEditor.PATH_FIELD_ID)));
        Assert.assertTrue(exists(LocalBinaryDriverProviderEditor.RELATIVE_PATH_LABEL));

        // switch path type to absolute
        clickOn(LocalBinaryDriverProviderEditor.RELATIVE_PATH_LABEL).clickOn(LocalBinaryDriverProviderEditor.ABSOLUTE_PATH_LABEL);
        Assert.assertTrue(exists(LocalBinaryDriverProviderEditor.ABSOLUTE_PATH_LABEL));
        Assert.assertEquals(start_path, chrome.getAbsolutePath());
        Assert.assertNull(chrome.getRelativePath());
        Assert.assertEquals(1, undo.getNumberOfUndoableActions());

        // change the path
        fillFieldAndTabAway(id(LocalBinaryDriverProviderEditor.PATH_FIELD_ID), changed_path);
        Assert.assertEquals(changed_path, chrome.getAbsolutePath());
        Assert.assertEquals(2, undo.getNumberOfUndoableActions());

        // change the OS
        clickOn(OperatingSystem.Windows.name()).clickOn(OperatingSystem.Linux.name());
        Assert.assertEquals(OperatingSystem.Linux, chrome.getOs());

        // revert the OS change
        undo.undoLastAction();
        Assert.assertEquals(OperatingSystem.Windows, chrome.getOs());

        // revert the path change
        undo.undoLastAction();
        Assert.assertEquals(start_path, chrome.getAbsolutePath());
        Assert.assertNull(chrome.getRelativePath());

        // revert the path type change
        undo.undoLastAction();
        Assert.assertEquals(start_path, chrome.getRelativePath());
        Assert.assertNull(chrome.getAbsolutePath());
        }

    @Test
    public void changeToRelativePath()
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

        Assert.assertEquals(1, undo.getNumberOfUndoableActions());
        Assert.assertEquals(start_path, chrome.getRelativePath());
        Assert.assertNull(chrome.getAbsolutePath());
        }
    @Override
    protected Node createComponentNode()
        {
        _root.setBottom(new Button("accepts focus"));
        return _root;
        }

    private BorderPane _root = new BorderPane();
    }


