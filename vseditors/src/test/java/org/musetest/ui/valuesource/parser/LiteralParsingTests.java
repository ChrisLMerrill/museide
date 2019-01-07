package org.musetest.ui.valuesource.parser;

import org.junit.*;
import org.musetest.builtins.value.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.values.*;
import org.musetest.parsing.valuesource.*;

/**
 * These tests are intended to verify that the parser (muse-parser) is correctly lexing and parsing
 * expected input and finding/using the built-in ValueSourceStringExpressionSupport implementations.
 *
 * These are NOT intended to test whether the ValueSourceStringExpressionSupport implementations are
 * doing the right thing in all cases - those tests should be directly access the implementations wherever
 * they reside (e.g. in the core Muse project).
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class LiteralParsingTests
    {
    @Test
    public void parseNull() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("null");
        Assert.assertNotNull(configuration);
        Assert.assertEquals(NullValueSource.TYPE_ID, configuration.getType());
        Assert.assertNull(configuration.getValue());
        }

    @Test
    public void parseString() throws ExpressionParsingException
        {
        testStringParsing("abc");
        }

    @Test
    public void parseEmptyString() throws ExpressionParsingException
        {
        testStringParsing("");
        }

    @Test
    public void parseStringWithQuote() throws ExpressionParsingException
        {
        testStringParsing("abc is a \\\"nice\\\" string to test");
        }

    private void testStringParsing(String s) throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("\"" + s + "\"");
        Assert.assertNotNull(configuration);
        Assert.assertEquals(StringValueSource.TYPE_ID, configuration.getType());
        Assert.assertEquals(s, configuration.getValue());
        }

    @Test
    public void parseBooleanTrue() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("true");
        Assert.assertNotNull(configuration);
        Assert.assertEquals(BooleanValueSource.TYPE_ID, configuration.getType());
        Assert.assertEquals(true, configuration.getValue());
        }

    @Test
    public void parseBooleanFalse() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("false");
        Assert.assertNotNull(configuration);
        Assert.assertEquals(BooleanValueSource.TYPE_ID, configuration.getType());
        Assert.assertEquals(false, configuration.getValue());
        }

    @Test
    public void parseInteger() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("123");
        Assert.assertNotNull(configuration);
        Assert.assertEquals(IntegerValueSource.TYPE_ID, configuration.getType());
        Assert.assertEquals(123L, configuration.getValue());
        }

    @Test
    public void parseUnrecognizedLiteral() throws ExpressionParsingException
        {
        try
            {
            new ValueSourceExpressionParser(_project).parse("blah");
            Assert.assertNull("should have thrown an exception", "notnull");
            }
        catch (ExpressionParsingException e)
            {
            // this is expected result
            }
        }

    MuseProject _project = new SimpleProject();
    }


