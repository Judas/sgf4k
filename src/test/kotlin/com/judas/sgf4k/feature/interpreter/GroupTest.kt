package com.judas.sgf4k.feature.interpreter

import com.judas.sgf4k.toGameCollection
import com.judas.sgf4k.utility.loadTestFile
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GroupTest {
    @Test
    fun `GIVEN intersection WHEN expanding THEN the whole group is retrieved`() {
        val collection = loadTestFile("simple_group.sgf").toGameCollection()
        val game = collection.games.first()
        val interpreter = Interpreter(game)

        val goban = interpreter.gobanFor(game.rootNode)
        val group = Group(goban.intersections[2][2]).apply { expand(goban.toMutableGoban()) }
        assertEquals(5, group.intersections.size)
    }
}
