package org.musetest.ui.editors.suite;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import org.musetest.core.*;
import org.musetest.ui.extend.glyphs.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TestSuiteResultsList
    {
    public TestSuiteResultsList()
        {
        _results.setCellFactory(param -> new TestResultCell());
        _root.setCenter(_results);
        _results.getSelectionModel().selectedItemProperty().addListener((observable, old_selection, new_selection) ->
            {
            for (SelectionListener listener : _listeners)
                {
                if (new_selection == null)
                    listener.selected(null);
                else
                    listener.selected(new_selection._result);
                }
            });

        HBox filter_button_area = new HBox();
        filter_button_area.setSpacing(5);
        filter_button_area.setPadding(new Insets(5));
        _root.setBottom(filter_button_area);
        filter_button_area.setAlignment(Pos.CENTER);

        filter_button_area.getChildren().add(new Label("Show:"));

        _success_button = new ToggleButton("", createSuccessGlyph());
        _success_button.setId(SUCCESS_FILTER_BUTTON_ID);
        _success_button.setSelected(true);
        _success_button.setOnAction((event) -> updateDisplay());
        filter_button_area.getChildren().add(_success_button);

        _failure_button = new ToggleButton("", createFailureGlyph());
        _failure_button.setId(FAILURE_FILTER_BUTTON_ID);
        _failure_button.setSelected(true);
        _failure_button.setOnAction((event) -> updateDisplay());
        filter_button_area.getChildren().add(_failure_button);

        _error_button = new ToggleButton("", createErrorGlyph());
        _error_button.setId(ERROR_FILTER_BUTTON_ID);
        _error_button.setSelected(true);
        _error_button.setOnAction((event) -> updateDisplay());
        filter_button_area.getChildren().add(_error_button);
        }

    private Node createErrorGlyph()
        {
        return Glyphs.create("FA:EXCLAMATION_TRIANGLE", Color.RED);
        }

    private Node createFailureGlyph()
        {
        return Glyphs.create("FA:TIMES", Color.DARKORANGE);
        }

    private Node createSuccessGlyph()
        {
        return Glyphs.create("FA:CHECK", Color.GREEN);
        }

    private Node createResultGlyph(TestResult result)
        {
        if (result.isPass())
            return createSuccessGlyph();
        else if (result.hasFailures())
            return createFailureGlyph();
        else if (result.hasErrors())
            return createErrorGlyph();
        else
            return Glyphs.create("FA:QUESTION_CIRCLE", Color.BLACK);
        }

    public Node getNode()
        {
        return _root;
        }

    public void setResults(List<TestResult> results)
        {
        _successes.clear();
        for (TestResult result : results)
            {
            if (result.isPass())
                _successes.add(new TestResultDisplay(result));
            else if (result.hasFailures())
                _failures.add(new TestResultDisplay(result));
            else if (result.hasErrors())
                _errors.add(new TestResultDisplay(result));
            }

        _success_button.setDisable(_successes.size() == 0);
        _success_button.setSelected(true);
        _success_button.setText(String.format("Success (%d)", _successes.size()));

        _failure_button.setDisable(_failures.size() == 0);
        _failure_button.setSelected(true);
        _failure_button.setText(String.format("Failure (%d)", _failures.size()));

        _error_button.setDisable(_errors.size() == 0);
        _error_button.setSelected(true);
        _error_button.setText(String.format("Error (%d)", _errors.size()));

        updateDisplay();
        }

    private void updateDisplay()
        {
        ObservableList<TestResultDisplay> results = FXCollections.observableArrayList();
        if (_error_button.isSelected())
            results.addAll(_errors);
        if (_failure_button.isSelected())
            results.addAll(_failures);
        if (_success_button.isSelected())
            results.addAll(_successes);
        _results.setItems(results);
        }

    public void addSelectionListener(SelectionListener listener)
        {
        _listeners.add(listener);
        }

    private BorderPane _root = new BorderPane();
    private ListView<TestResultDisplay> _results = new ListView<>();
    private List<TestResultDisplay> _successes = new ArrayList<>();
    private List<TestResultDisplay> _failures = new ArrayList<>();
    private List<TestResultDisplay> _errors = new ArrayList<>();
    private final ToggleButton _success_button;
    private final ToggleButton _failure_button;
    private final ToggleButton _error_button;
    private Set<SelectionListener> _listeners = new HashSet<>();

    public static final String SUCCESS_FILTER_BUTTON_ID = "omues-tsrt-success";
    public static final String FAILURE_FILTER_BUTTON_ID = "omues-tsrt-failure";
    public static final String ERROR_FILTER_BUTTON_ID = "omues-tsrt-error";

    private class TestResultDisplay
        {
        TestResultDisplay(TestResult result)
            {
            _result = result;
            }

        @Override
        public String toString()
            {
            final String name = _result.getName();
            if (_result.isPass())
                return name;
            else
            	return name + ": " + _result.getSummary();
            }

        private TestResult _result;
        }

    public interface SelectionListener
        {
        void selected(TestResult result);
        }

    private class TestResultCell extends ListCell<TestResultDisplay>
        {
        @Override
        protected void updateItem(TestResultDisplay item, boolean empty)
            {
            super.updateItem(item, empty);
            if (!empty && item != null)
                {
                setGraphic(createResultGlyph(item._result));
                setText(item.toString());
                }
            else
                {
                setGraphic(null);
                setText(null);
                }
            }
        }
    }


