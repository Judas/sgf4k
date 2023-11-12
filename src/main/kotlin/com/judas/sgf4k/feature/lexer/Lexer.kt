package com.judas.sgf4k.feature.lexer

import com.judas.sgf4k.feature.lexer.Token.Separator.*
import com.judas.sgf4k.feature.lexer.Token.Type.*
import com.judas.sgf4k.feature.exceptions.InvalidSgfException

/**
 * Lexical tokenizer in charge of reading the sgf string
 * and convert it to lexical tokens (string value with
 * meaning).
 *
 * See https://www.red-bean.com/sgf/sgf4.html
 */
internal class Lexer {
    /**
     * The internal state of the lexer, that changes while reading
     * the SGF string. The state impacts the token generation as
     * some characters may have different meaning based on it.
     *
     * For instance, space characters are
     * discarded in DEFAULT mode, but should be kept inside
     * comment values (VALUE state).
     */
    enum class State {
        DEFAULT, KEY, VALUE
    }

    /**
     * The main lexer task : reads the sgf input String and
     * transform it into a list of tokens, following the sgf
     * grammar rules.
     */
    fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var state: State = State.DEFAULT
        var buffer = StringBuffer()
        var escaping = false

        input.forEachIndexed { index, char ->
            val separator = Token.Separator.entries.find { it.char == char }
            when (state) {
                State.DEFAULT -> when (separator) {
                    // Invalid separator character : '[', ']', '\' => error
                    RIGHT_BRACKET, ESCAPING_CHAR ->
                        throw InvalidSgfException("Invalid character $char at index $index")

                    // Valid separator character : '[' => switch to value mode
                    LEFT_BRACKET -> {
                        tokens.add(Token(SEPARATOR, char.toString())) // Add '[' separator
                        state = State.VALUE
                        buffer = StringBuffer()
                    }

                    // Valid separator character : '(', ')', ';' => add token
                    LEFT_PARENTHESIS, RIGHT_PARENTHESIS, SEMI_COLON ->
                        tokens.add(Token(SEPARATOR, char.toString()))

                    null -> when {
                        // Uppercase letter => switch to KEY state
                        char in 'A'..'Z' -> {
                            state = State.KEY
                            buffer.append(char)
                        }

                        // Skip whitespaces
                        char.isWhitespace() -> {}

                        // Invalid character
                        else -> throw InvalidSgfException("Invalid character $char at index $index")
                    }
                }

                State.KEY -> when (separator) {
                    // Invalid separator character : '(', ')', ']', ';', '\' => error
                    LEFT_PARENTHESIS, RIGHT_PARENTHESIS, RIGHT_BRACKET, SEMI_COLON, ESCAPING_CHAR ->
                        throw InvalidSgfException("Invalid character $char at index $index")

                    // Valid separator character : '[' => switch to VALUE
                    LEFT_BRACKET -> {
                        tokens.add(Token(IDENTIFIER, buffer.toString())) // Add buffered key
                        tokens.add(Token(SEPARATOR, char.toString())) // Add '[' separator
                        state = State.VALUE
                        buffer = StringBuffer()
                    }

                    null -> when {
                        // Uppercase letter => append to buffered key
                        char in 'A'..'Z' -> {
                            state = State.KEY
                            buffer.append(char)
                        }

                        // Skip whitespaces
                        char.isWhitespace() -> {}

                        // Invalid character
                        else -> throw InvalidSgfException("Invalid character $char at index $index")
                    }
                }

                State.VALUE -> {
                    when (separator) {
                        // Valid separator character used as text : '(', ')', '[', ';'
                        LEFT_PARENTHESIS, RIGHT_PARENTHESIS, LEFT_BRACKET, SEMI_COLON ->
                            buffer.append(char)

                        // Escaping char => toggle escaping mode
                        ESCAPING_CHAR -> {
                            if (escaping) buffer.append(char)
                            escaping = !escaping
                        }

                        // Valid separator character : ']' => switch to DEFAULT
                        RIGHT_BRACKET -> {
                            if (escaping) {
                                buffer.append(char)
                                escaping = false
                            } else {
                                tokens.add(Token(LITERAL, buffer.toString())) // Add buffered value
                                tokens.add(Token(SEPARATOR, char.toString())) // Add ']' separator
                                state = State.DEFAULT
                                buffer = StringBuffer()
                            }
                        }

                        // Non separator character => append to buffered value
                        null -> buffer.append(char)
                    }
                }
            }
        }
        return tokens
    }
}
