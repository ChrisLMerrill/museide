package org.musetest.ui.editors.suite.runner;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.core.*;
import org.musetest.core.context.*;
import org.musetest.core.events.*;
import org.musetest.core.execution.*;
import org.musetest.core.step.*;
import org.musetest.core.steptest.*;
import org.musetest.core.test.*;
import org.musetest.ui.extend.components.*;
import org.musetest.ui.extend.glyphs.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TestSuiteRunnerControlPanel
	{
	public TestSuiteRunnerControlPanel(MuseTestSuite suite, MuseProject project)
		{
		_suite = suite;
		_project = project;
		_grid = new GridPane();
		_grid.setPadding(new Insets(10));
		_grid.setVgap(5);
		_grid.setHgap(5);
		_grid.setStyle("-fx-background-color: white; -fx-background-radius: 5");
		_grid.setPrefWidth(600);
		_grid.setPrefHeight(200);

		ColumnConstraints col1_size = new ColumnConstraints();
		ColumnConstraints col2_size = new ColumnConstraints(100, 200, Double.MAX_VALUE);
		ColumnConstraints col3_size = new ColumnConstraints(50);
		_grid.getColumnConstraints().addAll(col1_size, col2_size, col3_size);

		_button_area = new HBox();
		_button_area.setSpacing(5);
		GridPane.setHgrow(_button_area, Priority.ALWAYS);
		_grid.add(_button_area, 0, 0, 2, 1);

		_run_button = new Button("Run", Glyphs.create("FA:PLAY"));
		_run_button.setId(RUN_BUTTON_ID);
		_run_button.setOnAction(event -> start());
		_button_area.getChildren().add(_run_button);

		_stop_button = new Button("Stop", Glyphs.create("FA:STOP"));
		_stop_button.setId(STOP_BUTTON_ID);
		_stop_button.setOnAction(event ->
		{
		_runner.stop();
		_stop_button.setDisable(true);
		});
		_button_area.getChildren().add(_stop_button);

		_suite_timer = new TimerLabel("mm:ss");
		GridPane.setHalignment(_suite_timer, HPos.RIGHT);
		_grid.add(_suite_timer, 2, 0);

		_complete_label = new Label();
		_complete_label.setText("0 tests complete");
		_complete_label.setId(COMPLETE_LABEL_ID);
		GridPane.setValignment(_complete_label, VPos.BOTTOM);
		GridPane.setVgrow(_complete_label, Priority.SOMETIMES);
		_grid.add(_complete_label, 0, 1, 3, 1);

		_failed_label = new Label();
		_failed_label.setText("0 tests failed");
		_failed_label.setId(FAILED_LABEL_ID);
		_grid.add(_failed_label, 0, 2, 3, 1);

		_error_label = new Label();
		_error_label.setText("0 tests encountered errors");
		_error_label.setId(ERROR_LABEL_ID);
		_grid.add(_error_label, 0, 3, 3, 1);

		Label test_label = new Label("Test: ");
		_grid.add(test_label, 0, 4);
		GridPane.setValignment(test_label, VPos.BOTTOM);
		GridPane.setVgrow(test_label, Priority.SOMETIMES);

		_test_name = new Label();
		_test_name.setId(TEST_LABEL_ID);
		_grid.add(_test_name, 1, 4);
		GridPane.setValignment(_test_name, VPos.BOTTOM);
		GridPane.setVgrow(_test_name, Priority.ALWAYS);
		GridPane.setHgrow(_test_name, Priority.ALWAYS);

		_test_timer = new TimerLabel("mm:ss");
		_grid.add(_test_timer, 2, 4);
		GridPane.setHalignment(_test_timer, HPos.RIGHT);
		GridPane.setValignment(_test_timer, VPos.BOTTOM);

		_grid.add(new Label("Step: "), 0, 5);
		_step_name = new Label();
		_step_name.setId(STEP_LABEL_ID);
		_grid.add(_step_name, 1, 5);
		GridPane.setHgrow(_step_name, Priority.ALWAYS);

		_step_timer = new TimerLabel("mm:ss");
		GridPane.setHalignment(_step_timer, HPos.RIGHT);
		_grid.add(_step_timer, 2, 5);

		_runner_listener = new InteractiveTestSuiteRunner.Listener()
			{
			@Override
			public void testSuiteStarted(MuseTestSuite suite)
				{
				_suite_timer.clear();
				_suite_timer.start();
				_run_button.setDisable(true);
				_results = new ArrayList<>();
				_logs = new HashMap<>();
				}

			@Override
			public void testSuiteCompleted(MuseTestSuite suite)
				{
				_suite_timer.stop();
				_runner.removeListener(this);
				Platform.runLater(() ->
					{
					_button_area.getChildren().clear();

					Button close_button = new Button("Close", Glyphs.create("FA:CLOSE"));
					_button_area.getChildren().add(close_button);
					close_button.setOnAction(event -> TestSuiteRunnerControlPanel.this._close_listener.close(false));

					Button details_button = new Button("Details...", Glyphs.create("FA:SEARCH"));
					_button_area.getChildren().add(details_button);
					details_button.setOnAction(event ->
						{
						TestSuiteRunnerControlPanel.this._close_listener.close(true);  // note: This fails, if called _after_ opening the viewer
						});
					});
				}

			@Override
			public void testStarted(TestConfiguration test_config, TestRunner test_runner)
				{
				Platform.runLater(() -> _test_name.setText(test_config.name()));

				_current_context = test_runner.getExecutionContext();
				_current_context.addEventListener(_step_event_listener);

				_test_timer.clear();
				_test_timer.start();
				}

			@Override
			public void testCompleted(TestResult result, int completed, Integer total, EventLog log)
				{
				String total_string = "?";
				if (total != null)
					total_string = total.toString();
				String complete_message = completed + " of " + total_string + " tests complete";

				String errors_message;
				String failures_message;
				if (result == null)
					{
					failures_message = "Error/failure status unknown";
					errors_message = "suggestion: Add a Test Result Calculator (test plugin) to the project";
					}
				else
					{
					if (!result.isPass())
						{
						if (result.hasErrors())
							_errors++;
						else if (result.hasFailures())
							_failures++;
						}

					errors_message = _errors + " test(s) encountered errors";
					failures_message = _failures + " test(s) failed";
					_results.add(result);
					if (log != null)
						_logs.put(result, log);
					}

				_test_timer.stop();
				_test_timer.clear();

				Platform.runLater(() ->
				{
				_complete_label.setText(complete_message);
				_failed_label.setText(failures_message);
				_error_label.setText(errors_message);
				_test_name.setText("");
				});

				_current_context.removeEventListener(_step_event_listener);
				_current_context = null;
				}
			};
		}

	public Node getNode()
		{
		return _grid;
		}

	public List<TestResult> getResults()
		{
		return _results;
		}

	public Map<TestResult, EventLog> getLogs()
		{
		return _logs;
		}

	public void start()
		{
		MuseTestSuiteRunner runner = getRunner();

		// note: This fails, if called _after_ opening the viewer
		runner.execute(_project, _suite, Collections.emptyList());
		}

	public void setCloseListener(CloseListener listener)
		{
		_close_listener = listener;
		}

	/**
	 * Force the panel to use a specific runner, instead of creating one itself. Only for unit tests?
	 */
	public void injectRunner(InteractiveTestSuiteRunner runner)
		{
		_runner = runner;
		_runner.addListener(_runner_listener);
		}

	private MuseTestSuiteRunner getRunner()
		{
		if (_runner == null)
			{
			_runner = new ThreadedInteractiveTestSuiteRunner();
			_runner.addListener(_runner_listener);
			}
		return _runner;
		}

	private MuseProject _project;
	private MuseTestSuite _suite;
	private InteractiveTestSuiteRunner _runner;
	private CloseListener _close_listener;
	private InteractiveTestSuiteRunner.Listener _runner_listener;

	private List<TestResult> _results = new ArrayList<>();
	private Map<TestResult, EventLog> _logs;

	private final GridPane _grid;
	private final Label _complete_label;
	private final Label _failed_label;
	private final Label _error_label;
	private final Label _test_name;
	private final Label _step_name;

	private final TimerLabel _suite_timer;
	private final TimerLabel _test_timer;
	private final TimerLabel _step_timer;

	private int _failures = 0;
	private int _errors = 0;
	private final Button _run_button;
	private final Button _stop_button;

	private TestExecutionContext _current_context = null;
	private final HBox _button_area;

	public static final String RUN_BUTTON_ID = "omuesr-tsrcp-run_button";
	private static final String STOP_BUTTON_ID = "omuesr-tsrcp-stop_button";
	public static final String COMPLETE_LABEL_ID = "omuesr-tsrcp-complete_label";
	public static final String FAILED_LABEL_ID = "omuesr-tsrcp-failed_label";
	public static final String ERROR_LABEL_ID = "omuesr-tsrcp-error_label";
	public static final String TEST_LABEL_ID = "omuesr-tsrcp-test_label";
	public static final String STEP_LABEL_ID = "omuesr-tsrcp-step_label";

	public interface CloseListener
		{
		void close(boolean show_result_details);
		}

	private MuseEventListener _step_event_listener = new MuseEventListener()
		{
		@Override
		public void eventRaised(MuseEvent event)
			{
			switch (event.getTypeId())
				{
				case StartStepEventType.TYPE_ID:
					_step_timer.clear();
					_step_timer.start();
					String step_description;
					if (_current_context == null)
						step_description = event.getTypeId();
					else
						{
						final Long step_id = StepEventType.getStepId(event);
						StepConfiguration step = ((SteppedTest) _current_context.getTest()).getStep().findByStepId(step_id);
						if (step == null)
							step = _dynamically_loaded_steps.get(step_id);
						step_description = _current_context.getProject().getStepDescriptors().get(step).getShortDescription(step);
						}
					Platform.runLater(() -> _step_name.setText(step_description));
					break;
				case EndStepEventType.TYPE_ID:
					_step_timer.stop();
					_step_timer.clear();
					Platform.runLater(() -> _step_name.setText(""));
					break;
				case DynamicStepLoadingEventType.TYPE_ID:
					final List<StepConfiguration> loaded = DynamicStepLoadingEventType.getLoadedSteps(event, _current_context);
					for (StepConfiguration step : loaded)
						_dynamically_loaded_steps.put(step.getStepId(), step);
				}
			}

		private Map<Long, StepConfiguration> _dynamically_loaded_steps = new HashMap<>();
		};
	}