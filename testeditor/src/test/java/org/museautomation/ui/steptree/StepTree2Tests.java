package org.museautomation.ui.steptree;

import javafx.scene.*;
import net.christophermerrill.FancyFxTree.*;
import net.christophermerrill.testfx.*;
import org.junit.*;
import org.museautomation.ui.steptest.*;
import org.museautomation.ui.valuesource.*;
import org.museautomation.builtins.step.*;
import org.museautomation.core.context.*;
import org.museautomation.core.events.*;
import org.museautomation.core.execution.*;
import org.museautomation.core.mocks.*;
import org.museautomation.core.project.*;
import org.museautomation.core.step.*;
import org.museautomation.core.step.descriptor.*;
import org.museautomation.core.steptest.*;
import org.museautomation.core.values.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.ui.extend.glyphs.*;

import java.lang.reflect.*;
import java.util.*;

import static javafx.scene.input.KeyCode.*;

/**
 * Note that since StepTree2 uses FancyFxTree for the heavy lifting, these tests are only intended to
 * ensure that the parts are all wired up correctly...thus they are not hitting edge cases for
 * functionality implemented in FancyFxTree. If an edge case is found that should be handled generally,
 * then it should be added to the FancyFxTree test suite and implemented there. Only edge cases
 * specific to the StepTree2 implementation should be tested here.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class StepTree2Tests extends ComponentTest
	{
	@Test
	public void displaySteps()
	    {
	    Assert.assertTrue(exists("root step"));

	    // first level children should be visible initially
        Assert.assertTrue(numberOf(getStepDescription(_step1))> 0);  // TODO Since Java 12 and TestFx 4.0.16, numberOf() returns 2 instead of 1
        Assert.assertTrue(numberOf(getStepDescription(_step2))> 0);  // TODO Since Java 12 and TestFx 4.0.16, numberOf() returns 2 instead of 1

	    // look for the icons for the steps
        String icon1_class = Glyphs.getStyleName(getIconDescriptor(_step1), null, null);
	    Assert.assertTrue("cant find step1 icon", exists("." + icon1_class));
        String icon2_class = Glyphs.getStyleName(getIconDescriptor(_step2), null, null);
	    Assert.assertTrue("cant find step2 icon", exists("." + icon2_class));
	    }

	private String getStepDescription(StepConfiguration step)
		{
		return _project.getStepDescriptors().get(step).getShortDescription(step);
		}

	private String getIconDescriptor(StepConfiguration step)
		{
		return _project.getStepDescriptors().get(step).getIconDescriptor();
		}

	@Test
	public void displayAddedStep()
	    {
	    StepConfiguration new_step = new StepConfiguration(StoreVariable.TYPE_ID);
	    new_step.addSource(StoreVariable.NAME_PARAM, ValueSourceConfiguration.forValue("var1"));
	    new_step.addSource(StoreVariable.VALUE_PARAM, ValueSourceConfiguration.forValue("value1"));
	    _root_step.addChild(new_step);
	    waitForUiEvents();

	    Assert.assertTrue(exists(getStepDescription(_step1)));
	    Assert.assertTrue(exists(getStepDescription(_step2)));
	    Assert.assertTrue(exists(getStepDescription(new_step)));
	    }

	@Test
	public void unDisplayRemovedStep()
	    {
	    _root_step.removeChild(_step1);
	    waitForUiEvents();

	    Assert.assertFalse(exists(getStepDescription(_step1)));
	    Assert.assertTrue(exists(getStepDescription(_step2)));
	    }

	@Test
	public void displayValueSourceChange()
	    {
	    String original_description = getStepDescription(_step1);
	    Assert.assertTrue(exists(original_description));

	    _step1.replaceSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("newmessage"));
	    waitForUiEvents();
	    Assert.assertFalse(exists(original_description));
	    Assert.assertTrue(exists(getStepDescription(_step1)));
	    }

	@Test
	public void deleteStep()
	    {
	    clickOn(getStepDescription(_step1));
	    push(DELETE);
	    Assert.assertFalse(exists(getStepDescription(_step1)));

	    _undo_stack.undoLastAction();
	    waitForUiEvents();
	    Assert.assertTrue(exists(getStepDescription(_step1)));
	    }

	@Test
	public void cutPasteStep()
	    {
	    clickOn(getStepDescription(_step1));
	    push(CONTROL, X);
	    Assert.assertFalse(exists(getStepDescription(_step1)));

	    clickOn(getStepDescription(_root_step));  // need to click elsewhere before click on step 2 is accepted (not sure why...)
	    clickOn(getStepDescription(_step2));
	    push(CONTROL, V);
	    waitForUiEvents();
	    Assert.assertTrue(exists(getStepDescription(_step1)));

        Assert.assertSame(_root_step.getChildren().get(0), _step2);
	    Assert.assertEquals(_root_step.getChildren().get(1).getType(), _step1.getType()); // has the same type from what was copied (checking that the copy function copies ALL the details of the step would be redundant...we're just verifying the right step was copied)
	    Assert.assertNotEquals(_root_step.getChildren().get(1).getStepId(), _step1.getStepId()); // has a new id
	    }

	@Test
	public void copyPasteStep()
	    {
	    clickOn(getStepDescription(_step1));
	    push(CONTROL, C);

	    clickOn(getStepDescription(_step2));
	    push(CONTROL, V);
	    waitForUiEvents();
	    Assert.assertTrue(exists(getStepDescription(_step1)));

	    Assert.assertEquals(3, _root_step.getChildren().size());
        Assert.assertSame(_root_step.getChildren().get(0), _step1);
        Assert.assertSame(_root_step.getChildren().get(1), _step2);
	    Assert.assertEquals(_step1.getType(), _root_step.getChildren().get(2).getType()); // is a copy of step1
	    Assert.assertNotEquals(_step1.getStepId(), _root_step.getChildren().get(2).getStepId()); // got a new  stepid
	    }

	@Test
	public void copyTwiceYieldsUniqueStepIds()
	    {
	    clickOn(getStepDescription(_step1));
	    push(CONTROL, C);

	    clickOn(getStepDescription(_step2));
	    push(CONTROL, V);
	    waitForUiEvents();
	    push(CONTROL, V);
	    waitForUiEvents();
	    Assert.assertTrue(exists(getStepDescription(_step1)));

	    Assert.assertEquals(4, _root_step.getChildren().size());
        Assert.assertSame(_root_step.getChildren().get(0), _step1);
        Assert.assertSame(_root_step.getChildren().get(1), _step2);
	    Assert.assertNotEquals(_root_step.getChildren().get(2).getStepId(), _root_step.getChildren().get(3).getStepId()); // each copy got a unique stepid
	    }

	@Test
	public void undo()
	    {
	    clickOn(getStepDescription(_step1));
	    push(DELETE);
	    Assert.assertFalse(exists(getStepDescription(_step1)));

	    push(CONTROL, Z);
	    waitForUiEvents();
	    Assert.assertTrue(exists(getStepDescription(_step1)));
	    }

	@Test
	public void showHideEditor()
	    {
	    Assert.assertFalse("editor already showing", exists(byClass(StepCellEditor.STYLE_CLASS)));

	    doubleClickOn(getStepDescription(_step1));
	    Assert.assertTrue("editor not showing", exists(byClass(StepCellEditor.STYLE_CLASS)));

	    pressEscape(byClass(StepCellEditor.STYLE_CLASS));
	    Assert.assertFalse("editor still showing", exists(byClass(StepCellEditor.STYLE_CLASS)));
	    }

	@Test
	public void editStepAndUndo()
	    {
	    doubleClickOn(getStepDescription(_step1));
	    final String old_message = _step1.getSource(LogMessage.MESSAGE_PARAM).getValue().toString();
	    final String new_message = "new message";
	    clickOn(byClass(StepCellEditor.STYLE_CLASS));
	    fillFieldAndPressEnter(id(DefaultInlineVSE.TEXT_ID), quoted(new_message));
	    waitForUiEvents();
	    Assert.assertEquals("change not made to step", new_message, _step1.getSource(LogMessage.MESSAGE_PARAM).getValue());
	    Assert.assertTrue("change not made in UI", exists(getStepDescription(_step1)));

	    _undo_stack.undoLastAction();
	    waitForUiEvents();
	    Assert.assertEquals("change not reverted", old_message, _step1.getSource(LogMessage.MESSAGE_PARAM).getValue());
	    Assert.assertTrue("change not made in UI", exists(getStepDescription(_step1)));
	    }

	@Test
	public void moveByDrag()
	    {
	    Node destination_area = lookup(getStepDescription(_step2)).query();
	    drag(getStepDescription(_step1));
	    moveTo(getStepDescription(_step2));
	    moveBy(0, destination_area.getBoundsInParent().getHeight() * 0.4d);
	    drop();

	    Assert.assertEquals(2, _root_step.getChildren().size());
        Assert.assertSame(_step2, _root_step.getChildren().get(0));
        Assert.assertSame(_step1, _root_step.getChildren().get(1));

        // TODO Since Java 12 and TestFx 4.0.16, this query returns 2 - one for the label and one for the containing tree cell.
//	    Assert.assertEquals("node was copied instead of moved", 1, numberOf(getStepDescription(_step1)));
	    }

	@Test
	public void copyByDragInto()
	    {
	    StepConfiguration if_step = new StepConfiguration(IfStep.TYPE_ID);
	    _root_step.addChild(if_step);
	    waitForUiEvents();

	    drag(getStepDescription(_step1));
	    press(CONTROL);
	    moveTo(getStepDescription(if_step));
	    drop();

	    Assert.assertEquals(1, if_step.getChildren().size());
	    Assert.assertEquals(LogMessage.TYPE_ID, if_step.getChildren().get(0).getType());
	    }

	@Test
	public void denyDragIntoNonCompoundStep()
	    {
	    drag(getStepDescription(_step1));
	    press(CONTROL);
	    moveTo(getStepDescription(_step2));
	    drop();

	    Assert.assertNull(_step2.getChildren());
	    }

	@Test
	public void changeStepDisplayName()
	    {
	    final String description = getStepDescription(_step1) + " - custom step description";
	    _step1.setMetadataField(StepConfiguration.META_DESCRIPTION, description);
	    waitForUiEvents();
	    Assert.assertTrue("name change not displayed", exists(description));
	    }

	@Test
	public void changeSubsource()
	    {
	    final String new_message = getStepDescription(_step1) + "-newmessage";
	    _step1.replaceSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue(new_message));
	    waitForUiEvents();
	    Assert.assertTrue("ValueSource change not displayed", exists(getStepDescription(_step1)));
	    }

	@Test
	public void changeStepType()
	    {
	    _step2.setType(IfStep.TYPE_ID);
	    waitForUiEvents();
	    String description = getStepDescription(_step2);
	    Assert.assertTrue("type change not displayed", exists(description));

	    String icon_class = Glyphs.getStyleName(getIconDescriptor(_step2), null, null);
		Assert.assertTrue("cant find new step2 icon", exists("." + icon_class));
	    }

	@Test
	public void showStepRunComplete()
	    {
	    StepDescriptor descriptor = _project.getStepDescriptors().get(_step2);
	    Node node = findTreeCellFor(descriptor.getShortDescription(_step2));
	    Assert.assertNotNull("cant locate the node", node);

	    _controller.run(new SteppedTestProviderImpl(_project, _test));

	    _controller.raiseStateChangeEvent(InteractiveTestState.STARTING);
	    _controller.getTestRunner().getExecutionContext().raiseEvent(StartStepEventType.create(_step2, new MockStepExecutionContext()));
	    waitForUiEvents();
	    node = findTreeCellFor(descriptor.getShortDescription(_step2));
	    Assert.assertTrue("node does not have RUNNING style", node.getStyleClass().contains(StepConfigurationTreeNode.RUNNING_STYLE));
	    Assert.assertFalse("node has the COMPLETE style", node.getStyleClass().contains(StepConfigurationTreeNode.COMPLETE_STYLE));
	    Assert.assertTrue("spinner not shown", exists("." + GraphicNodeBuilder.SPINNER_CLASS));

	    _controller.getTestRunner().getExecutionContext().raiseEvent(EndStepEventType.create(_step2, new MockStepExecutionContext(), new BasicStepExecutionResult(StepExecutionStatus.COMPLETE)));
	    waitForUiEvents();
	    node = findTreeCellFor(descriptor.getShortDescription(_step2));
	    Assert.assertTrue("node does not have COMPLETE style", node.getStyleClass().contains(StepConfigurationTreeNode.COMPLETE_STYLE));
	    Assert.assertFalse("node has the RUNNING style", node.getStyleClass().contains(StepConfigurationTreeNode.RUNNING_STYLE));
	    Assert.assertFalse("spinner still showning", exists("." + GraphicNodeBuilder.SPINNER_CLASS));

	    _controller.raiseStateChangeEvent(InteractiveTestState.STOPPING);
	    waitForUiEvents();
	    node = findTreeCellFor(descriptor.getShortDescription(_step2));
	    Assert.assertTrue("node does not have DEFAULT style", node.getStyleClass().contains(StepConfigurationTreeNode.DEFAULT_STYLE));
	    Assert.assertFalse("node still has the COMPLETE style", node.getStyleClass().contains(StepConfigurationTreeNode.COMPLETE_STYLE));
	    Assert.assertFalse("node still has the RUNNING style", node.getStyleClass().contains(StepConfigurationTreeNode.RUNNING_STYLE));
	    }

	@Test
	public void showStepIncomplete()
	    {
	    StepDescriptor descriptor = _project.getStepDescriptors().get(_root_step);
	    Node node = findTreeCellFor(descriptor.getShortDescription(_root_step));
	    Assert.assertNotNull("cant locate the node", node);

	    _controller.run(new SteppedTestProviderImpl(_project, _test));

	    _controller.raiseStateChangeEvent(InteractiveTestState.STARTING);
	    _controller.getTestRunner().getExecutionContext().raiseEvent(StartStepEventType.create(_root_step, new MockStepExecutionContext()));
	    waitForUiEvents();
	    _controller.getTestRunner().getExecutionContext().raiseEvent(EndStepEventType.create(_root_step, new MockStepExecutionContext(), new BasicStepExecutionResult(StepExecutionStatus.INCOMPLETE)));
	    waitForUiEvents();

	    node = findTreeCellFor(descriptor.getShortDescription(_root_step));
	    Assert.assertTrue("node does not have INCOMPLETE style", node.getStyleClass().contains(StepConfigurationTreeNode.INCOMPLETE_STYLE));
	    Assert.assertFalse("node has the RUNNING style", node.getStyleClass().contains(StepConfigurationTreeNode.RUNNING_STYLE));
	    Assert.assertTrue("ellipsis not showing", exists("." + GraphicNodeBuilder.ELLIPSIS_CLASS));
	    Assert.assertFalse("spinner still showning", exists("." + GraphicNodeBuilder.SPINNER_CLASS));
	    }

	@Test
	public void autoExpandIntoChildSteps()
	    {
	    StepConfiguration if_step = new StepConfiguration(IfStep.TYPE_ID);
	    if_step.addSource(IfStep.CONDITION_PARAM, ValueSourceConfiguration.forValue(true));
	    if_step.setStepId(IdGenerator.get(_project).generateLongId());
	    StepConfiguration log_step = new StepConfiguration(LogMessage.TYPE_ID);
	    log_step.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("if condition was true"));
	    log_step.setStepId(IdGenerator.get(_project).generateLongId());
	    if_step.addChild(log_step);
	    _root_step.addChild(0, if_step);
	    waitForUiEvents();
	    Assert.assertFalse("should not be visible yet", exists(getStepDescription(log_step)));

	    // start the hidden child step
	    _controller.raiseStateChangeEvent(InteractiveTestState.STARTING);
	    _controller.getTestRunner().getExecutionContext().raiseEvent(StartStepEventType.create(log_step, new MockStepExecutionContext()));
	    waitForUiEvents();
	    Assert.assertTrue("log step was not revealed", exists(getStepDescription(log_step)));

	    // end the parent (containing) step
	    _controller.getTestRunner().getExecutionContext().raiseEvent(EndStepEventType.create(if_step, new MockStepExecutionContext(), new BasicStepExecutionResult(StepExecutionStatus.COMPLETE)));
	    waitForUiEvents();
	    Assert.assertFalse("log step was not hidden", exists(getStepDescription(log_step)));
	    }

	@Test
	public void higlightNextStep()
	    {
	    StepConfiguration if_step = new StepConfiguration(IfStep.TYPE_ID);
	    if_step.addSource(IfStep.CONDITION_PARAM, ValueSourceConfiguration.forValue(true));
	    if_step.setStepId(IdGenerator.get(_project).generateLongId());
	    StepConfiguration log_step = new StepConfiguration(LogMessage.TYPE_ID);
	    log_step.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("if condition was true"));
	    log_step.setStepId(IdGenerator.get(_project).generateLongId());
	    if_step.addChild(log_step);
	    _root_step.addChild(0, if_step);
	    waitForUiEvents();

	    Assert.assertFalse("should not be visible yet", exists(getStepDescription(log_step)));

	    _controller.raiseStateChangeEvent(InteractiveTestState.STARTING);
	    _controller.getTestRunner().getExecutionContext().raiseEvent(PauseTestEventType.create(log_step, _controller.getTestRunner().getExecutionContext()));
	    waitForUiEvents();

	    Node log_node = lookup(getStepDescription(log_step)).query();
	    Assert.assertNotNull("log step not shown", log_node);
	    Node styled_node = lookup(byClass(StepConfigurationTreeNode.PAUSED_STYLE)).query();
	    Assert.assertNotNull("no step with pause style", styled_node);
        Assert.assertSame("styled step is not the paused step", log_node, styled_node);
	    Assert.assertTrue("pause is not showning", exists("." + Glyphs.getStyleName("FA:PAUSE", null, null)));
	    Assert.assertFalse("pause is showning on more than one step", lookup("." + Glyphs.getStyleName("FA:PAUSE", null, null)).queryAll().size() > 1);

	    // stop and ensure icon is reset
	    _controller.raiseStateChangeEvent(InteractiveTestState.STOPPING);
	    waitForUiEvents();
	    Assert.assertFalse("step icon was not reset", lookup("." + Glyphs.getStyleName("FA:PAUSE", null, null)).queryAll().size() > 0);
	    }

	/**
	 * Steps from macros and functions are stored in a separate project resource.
	 * The should be dynamically loaded into and unloaded from the step tree when executing.
	 */
	@Test
	public void showStepsFromExternalResourceWhileStepping()
		{
	    StepConfiguration macro_step = new StepConfiguration(CallMacroStep.TYPE_ID);
	    macro_step.addSource(CallMacroStep.ID_PARAM, ValueSourceConfiguration.forValue("macro-id"));
		macro_step.setStepId(IdGenerator.get(_project).generateLongId());
		final StepConfiguration step_in_macro = new StepConfiguration(LogMessage.TYPE_ID);
		step_in_macro.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("macro message"));
		step_in_macro.setStepId(IdGenerator.get(_project).generateLongId());

	    // add the macro step to the test
	    _root_step.addChild(0, macro_step);
		waitForUiEvents();
	    Assert.assertFalse("macro steps should not be shown yet", exists(getStepDescription(step_in_macro)));

	    // start the test
		_controller.raiseStateChangeEvent(InteractiveTestState.STARTING);

	    // load the steps
		List<StepConfiguration> loaded_steps = new ArrayList<>();
		loaded_steps.add(step_in_macro);
		((SteppedTestExecutionContext) _controller.getTestRunner().getExecutionContext()).getStepLocator().loadSteps(loaded_steps);
		_controller.getTestRunner().getExecutionContext().raiseEvent(DynamicStepLoadingEventType.create(macro_step, loaded_steps));
	    waitForUiEvents();

	    // expand the macro node
		Node arrow_node = lookup(getStepDescription(macro_step)).lookup(".arrow").query();
		clickOn(arrow_node);
		Assert.assertTrue("macro step should be shown now", exists(getStepDescription(step_in_macro)));

		// run a step from the macro and be sure that cells get updates
		_controller.getTestRunner().getExecutionContext().raiseEvent(StartStepEventType.create(step_in_macro, new MockStepExecutionContext()));
		waitForUiEvents();
		Node node_in_macro = findTreeCellFor(getStepDescription(step_in_macro));
		Assert.assertTrue("node does not have RUNNING style", node_in_macro.getStyleClass().contains(StepConfigurationTreeNode.RUNNING_STYLE));

		// complete the call-macro step
		_controller.getTestRunner().getExecutionContext().raiseEvent(EndStepEventType.create(macro_step, new MockStepExecutionContext(), new BasicStepExecutionResult(StepExecutionStatus.COMPLETE)));
		waitForUiEvents();
		Assert.assertFalse("macro steps should be hidden after macro complete", exists(getStepDescription(step_in_macro)));

		// load the steps again
		_controller.getTestRunner().getExecutionContext().raiseEvent(DynamicStepLoadingEventType.create(macro_step, loaded_steps));
		waitForUiEvents();
		Assert.assertTrue("macro step should be shown again", exists(getStepDescription(step_in_macro)));

		// stop the test
		_controller.raiseStateChangeEvent(InteractiveTestState.STOPPING);
		waitForUiEvents();
		Assert.assertFalse("macro step should be removed", exists(getStepDescription(step_in_macro)));
	    }

	@Test
	public void cleanupStepListeners() throws Exception
		{
	    _tree.dispose();
	    waitForUiEvents();

	    Assert.assertEquals("root step still has listeners", 0, getNumberOfListeners(_root_step));
	    Assert.assertEquals("step1 still has listeners", 0, getNumberOfListeners(_step1));
	    Assert.assertEquals("step2 still has listeners", 0, getNumberOfListeners(_step2));
	    }

	private int getNumberOfListeners(StepConfiguration root_step) throws Exception
		{
		try
			{
			Field listeners_field = root_step.getClass().getDeclaredField("_listeners");
			listeners_field.setAccessible(true);
			Set listeners = (Set) listeners_field.get(root_step);
			return listeners.size();
			}
		catch (NoSuchFieldException e)
			{
			e.printStackTrace();
			throw new Exception("can't access the _listeners field. Name changed?");
			}
		}

	private Node findTreeCellFor(String query)
		{
		Node node = lookup(query).query();
		while (!(node instanceof FancyTreeCell) && node != null)
			node = node.getParent();
		return node;
		}

	@Override
	protected Node createComponentNode()
		{
		_project = new SimpleProject();
		_undo_stack = new UndoStack();

		_root_step = new StepConfiguration(BasicCompoundStep.TYPE_ID);
		_root_step.setMetadataField(StepConfiguration.META_DESCRIPTION, "root step");
		_root_step.setStepId(IdGenerator.get(_project).generateLongId());
		_step1 = new StepConfiguration(LogMessage.TYPE_ID);
		_step1.addSource(LogMessage.MESSAGE_PARAM, ValueSourceConfiguration.forValue("mymessage"));
		_step1.setStepId(IdGenerator.get(_project).generateLongId());
		_root_step.addChild(_step1);
		_step2 = new StepConfiguration(Verify.TYPE_ID);
		_step2.addSource(Verify.CONDITION_PARAM, ValueSourceConfiguration.forValue("untrue string"));
		_step2.setStepId(IdGenerator.get(_project).generateLongId());
		_root_step.addChild(_step2);

		_controller = new MockInteractiveTestController();
		MockTestRunner runner = new MockTestRunner();
		TestExecutionContext test_context = new MockSteppedTestExecutionContext();
		runner.setExecutionContext(test_context);
		_controller.setRunner(runner);
		_test = new SteppedTest(_root_step);

		_tree = new StepTree2(_project, _root_step, _undo_stack, _controller);
		return _tree.getNode();
		}

	private StepConfiguration _root_step;
	private StepConfiguration _step1;
	private StepConfiguration _step2;
	private SimpleProject _project;
	private UndoStack _undo_stack;
	private MockInteractiveTestController _controller;
	private SteppedTest _test;
	private StepTree2 _tree;
	}
