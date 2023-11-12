package com.judas.sgf4k.feature.interpreter

import com.judas.sgf4k.feature.interpreter.IntersectionState.*
import com.judas.sgf4k.toGameCollection
import com.judas.sgf4k.utility.loadTestFile
import com.judas.sgf4k.utility.nthChild
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InterpreterTest {
    @Test
    fun `GIVEN valid game WHEN interpreting THEN size is retrieved`() {
        val collection = loadTestFile("minimal.sgf").toGameCollection()
        val game = collection.games.first()
        val goban = Interpreter(game).gobanFor(game.rootNode)
        assertEquals(19, goban.size)
    }

    @Test
    fun `GIVEN game with moves WHEN interpreting THEN move count is computed`() {
        val collection = loadTestFile("simple_game.sgf").toGameCollection()
        val game = collection.games.first()
        val interpreter = Interpreter(game)

        var goban = interpreter.gobanFor(game.rootNode)
        assertNull(goban.moveNumber)

        goban = interpreter.gobanFor(game.rootNode.nthChild(1))
        assertEquals(1, goban.moveNumber)

        goban = interpreter.gobanFor(game.rootNode.nthChild(3))
        assertNull(goban.moveNumber)

        goban = interpreter.gobanFor(game.rootNode.nthChild(7))
        assertEquals(5, goban.moveNumber)
    }

    @Test
    fun `GIVEN game with capture WHEN interpreting THEN stones are removed`() {
        listOf(1, 2, 3, 4, 5)
            .forEach {
                val game = loadTestFile("capture_0$it.sgf")
                    .toGameCollection()
                    .games.first()
                val goban = Interpreter(game).gobanFor(game.rootNode.nthChild(1))

                when (it) {
                    1 -> {
                        assertEquals(EMPTY, goban.intersections[2][2].state)
                        assertEquals(BLACK, goban.intersections[3][2].state)
                    }

                    2 -> {
                        assertEquals(EMPTY, goban.intersections[2][2].state)
                        assertEquals(BLACK, goban.intersections[3][2].state)
                    }

                    3 -> {
                        assertEquals(WHITE, goban.intersections[2][2].state)
                        val mutableGoban = goban.toMutableGoban()
                        val group = Group(goban.intersections[2][2]).apply { expand(mutableGoban) }
                        assertTrue(group.isDead(mutableGoban))
                    }

                    4 -> {
                        assertEquals(EMPTY, goban.intersections[3][2].state)
                        assertEquals(WHITE, goban.intersections[2][3].state)
                        val mutableGoban = goban.toMutableGoban()
                        val group = Group(goban.intersections[2][3]).apply { expand(mutableGoban) }
                        assertTrue(group.isDead(mutableGoban))
                    }

                    5 -> {
                        assertEquals(EMPTY, goban.intersections[2][2].state)
                        assertEquals(EMPTY, goban.intersections[3][2].state)
                    }
                }
            }
    }

    @Test
    fun `GIVEN game with capture WHEN interpreting THEN prisoners are added`() {
        listOf(1, 2, 3, 4, 5)
            .forEach {
                val game = loadTestFile("capture_0$it.sgf")
                    .toGameCollection()
                    .games.first()
                val goban = Interpreter(game).gobanFor(game.rootNode.nthChild(1))

                when (it) {
                    1 -> assertEquals(1, goban.capturedWhiteStones)
                    2 -> assertEquals(1, goban.capturedWhiteStones)
                    3 -> assertEquals(0, goban.capturedWhiteStones)
                    4 -> assertEquals(1, goban.capturedWhiteStones)
                    5 -> assertEquals(2, goban.capturedWhiteStones)
                }
            }
    }
}