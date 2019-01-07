package org.musetest.ui.ide;

import com.anchorage.docks.node.*;
import com.anchorage.docks.stations.*;
import com.anchorage.system.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import org.musetest.core.*;
import org.musetest.core.resource.*;
import org.musetest.ui.extend.edit.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Opens new resource editors, positions them in the right place and keeps track
 * of them.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class IdeTabs implements ResourceEditors
    {
    public IdeTabs(DockStation station)
        {
        _station = station;
        installPlaceholder();
        }

    private void installPlaceholder()
        {
        if (_placeholder_node == null)
            {
            _placeholder_node = AnchorageSystem.createDock("Editor", new Label("Double-click a resource\n(in the project tree on the left)\nto edit it here"));
            _placeholder_node.floatableProperty().setValue(false);
            _placeholder_node.closeableProperty().setValue(false);
            _placeholder_node.maximizableProperty().setValue(false);
            _placeholder_node.dock(_station, DockNode.DockPosition.RIGHT, 0.25);
            }
        }

    private void removePlaceholder()
        {
        if (_placeholder_node != null)
            {
            _placeholder_node.undock();
            _placeholder_node = null;
            }
        }

    @Override
    public boolean editResource(ResourceToken token, MuseProject project)
        {
        pruneUndockedEditors();
        MuseResource resource = project.getResourceStorage().getResource(token);
        DockNode existing_docked = findExistingEditor(resource);
        if (existing_docked != null)
            {
            try
                {
                existing_docked.ensureVisibility();
                return true;
                }
            catch (Exception e)
                {
                // couldn't re-show it...so contine and open new
                }
            }

        MuseResourceEditor editor = EditorSelector.get(project).get(resource);
        if (editor == null)
            return false;
        editor.editResource(project, resource);

        DockNode docked_editor = AnchorageSystem.createDock(resource.getId(), editor.getNode());
        docked_editor.floatableProperty().setValue(false);
        DockNode dock_over = findAnyEditorNode();
        docked_editor.dock(dock_over, DockNode.DockPosition.CENTER);
        _editors.add(new ResourceEditorToken(resource, editor, docked_editor));

        docked_editor.setCloseRequestHandler(() ->
            {
            AtomicBoolean allow_close = new AtomicBoolean(true);
            if (editor.isChanged())
                {
                allow_close.set(SaveChangesDialog.createShowAndWait(
                    editor::saveChanges,
                    editor::revertChanges
                    ));
                }

            if (allow_close.get())
                {
                removeEditorFromList(docked_editor);
                if (_editors.size() == 0)
                    Platform.runLater(this::installPlaceholder);
                editor.dispose();
                }

            return allow_close.get();
            });

        removePlaceholder();

        return true;
        }

    public void showInTab(Node viewer, String name)
        {
        pruneUndockedEditors();

        DockNode docked_node = AnchorageSystem.createDock(name, viewer);
        DockNode dock_over = findAnyEditorNode();
        docked_node.dock(dock_over, DockNode.DockPosition.CENTER);

/*
        docked_node.setCloseRequestHandler(() ->
            {
            AtomicBoolean allow_close = new AtomicBoolean(true);
            if (editor.isChanged())
                {
                allow_close.set(SaveChangesDialog.createShowAndWait(
                    editor::saveChanges,
                    editor::revertChanges
                    ));
                }

            if (allow_close.get())
                {
                removeEditorFromList(docked_node);
                if (_editors.size() == 0)
                    Platform.runLater(this::installPlaceholder);
                editor.dispose();
                }

            return allow_close.get();
            });
*/
        }

    @Override
    public boolean hasUnsavedChanges()
        {
        for (ResourceEditorToken editor : _editors)
            if (editor._editor.isChanged())
                return true;
        return false;
        }

    @Override
    public String saveAllChanges()
        {
        for (ResourceEditorToken editor : _editors)
            if (editor._editor.isChanged())
                {
                String error = editor._editor.saveChanges();
                if (error != null)
                    return error;
                }
        return null;
        }

    @Override
    public void revertAllChanges()
        {
        for (ResourceEditorToken editor : _editors)
            if (editor._editor.isChanged())
                editor._editor.revertChanges();
        }

    private void removeEditorFromList(DockNode to_remove)
        {
        for (ResourceEditorToken editor : _editors)
            if (editor._docked == to_remove)
                {
                _editors.remove(editor);
                return;
                }
        }

    private DockNode findExistingEditor(MuseResource resource)
        {
        pruneUndockedEditors();
        for (ResourceEditorToken editor : _editors)
            if (editor._resource == resource)
                return editor._docked;
        return null;
        }

    private DockNode findAnyEditorNode()
        {
        pruneUndockedEditors();
        if (_editors.size() > 0)
            return _editors.get(0)._docked;
        else
            {
            installPlaceholder();
            return _placeholder_node;
            }
        }

    private void pruneUndockedEditors()
        {
        int i = 0;
        while (i < _editors.size())
            {
            ResourceEditorToken token = _editors.get(i);
            if (token._docked.getParentContainer() == null)
                _editors.remove(i);
            else
                i++;
            }
        }

    private final DockStation _station;
    private final List<ResourceEditorToken> _editors = new ArrayList<>();
    private DockNode _placeholder_node;

    class ResourceEditorToken
        {
        public ResourceEditorToken(MuseResource resource, MuseResourceEditor editor, DockNode docked)
            {
            _resource = resource;
            _editor = editor;
            _docked = docked;
            }

        MuseResource _resource;
        MuseResourceEditor _editor;
        DockNode _docked;
        }
    }


