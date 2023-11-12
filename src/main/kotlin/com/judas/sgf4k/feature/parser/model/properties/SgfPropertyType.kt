package com.judas.sgf4k.feature.parser.model.properties

/**
 * Type of the SGF property.
 */
enum class SgfPropertyType {
    /**
     * Property about the move made
     * Must not be mixed with SETUP properties in the same node
     * Can be in root node, though it's bad style
     */
    MOVE,

    /**
     * Properties of the current position
     * Must not be mixed with MOVE properties in the same node
     */
    SETUP,

    /**
     * Global properties of the game, only present in game root node
     */
    ROOT,

    /**
     * Game-info properties, usually stored in root nodes.
     * May appear outside root nodes after merging games into
     * a single game tree.
     * Only one GAME_INFO node is allowed inside a given variation (from root to leaf)
     */
    GAME_INFO,

    /**
     * Anyone can create its own type of node so this is for fallback.
     */
    OTHER
}
