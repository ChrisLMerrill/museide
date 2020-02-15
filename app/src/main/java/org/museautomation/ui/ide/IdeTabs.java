package org.museautomation.ui.ide;

import javafx.scene.control.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.ui.extend.edit.*;

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
    IdeTabs(TabPane tabs)
        {
        _tabs = tabs;
        }

    @Override
    public boolean editResource(ResourceToken token, MuseProject project)
        {
        MuseResource resource = project.getResourceStorage().getResource(token);
        Tab existing_tab = findExistingEditor(resource);
        if (existing_tab != null)
            {
            _tabs.getSelectionModel().select(existing_tab);
            return true;
            }

        MuseResourceEditor editor = EditorSelector.get(project).get(resource);
        if (editor == null)
            return false;
        editor.editResource(project, resource);

        Tab new_tab = new Tab();
        new_tab.setContent(editor.getNode());
        new_tab.setText(resource.getId());
        _tabs.getTabs().add(new_tab);
        _tabs.getSelectionModel().select(new_tab);

        _editors.add(new ResourceEditorToken(resource, editor, new_tab));

        new_tab.setOnClosed((event) ->
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
                removeEditorFromList(new_tab);
                editor.dispose();
                }
            });

        return true;
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

    private void removeEditorFromList(Tab to_remove)
        {
        for (ResourceEditorToken editor : _editors)
            if (editor._tab == to_remove)
                {
                _editors.remove(editor);
                return;
                }
        }

    private Tab findExistingEditor(MuseResource resource)
        {
        for (ResourceEditorToken editor : _editors)
            if (editor._resource == resource)
                return editor._tab;
        return null;
        }

    @Override
    public void closeAll()
        {
        _tabs.getTabs().clear();
        _editors.clear();
        }

    private final TabPane _tabs;
    private final List<ResourceEditorToken> _editors = new ArrayList<>();

    class ResourceEditorToken
        {
        ResourceEditorToken(MuseResource resource, MuseResourceEditor editor, Tab tab)
            {
            _resource = resource;
            _editor = editor;
            _tab = tab;
            }

        MuseResource _resource;
        MuseResourceEditor _editor;
        Tab _tab;
        }
    }


