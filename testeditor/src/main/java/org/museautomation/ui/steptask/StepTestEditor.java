package org.museautomation.ui.steptask;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import org.controlsfx.control.*;
import org.controlsfx.control.action.*;
import org.museautomation.core.*;
import org.museautomation.core.context.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.step.*;
import org.museautomation.core.steptask.*;
import org.museautomation.core.task.*;
import org.museautomation.core.task.input.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.events.*;
import org.museautomation.ui.extend.glyphs.*;
import org.museautomation.ui.extend.javafx.*;
import org.museautomation.ui.steptask.actions.*;
import org.museautomation.ui.steptask.execution.*;
import org.museautomation.ui.steptree.*;
import org.museautomation.ui.taskinput.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Edit a SteppedTask (or anything else that implements ContainsStep, in theory).
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused")  // invoked via reflection that finds MuseResourceEditors dynamically
public class StepTestEditor extends BaseResourceEditor implements SteppedTaskProvider, InteractiveTaskStateListener
    {
    public StepTestEditor()
        {
        _controller.addListener(this);
        _controller.addInputProvider(provider);
        }

    @Override
    public boolean canEdit(MuseResource resource)
        {
        return resource instanceof ContainsStep;
        }

    @Override
    public void editResource(MuseProject project, MuseResource resource)
        {
        super.editResource(project, resource);

        if (getResource() instanceof SteppedTask)
            _task = (SteppedTask) getResource();
        else
            {
            ContainsStep steps = (ContainsStep) getResource();
            _task = new SteppedTask(steps.getStep());  // Ok, this is a hack. Need to abstract this in some intelligent way.
            }

        new StepTaskUpgrades(_task, project).apply();
        if (_step_tree == null)
            _step_tree = new StepTree2(project, _task.getStep(), getUndoStack(), _controller);
        }

    @Override
    protected Parent getEditorArea()
        {
        _event_table = createEventTable();

        _splitter = new SplitPane();
        _splitter.setOrientation(Orientation.VERTICAL);

        _splitter.getItems().add(_step_tree.getNode());

        Tab step_tab = new Tab("Steps");
        step_tab.setContent(_splitter);
        step_tab.closableProperty().setValue(false);

        ParamsTab params_tab = new ParamsTab(getProject(), getUndoStack(), _task);

        MetadataTab metadata_tab = new MetadataTab(getUndoStack());
        metadata_tab.setsetResource(_task);

        TabPane tabs = new TabPane(step_tab, params_tab.getTab(), metadata_tab.getTab());
        tabs.sideProperty().setValue(Side.LEFT);
        return tabs;
        }

    @Override
    protected void addButtons(GridPane button_bar)
        {
        HBox edit_buttons = new HBox();
        edit_buttons.setPadding(new Insets(4, 4, 4, 4));
        edit_buttons.setAlignment(Pos.TOP_LEFT);
        edit_buttons.setSpacing(4);
        button_bar.add(edit_buttons, button_bar.getChildren().size(), 0);
        GridPane.setHgrow(edit_buttons, Priority.ALWAYS);

        HBox run_buttons = new HBox();
        run_buttons.setPadding(new Insets(4, 4, 4, 4));
        run_buttons.setAlignment(Pos.TOP_CENTER);
        run_buttons.setSpacing(4);
        button_bar.add(run_buttons, button_bar.getChildren().size(), 0);
        GridPane.setHgrow(run_buttons, Priority.ALWAYS);

        AddStepButton add_button = new AddStepButton(getProject(), _step_tree);
        edit_buttons.getChildren().add(add_button.getButton());
        new TreeItemSelectedNodeEnabler(_step_tree.getTree()).setNode(add_button.getButton());

        Button play = new Button("Play", Glyphs.create("FA:PLAY"));
        play.setTooltip(new Tooltip("Start the test"));
        run_buttons.getChildren().add(play);
        new RunTestEnabler(_controller).setNode(play);
        play.setOnAction(event ->
            {
            if (_controller.getState().equals(InteractiveTaskState.PAUSED))
                _controller.resume();
            else
                _controller.run(StepTestEditor.this);
            });

        Button step = new Button("Step", Glyphs.create("FA:STEP_FORWARD"));
        step.setTooltip(new Tooltip("Execute a single step (or start the test and then pause)"));
        run_buttons.getChildren().add(step);
        new RunTestEnabler(_controller).setNode(step);
        step.setOnAction(event ->
            {
            if (_controller.getState().equals(InteractiveTaskState.IDLE))
                _controller.runOneStep(StepTestEditor.this);
            else
                _controller.step();
            });

        Button pause = new Button("Pause", Glyphs.create("FA:PAUSE"));
        pause.setTooltip(new Tooltip("Pause the test after the current step completes"));
        run_buttons.getChildren().add(pause);
        new PauseTestEnabler(_controller).setNode(pause);
        pause.setOnAction(event -> _controller.pause());

        Button stop = new Button("Stop", Glyphs.create("FA:STOP"));
        stop.setTooltip(new Tooltip("Terminate the test after the current step completes"));
        run_buttons.getChildren().add(stop);
        new StopTestEnabler(_controller).setNode(stop);
        stop.setOnAction(event -> _controller.stop());
        }

    @Override
    public void dispose()
	    {
	    super.dispose();
	    _step_tree.dispose();
	    }

    private Node createEventTable()
        {
        EventTable table = new EventTable(getProject());
        EventTableController event_table_controller = new EventTableController(table, _controller);
        event_table_controller.setTask(_task);

        Node table_node = table.getNode();
        AnchorPane.setTopAnchor(table_node, 0d);
        AnchorPane.setBottomAnchor(table_node, 0d);
        AnchorPane.setLeftAnchor(table_node, 0d);
        AnchorPane.setRightAnchor(table_node, 0d);

        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(table_node);
        return pane;
        }

    @Override
    public SteppedTask getTask()
        {
        return _task;
        }

    private void showEventLog()
        {
        _splitter.getItems().add(_event_table);
        _splitter.setDividerPositions(_divider_pos);
        }

    private void hideEventLog()
        {
        if (_splitter.getItems().size() > 1)
            {
            _divider_pos = _splitter.getDividerPositions()[0];
            _splitter.getItems().remove(_splitter.getItems().size() - 1);
            }
        }

    @Override
    public void stateChanged(InteractiveTaskState state)
        {
        if (state.equals(InteractiveTaskState.STARTING))
            {
            TaskRunner runner = _controller.getTestRunner();
            _context = runner.getExecutionContext();
            Platform.runLater(StepTestEditor.this::showEventLog);
            }
        else if (state.equals(InteractiveTaskState.IDLE))
            {
            // show the notifier - tell the user that the test is over, but leave the step tree in the running state (so they can review if desired) and give the user a reset button
            Platform.runLater(() ->
                {
                TaskResult result = TaskResult.find(_context);
                NotificationPane notifier = getNotifier();
                notifier.getActions().clear();
                notifier.setOnHidden(event ->
                    {
                    event.consume();
                    notifier.hide();
                    hideEventLog();
                    });
                notifier.getActions().add(new Action("Reset Editor", event ->
                    {
                    event.consume();
                    notifier.hide();
                    hideEventLog();
                    }));
                if (result == null)
                    {
                    notifier.setText("Test stopped (cannot find result to evaluate).");
                    notifier.setGraphic(Glyphs.create("FA:POWER_OFF", Color.BLACK));
                    }
                else if (result.isPass())
                    {
                    notifier.setText("Test passed.");
                    notifier.setGraphic(Glyphs.create("FA:CHECK", Color.GREEN));
                    }
                else
                    {
                    notifier.setText(result.getSummary());
                    notifier.setGraphic(Glyphs.create("FA:REMOVE", Color.RED));
                    }
                notifier.show();
                });
            }
        }

    @Override
    public ValidationStateSource getValidationStateSource()
        {
        return null; // TODO
        }

    @Override
    public void requestFocus()
        {
        _step_tree.getNode().requestFocus();
        }

    private SteppedTask _task;
    private TaskExecutionContext _context = null;

    private SplitPane _splitter;
    private StepTree2 _step_tree;
    private double _divider_pos = 0.7;
    private Node _event_table;

    private final InteractiveTestControllerImpl _controller = new InteractiveTestControllerImpl();

    TaskInputProvider provider = new TaskInputProvider()  // TODO build a provider GUI
        {
        @Override
        public List<ResolvedTaskInput> resolveInputs(TaskInputResolutionResults resolved, TaskInputSet inputs, MuseExecutionContext context)
            {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<List<ResolvedTaskInput>> list_holder = new AtomicReference<>(null);
            Platform.runLater(() ->
                {
                TaskInputDialog dialog = new TaskInputDialog(inputs, context);
                Optional<List<ResolvedTaskInput>> result = dialog.createDialog().showAndWait();
                list_holder.set(result.orElse(Collections.emptyList()));
                latch.countDown();
                });

            try
                {
                latch.await();
                }
            catch (InterruptedException e)
                {
                LOG.error("TaskInputDialog was interrupted before completion.");
                }
            return list_holder.get();
            }

        @Override
        public String getDescription()
            {
            return "default interactive TaskInputProvider";
            }
        };

    final static Logger LOG = LoggerFactory.getLogger(StepTestEditor.class);
    }