package org.museautomation.parsing.valuesource;

import org.antlr.v4.runtime.tree.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExpressionParsingException extends Exception
    {
    public ExpressionParsingException(List<String> parse_errors, ParseTree tree)
        {
        super(parse_errors.get(0));
        _parse_errors = parse_errors;
        _tree = tree;
        }

    @SuppressWarnings("unused")  // provided for UI support
    public String getMessages()
        {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String error : _parse_errors)
            {
            if (!first)
                builder.append("\n");
            builder.append(error);
            first = false;
            }
        return builder.toString();
        }

    final List<String> _parse_errors;
    final ParseTree _tree;
    }


