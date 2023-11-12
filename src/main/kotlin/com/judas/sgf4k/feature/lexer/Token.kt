package com.judas.sgf4k.feature.lexer

/**
 * A lexical token produced by the Lexer when reading
 * the SGF input string.
 * A token is a simple string value with a type which
 * gives it some meaning for the parser to analyze.
 */
internal data class Token(val type: Type, val value: String) {
    /**
     * The handled token types.
     * There are no keywords or operators in SGF format.
     * Whitespaces (outside property values) are discarded.
     */
    enum class Type {
        IDENTIFIER, // For properties names
        LITERAL, // For properties values
        SEPARATOR // For language delimiters ()[];
    }

    /**
     * The SGF grammar separators characters.
     */
    enum class Separator(val char: Char) {
        LEFT_PARENTHESIS('('),
        RIGHT_PARENTHESIS(')'),
        LEFT_BRACKET('['),
        RIGHT_BRACKET(']'),
        SEMI_COLON(';'),
        ESCAPING_CHAR('\\')
    }
}
