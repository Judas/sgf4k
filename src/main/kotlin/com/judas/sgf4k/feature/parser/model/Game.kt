package com.judas.sgf4k.feature.parser.model


/**
 * Represents a go game. A game is structured syntactically
 * as a tree. The game exposes a first root GameNode, which
 * itself can contain zero, one or multiple child nodes.
 */
class Game(val rootNode: GameNode)