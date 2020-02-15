// Define a grammar called ValueSource
grammar ValueSource;

@header
    {
    package org.museautomation.parsing.valuesource.antlr;
    }

literal
    : DecimalIntegerLiteral
      | NullLiteral
      | BooleanLiteral
      | StringLiteral
    ;

prefixedExpression
    : '$$' simpleExpression
    | '$' simpleExpression
    | '#' simpleExpression
    | '%' simpleExpression
    ;

simpleExpression
    : literal
    | prefixedExpression
    | parenthesizedExpression
    | arrayExpression
    | argumentedExpression
    | elementLookupExpression
    | elementExpression
    ;

fullExpression
    : singleExpression EOF
    ;

elementLookupExpression
    : '<' (Identifier | singleExpression) '.' (Identifier | singleExpression) '>'
    ;

elementExpression
    : '<' Identifier (':' singleExpression)* '>'
    ;

parenthesizedExpression
    : '(' singleExpression ')'
    ;

argumentedExpression
    : Identifier arguments
    ;

singleExpression
    : simpleExpression                                                      # oneExpression
    | singleExpression '.' singleExpression                                 # dotExpression
    | singleExpression '[' singleExpression ']'                             # arrayItemExpression
    | singleExpression ( '+' | '<' | '>' | '==') singleExpression           # binaryExpression
    | singleExpression ('||' | '&&') singleExpression                       # booleanExpression
    ;

arrayExpression
    : '[' argumentList? ']'
    ;

arguments
    : '(' argumentList? ')'
    ;

argumentList
    : singleExpression ( ',' singleExpression )*
    ;

StringLiteral
    : '"' DoubleStringCharacter* '"'
    | '\'' SingleStringCharacter* '\''
    ;

WhiteSpaces
    : [\t\u000B\u000C\u0020\u00A0]+ -> channel(HIDDEN)
    ;

NullLiteral
    : 'null'
    ;

BooleanLiteral
    : 'true'
    | 'false'
    ;

DecimalIntegerLiteral
    : '0'
    | [1-9] DecimalDigit*
    ;

Identifier
    : IdentifierStart IdentifierPart*
    ;

IdentifierStart
    : [a-z]
    | [A-Z]
    | [_]
    ;

IdentifierPart
    : IdentifierStart
    | [0-9]
    | [-]
    ;

fragment DoubleStringCharacter
    : ~["\\\r\n]
    | '\\' EscapeSequence
    ;

fragment SingleStringCharacter
    : ~['\\\r\n]
    | '\\' EscapeSequence
    ;

fragment EscapeSequence
    : CharacterEscapeSequence
    | '0' // no digit ahead! TODO
    | HexEscapeSequence
    | UnicodeEscapeSequence
    ;

fragment CharacterEscapeSequence
    : SingleEscapeCharacter
    | NonEscapeCharacter
    ;

fragment HexEscapeSequence
    : 'x' HexDigit HexDigit
    ;

fragment UnicodeEscapeSequence
    : 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment SingleEscapeCharacter
    : ['"\\bfnrtv]
    ;

fragment NonEscapeCharacter
    : ~['"\\bfnrtv0-9xu\r\n]
    ;

fragment EscapeCharacter
    : SingleEscapeCharacter
    | DecimalDigit
    | [xu]
    ;

fragment DecimalDigit
    : [0-9]
    ;

fragment HexDigit
    : [0-9a-fA-F]
    ;

