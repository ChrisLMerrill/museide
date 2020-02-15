package org.museautomation.ui.valuesource.parser;

import org.junit.*;
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
public class StringConcatenationTests
    {
    @Test
    public void stringConcatenation2() throws ExpressionParsingException
        {
        testMultipleOperands("+", AdditionSource.TYPE_ID, 123L, 456L);
        }

    @Test
    public void stringConcatenation3() throws ExpressionParsingException
        {
        testMultipleOperands("+", AdditionSource.TYPE_ID, 123L, 456L, 789L);
        }

    @Test
    public void stringConcatenation4() throws ExpressionParsingException
        {
        testMultipleOperands("+", AdditionSource.TYPE_ID, 11L, 22L, 33L, 44L);
        }

    @Test
    public void stringConcatenation5() throws ExpressionParsingException
        {
        testMultipleOperands("+", AdditionSource.TYPE_ID, 11L, 22L, 33L, 44L, 55L);
        }

    private void testMultipleOperands(String operator, String muse_type, Long... operands) throws ExpressionParsingException
        {
        StringBuilder builder = new StringBuilder();
        String separator = " " + operator + " ";
        for (Long operand : operands)
            {
            if (builder.length() > 0)
                builder.append(separator);
            builder.append(operand);
            }
        String to_parse = builder.toString();

        ValueSourceConfiguration config = new ValueSourceExpressionParser(_project).parse(to_parse);
        Assert.assertNotNull(config);
        Assert.assertEquals(muse_type, config.getType());
        int index = 0;
        for (Long operand : operands)
            {
            Assert.assertEquals(operand, config.getSource(index).getValue());
            index++;
            }
        }

    MuseProject _project = new SimpleProject();
    }


