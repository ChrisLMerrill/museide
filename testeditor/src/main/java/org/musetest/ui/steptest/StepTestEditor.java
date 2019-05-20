package org.musetest.ui.steptest;

import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import org.controlsfx.control.*;
import org.controlsfx.control.action.*;
import org.musetest.core.*;
import org.musetest.core.context.*;
import org.musetest.core.execution.*;
import org.musetest.core.step.*;
import org.musetest.core.steptest.*;
import org.musetest.core.values.*;
import org.musetest.ui.event.*;
import org.musetest.ui.extend.edit.*;
import org.musetest.ui.extend.edit.step.*;
import org.musetest.ui.extend.glyphs.*;
import org.musetest.ui.extend.javafx.*;
import org.musetest.ui.steptest.actions.*;
import org.musetest.ui.steptest.execution.*;
import org.musetest.ui.steptree.*;
import org.musetest.ui.valuesource.map.*;
import org.slf4j.*;

import java.util.*;

/**
 * Edit a SteppedTest (or anything else that implements ContainsStep, in theory).
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused")  // invoked via reflection that finds MuseResourceEditors dynamically
public class StepTestEditor extends BaseResourceEditor implements SteppedTestProvider, InteractiveTestStateListener
    {
    public StepTestEditor()
        {
        _controller.addListener(this);
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

        if (getResource() instanceof SteppedTest)
            _test = (SteppedTest) getResource();
        else
            {
            ContainsStep steps = (ContainsStep) getResource();
            _test = new SteppedTest(steps.getStep());  // Ok, this is a hack. Need to abstract this in some intelligent way.
            }

        new StepTestUpgrades(_test, project).apply();
        if (_step_tree == null)
            _step_tree = new StepTree2(project, _test.getStep(), getUndoStack(), _controller);
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

        Tab params_tab = new Tab("Parameters");
        BorderPane params_pane = new BorderPane();
        params_pane.setPadding(new Insets(5));

        Label heading = new Label("Default parameters for the test:");
        params_pane.setTop(heading);
        ValueSourceMapEditor initial_values_editor = new ValueSourceMapEditor(getProject(), getUndoStack());

        // setup a source to contain the map
        ValueSourceConfiguration fake_source = new ValueSourceConfiguration();
        if (_test.getDefaultVariables() == null)
            _test.setDefaultVariables(new HashMap<>());
        fake_source.setSourceMap(_test.getDefaultVariables());
        initial_values_editor.setSource(fake_source);

        params_pane.setCenter(initial_values_editor.getNode());
        params_tab.setContent(params_pane);
        params_tab.closableProperty().setValue(false);

        TabPane tabs = new TabPane(step_tab, params_tab);
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
            if (_controller.getState().equals(InteractiveTestState.PAUSED))
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
            if (_controller.getState().equals(InteractiveTestState.IDLE))
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
        event_table_controller.setTest(_test);

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
    public SteppedTest getTest()
        {
        return _test;
        }

    private void showEventLog()
        {
        _splitter.getItems().add(_event_table);
        _splitter.setDividerPositions(_divider_pos);
        }

    private void hideEventLog()
        {
        _divider_pos = _splitter.getDividerPositions()[0];
        _splitter.getItems().remove(1);
        }

    @Override
    public void stateChanged(InteractiveTestState state)
        {
        if (state.equals(InteractiveTestState.STARTING))
            {
            TestRunner runner = _controller.getTestRunner();
            _context = runner.getExecutionContext();
            Platform.runLater(StepTestEditor.this::showEventLog);
            }
        else if (state.equals(InteractiveTestState.IDLE))
            {
            // show the notifier - tell the user that the test is over, but leave the step tree in the running state (so they can review if desired) and give the user a reset button
            Platform.runLater(() ->
                {
                TestResult result = TestResult.find(_context);
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

    private SteppedTest _test;
    private TestExecutionContext _context = null;

    private SplitPane _splitter;
    private StepTree2 _step_tree;
    private double _divider_pos = 0.7;
    private Node _event_table;

    private InteractiveTestController _controller = new InteractiveTestControllerImpl();

    final static Logger LOG = LoggerFactory.getLogger(StepTestEditor.class);
    }