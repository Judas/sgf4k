package com.judas.sgf4k.feature.parser.model

import com.judas.sgf4k.feature.parser.model.properties.SgfCommonPropertyKey
import com.judas.sgf4k.feature.parser.model.properties.SgfProperty
import com.judas.sgf4k.feature.parser.model.properties.SgfPropertyKey
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey

/**
 * GameNode is any node of a game. This can be any SGF node, whether it is an
 * actual move, markers, placement of stones...
 */
data class GameNode(val parentNode: GameNode?, val properties: List<SgfProperty>) {
    val children = mutableListOf<GameNode>()

    val isMoveNode: Boolean = properties.any { it.isMove }
    val isSetupNode: Boolean = properties.any { it.isSetup }

    fun getProperty(standardKey: SgfStandardPropertyKey): SgfProperty? =
        properties.firstOrNull { it.key is SgfPropertyKey.Standard && it.key.label == standardKey }

    fun getProperty(commonKey: SgfCommonPropertyKey): SgfProperty? =
        properties.firstOrNull { it.key is SgfPropertyKey.Common && it.key.label == commonKey }

    fun getProperty(customKey: String): SgfProperty? =
        properties.firstOrNull { it.key is SgfPropertyKey.Custom && it.key.label == customKey }

    internal fun previousMoveNode(): GameNode? {
        var tmpNode: GameNode? = parentNode
        while (tmpNode != null) {
            if (tmpNode.isMoveNode) return tmpNode
            tmpNode = tmpNode.parentNode
        }
        return null
    }
}

