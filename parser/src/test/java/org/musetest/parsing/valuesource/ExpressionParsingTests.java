package org.musetest.parsing.valuesource;

import org.antlr.v4.runtime.tree.*;
import org.junit.*;
import org.musetest.parsing.valuesource.antlr.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExpressionParsingTests
    {
    @Test
    public void prefixedExpression() throws ExpressionParsingException
        {
        checkPrefixed("$$", "\"abc\"");
        checkPrefixed("$", "\"def\"");
        checkPrefixed("#", "\"xyz\"");
        checkPrefixed("%", "\"clm\"");
        }

    private void checkPrefixed(String prefix, String expression) throws ExpressionParsingException
        {
        String string_to_parse = prefix + expression;
        final PrefixHolder holder = new PrefixHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitPrefixedExpression(ValueSourceParser.PrefixedExpressionContext context)
                {
                holder._prefix = context.children.get(0).getText();
                }

            @Override
            public void enterLiteral(ValueSourceParser.LiteralContext context)
                {
                holder._expression = context.getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals(prefix, holder._prefix);
        Assert.assertEquals(expression, holder._expression);
        }

    class PrefixHolder
        {
        String _prefix = null;
        String _expression = null;
        }


    class ElementHolder
        {
        String _type = null;
        String _qualifier = null;
        String _qualifier2 = null;
        }

    @Test
    public void elementExpressionNoArgs() throws ExpressionParsingException
        {
        final String type = "type";
        String string_to_parse = String.format("<%s>", type);
        final ElementHolder holder = new ElementHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitElementExpression(ValueSourceParser.ElementExpressionContext context)
                {
                holder._type = context.getChild(1).getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals(type, holder._type);
        }

    @Test
    public void elementExpression1Arg() throws ExpressionParsingException
        {
        final String type = "type";
        final String qualifier = "\"qualifier\"";
        String string_to_parse = String.format("<%s:%s>", type, qualifier);
        final ElementHolder holder = new ElementHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitElementExpression(ValueSourceParser.ElementExpressionContext context)
                {
                holder._type = context.getChild(1).getText();
                holder._qualifier = context.getChild(3).getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals(type, holder._type);
        Assert.assertEquals(qualifier, holder._qualifier);
        }

    @Test
    public void elementExpression2Args() throws ExpressionParsingException
        {
        final String type = "type";
        final String qualifier = "\"qualifier\"";
        final String qualifier2 = "\"q2\"";
        String string_to_parse = String.format("<%s:%s:%s>", type, qualifier, qualifier2);
        final ElementHolder holder = new ElementHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitElementExpression(ValueSourceParser.ElementExpressionContext context)
                {
                holder._type = context.getChild(1).getText();
                holder._qualifier = context.getChild(3).getText();
                holder._qualifier2 = context.getChild(5).getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals(type, holder._type);
        Assert.assertEquals(qualifier, holder._qualifier);
        Assert.assertEquals(qualifier2, holder._qualifier2);
        }

    class ElementLookupHolder
        {
        String _page;
        String _element;
        }

    @Test
    public void elementLookupExpression() throws ExpressionParsingException
        {
        final String page = "page1";
        final String element = "element1";
        String string_to_parse = String.format("<%s.%s>", page, element);
        final ElementLookupHolder holder = new ElementLookupHolder();

        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitElementLookupExpression(ValueSourceParser.ElementLookupExpressionContext context)
                {
                holder._page = context.getChild(1).getText();
                holder._element = context.getChild(3).getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);

        Assert.assertEquals(page, holder._page);
        Assert.assertEquals(element, holder._element);
        }

    @Test
    public void elementLookupExpression2() throws ExpressionParsingException
        {
        final String page = "\"page1\"";
        final String element = "\"element1\"";
        String string_to_parse = String.format("<%s.%s>", page, element);
        final ElementLookupHolder holder = new ElementLookupHolder();

        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitElementLookupExpression(ValueSourceParser.ElementLookupExpressionContext context)
                {
                holder._page = context.getChild(1).getText();
                holder._element = context.getChild(3).getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);

        Assert.assertEquals(page, holder._page);
        Assert.assertEquals(element, holder._element);
        }

    class ArgumentedHolder
        {
        String _name;
        List<String> _arguments = new ArrayList<>();
        }

    @Test
    public void argumentedExpressionNoArguments() throws ExpressionParsingException
        {
        final String name = "f1";
        String string_to_parse = String.format("%s()", name);
        final ArgumentedHolder holder = new ArgumentedHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitArgumentedExpression(ValueSourceParser.ArgumentedExpressionContext context)
                {
                holder._name = context.getChild(0).getText();
                }
        };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals(name, holder._name);
        }

    @Test
    public void argumentedExpression1Argument() throws ExpressionParsingException
        {
        final String name = "f1";
        final String arg = "123";
        String string_to_parse = String.format("%s(%s)", name, arg);
        final ArgumentedHolder holder = new ArgumentedHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitArgumentedExpression(ValueSourceParser.ArgumentedExpressionContext context)
                {
                holder._name = context.getChild(0).getText();
                holder._arguments.add(context.getChild(1).getChild(1).getText());
                }
        };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals(name, holder._name);
        Assert.assertEquals(arg, holder._arguments.get(0));
        }

    @Test
    public void argumentedExpressionMultipleArguments() throws ExpressionParsingException
        {
        final String name = "f1";
        final String arg0 = "123";
        final String arg1 = "456";
        final String arg2 = "789";
        String string_to_parse = String.format("%s(%s,%s,%s)", name, arg0, arg1, arg2);
        final ArgumentedHolder holder = new ArgumentedHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitArgumentedExpression(ValueSourceParser.ArgumentedExpressionContext context)
                {
                holder._name = context.getChild(0).getText();
                holder._arguments.add(context.getChild(1).getChild(1).getChild(0).getText());
                holder._arguments.add(context.getChild(1).getChild(1).getChild(2).getText());
                holder._arguments.add(context.getChild(1).getChild(1).getChild(4).getText());
                }
        };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals(name, holder._name);
        Assert.assertEquals(arg0, holder._arguments.get(0));
        Assert.assertEquals(arg1, holder._arguments.get(1));
        Assert.assertEquals(arg2, holder._arguments.get(2));
        }

    @Test
    public void arrayZeroLength() throws ExpressionParsingException
	    {
	    List<String> elements = parseTrivialArray("[]");
        Assert.assertEquals(0, elements.size());
        }

    @Test
    public void arrayLengthOne() throws ExpressionParsingException
	    {
        List<String> elements = parseTrivialArray("[123]");
        Assert.assertEquals(1, elements.size());
        Assert.assertEquals("123", elements.get(0));
        }

    @Test
    public void arrayLengthTwo() throws ExpressionParsingException
	    {
        List<String> elements = parseArray("[123,456]");
        Assert.assertEquals(2, elements.size());
        Assert.assertEquals("123", elements.get(0));
        Assert.assertEquals("456", elements.get(1));
        }

    @Test
    public void arrayLengthThree() throws ExpressionParsingException
	    {
        List<String> elements = parseArray("[123,456,789]");
        Assert.assertEquals(3, elements.size());
        Assert.assertEquals("123", elements.get(0));
        Assert.assertEquals("456", elements.get(1));
        Assert.assertEquals("789", elements.get(2));
        }

    private List<String> parseTrivialArray(String string_to_parse) throws ExpressionParsingException
	    {
	    AtomicBoolean called = new AtomicBoolean(false);
	    ArrayList<String> elements = new ArrayList<>();
	    ValueSourceBaseListener listener = new ValueSourceBaseListener()
		    {
		    @Override
		    public void exitArrayExpression(ValueSourceParser.ArrayExpressionContext context)
			    {
			    called.set(true);
			    int size = context.getChild(1).getChildCount();
			    for (int i = 0; i < size; i++)
				    elements.add(context.getChild(1).getChild(i).getText());
			    }
		    };

	    ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
	    Assert.assertTrue(called.get());
	    return elements;
	    }

    private List<String> parseArray(String string_to_parse) throws ExpressionParsingException
	    {
	    AtomicBoolean called = new AtomicBoolean(false);
	    ArrayList<String> elements = new ArrayList<>();
	    ValueSourceBaseListener listener = new ValueSourceBaseListener()
		    {
		    @Override
		    public void exitArrayExpression(ValueSourceParser.ArrayExpressionContext context)
			    {
			    called.set(true);
			    ParseTree list_parent = context.getChild(1);
			    int size = list_parent.getChildCount();
			    for (int i = 0; i < size; i+=2)
				    elements.add(list_parent.getChild(i).getText());
			    }
		    };

	    ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
	    Assert.assertTrue(called.get());
	    return elements;
	    }

    class BinaryHolder
        {
        String _operator;
        String _arg0;
        String _arg1;
        }

    @Test
    public void binaryExpression() throws ExpressionParsingException
        {
        final String operator = "<";
        final String arg0 = "123";
        final String arg1 = "456";
        String string_to_parse = String.format("%s %s %s", arg0, operator, arg1);
        final BinaryHolder holder = new BinaryHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitBinaryExpression(ValueSourceParser.BinaryExpressionContext context)
                {
                holder._arg0 = context.getChild(0).getText();
                holder._operator = context.getChild(1).getText();
                holder._arg1 = context.getChild(2).getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals(operator, holder._operator);
        Assert.assertEquals(arg0, holder._arg0);
        Assert.assertEquals(arg1, holder._arg1);
        }

    @Test
    public void booleanExpression() throws ExpressionParsingException
        {
        final String operator = "||";
        final String arg0 = "true";
        final String arg1 = "false";
        String string_to_parse = String.format("%s %s %s", arg0, operator, arg1);
        final BinaryHolder holder = new BinaryHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitBooleanExpression(ValueSourceParser.BooleanExpressionContext context)
                {
                holder._arg0 = context.getChild(0).getText();
                holder._operator = context.getChild(1).getText();
                holder._arg1 = context.getChild(2).getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals(operator, holder._operator);
        Assert.assertEquals(arg0, holder._arg0);
        Assert.assertEquals(arg1, holder._arg1);
        }

    @Test
    public void dotExpression() throws ExpressionParsingException
        {
        final String operator = ".";
        final String arg0 = "\"abc\"";
        final String arg1 = "\"xyz\"";
        String string_to_parse = String.format("%s%s%s", arg0, operator, arg1);
        final BinaryHolder holder = new BinaryHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitDotExpression(ValueSourceParser.DotExpressionContext context)
                {
                holder._arg0 = context.getChild(0).getText();
                holder._operator = context.getChild(1).getText();
                holder._arg1 = context.getChild(2).getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals(operator, holder._operator);
        Assert.assertEquals(arg0, holder._arg0);
        Assert.assertEquals(arg1, holder._arg1);
        }

    class ArrayItemHolder
        {
        String _bracket1;
        String _bracket2;
        String _arg0;
        String _arg1;
        }
    
    @Test
    public void arrayItemAccess() throws ExpressionParsingException
        {
        final String arg0 = "$\"list\"";
        final String arg1 = "0";
        String string_to_parse = String.format("%s[%s]", arg0, arg1);
        final ArrayItemHolder holder = new ArrayItemHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitArrayItemExpression(ValueSourceParser.ArrayItemExpressionContext context)
                {
                holder._arg0 = context.getChild(0).getText();
                holder._bracket1 = context.getChild(1).getText();
                holder._arg1 = context.getChild(2).getText();
                holder._bracket2 = context.getChild(3).getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals("[", holder._bracket1);
        Assert.assertEquals("]", holder._bracket2);
        Assert.assertEquals(arg0, holder._arg0);
        Assert.assertEquals(arg1, holder._arg1);
        }

    @Test
    public void arrayItemAccessByVariable() throws ExpressionParsingException
        {
        final String arg0 = "$\"list\"";
        final String arg1 = "$\"index\"";
        String string_to_parse = String.format("%s[%s]", arg0, arg1);
        final ArrayItemHolder holder = new ArrayItemHolder();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitArrayItemExpression(ValueSourceParser.ArrayItemExpressionContext context)
                {
                holder._arg0 = context.getChild(0).getText();
                holder._bracket1 = context.getChild(1).getText();
                holder._arg1 = context.getChild(2).getText();
                holder._bracket2 = context.getChild(3).getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals("[", holder._bracket1);
        Assert.assertEquals("]", holder._bracket2);
        Assert.assertEquals(arg0, holder._arg0);
        Assert.assertEquals(arg1, holder._arg1);
        }

    @Test
    public void dotPrecedenceAboveBinaryExp() throws ExpressionParsingException
        {
        // "abc" + "def"."ghi" is  "abc" + ("def"."ghi") rather than ("abc" + "def")."ghi"
        final String expression = "\"abc\" + \"def\".\"ghi\"";
        final List<String> operators = new ArrayList<>();

        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            @Override
            public void exitDotExpression(ValueSourceParser.DotExpressionContext context)
                {
                operators.add(context.getChild(1).getText());
                }

            @Override
            public void exitBinaryExpression(ValueSourceParser.BinaryExpressionContext context)
                {
                operators.add(context.getChild(1).getText());
                }
            };
        ValueSourceStringExpressionParsing.walk(expression, listener);
        Assert.assertEquals("The '.' operator should be evalated first", ".", operators.get(0));
        Assert.assertEquals("The '+' operator should be evalated second", "+", operators.get(1));
        }

    @Test
    public void prefixPrecedence() throws ExpressionParsingException
        {
        String left = "$\"abc\"";
        String operator = "+";
        String right = "123";
        String to_parse = String.format("%s %s %s", left, operator, right);

        ParseTree tree = ValueSourceStringExpressionParsing.parse(to_parse);
        Assert.assertEquals(2, tree.getChildCount()); // two nodes: binary plus EOF
        ParseTree binary = tree.getChild(0);
        Assert.assertEquals(3, binary.getChildCount()); // three nodes: left, '+' and right
        Assert.assertEquals(left, binary.getChild(0).getText());
        Assert.assertEquals(operator, binary.getChild(1).getText());
        Assert.assertEquals(right, binary.getChild(2).getText());
        }
    }


