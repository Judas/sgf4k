package com.judas.sgf4k.feature.validator

import com.judas.sgf4k.feature.exceptions.InvalidGameException
import com.judas.sgf4k.feature.exceptions.InvalidSgfException
import com.judas.sgf4k.feature.interpreter.toCoordinates
import com.judas.sgf4k.feature.parser.model.GameCollection
import com.judas.sgf4k.feature.parser.model.GameNode
import com.judas.sgf4k.feature.parser.model.properties.SgfPropertyKey
import com.judas.sgf4k.feature.parser.model.properties.SgfPropertyType.*
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey.*

/**
 * Validator to ensure some checks on the GameCollection that are more
 * than language grammar checks.
 */
internal class Validator {
    /**
     * Check the validity of the given collection for invalid set of nodes / properties etc...
     */
    fun validate(gameCollection: GameCollection): GameCollection {
        // Cut the games into variations
        val variations: MutableList<MutableList<GameNode>> = mutableListOf()
        gameCollection.games.forEach { game ->
            // Skip empty games
            variations.add(mutableListOf(game.rootNode))
            extractAllVariations(game.rootNode, variations)
        }

        variations.forEach { variation ->
            // Size must be present at root node
            val sizeProperty = variation.first().getProperty(SZ)
            var gobanSize: Int?
            when {
                sizeProperty == null ->
                    throw InvalidGameException("No size property found at root node")

                sizeProperty.values.isEmpty() ->
                    throw InvalidGameException("Size property is empty")

                sizeProperty.values.size > 1 ->
                    throw InvalidGameException("Multiple size property values")

                sizeProperty.values.first().toIntOrNull() == null ->
                    throw InvalidGameException("Invalid size property value")

                else -> gobanSize = sizeProperty.values.first().toInt()
            }

            // Game mode should be present at root
            val gameModeProperty = variation.first().getProperty(GM)
            when {
                gameModeProperty == null ->
                    throw InvalidGameException("No game mode property found at root node")

                gameModeProperty.values.isEmpty() ->
                    throw InvalidGameException("Game mode property is empty")

                gameModeProperty.values.size > 1 ->
                    throw InvalidGameException("Multiple game mode property values")

                gameModeProperty.values.first().toIntOrNull() == null ->
                    throw InvalidGameException("Invalid game mode property value")

                gameModeProperty.values.first().toInt() != 1 ->
                    throw InvalidGameException("Game mode property should be 1")
            }

            // Each variation must contain only one node with game info properties
            variation
                .filter { node -> node.properties.any { it.key is SgfPropertyKey.Standard && it.key.label.type == GAME_INFO } }
                .let {
                    if (it.size > 1)
                        throw InvalidSgfException("Game info properties appear in multiple nodes of the same variation")
                }

            variation.forEach { node ->
                val hasRootNodeProperties =
                    node.properties.any { it.key is SgfPropertyKey.Standard && it.key.label.type == ROOT }
                val hasSetupProperties =
                    node.properties.any { it.key is SgfPropertyKey.Standard && it.key.label.type == SETUP }
                val hasMoveProperties =
                    node.properties.any { it.key is SgfPropertyKey.Standard && it.key.label.type == MOVE }

                // Root node properties should only appear inside root nodes
                val isRootNode = node.parentNode == null
                if (!isRootNode && hasRootNodeProperties)
                    throw InvalidSgfException("Root node properties appear outside of a game root node")

                // Setup properties must not be mixed with move properties in the same node
                if (hasSetupProperties && hasMoveProperties)
                    throw InvalidSgfException("Setup property alongside move property in the same node")

                // Points should be in the format [aa] or [aa:bb]
                listOf(AB, AW, AE, B, W)
                    .mapNotNull { node.getProperty(it) }
                    .flatMap { it.values }
                    .forEach {
                        if (!it.isMoveString()) throw InvalidGameException("Game contains a malformed move")
                    }

                // Out-of-bounds nodes are an error
                // It used to be ok in FF3, like [tt] for a pass move, we don't want it here
                listOf(AB, AW, AE, B, W)
                    .mapNotNull { node.getProperty(it) }
                    .flatMap { it.values }
                    .flatMap { it.toCoordinates() }
                    .filter { it.first >= gobanSize || it.second >= gobanSize }
                    .toList()
                    .size.let {
                        if (it > 0) throw InvalidGameException("Coordinates are out-of-bounds")
                    }

                // Points must be unique through setup properties
                listOf(AB, AW, AE)
                    .mapNotNull { node.getProperty(it) }
                    .flatMap { it.values }
                    .flatMap { it.toCoordinates() }
                    .let {
                        if (it.size != it.distinct().size)
                            throw InvalidGameException("Duplicate coordinates appear in setup node (AB / AE / AW)")
                    }

                // It is illegal to have B & W properties in the same node
                if (node.getProperty(W) != null && node.getProperty(B) != null)
                    throw InvalidGameException("B and W moves appear in the same node")

                // Standard moves must have only one value
                listOf(B, W)
                    .mapNotNull { node.getProperty(it) }
                    .flatMap { it.values }
                    .size.let {
                        if (it > 1) throw InvalidGameException("Multiple values in move node property")
                    }

                // A KO property without a black or white move within the same node is illegal.
                if (node.getProperty(KO) != null && (node.getProperty(B) == null && node.getProperty(W) == null))
                    throw InvalidGameException("KO appears in a node without B or W ")

                // DM / GB / GW / UC must not be mixed within the same node
                listOf(DM, GB, GW, UC)
                    .mapNotNull { node.getProperty(it) }
                    .size.let {
                        if (it > 1) throw InvalidGameException("Multiple DM / GB / GW / UC properties appear in the same node")
                    }

                node.getProperty(AR)?.values?.let { arValues ->
                    // AR values must be point list
                    arValues.forEach {
                        if (it.length != 5 || it.isMoveString().not())
                            throw InvalidGameException("AR value is malformed")

                        // It's illegal to specify a one point arrow, e.g. AR[cc:cc]
                        if (it.subSequence(0, 2) == it.subSequence(3, 5))
                            throw InvalidGameException("AR has a one-point value")
                    }

                    // It's illegal to specify the same arrow twice AR[aa:bb][aa:bb].
                    if (arValues.size != arValues.distinct().size)
                        throw InvalidGameException("Duplicate AR values appear")
                }

                // Points must be unique through markup properties
                listOf(LB, MA, SL, SQ, TR)
                    .mapNotNull { node.getProperty(it) }
                    .flatMap { it.values }
                    .flatMap { it.toCoordinates() }
                    .let {
                        if (it.size != it.distinct().size)
                            throw InvalidGameException("Duplicate coordinates appear inside markup node (LB / MA / SL / SQ / TR)")
                    }

                node.getProperty(MN)?.values?.let {
                    // MN value should be unique
                    if (it.size != 1) throw InvalidGameException("Multiple values in MN property")

                    // MN value should be an integer
                    if (it.first().toIntOrNull() == null) throw InvalidGameException("Invalid MN property")

                    // MN value should be a positive integer
                    if (it.first().toInt() < 0) throw InvalidGameException("Invalid MN property")
                }
            }
        }

        // Return input collection if valid
        return gameCollection
    }

