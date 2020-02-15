package org.museautomation.parsing.valuesource;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.museautomation.parsing.valuesource.antlr.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */

public class ValueSourceStringExpressionParsing
    {
    public static void walk(String input, ValueSourceListener walk_listener) throws ExpressionParsingException
        {
        ParseTree tree = parse(input);
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(walk_listener, tree);
        }

    public static ParseTree parse(String input) throws ExpressionParsingException
        {
        ValueSourceLexer lexer = new ValueSourceLexer(new ANTLRInputStream(input));
        final List<String> _errors = new ArrayList<>();
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener()
            {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String message, RecognitionException e)
                {
                _errors.add(message);
                }
            });

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ValueSourceParser parser = new ValueSourceParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener()
            {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String message, RecognitionException e)
                {
                _errors.add(message);
                }
            });
        ParseTree tree = parser.fullExpression();
        if (_errors.size() > 0)
            throw new ExpressionParsingException(_errors, tree);
        else
            return tree;
        }
    }

