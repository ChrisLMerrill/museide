package org.musetest.ui.valuesource.parser;

import org.junit.*;
import org.musetest.builtins.value.*;
import org.musetest.builtins.value.logic.*;
import org.musetest.builtins.value.property.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.values.*;
import org.musetest.parsing.valuesource.*;

/**
 * These tests are intended to verify that the parser (muse-parser) is correctly lexing and parsing
 * expected input and finding/using the built-in ValueSourceStringExpressionSupport implementations.
 *
 * These are NOT intended to test whether the ValueSourceStringExpressionSupport implementations are
 * doing the right thing in all cases - those tests should directly access the implementations wherever
 * they reside (e.g. in the core Muse project). Instead, these test should just ensure the correct
 * value source is created based on the parse result.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class BooleanOperatorParsingTests
    {
    @Test
    public void parseOrSource() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("true || false");
        Assert.assertEquals(OrValueSource.TYPE_ID, configuration.getType());

        ValueSourceConfiguration left_source = configuration.getSource(0);
        Assert.assertEquals(BooleanValueSource.TYPE_ID, left_source.getType());
        Assert.assertEquals(true, left_source.getValue());

        ValueSourceConfiguration right_source = configuration.getSource(1);
        Assert.assertEquals(BooleanValueSource.TYPE_ID, right_source.getType());
        Assert.assertEquals(false, right_source.getValue());
        }

    private MuseProject _project = new SimpleProject();
    }


