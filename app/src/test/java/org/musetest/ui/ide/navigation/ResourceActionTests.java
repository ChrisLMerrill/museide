package org.musetest.ui.ide.navigation;

import org.junit.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.resource.*;
import org.musetest.core.steptest.*;
import org.musetest.core.variables.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.ide.navigation.resources.*;
import org.musetest.ui.ide.navigation.resources.actions.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourceActionTests
    {
    @Test
    public void createResource()
        {
        UndoStack undo = new UndoStack();
        CreateResourceAction action = new CreateResourceAction(new VariableList.VariableListResourceType(), "new_list", _project);
        action.execute(undo);
        ResourceToken token = action.getToken();

        Assert.assertNotNull("didn't get a resource token", token);
        Assert.assertNotNull("can't find new resource in the project", _project.getResourceStorage().getResource(token));

        undo.undoLastAction();

        Assert.assertNull("new resource was not removed by undo", _project.getResourceStorage().getResource(token));
        }

    @Test
    public void copyResource()
        {
        UndoStack undo = new UndoStack();
        UndoableAction action = CopyResourceAction.create(Collections.singletonList(_project.getResourceStorage().findResource(LIST1_ID)), _project);
        action.execute(undo);

        Assert.assertNotNull("copy not copied to project", _project.getResourceStorage().findResource(LIST1_COPIED_ID));
        Assert.assertEquals("listener was not notified copy", LIST1_COPIED_ID, _listener._added);

        undo.undoLastAction();

        Assert.assertNull("copy was not un-copied from project", _project.getResourceStorage().findResource(LIST1_COPIED_ID));
        Assert.assertEquals("listener was not notified of copy", LIST1_COPIED_ID, _listener._deleted);
        }

    @Test
    public void copyMultipleResources()
        {
        UndoStack undo = new UndoStack();
        List<ResourceToken> tokens = new ArrayList<>();
        tokens.add(_project.getResourceStorage().findResource(LIST1_ID));
        tokens.add(_project.getResourceStorage().findResource(LIST2_ID));
        UndoableAction action = CopyResourceAction.create(tokens, _project);
        action.execute(undo);

        Assert.assertNotNull("copy 1 not copied to project", _project.getResourceStorage().findResource(LIST1_COPIED_ID));
        Assert.assertNotNull("copy 2 not copied to project", _project.getResourceStorage().findResource(LIST2_COPIED_ID));
        Assert.assertEquals("listener was not notified of copy 1", LIST1_COPIED_ID, _listener._added);
        Assert.assertEquals("listener was not notified of copy 2", LIST2_COPIED_ID, _listener._added2);

        undo.undoLastAction();

        Assert.assertNull("copy 1 was not un-copied from project", _project.getResourceStorage().findResource(LIST1_COPIED_ID));
        Assert.assertNull("copy 2 was not un-copied from project", _project.getResourceStorage().findResource(LIST2_COPIED_ID));
        Assert.assertEquals("listener was not notified of un-copy 1", LIST1_COPIED_ID, _listener._deleted2);  // note that they are removed in reverse order
        Assert.assertEquals("listener was not notified of un-copy 2", LIST2_COPIED_ID, _listener._deleted);
        }

    @Test
    public void deleteResource()
        {
        UndoStack undo = new UndoStack();
        DeleteResourceAction action = new DeleteResourceAction(_project.getResourceStorage().findResource(LIST1_ID), _project);
        action.execute(undo);

        Assert.assertNull("new resource was not deleted from project", _project.getResourceStorage().findResource(LIST1_ID));
        Assert.assertEquals("listener was not notified of delete", LIST1_ID, _listener._deleted);

        undo.undoLastAction();

        Assert.assertNotNull("new resource not un-deleted from project", _project.getResourceStorage().findResource(LIST1_ID));
        Assert.assertEquals("listener was not notified of un-delete", LIST1_ID, _listener._added);
        }

    @Test
    public void deleteMultipleResources()
        {
        UndoStack undo = new UndoStack();
        List<ResourceToken> tokens = new ArrayList<>();
        tokens.add(_project.getResourceStorage().findResource(LIST1_ID));
        tokens.add(_project.getResourceStorage().findResource(LIST2_ID));
        UndoableAction action = DeleteResourceAction.create(tokens, _project);
        action.execute(undo);

        Assert.assertNull("copy 1 not deleted from project", _project.getResourceStorage().findResource(LIST1_ID));
        Assert.assertNull("copy 2 not deleted from project", _project.getResourceStorage().findResource(LIST2_ID));
        Assert.assertEquals("listener was not notified of delete 1", LIST1_ID, _listener._deleted);
        Assert.assertEquals("listener was not notified of delete 2", LIST2_ID, _listener._deleted2);

        undo.undoLastAction();

        Assert.assertNotNull("copy 1 was not un-deleted from project", _project.getResourceStorage().findResource(LIST1_ID));
        Assert.assertNotNull("copy 2 was not un-deleted from project", _project.getResourceStorage().findResource(LIST2_ID));
        Assert.assertEquals("listener was not notified of un-copy 1", LIST1_ID, _listener._added2);  // note that they are removed in reverse order
        Assert.assertEquals("listener was not notified of un-copy 2", LIST2_ID, _listener._added);
        }

    @Test
    public void addResource()
        {
        final String new_id = "new_resource";
        MuseResource resource = createResource(new_id);
        AddResourceAction add = new AddResourceAction(resource, _project);
        Assert.assertTrue(add.execute(new UndoStack()));
        Assert.assertEquals(resource, _project.getResourceStorage().getResource(new_id));
        }

    @Test
    public void dontAddDuplicateResourceId()
        {
        AddResourceAction add = new AddResourceAction(createResource(LIST1_ID), _project);
        Assert.assertFalse(add.execute(new UndoStack()));
        }

    @Before
    public void setup() throws IOException
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
        MuseTest test = new SteppedTest();
        test.setId(resource_id);
        return test;
        }

    private class MockProjectResourceListener implements ProjectResourceListener
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


