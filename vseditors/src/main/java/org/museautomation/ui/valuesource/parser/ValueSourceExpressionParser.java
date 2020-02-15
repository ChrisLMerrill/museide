package org.museautomation.ui.valuesource.parser;

import org.museautomation.parsing.valuesource.*;
import org.museautomation.core.*;
import org.museautomation.core.values.*;

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



