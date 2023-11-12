package com.judas.sgf4k.feature.validator

import com.judas.sgf4k.feature.exceptions.InvalidGameException
import com.judas.sgf4k.feature.exceptions.InvalidSgfException
import com.judas.sgf4k.feature.lexer.Lexer
import com.judas.sgf4k.feature.parser.Parser
import com.judas.sgf4k.utility.loadTestFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class ValidatorTest {
    @Test
    fun `GIVEN game without size WHEN validating THEN an InvalidGameException is thrown`() {
        val sgf = loadTestFile("no_size.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN game with empty size WHEN validating THEN an InvalidGameException is thrown`() {
        val sgf = loadTestFile("empty_size.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN game with multiple size values WHEN validating THEN an InvalidGameException is thrown`() {
        val sgf = loadTestFile("multiple_sizes.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN game with invalid size value WHEN validating THEN an InvalidGameException is thrown`() {
        val sgf = loadTestFile("invalid_size.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN multiple game info nodes in the same path WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("mutiple_gameinfo_nodes_invalid.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidSgfException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN multiple game info nodes in neighbor path WHEN validating THEN no InvalidSgfException is thrown`() {
        val sgf = loadTestFile("mutiple_gameinfo_nodes_valid.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertDoesNotThrow { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN root properties outside of root node WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("misplaced_root_properties.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidSgfException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN node with both setup & move properties WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("mixed_setup_move_properties.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidSgfException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN invalid move properties format WHEN validating THEN an InvalidSgfException is thrown`() {
        listOf(1, 2, 3)
            .map { loadTestFile("invalid_move_value_0$it.sgf") }
            .map { Lexer().tokenize(it) }
            .map { Parser().parse(it) }
            .forEach { assertThrows<InvalidGameException> { Validator().validate(it) } }
    }

    @Test
    fun `GIVEN out-of-bounds move WHEN validating THEN an InvalidSgfException is thrown`() {
        listOf(1, 2, 3)
            .map { loadTestFile("out_of_bounds_move_0$it.sgf") }
            .map { Lexer().tokenize(it) }
            .map { Parser().parse(it) }
            .forEach {
                assertThrows<InvalidGameException> { Validator().validate(it) }
            }
    }

    @Test
    fun `GIVEN duplicate point in setup property WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("duplicate_setup_move_point.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN node with black an white move WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("b_and_w_move.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN move with multiple values WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("multiple_value_move.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN KO property without move WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("single_ko.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN node with multiple move estimations WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("multiple_move_estimation.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN non-pointlist arrow WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("point_ar.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN one-point arrow WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("single_point_ar.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN duplicate arrow WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("duplicate_ar.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN node with multiple markup on same point WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("multiple_markup_point.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN node with multiple move number WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("multiple_move_number.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN node with invalid move number WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("invalid_move_number.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }

    @Test
    fun `GIVEN node with negative move number WHEN validating THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("negative_move_number.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertThrows<InvalidGameException> { Validator().validate(collection) }
    }
}
