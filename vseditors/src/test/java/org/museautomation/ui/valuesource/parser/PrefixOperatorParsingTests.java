package org.museautomation.ui.valuesource.parser;

import org.junit.jupiter.api.*;
import org.museautomation.parsing.valuesource.*;
import org.museautomation.builtins.value.*;
import org.museautomation.builtins.value.sysvar.*;
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
public class PrefixOperatorParsingTests
    {
    @Test
    public void parseVariableSource() throws ExpressionParsingException
        {
        String var_name = "var1";
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("$\"" + var_name + "\"");
        Assertions.assertEquals(VariableValueSource.TYPE_ID, configuration.getType());
        ValueSourceConfiguration name_source = configuration.getSource();
        Assertions.assertEquals(StringValueSource.TYPE_ID, name_source.getType());
        Assertions.assertEquals(var_name, name_source.getValue());
        }

    @Test
    public void parseSystemVariableSource() throws ExpressionParsingException
        {
        String var_name = "sysvar1";
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("$$\"" + var_name + "\"");
        Assertions.assertEquals(SystemVariableSource.TYPE_ID, configuration.getType());
        ValueSourceConfiguration name_source = configuration.getSource();
        Assertions.assertEquals(StringValueSource.TYPE_ID, name_source.getType());
        Assertions.assertEquals(var_name, name_source.getValue());
        }

    @Test
    public void parseProjectResourceSource() throws ExpressionParsingException
        {
        String resource_name = "resource1";
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("#\"" + resource_name + "\"");
        Assertions.assertEquals(ProjectResourceValueSource.TYPE_ID, configuration.getType());
        ValueSourceConfiguration name_source = configuration.getSource();
        Assertions.assertEquals(StringValueSource.TYPE_ID, name_source.getType());
        Assertions.assertEquals(resource_name, name_source.getValue());
        }

    MuseProject _project = new SimpleProject();
    }


