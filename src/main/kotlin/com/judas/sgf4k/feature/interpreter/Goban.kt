package com.judas.sgf4k.feature.interpreter

import com.judas.sgf4k.feature.parser.model.GameNode
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey.B
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey.W

/**
 * Represents the state of the goban for a given game node.
 *
 * Intersections are described by their coordinates and state.
 * Coordinates consists of a column index (0 is leftmost column) and
 * a row index (0 is topmost row).
 * B[db] => column=3, row=1, state=BLACK => intersections[3][1] = BLACK
 */
data class Goban internal constructor(
    /**
     * The game node represented in this goban
     */
    val gameNode: GameNode,
    /**
     * Size of the goban
     */
    val size: Int,
    /**
     * Move number of this node, can be null if it is not a move node
     */
    val moveNumber: Int?,
    /**
     * Black stones captured by white since the beginning of the game
     */
    val capturedBlackStones: Int,
    /**
     * White stones captured by black since the beginning of the game
     */
    val capturedWhiteStones: Int,
    /**
     * List of the intersections ot the goban. It's a list of list, each of the defined goban size.
     * B[db] => column=3, row=1, state=BLACK => intersections[3][1] = BLACK
     */
    val intersections: List<List<Intersection>>
) {
    internal companion object {
        fun empty(gameNode: GameNode, size: Int): Goban = Goban(
            gameNode,
            size,
            if (gameNode.isMoveNode) 1 else null,
            0,
            0,
            List(size) { i -> List(size) { j -> Intersection(i, j, IntersectionState.EMPTY) } }
        )

        fun copyFromParent(gameNode: GameNode, parentGoban: Goban): Goban = Goban(
            gameNode,
            parentGoban.size,
            parentGoban.moveNumber,
            parentGoban.capturedBlackStones,
            parentGoban.capturedWhiteStones,
            List(parentGoban.size) { i -> List(parentGoban.size) { j -> parentGoban.intersections[i][j] } }
        )
    }

    /**
     * Returns the specific intersections corresponding this Goban GameNode.
     * If the Goban GameNode is a play node this is the played move intersection.
     * If this is a pass move, or another type of node, the Intersection is null.
     */
    val currentIntersection: Intersection? =
        (gameNode.getProperty(B) ?: gameNode.getProperty(W))
            ?.values
            ?.flatMap { it.toCoordinates() }
            ?.map { intersections[it.first][it.second] }
            ?.firstOrNull()
}

internal class MutableGoban(
    var gameNode: GameNode,
    var size: Int,
    var moveNumber: Int?,
    var blackDeadStones: Int, // Black stone captured => White points
    var whiteDeadStones: Int, // White stone captured => Black points
    var intersections: MutableList<MutableList<Intersection>>
)

/**
 * Map this node value into goban coordinates if applicable.
 */
internal fun String.toCoordinates(): List<Pair<Int, Int>> =
    if (contains(":")) {
        // Point list [aa:bb]
        val points = mutableListOf<Pair<Int, Int>>()
        for (i in (this[0].toCoordinate())..(this[3].toCoordinate()))
            for (j in (this[1].toCoordinate())..(this[4].toCoordinate()))
                points.add(i to j)
        points
    } else if (isNotEmpty()) {
        // Point [aa]
        listOf(this[0].toCoordinate() to this[1].toCoordinate())
    } else listOf() // Pass move

internal fun Char.toCoordinate(): Int = this - 'a'

internal fun Goban.toMutableGoban(): MutableGoban =
    MutableGoban(gameNode, size, moveNumber, capturedBlackStones, capturedWhiteStones,
        MutableList(size) { i -> MutableList(size) { j -> intersections[i][j] } }
    )

internal fun MutableGoban.toReadOnlyGoban(): Goban =
    Goban(gameNode, size, moveNumber, blackDeadStones, whiteDeadStones, intersections)
