package org.museautomation.ui.ide.navigation;

import org.junit.jupiter.api.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.steptask.*;
import org.museautomation.core.variables.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.ide.navigation.resources.*;
import org.museautomation.ui.ide.navigation.resources.actions.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ResourceActionTests
    {
    @Test
    void createResource()
        {
        UndoStack undo = new UndoStack();
        CreateResourceAction action = new CreateResourceAction(new VariableList.VariableListResourceType(), "new_list", _project);
        action.execute(undo);
        ResourceToken<MuseResource> token = action.getToken();

        Assertions.assertNotNull(token, "didn't get a resource token");
        Assertions.assertNotNull(_project.getResourceStorage().getResource(token), "can't find new resource in the project");

        undo.undoLastAction();

        Assertions.assertNull(_project.getResourceStorage().getResource(token), "new resource was not removed by undo");
        }

    @Test
    void copyResource()
        {
        UndoStack undo = new UndoStack();
        UndoableAction action = CopyResourceAction.create(Collections.singletonList(_project.getResourceStorage().findResource(LIST1_ID)), _project);
        action.execute(undo);

        Assertions.assertNotNull(_project.getResourceStorage().findResource(LIST1_COPIED_ID), "copy not copied to project");
        Assertions.assertEquals(LIST1_COPIED_ID, _listener._added, "listener was not notified copy");

        undo.undoLastAction();

        Assertions.assertNull(_project.getResourceStorage().findResource(LIST1_COPIED_ID), "copy was not un-copied from project");
        Assertions.assertEquals(LIST1_COPIED_ID, _listener._deleted, "listener was not notified of copy");
        }

    @Test
    void copyMultipleResources()
        {
        UndoStack undo = new UndoStack();
        List<ResourceToken<MuseResource>> tokens = new ArrayList<>();
        tokens.add(_project.getResourceStorage().findResource(LIST1_ID));
        tokens.add(_project.getResourceStorage().findResource(LIST2_ID));
        UndoableAction action = CopyResourceAction.create(tokens, _project);
        action.execute(undo);

        Assertions.assertNotNull(_project.getResourceStorage().findResource(LIST1_COPIED_ID), "copy 1 not copied to project");
        Assertions.assertNotNull(_project.getResourceStorage().findResource(LIST2_COPIED_ID), "copy 2 not copied to project");
        Assertions.assertEquals(LIST1_COPIED_ID, _listener._added, "listener was not notified of copy 1");
        Assertions.assertEquals(LIST2_COPIED_ID, _listener._added2, "listener was not notified of copy 2");

        undo.undoLastAction();

        Assertions.assertNull(_project.getResourceStorage().findResource(LIST1_COPIED_ID), "copy 1 was not un-copied from project");
        Assertions.assertNull(_project.getResourceStorage().findResource(LIST2_COPIED_ID), "copy 2 was not un-copied from project");
        Assertions.assertEquals(LIST1_COPIED_ID, _listener._deleted2, "listener was not notified of un-copy 1");  // note that they are removed in reverse order
        Assertions.assertEquals(LIST2_COPIED_ID, _listener._deleted, "listener was not notified of un-copy 2");
        }

    @Test
    void deleteResource()
        {
        UndoStack undo = new UndoStack();
        DeleteResourceAction action = new DeleteResourceAction(_project.getResourceStorage().findResource(LIST1_ID), _project);
        action.execute(undo);

        Assertions.assertNull(_project.getResourceStorage().findResource(LIST1_ID), "new resource was not deleted from project");
        Assertions.assertEquals(LIST1_ID, _listener._deleted, "listener was not notified of delete");

        undo.undoLastAction();

        Assertions.assertNotNull(_project.getResourceStorage().findResource(LIST1_ID), "new resource not un-deleted from project");
        Assertions.assertEquals(LIST1_ID, _listener._added, "listener was not notified of un-delete");
        }

    @Test
    void deleteMultipleResources()
        {
        UndoStack undo = new UndoStack();
        List<ResourceToken<MuseResource>> tokens = new ArrayList<>();
        tokens.add(_project.getResourceStorage().findResource(LIST1_ID));
        tokens.add(_project.getResourceStorage().findResource(LIST2_ID));
        UndoableAction action = DeleteResourceAction.create(tokens, _project);
        action.execute(undo);

        Assertions.assertNull(_project.getResourceStorage().findResource(LIST1_ID), "copy 1 not deleted from project");
        Assertions.assertNull(_project.getResourceStorage().findResource(LIST2_ID), "copy 2 not deleted from project");
        Assertions.assertEquals(LIST1_ID, _listener._deleted, "listener was not notified of delete 1");
        Assertions.assertEquals(LIST2_ID, _listener._deleted2, "listener was not notified of delete 2");

        undo.undoLastAction();

        Assertions.assertNotNull(_project.getResourceStorage().findResource(LIST1_ID), "copy 1 was not un-deleted from project");
        Assertions.assertNotNull(_project.getResourceStorage().findResource(LIST2_ID), "copy 2 was not un-deleted from project");
        Assertions.assertEquals(LIST1_ID, _listener._added2, "listener was not notified of un-copy 1");  // note that they are removed in reverse order
        Assertions.assertEquals(LIST2_ID, _listener._added, "listener was not notified of un-copy 2");
        }

    @Test
    void addResource()
        {
        final String new_id = "new_resource";
        MuseResource resource = createResource(new_id);
        AddResourceAction add = new AddResourceAction(resource, _project);
        Assertions.assertTrue(add.execute(new UndoStack()));
        Assertions.assertEquals(resource, _project.getResourceStorage().getResource(new_id));
        }

    @Test
    void dontAddDuplicateResourceId()
        {
        AddResourceAction add = new AddResourceAction(createResource(LIST1_ID), _project);
        Assertions.assertFalse(add.execute(new UndoStack()));
        }

    @BeforeEach
    void setup() throws IOException
        {
        _project = new SimpleProject();
        VariableList list = new VariableList();
        list.setId(LIST1_ID);
        _project.getResourceStorage().addResource(list);
        list = new VariableList();
        list.setId(LIST2_ID);
        _project.getResourceStorage().addResource(list);

        _listener = new MockProjectResourceListener();
        _project.addResourceListener(_listener);
        }

    private MuseResource createResource(String resource_id)
        {
        MuseTask test = new SteppedTask();
        test.setId(resource_id);
        return test;
        }

    static private class MockProjectResourceListener implements ProjectResourceListener
        {
        @Override
        public void resourceAdded(ResourceToken added)
            {
            if (_added == null)
                _added = added.getId();
            else
                _added2 = added.getId();
            }

        @Override
        public void resourceRemoved(ResourceToken removed)
            {
            if (_deleted == null)
                _deleted = removed.getId();
            else
                _deleted2 = removed.getId();
            }

        String _added = null;
        String _added2 = null;
        String _deleted = null;
        String _deleted2 = null;
        }

    private MuseProject _project;
    private MockProjectResourceListener _listener;

    private final static String LIST1_ID = "list1";
    private final static String LIST2_ID = "list2";
    private final static String LIST1_COPIED_ID = ResourceIdSuggestions.suggestCopy(LIST1_ID, new SimpleProject());
    private final static String LIST2_COPIED_ID = ResourceIdSuggestions.suggestCopy(LIST2_ID, new SimpleProject());
    }