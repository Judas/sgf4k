package com.judas.sgf4k.feature.parser.model

/**
 * This is the main SGF model object. An SGF file consists
 * of a Game Collection. A game collection should contain at
 * least one Game as of the SGF format requirement.
 */
class GameCollection {
    val games: MutableList<Game> = mutableListOf()
}
