package org.museautomation.ui.editors.suite;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import net.christophermerrill.ShadowboxFx.*;
import org.controlsfx.control.*;
import org.controlsfx.control.action.*;
import org.museautomation.ui.editors.suite.runner.*;
import org.museautomation.core.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.glyphs.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
abstract class BaseTaskSuiteEditor extends BaseResourceEditor
	{
	@Override
	protected void addButtons(GridPane button_bar)
		{
		HBox run_buttons = new HBox();
		run_buttons.setPadding(new Insets(4, 4, 4, 4));
		run_buttons.setAlignment(Pos.TOP_CENTER);
		run_buttons.setSpacing(4);
		button_bar.add(run_buttons, button_bar.getChildren().size(), 0);
		GridPane.setHgrow(run_buttons, Priority.ALWAYS);

		Button play = new Button("Run", Glyphs.create("FA:PLAY"));
		play.setTooltip(new Tooltip("Run the task suite"));
		run_buttons.getChildren().add(play);
		play.setOnAction(event ->
			{
			TaskSuiteRunnerControlPanel panel = new TaskSuiteRunnerControlPanel((MuseTaskSuite) getResource(), getProject());
			panel.setCloseListener((show_results) ->
				{
				ShadowboxPane.findFromNode(button_bar).removeOverlay();

				if (show_results)
					{
					TaskSuiteResultsView viewer = new TaskSuiteResultsView(panel.getResults(), panel.getLogs(), getProject());
					showInLowerSplitPane(viewer.getNode());

					NotificationPane notifier = getNotifier();
					notifier.getActions().clear();
					notifier.getActions().add(new Action("Reset Editor", event2 ->
						{
						event2.consume();
						notifier.hide();
						hideLowerSplitPane();
						}));

					boolean has_failures = false;
					for (TaskResult result : panel.getResults())
						if (!result.isPass())
							{
							has_failures = true;
							break;
							}

					if (has_failures)
						{
						notifier.setText("Task Suite completed with failures.");
						notifier.setGraphic(Glyphs.create("FA:REMOVE", Color.RED));
						}
					else
						{
						notifier.setText("Task Suite completed successfully.");
						notifier.setGraphic(Glyphs.create("FA:CHECK", Color.GREEN));
						}
					notifier.show();
					}
				});
			ShadowboxPane.findFromNode(button_bar).showOverlayOnShadowbox(panel.getNode());
			Platform.runLater(panel::start);
			});
		}
	}