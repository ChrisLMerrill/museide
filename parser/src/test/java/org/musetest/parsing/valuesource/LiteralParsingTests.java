package org.musetest.parsing.valuesource;

import org.junit.*;
import org.musetest.parsing.valuesource.antlr.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class LiteralParsingTests
    {
    @Test
    public void singleQuoteString() throws ExpressionParsingException
        {
        String expected_value = "'stuff'";
        check(expected_value, expected_value);
        check(inParens(expected_value), expected_value);
        check(inSpaces(expected_value), expected_value);
        }

    @Test
    public void doubleQuoteString() throws ExpressionParsingException
        {
        String expected_value = "\"stuff\"";
        check(expected_value, expected_value);
        check(inParens(expected_value), expected_value);
        check(inSpaces(expected_value), expected_value);
        }

    @Test
    public void trueLiteral() throws ExpressionParsingException
        {
        String expression = Boolean.TRUE.toString();
        check(expression, expression);
        check(inParens(expression), expression);
        }

    @Test
    public void falseLiteral() throws ExpressionParsingException
        {
        String expression = Boolean.FALSE.toString();
        check(expression, expression);
        check(inParens(expression), expression);
        }

    @Test
    public void nullLiteral() throws ExpressionParsingException
        {
        String expression = "null";
        check(expression, expression);
        check(inParens(expression), expression);
        }

    @Test
    public void integerLiteral() throws ExpressionParsingException
        {
        String expression = Integer.toString(123);
        check(expression, expression);
        check(inParens(expression), expression);
        check(inSpaces(expression), expression);
        }

    @Test
    public void zeroLiteral() throws ExpressionParsingException
        {
        int value = 0;
        String expression = Integer.toString(value);
        check(expression, expression);
        check(inParens(expression), expression);
        check(inSpaces(expression), expression);
        }

    @Test
    public void invalidInteger() throws ExpressionParsingException
        {
        checkFailure("123a");
        }

    @Test
    public void unquotedString() throws ExpressionParsingException
        {
        String expression = "ugh";
        checkFailure(expression);
        }

    private String inParens(String value)
        {
        return "(" + value + ")";
        }

    private String inSpaces(String value)
        {
        return " " + value + " ";
        }

    private void check(String string_to_parse, Object expected_value) throws ExpressionParsingException
        {
        final ObjectHolder<String> holder = new ObjectHolder<>();
        ValueSourceBaseListener listener = new ValueSourceBaseListener()
            {
            public void enterLiteral(ValueSourceParser.LiteralContext context)
                {
                holder._object = context.getText();
                }
            };
        ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
        Assert.assertEquals(expected_value, holder._object);
        }

    private void checkFailure(String string_to_parse) throws ExpressionParsingException
        {
        ValueSourceBaseListener listener = new ValueSourceBaseListener() {};
        try
            {
            ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
            Assert.assertTrue("This should have thrown an exception", false);
            }
        catch (ExpressionParsingException e)
            {
            // ok
            }
        }

    class ObjectHolder<T>
        {
        T _object = null;
        }
    }


