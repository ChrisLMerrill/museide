package org.museautomation.parsing.valuesource;

import org.junit.jupiter.api.*;
import org.museautomation.parsing.valuesource.antlr.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class LiteralParsingTests
    {
    @Test
    void singleQuoteString() throws ExpressionParsingException
        {
        String expected_value = "'stuff'";
        check(expected_value, expected_value);
        check(inParens(expected_value), expected_value);
        check(inSpaces(expected_value), expected_value);
        }

    @Test
    void doubleQuoteString() throws ExpressionParsingException
        {
        String expected_value = "\"stuff\"";
        check(expected_value, expected_value);
        check(inParens(expected_value), expected_value);
        check(inSpaces(expected_value), expected_value);
        }

    @Test
    void trueLiteral() throws ExpressionParsingException
        {
        String expression = Boolean.TRUE.toString();
        check(expression, expression);
        check(inParens(expression), expression);
        }

    @Test
    void falseLiteral() throws ExpressionParsingException
        {
        String expression = Boolean.FALSE.toString();
        check(expression, expression);
        check(inParens(expression), expression);
        }

    @Test
    void nullLiteral() throws ExpressionParsingException
        {
        String expression = "null";
        check(expression, expression);
        check(inParens(expression), expression);
        }

    @Test
    void integerLiteral() throws ExpressionParsingException
        {
        String expression = Integer.toString(123);
        check(expression, expression);
        check(inParens(expression), expression);
        check(inSpaces(expression), expression);
        }

    @Test
    void zeroLiteral() throws ExpressionParsingException
        {
        int value = 0;
        String expression = Integer.toString(value);
        check(expression, expression);
        check(inParens(expression), expression);
        check(inSpaces(expression), expression);
        }

    @Test
    void invalidInteger()
        {
        checkFailure("123a");
        }

    @Test
    void unquotedString()
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
        Assertions.assertEquals(expected_value, holder._object);
        }

    private void checkFailure(String string_to_parse)
        {
        ValueSourceBaseListener listener = new ValueSourceBaseListener() {};
        try
            {
            ValueSourceStringExpressionParsing.walk(string_to_parse, listener);
            Assertions.fail("This should have thrown an exception");
            }
        catch (ExpressionParsingException e)
            {
            // ok
            }
        }

    static class ObjectHolder<T>
        {
        T _object = null;
        }
    }