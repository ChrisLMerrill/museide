package org.museautomation.ui.valuesource.parser;

import org.junit.*;
import org.museautomation.parsing.valuesource.*;
import org.museautomation.builtins.value.*;
import org.museautomation.builtins.value.property.*;
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
public class BinaryOperatorParsingTests
    {
    @Test
    public void parsePropertySource() throws ExpressionParsingException
        {
        String target_value = "target";
        String property_value = "property";
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse(String.format("\"%s\".\"%s\"", target_value, property_value));
        Assert.assertEquals(PropertySource.TYPE_ID, configuration.getType());

        ValueSourceConfiguration target_source = configuration.getSource(PropertySource.TARGET_PARAM);
        Assert.assertEquals(StringValueSource.TYPE_ID, target_source.getType());
        Assert.assertEquals(target_value, target_source.getValue());

        ValueSourceConfiguration property_source = configuration.getSource(PropertySource.NAME_PARAM);
        Assert.assertEquals(StringValueSource.TYPE_ID, property_source.getType());
        Assert.assertEquals(property_value, property_source.getValue());
        }

    MuseProject _project = new SimpleProject();
    }


