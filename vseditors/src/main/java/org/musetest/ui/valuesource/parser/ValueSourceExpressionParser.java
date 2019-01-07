package org.musetest.ui.valuesource.parser;

import org.musetest.core.*;
import org.musetest.core.values.*;
import org.musetest.parsing.valuesource.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ValueSourceExpressionParser
    {
    public ValueSourceExpressionParser(MuseProject project)
        {
        _project = project;
        }

    public ValueSourceConfiguration parse(String input) throws ExpressionParsingException
        {
        VSBuilder builder = new VSBuilder(_project);
        ValueSourceStringExpressionParsing.walk(input, builder);
        return builder.getSource();
        }

    private MuseProject _project;
    }



