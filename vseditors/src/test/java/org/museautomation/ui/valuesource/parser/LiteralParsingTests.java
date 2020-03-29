package org.museautomation.ui.valuesource.parser;

import org.junit.jupiter.api.*;
import org.museautomation.parsing.valuesource.*;
import org.museautomation.builtins.value.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;

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
class LiteralParsingTests
    {
    @Test
    void parseNull() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("null");
        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(NullValueSource.TYPE_ID, configuration.getType());
        Assertions.assertNull(configuration.getValue());
        }

    @Test
    void parseString() throws ExpressionParsingException
        {
        testStringParsing("abc");
        }

    @Test
    void parseEmptyString() throws ExpressionParsingException
        {
        testStringParsing("");
        }

/*
    @Test
    public void parseStringWithQuote() throws ExpressionParsingException
        {
        testStringParsing("abc is a \\\"nice\\\" string to test");
        }
*/

    private void testStringParsing(String s) throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("\"" + s + "\"");
        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(StringValueSource.TYPE_ID, configuration.getType());
        Assertions.assertEquals(s, configuration.getValue());
        }

    @Test
    void parseBooleanTrue() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("true");
        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(BooleanValueSource.TYPE_ID, configuration.getType());
        Assertions.assertEquals(true, configuration.getValue());
        }

    @Test
    void parseBooleanFalse() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("false");
        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(BooleanValueSource.TYPE_ID, configuration.getType());
        Assertions.assertEquals(false, configuration.getValue());
        }

    @Test
    void parseInteger() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("123");
        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(IntegerValueSource.TYPE_ID, configuration.getType());
        Assertions.assertEquals(123L, configuration.getValue());
        }

    @Test
    void parseUnrecognizedLiteral()
        {
        try
            {
            new ValueSourceExpressionParser(_project).parse("blah");
            Assertions.fail("should have thrown an exception");
            }
        catch (ExpressionParsingException e)
            {
            // this is expected result
            }
        }

    private MuseProject _project = new SimpleProject();
    }