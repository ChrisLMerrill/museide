package org.museautomation.ui.valuesource.parser;

import org.museautomation.parsing.valuesource.antlr.*;
import org.museautomation.core.*;
import org.museautomation.core.values.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class VSBuilder extends ValueSourceBaseListener
    {
    VSBuilder(MuseProject project)
        {
        _project = project;
        }

    public ValueSourceConfiguration getSource()
        {
        if (_error != null)
            return null;
        if (_parse_stack.peek() instanceof ValueSourceConfiguration)
            return (ValueSourceConfiguration) _parse_stack.pop();
        return null;
        }

    @Override
    public void exitLiteral(ValueSourceParser.LiteralContext context)
        {
        String text = context.getText();
        for (ValueSourceStringExpressionSupport support : getSupporters())
            {
            ValueSourceConfiguration config = support.fromLiteral(text, _project);
            if (config != null)
                {
                _parse_stack.push(config);
                return;
                }
            }
        }

    @Override
    public void exitPrefixedExpression(ValueSourceParser.PrefixedExpressionContext context)
        {
        String prefix = context.getChild(0).getText();
        for (ValueSourceStringExpressionSupport support : getSupporters())
            {
            if (!_parse_stack.isEmpty())
                {
                ValueSourceConfiguration config = support.fromPrefixedExpression(prefix, (ValueSourceConfiguration) _parse_stack.peek(), _project);
                if (config != null)
                    {
                    _parse_stack.pop();
                    _parse_stack.push(config);
                    return;
                    }
                }
            }
        _error = "No support found for prefixed expression: " + prefix + context.getChild(1).getText();
        }

    @Override
    public void enterElementExpression(ValueSourceParser.ElementExpressionContext ctx)
        {
        _parse_stack.push(ParseStackMarker.ElementExpression);
        }

    @Override
    public void exitElementExpression(ValueSourceParser.ElementExpressionContext context)
        {
        String type_id = context.getChild(1).getText();

        // pop the arguments off the stack
        Object stack_item = _parse_stack.pop();
        List<ValueSourceConfiguration> arguments = new ArrayList<>();
        while (!(stack_item.equals(ParseStackMarker.ElementExpression)))
            {
            if (stack_item instanceof ValueSourceConfiguration)
                arguments.add(0, (ValueSourceConfiguration)stack_item);
            else
                _error = String.format("Did not expect to see a %s on the stack: %s", stack_item.getClass().getSimpleName(), stack_item.toString());
            stack_item = _parse_stack.pop();
            }

        for (ValueSourceStringExpressionSupport support : getSupporters())
            {
            ValueSourceConfiguration config = support.fromElementExpression(type_id, arguments, _project);
            if (config != null)
                {
                _parse_stack.push(config);
                return;
                }
            }
        _error = String.format("No support found for element expression: %s", context.getText());
        }

    @Override
    public void enterElementLookupExpression(ValueSourceParser.ElementLookupExpressionContext context)
        {
        _parse_stack.push(ParseStackMarker.ElementLookupExpression);
        }

    @Override
    public void exitElementLookupExpression(ValueSourceParser.ElementLookupExpressionContext context)
        {
        // pop the arguments off the stack
        Object stack_item = _parse_stack.pop();
        List<ValueSourceConfiguration> arguments = new ArrayList<>();
        while (!(stack_item.equals(ParseStackMarker.ElementLookupExpression)))
            {
            if (stack_item instanceof ValueSourceConfiguration)
                arguments.add(0, (ValueSourceConfiguration)stack_item);
            else if (stack_item instanceof String)
                arguments.add(0, ValueSourceConfiguration.forValue(stack_item));
            else
                _error = String.format("Did not expect to see a %s on the stack: %s", stack_item.getClass().getSimpleName(), stack_item.toString());
            stack_item = _parse_stack.pop();
            }

        if (arguments.size() == 1)
            _error = "Must not use a literal and an expresssion. Pick one or the other and use for both page and element";

        if (arguments.size() < 2)
            {
            arguments.add(0, ValueSourceConfiguration.forValue(context.getChild(1).getText()));
            arguments.add(1, ValueSourceConfiguration.forValue(context.getChild(3).getText()));
            }

        for (ValueSourceStringExpressionSupport support : getSupporters())
            {
            ValueSourceConfiguration config = support.fromElementLookupExpression(arguments, _project);
            if (config != null)
                {
                _parse_stack.push(config);
                return;
                }
            }
        _error = String.format("No support found for element expression: %s", context.getText());
        }

    @Override
    public void enterArgumentedExpression(ValueSourceParser.ArgumentedExpressionContext ctx)
        {
        _parse_stack.push(ParseStackMarker.ArgumentedExpression);
        }

    @Override
    public void exitArgumentedExpression(ValueSourceParser.ArgumentedExpressionContext context)
        {
        String function_name = context.getChild(0).getText();

        // pop the arguments off the stack
        Object stack_item = _parse_stack.pop();
        List<ValueSourceConfiguration> arguments = new ArrayList<>();
        while (!(stack_item.equals(ParseStackMarker.ArgumentedExpression)))
            {
            if (stack_item instanceof ValueSourceConfiguration)
                arguments.add(0, (ValueSourceConfiguration)stack_item);
            else
                _error = String.format("Did not expect to see a %s on the stack: %s", stack_item.getClass().getSimpleName(), stack_item.toString());
            stack_item = _parse_stack.pop();
            }

        for (ValueSourceStringExpressionSupport support : getSupporters())
            {
            ValueSourceConfiguration config = support.fromArgumentedExpression(function_name, arguments, _project);
            if (config != null)
                {
                _parse_stack.push(config);
                return;
                }
            }
        _error = String.format("No support found for function name '%s' in argumented expression: %s", function_name, context.getText());
        }

    @Override
    public void exitBinaryExpression(ValueSourceParser.BinaryExpressionContext context)
        {
        String operator = context.getChild(1).getText();
        ValueSourceConfiguration right = (ValueSourceConfiguration) _parse_stack.pop();
        ValueSourceConfiguration left = (ValueSourceConfiguration) _parse_stack.pop();

        for (ValueSourceStringExpressionSupport support : getSupporters())
            {
            ValueSourceConfiguration config = support.fromBinaryExpression(left, operator, right, _project);
            if (config != null)
                {
                _parse_stack.push(config);
                return;
                }
            }
        _error = String.format("No support found for binary operator '%s' in expression: %s", operator, context.getText());
        }

    @Override
    public void exitBooleanExpression(ValueSourceParser.BooleanExpressionContext context)
        {
        String operator = context.getChild(1).getText();
        ValueSourceConfiguration right = (ValueSourceConfiguration) _parse_stack.pop();
        ValueSourceConfiguration left = (ValueSourceConfiguration) _parse_stack.pop();

        for (ValueSourceStringExpressionSupport support : getSupporters())
            {
            ValueSourceConfiguration config = support.fromBooleanExpression(left, operator, right, _project);
            if (config != null)
                {
                _parse_stack.push(config);
                return;
                }
            }
        _error = String.format("No support found for boolean operator '%s' in expression: %s", operator, context.getText());
        }

    @Override
    public void exitDotExpression(ValueSourceParser.DotExpressionContext context)
        {
        String operator = context.getChild(1).getText();
        ValueSourceConfiguration right = (ValueSourceConfiguration) _parse_stack.pop();
        ValueSourceConfiguration left = (ValueSourceConfiguration) _parse_stack.pop();

        for (ValueSourceStringExpressionSupport support : getSupporters())
            {
            ValueSourceConfiguration config = support.fromDotExpression(left, right, _project);
            if (config != null)
                {
                _parse_stack.push(config);
                return;
                }
            }
        _error = String.format("No support found for binary operator '%s' in expression: %s", operator, context.getText());
        }

    @Override
    public void enterArrayExpression(ValueSourceParser.ArrayExpressionContext ctx)
	    {
	    _parse_stack.push(ParseStackMarker.ArrayExpression);
	    }

    @Override
    public void exitArrayExpression(ValueSourceParser.ArrayExpressionContext context)
	    {
	    // pop the arguments off the stack
	    Object stack_item = _parse_stack.pop();
	    List<ValueSourceConfiguration> arguments = new ArrayList<>();
	    while (!(stack_item.equals(ParseStackMarker.ArrayExpression)))
		    {
		    if (stack_item instanceof ValueSourceConfiguration)
			    arguments.add(0, (ValueSourceConfiguration) stack_item);
		    else
			    _error = String.format("Did not expect to see a %s on the stack: %s", stack_item.getClass().getSimpleName(), stack_item.toString());
		    stack_item = _parse_stack.pop();
		    }

	    for (ValueSourceStringExpressionSupport support : getSupporters())
		    {
		    ValueSourceConfiguration config = support.fromArrayExpression(arguments, _project);
		    if (config != null)
			    {
			    _parse_stack.push(config);
			    return;
			    }
		    }
	    _error = "No support found for array expression";
	    }

    @Override
    public void exitArrayItemExpression(ValueSourceParser.ArrayItemExpressionContext context)
        {
        String operator = context.getChild(1).getText();
        ValueSourceConfiguration selector = (ValueSourceConfiguration) _parse_stack.pop();
        ValueSourceConfiguration collection = (ValueSourceConfiguration) _parse_stack.pop();

        for (ValueSourceStringExpressionSupport support : getSupporters())
            {
            ValueSourceConfiguration config = support.fromArrayItemExpression(collection, selector, _project);
            if (config != null)
                {
                _parse_stack.push(config);
                return;
                }
            }
        _error = String.format("No support found for binary operator '%s' in expression: %s", operator, context.getText());
        }

    private List<ValueSourceStringExpressionSupport> getSupporters()
        {
        if (_supporters == null)
            _supporters = _project.getClassLocator().getInstances(ValueSourceStringExpressionSupport.class);
        return _supporters;
        }

    private Stack _parse_stack = new Stack<>();
    private MuseProject _project;
    private String _error = null;

    private List<ValueSourceStringExpressionSupport> _supporters;
    }