    /**
     * Cut the games into a list of variations. Each variation starts from
     * the root node and finish at one of the game leaves.
     */
    private fun extractAllVariations(
        parentNode: GameNode,
        variations: MutableList<MutableList<GameNode>>
    ) {
        val parentVariation = variations.first { it.last() == parentNode }
        when (parentNode.children.size) {
            0 -> {} // No child : nothing to do

            1 -> {
                // Only one child
                // - add the node to the parentVariation
                // - extract variations from child
                val child = parentNode.children.first()
                parentVariation.add(child)
                extractAllVariations(child, variations)
            }

            else -> {
                // Multiple children
                // - remove parentVariation from list
                // - for each child
                //   - create a new variation with parentVariation + child
                //   - add it to list
                //   - extract variations from child
                variations.remove(parentVariation)
                parentNode.children.forEach { child ->
                    variations.add(parentVariation.toMutableList().also { it.add(child) })
                    extractAllVariations(child, variations)
                }
            }
        }
    }
}

private fun String.isMoveString(): Boolean = when (length) {
    0 -> true
    2 -> this[0].isLowercareLetter() && this[1].isLowercareLetter()
    5 -> this[0].isLowercareLetter() && this[1].isLowercareLetter()
            && this[2] == ':'
            && this[3].isLowercareLetter() && this[4].isLowercareLetter()

    else -> false
}

private fun Char.isLowercareLetter(): Boolean = this in 'a'..'z'