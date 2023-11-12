package com.judas.sgf4k.feature.parser

import com.judas.sgf4k.feature.exceptions.InvalidSgfException
import com.judas.sgf4k.feature.interpreter.Interpreter
import com.judas.sgf4k.feature.lexer.Lexer
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey
import com.judas.sgf4k.toGameCollection
import com.judas.sgf4k.utility.assertSgfPropertyEquals
import com.judas.sgf4k.utility.loadTestFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ParserTest {
    @Test
    fun `GIVEN standard sgf WHEN parsing THEN game collection is well formed`() {
        val sgf = loadTestFile("minimal.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertEquals(1, collection.games.size)

        val rootNode = collection.games.first().rootNode
        assertEquals(9, rootNode.properties.size)
        assertSgfPropertyEquals(rootNode.getProperty(SgfStandardPropertyKey.FF), "4")
        assertSgfPropertyEquals(rootNode.getProperty(SgfStandardPropertyKey.GM), "1")
        assertSgfPropertyEquals(rootNode.getProperty(SgfStandardPropertyKey.PB), "Joueur Noir")
        assertSgfPropertyEquals(rootNode.getProperty(SgfStandardPropertyKey.PW), "Joueur Blanc")
        assertSgfPropertyEquals(rootNode.getProperty(SgfStandardPropertyKey.BR), "10k")
        assertSgfPropertyEquals(rootNode.getProperty(SgfStandardPropertyKey.WR), "8k")
        assertSgfPropertyEquals(rootNode.getProperty(SgfStandardPropertyKey.SZ), "19")
        assertSgfPropertyEquals(rootNode.getProperty(SgfStandardPropertyKey.KM), "6.5")
        assertSgfPropertyEquals(rootNode.getProperty(SgfStandardPropertyKey.RU), "Japanese")
        assertEquals(1, rootNode.children.size)

        val firstNode = rootNode.children.first()
        assertEquals(1, firstNode.properties.size)
        assertSgfPropertyEquals(firstNode.getProperty(SgfStandardPropertyKey.B), "cc")
        assertEquals(1, firstNode.children.size)

        val secondNode = firstNode.children.first()
        assertEquals(1, secondNode.properties.size)
        assertSgfPropertyEquals(secondNode.getProperty(SgfStandardPropertyKey.W), "qq")
        assertTrue(secondNode.children.isEmpty())
    }

    @Test
    fun `GIVEN multiple games in a collection WHEN parsing THEN all games are retrieved`() {
        val sgf = loadTestFile("collection.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)
        assertEquals(3, collection.games.size)
    }

    @Test
    fun `GIVEN missing property key WHEN lexing THEN key is skipped`() {
        val sgf = loadTestFile("missing_property_key.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)

        val rootNode = collection.games.first().rootNode
        assertEquals(1, rootNode.children.size)

        val firstNode = rootNode.children.first()
        assertEquals(1, firstNode.properties.size)
        assertSgfPropertyEquals(firstNode.getProperty(SgfStandardPropertyKey.W), "bb")
        assertEquals(0, firstNode.children.size)
    }

    @Test
    fun `GIVEN duplicate properties in same node WHEN parsing THEN an InvalidSgfException is thrown`() {
        val sgf = loadTestFile("duplicate_properties.sgf")
        val tokens = Lexer().tokenize(sgf)
        assertThrows<InvalidSgfException> { Parser().parse(tokens) }
    }

    @Test
    fun `GIVEN a property with empty value WHEN parsing THEN the property value is blank`() {
        val sgf = loadTestFile("empty_values.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)

        val emptyValueNode = collection.games.firstOrNull()
            ?.rootNode // ;FF[4]
            ?.children?.firstOrNull() // ;B[ab]
            ?.children?.firstOrNull() // ;W[]

        assertNotNull(emptyValueNode)
        val property = emptyValueNode.getProperty(SgfStandardPropertyKey.W)
        assertNotNull(property)
        assertEquals(1, property.values.size)
        assertTrue(property.values[0].isBlank())
    }

    @Test
    fun `GIVEN empty nodes WHEN parsing THEN empty nodes are skipped`() {
        val sgf = loadTestFile("empty_nodes.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)

        val lastNode = collection.games.firstOrNull()
            ?.rootNode // ;FF[4]
            ?.children?.firstOrNull() // ;B[aa]
            ?.children?.firstOrNull() // ;W[bb]

        assertNotNull(lastNode)
        assertEquals(0, lastNode.children.size)
        val property = lastNode.getProperty(SgfStandardPropertyKey.W)
        assertNotNull(property)
        assertEquals(1, property.values.size)
        assertEquals("bb", property.values[0])
    }

    @Test
    fun `GIVEN a valid game WHEN parsing THEN only move nodes are detected as such`() {
        val sgf = loadTestFile("minimal.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)

        val rootNode = collection.games.first().rootNode
        assertFalse(rootNode.isMoveNode)

        val firstNode = rootNode.children.first()
        assertTrue(firstNode.isMoveNode)

        val secondNode = firstNode.children.first()
        assertTrue(secondNode.isMoveNode)
    }

    @Test
    fun `GIVEN empty games WHEN parsing THEN empty games are filtered`() {
        val sgf = loadTestFile("empty_games.sgf")
        val tokens = Lexer().tokenize(sgf)
        val collection = Parser().parse(tokens)

        assertEquals(0, collection.games.size)
    }
}

