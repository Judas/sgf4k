package com.judas.sgf4k.feature.lexer

import com.judas.sgf4k.feature.exceptions.InvalidSgfException
import com.judas.sgf4k.utility.loadTestFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LexerTest {
    @Test
    fun `GIVEN invalid separator at sgf start WHEN lexing THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("separator_at_start.sgf")
        assertThrows<InvalidSgfException> { Lexer().tokenize(sgf) }
    }

    @Test
    fun `GIVEN invalid lowercase property key WHEN lexing THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("lowercase_property.sgf")
        assertThrows<InvalidSgfException> { Lexer().tokenize(sgf) }
    }

    @Test
    fun `GIVEN invalid separator inside property key WHEN lexing THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("separator_in_property.sgf")
        assertThrows<InvalidSgfException> { Lexer().tokenize(sgf) }
    }

    @Test
    fun `GIVEN missing property key WHEN lexing THEN no InvalidSgfException is thrown`() {
        val sgf = loadTestFile("missing_property_key.sgf")
        assertDoesNotThrow { Lexer().tokenize(sgf) }
    }

    @Test
    fun `GIVEN an empty sgf WHEN lexing THEN an empty token list is returned`() {
        assertTrue(Lexer().tokenize("").isEmpty())
    }

    @Test
    fun `GIVEN a non-empty sgf WHEN lexing THEN the right number of tokens is returned`() {
        val sgf = loadTestFile("minimal.sgf")
        val tokens = Lexer().tokenize(sgf)
        assertEquals(49, tokens.size)
    }

    @Test
    fun `GIVEN escaped characters WHEN lexing THEN the escaped chars are preserved`() {
        val sgf = loadTestFile("escaping.sgf")
        val tokens = Lexer().tokenize(sgf)

        assertEquals(24, tokens.size)
        assertEquals(Token.Type.LITERAL, tokens[17].type)
        assertEquals("These chars should be used verbatim ();:[", tokens[17].value)
        assertEquals(Token.Type.LITERAL, tokens[21].type)
        assertEquals("These chars should be escaped to be valid ] \\", tokens[21].value)
    }

    @Test
    fun `GIVEN a node property with multiple values WHEN lexing THEN no InvalidSgfException is thrown`() {
        val sgf = loadTestFile("multiple_values.sgf")

        assertDoesNotThrow {
            val tokens = Lexer().tokenize(sgf)

            assertEquals(58, tokens.size)
            assertEquals(Token.Type.LITERAL, tokens[24].type)
            assertEquals("dr:ds", tokens[24].value)
        }
    }

    @Test
    fun `GIVEN whitespaces WHEN lexing THEN whitespaces are filtered`() {
        val sgfWithSpaces = loadTestFile("minimal_with_spaces.sgf")
        val sgfWithoutSpaces = loadTestFile("minimal.sgf")

        val tokensWithSpaces = Lexer().tokenize(sgfWithSpaces)
        val tokensWithoutSpaces = Lexer().tokenize(sgfWithoutSpaces)
        assertContentEquals(tokensWithSpaces, tokensWithoutSpaces)
    }

    @Test
    fun `GIVEN an empty node WHEN lexing THEN no InvalidSgfException is thrown`() {
        val sgf = loadTestFile("empty_nodes.sgf")

        assertDoesNotThrow {
            val tokens = Lexer().tokenize(sgf)
            assertEquals(26, tokens.size)
        }
    }

    @Test
    fun `GIVEN a property with empty value WHEN lexing THEN an empty string is added as literal token`() {
        val sgf = loadTestFile("empty_values.sgf")
        val tokens = Lexer().tokenize(sgf)

        assertEquals(26, tokens.size)
        assertEquals(Token.Type.LITERAL, tokens[18].type)
        assertTrue(tokens[18].value.isBlank())
        assertEquals(Token.Type.LITERAL, tokens[23].type)
        assertTrue(tokens[23].value.isBlank())
    }

    @Test
    fun `GIVEN empty games WHEN lexing THEN no InvalidSgfException is thrown`() {
        val sgf = loadTestFile("empty_games.sgf")

        assertDoesNotThrow {
            val tokens = Lexer().tokenize(sgf)
            assertEquals(12, tokens.size)
        }
    }
}
