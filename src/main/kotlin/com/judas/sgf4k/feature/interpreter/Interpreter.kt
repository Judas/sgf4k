package com.judas.sgf4k.feature.interpreter

import com.judas.sgf4k.feature.exceptions.InvalidGameException
import com.judas.sgf4k.feature.exceptions.InvalidGameNodeException
import com.judas.sgf4k.feature.exceptions.Sgf4kRuntimeException
import com.judas.sgf4k.feature.interpreter.IntersectionState.*
import com.judas.sgf4k.feature.parser.model.Game
import com.judas.sgf4k.feature.parser.model.GameNode
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey.*

class Interpreter(val game: Game) {
    private val states = mutableMapOf<GameNode, Goban>()

    /**
     * Loads a game inside this goban.
     * The SGF standard are used to define the board position after
     * playing the moves : https://www.red-bean.com/sgf/ff5/m_vs_ax.htm
     */
    init {
        // Get size from root node (validator has ensured validity beforehand)
        val size = game.rootNode.getProperty(SZ)?.values?.firstOrNull()?.toIntOrNull()
            ?: throw InvalidGameException("Invalid size")
        loadNode(game.rootNode, size)
    }

    fun gobanFor(node: GameNode): Goban = states[node]
        ?: throw InvalidGameNodeException("Node can't be found in this game")

    /**
     * Loads the node into the goban using the SGF standard recommendations
     * https://www.red-bean.com/sgf/ff5/m_vs_ax.htm
     */
    private fun loadNode(gameNode: GameNode, size: Int) {
        // Get parent node goban
        val goban = (gameNode.parentNode
            ?.let { states[it] }
            ?.let { Goban.copyFromParent(gameNode, it) }
            ?: Goban.empty(gameNode, size)).toMutableGoban()

        states[gameNode] = when {
            // Play the setup on the previous goban
            gameNode.isSetupNode -> loadSetupNode(goban)

            // Play the move on the previous goban
            gameNode.isMoveNode -> loadMoveNode(goban)

            // Reset goban after misc node
            else -> loadMiscNode(goban)
        }

        // Load each child node
        gameNode.children.forEach { loadNode(it, size) }
    }

    /**
     * Apply the setup node to the provided goban.
     * No prisoner or move number update.
     */
    private fun loadSetupNode(goban: MutableGoban): Goban {
        overwrite(goban, AE, EMPTY)
        overwrite(goban, AB, BLACK)
        overwrite(goban, AW, WHITE)

        goban.moveNumber = null
        return goban.toReadOnlyGoban()
    }

    /**
     * Apply the setup node to the provided goban.
     * No prisoner or move number update.
     */
    private fun loadMoveNode(goban: MutableGoban): Goban {
        // 1. Overwrite intersection
        overwrite(goban, B, BLACK)
        overwrite(goban, W, WHITE)

        // 2. Check opponent adjacent groups for death
        val playedIntersection = intersections(goban, B)?.firstOrNull()
            ?: intersections(goban, W)?.firstOrNull()

        // If playedIntersection is null, this is a pass move, go to step 4
        playedIntersection?.let { played ->
            played.adjacentIntersections()
                .asSequence()
                .filter { it.first >= 0 && it.first < goban.size } // Ensure valid column
                .filter { it.second >= 0 && it.second < goban.size } // Ensure valid row
                .map { goban.intersections[it.first][it.second] } // Get corresponding intersection
                .filter { it.state == if (played.state == BLACK) WHITE else BLACK } // Ensure valid state (opponent color)
                .map { Group(it).apply { expand(goban) } } // Expand to full group
                .distinct()
                .toList()
                .forEach { group ->
                    if (group.isDead(goban)) group.removeFrom(goban)
                }

            // 3. Check played group for suicide
            val playedGroup = Group(played).apply { expand(goban) }
            if (playedGroup.isDead(goban)) playedGroup.removeFrom(goban)
        }

        // 4. Update move number (MN => overwrite the move number for this node)
        goban.gameNode.getProperty(MN)?.values?.firstOrNull()?.toInt()?.let {
            // MN is present, overwrite move number with given value
            goban.moveNumber = it
        } ?: run {
            // Update move number to last found move + 1
            val previousMoveNode = goban.gameNode.previousMoveNode()
            if (previousMoveNode == null) goban.moveNumber = 1
            else {
                val number = states[previousMoveNode]?.moveNumber
                    ?: throw Sgf4kRuntimeException("Move number computation error")
                goban.moveNumber = number + 1
            }
        }

        return goban.toReadOnlyGoban()
    }

    /**
     * Resets the goban after the misc node
     */
    private fun loadMiscNode(goban: MutableGoban): Goban {
        goban.moveNumber = null
        return goban.toReadOnlyGoban()
    }

    private fun overwrite(goban: MutableGoban, key: SgfStandardPropertyKey, state: IntersectionState) =
        goban.gameNode.getProperty(key)
            ?.values
            ?.flatMap { it.toCoordinates() }
            ?.forEach {
                goban.intersections[it.first][it.second] = Intersection(it.first, it.second, state)
            }

    private fun intersections(goban: MutableGoban, key: SgfStandardPropertyKey) =
        goban.gameNode.getProperty(key)
            ?.values
            ?.flatMap { it.toCoordinates() }
            ?.map { goban.intersections[it.first][it.second] }
}
