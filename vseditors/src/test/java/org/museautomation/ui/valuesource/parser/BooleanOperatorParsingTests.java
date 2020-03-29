package org.museautomation.ui.valuesource.parser;

import org.junit.jupiter.api.*;
import org.museautomation.parsing.valuesource.*;
import org.museautomation.builtins.value.*;
import org.museautomation.builtins.value.logic.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;

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
class BooleanOperatorParsingTests
    {
    @Test
    void parseOrSource() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("true || false");
        Assertions.assertEquals(OrValueSource.TYPE_ID, configuration.getType());

        ValueSourceConfiguration left_source = configuration.getSource(0);
        Assertions.assertEquals(BooleanValueSource.TYPE_ID, left_source.getType());
        Assertions.assertEquals(true, left_source.getValue());

        ValueSourceConfiguration right_source = configuration.getSource(1);
        Assertions.assertEquals(BooleanValueSource.TYPE_ID, right_source.getType());
        Assertions.assertEquals(false, right_source.getValue());
        }

    private MuseProject _project = new SimpleProject();
    }